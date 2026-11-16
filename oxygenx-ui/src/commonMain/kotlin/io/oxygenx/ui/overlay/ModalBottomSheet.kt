// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.overlay

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import io.oxygenx.motion.OxygenMotionDefaults
import io.oxygenx.motion.OxygenMotionSpec
import io.oxygenx.theme.LocalContentColor
import io.oxygenx.theme.LocalTextStyle
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.theme.generated.GeneratedTokens
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/** 底部面板的停靠位置，与 M3 `SheetValue` 同义。 */
@Immutable
enum class SheetValue {
    /** 完全收起（不可见）。 */
    Hidden,

    /** 半屏停靠：面板顶边大约在容器中线。 */
    PartiallyExpanded,

    /** 完全展开：面板顶边贴到内容高度处。 */
    Expanded,
}

/**
 * 底部面板的状态机，语义对齐 M3 的 `SheetState`：持有停靠值 [currentValue]、位移与锚点，
 * 提供 [show] / [hide] / [expand] / [partialExpand] 四个挂起动作。
 *
 * 位移统一按像素记，所以状态本身不依赖 `Density`：[rememberModalBottomSheetState] 负责
 * 在测量后把「容器高 / 面板高」喂给 [updateAnchors]。
 *
 * **关闭要等 [hide] 返回**再让宿主移除面板：动画播完 `currentValue` 才会落到
 * [SheetValue.Hidden]，`ModalBottomSheet` 也正是在那时回调 `onDismissRequest`。
 */
@Stable
class SheetState internal constructor(
    val skipPartiallyExpanded: Boolean = false,
    val confirmValueChange: (SheetValue) -> Boolean = { true },
    internal val motion: OxygenMotionSpec,
) {
    /** 位移（px）：`Hidden` 时为容器高，`Expanded` 时为容器高减面板高。 */
    internal var offset by mutableFloatStateOf(0f)
        private set

    /** 三档锚点；容器或面板尺寸变化时整体替换。 */
    internal var anchors by mutableStateOf<Map<SheetValue, Float>>(emptyMap())
        private set

    /** 锚点是否已经算过；没算过之前不能入场（否则面板会先闪到顶部）。 */
    internal var positioned by mutableStateOf(false)
        private set

    internal var hasBeenShown by mutableStateOf(false)
        private set

    var currentValue: SheetValue by mutableStateOf(SheetValue.Hidden)
        private set

    val isVisible: Boolean get() = currentValue != SheetValue.Hidden

    /** 当前位移（px）。调试与测试用；业务读 [currentValue] 就够。 */
    fun requireOffset(): Float = offset

    /** 入场：支持半屏时停在半屏，否则直接全屏。 */
    suspend fun show() {
        hasBeenShown = true
        settleTo(SheetValue.PartiallyExpanded)
    }

    /** 收起。返回后宿主可以安全移除面板。 */
    suspend fun hide() = animateTo(SheetValue.Hidden)

    /** 展开到全屏。 */
    suspend fun expand() = settleTo(SheetValue.Expanded)

    /** 半屏停靠；不支持半屏时等价于 [expand]。 */
    suspend fun partialExpand() = settleTo(SheetValue.PartiallyExpanded)

    /** 按「容器高 / 面板高」重算锚点；首次算完后把位移摆到隐藏位。 */
    internal fun updateAnchors(containerHeight: Float, sheetHeight: Float) {
        if (containerHeight <= 0f || sheetHeight <= 0f) return
        val expanded = (containerHeight - sheetHeight).coerceAtLeast(0f)
        val hidden = containerHeight
        val partial = (containerHeight * PARTIAL_FRACTION).coerceIn(expanded, hidden)
        val next = buildMap {
            put(SheetValue.Hidden, hidden)
            put(SheetValue.Expanded, expanded)
            // 面板本身比半屏矮、或半屏与两端重合时，不提供这个停靠点。
            val usable = partial - expanded > MIN_DETENT_GAP && hidden - partial > MIN_DETENT_GAP
            if (!skipPartiallyExpanded && usable) put(SheetValue.PartiallyExpanded, partial)
        }
        if (next != anchors) {
            anchors = next
            if (!positioned) {
                offset = hidden
                positioned = true
            }
        }
    }

    /**
     * 拖拽位移（px，正数向下）。夹在两端之间，不做过冲。
     *
     * 这里**只动位移、不改 [currentValue]**：停靠值代表「已经落定的位置」，
     * 拖动途中改成就近档位会让宿主以为「已经收起」，面板会在动画前就被抽走。
     */
    internal fun dragBy(delta: Float) {
        val top = anchors[SheetValue.Expanded] ?: return
        val bottom = anchors[SheetValue.Hidden] ?: return
        offset = (offset + delta).coerceIn(top, bottom)
    }

    /** 松手：速度够大按方向翻一档，否则就近吸附，最后跑弹簧。 */
    internal suspend fun settle(velocity: Float) {
        animateTo(targetFor(velocity), initialVelocity = velocity)
    }

    private fun targetFor(velocity: Float): SheetValue {
        val ordered = anchors.entries.sortedBy { it.value } // Expanded → Partially → Hidden
        if (ordered.isEmpty()) return currentValue
        if (abs(velocity) > FLING_THRESHOLD) {
            // 从「手指现在最近的那一档」出发再走一格，而不是从上次落定的档位：
            // 否则一次长距离拖动后甩一下会往反方向跳。
            val nearest = ordered.indexOfFirst { it.key == nearestValue() }.coerceAtLeast(0)
            // 位移越小越「展开」：向上甩（负速度）走 index - 1，向下甩走 index + 1。
            val next = if (velocity < 0f) nearest - 1 else nearest + 1
            return ordered[next.coerceIn(0, ordered.lastIndex)].key
        }
        return nearestValue() ?: currentValue
    }

    private fun nearestValue(): SheetValue? = anchors.entries.minByOrNull { abs(it.value - offset) }?.key

    private suspend fun settleTo(value: SheetValue) {
        animateTo(if (value in anchors) value else SheetValue.Expanded)
    }

    internal suspend fun animateTo(target: SheetValue, initialVelocity: Float = 0f) {
        val targetOffset = anchors[target] ?: return
        if (target == currentValue && abs(offset - targetOffset) < SETTLE_EPSILON) return
        if (target != currentValue && !confirmValueChange(target)) {
            // 被拦下就回到当前停靠点，不能把面板丢在半空。
            val home = anchors[currentValue] ?: return
            animate(
                initialValue = offset,
                targetValue = home,
                initialVelocity = initialVelocity,
                animationSpec = settleSpring,
            ) { value, _ -> offset = value }
            offset = home
            return
        }
        // 收起走 `motion.exit`（与弹窗同一套滑出），其余一律用弹簧，拖动松手才有惯性。
        val spec = if (target == SheetValue.Hidden) motion.exit else settleSpring
        animate(
            initialValue = offset,
            targetValue = targetOffset,
            initialVelocity = initialVelocity,
            animationSpec = spec,
        ) { value, _ ->
            offset = value
        }
        offset = targetOffset
        currentValue = target
    }

    private companion object {
        /** 半屏停靠点取容器高度的一半（与 M3 同值）。 */
        const val PARTIAL_FRACTION = 0.5f

        /** 两档之间小于这个距离（px）就不算一个有效停靠点。 */
        const val MIN_DETENT_GAP = 1f

        /** 超过这个速度（px/s）就按方向翻一档，不看距离。 */
        const val FLING_THRESHOLD = 900f

        const val SETTLE_EPSILON = 0.5f
    }
}

/** 面板收放 / 拖动松手用的弹簧：与 `spring.dialog` 同源。 */
private val settleSpring = spring<Float>(
    dampingRatio = GeneratedTokens.SPRING_DIALOG_DAMPING_RATIO,
    stiffness = GeneratedTokens.SPRING_DIALOG_STIFFNESS,
    visibilityThreshold = GeneratedTokens.SPRING_DIALOG_VISIBILITY_THRESHOLD,
)

/**
 * 记住一个 [SheetState]。
 *
 * 不用 `rememberSaveable`：面板不是跨进程恢复的对象，重建后重新入场比恢复一个半开的
 * 面板更符合直觉（配置变更时也会重新入场，与 M3 的行为一致）。
 *
 * @param skipPartiallyExpanded 为 true 时只保留「全屏 / 收起」两档。
 * @param confirmValueChange 拦截停靠变化；返回 false 的那次切换不执行，面板回到原位。
 * @param motion 入场 / 收起的动效规格。
 */
@Composable
fun rememberModalBottomSheetState(
    skipPartiallyExpanded: Boolean = false,
    confirmValueChange: (SheetValue) -> Boolean = { true },
    motion: OxygenMotionSpec = OxygenMotionDefaults.dialogSlide(),
): SheetState = remember(skipPartiallyExpanded, motion.specId) {
    SheetState(
        skipPartiallyExpanded = skipPartiallyExpanded,
        confirmValueChange = confirmValueChange,
        motion = motion,
    )
}

/**
 * 底部面板：从底部滑入的模态面板，带拖拽手柄与 M3 语义的停靠点。
 *
 * 与 [io.oxygenx.ui.basic.Dialog] 的分工：Dialog 是贴底的「弹窗卡片」（左右留 8dp、
 * 20dp 圆角、内容由调用方排）；底部面板通栏贴底、只有上面两个角是圆角。
 *
 * 语义（与 M3 `ModalBottomSheet` 一致，只有参数形态按本项目风格收敛）：
 * - 停靠：「半屏 ↔ 全屏 ↔ 收起」三档，`skipPartiallyExpanded = true` 退化成两档；
 * - 入场即 `show()`，收起走 `sheetState.hide()`，**`hide()` 返回后宿主再移除面板**；
 * - 点遮罩、返回键都会先播完下滑再回调 [onDismissRequest]；
 * - 内容可滚动时内容先滚，滚到顶继续下拉才拖面板（走 nested-scroll 残量）；
 *   拖拽手柄和不可滚动的区域直接拖面板；
 * - [SheetState.confirmValueChange] 可以拦下某次切换（例如有未保存输入时不许关）。
 *
 * - Spec ID: `CMP-BOTTOM-SHEET`
 * - Baseline: `Material 3 modal bottom sheet / OnePlus 9R (LE2100) / 2026-11-16`
 * - Reference: `catalog/reference/CMP-BOTTOM-SHEET/`
 * - Motion: `MOT-ENTER-001`（`spec.dialog.slide`）；拖动松手用 `spring.dialog`
 * - Status: `draft`
 *
 * @param onDismissRequest 面板收起到不可见之后回调。
 * @param modifier 外部 Modifier，作用在整块弹层上（遮罩 + 面板）。
 * @param sheetState 停靠状态，默认半屏入场。
 * @param dragHandle 顶部手柄槽；传 null 去掉手柄（此时只能点遮罩 / 返回键收起）。
 * @param scrimColor 遮罩颜色；透明度跟着面板位置走。
 * @param containerColor 面板底色。
 * @param content 面板内容槽（[ColumnScope]），已下发内容色与 `type.body.medium` 排版。
 */
@Composable
fun ModalBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    dragHandle: (@Composable () -> Unit)? = { SheetDragHandle() },
    scrimColor: Color = OxygenTheme.colorScheme.scrim,
    containerColor: Color = OxygenTheme.colorScheme.surfaceContainer,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    var sheetHeightPx by remember { mutableFloatStateOf(0f) }
    // 调用方每次重组都可能传一个新 lambda；用 updated state 取最新值，
    // 免得 effect / pointerInput 里捕获到过期的那一个。
    val currentOnDismiss by rememberUpdatedState(onDismissRequest)

    val dismiss: () -> Unit = {
        scope.launch {
            sheetState.hide()
            currentOnDismiss()
        }
    }

    // 所有收起路径（遮罩 / 返回键 / 内容里调 hide）最终都会把停靠值落到 Hidden，
    // 这里统一补一次通知：即便调用方忘了在 hide() 之后回调，面板也不会「关不掉」。
    LaunchedEffect(sheetState.currentValue, sheetState.hasBeenShown) {
        if (sheetState.hasBeenShown && sheetState.currentValue == SheetValue.Hidden) {
            currentOnDismiss()
        }
    }

    Popup(
        onDismissRequest = { dismiss() },
        properties = PopupProperties(focusable = true),
    ) {
        BoxWithConstraints(modifier = modifier.fillMaxSize()) {
            val maxSheetHeight = maxHeight * 0.9f
            val containerHeightPx = with(density) { maxHeight.toPx() }

            LaunchedEffect(containerHeightPx, sheetHeightPx, sheetState) {
                sheetState.updateAnchors(containerHeightPx, sheetHeightPx)
                // 两样尺寸都到手才入场，否则面板会先从顶部滑一下。
                if (sheetState.positioned && !sheetState.hasBeenShown) {
                    sheetState.show()
                }
            }

            // 遮罩：透明度跟着面板位置走，面板越靠上遮罩越实。
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val hidden = sheetState.anchors[SheetValue.Hidden] ?: 0f
                        val visible = if (hidden <= 0f) 1f else 1f - sheetState.offset / hidden
                        alpha = visible.coerceIn(0f, 1f)
                    }
                    .background(scrimColor)
                    .pointerInput(Unit) { detectTapGestures { dismiss() } },
            )

            val dragState = rememberDraggableState { delta -> sheetState.dragBy(delta) }
            val nestedScroll = remember(sheetState) { sheetNestedScroll(sheetState) }
            val sheetModifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .heightIn(max = maxSheetHeight)
                .offset { IntOffset(0, sheetState.offset.roundToInt()) }
                .clip(
                    // 只有上面两个角是圆的：用卡片圆角令牌拼一个上圆下方。
                    RoundedCornerShape(
                        topStart = OxygenTheme.shapes.cornerCard.topStart,
                        topEnd = OxygenTheme.shapes.cornerCard.topEnd,
                        bottomEnd = CornerSize(0.dp),
                        bottomStart = CornerSize(0.dp),
                    ),
                )
                .background(containerColor)
                .onSizeChanged { sheetHeightPx = it.height.toFloat() }
                .draggable(
                    state = dragState,
                    orientation = Orientation.Vertical,
                    onDragStopped = { velocity -> sheetState.settle(velocity) },
                )
                .nestedScroll(nestedScroll)

            CompositionLocalProvider(
                LocalContentColor provides OxygenTheme.colorScheme.onSurface,
                LocalTextStyle provides OxygenTheme.typography.bodyMedium,
            ) {
                Column(modifier = sheetModifier) {
                    if (dragHandle != null) {
                        dragHandle()
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding(),
                        verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall),
                    ) {
                        content()
                        Spacer(modifier = Modifier.height(OxygenTheme.spacing.gapSmall))
                    }
                }
            }
        }
    }
}

/**
 * 内容与面板之间的仲裁：向上先展开面板，向下在内容到顶后再收面板。
 *
 * 之所以走 nested-scroll 而不是全局 `draggable`：内容自带滚动时手势归内容，
 * 只有内容吃不下的残量才应该驱动面板，否则「半屏 + 长列表」会变成拖不动内容。
 */
private fun sheetNestedScroll(state: SheetState) = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        // 手指向上（available.y < 0）且面板还没展开到位：先展开面板。
        if (source != NestedScrollSource.UserInput || available.y >= 0f) return Offset.Zero
        val expanded = state.anchors[SheetValue.Expanded] ?: return Offset.Zero
        val room = state.offset - expanded
        if (room <= 0f) return Offset.Zero
        val delta = available.y.coerceAtLeast(-room)
        state.dragBy(delta)
        return Offset(0f, delta)
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset {
        // 手指向下（available.y > 0）且内容已经到顶：残量用来收面板。
        if (source != NestedScrollSource.UserInput || available.y <= 0f) return Offset.Zero
        val hidden = state.anchors[SheetValue.Hidden] ?: return Offset.Zero
        val room = hidden - state.offset
        if (room <= 0f) return Offset.Zero
        val delta = available.y.coerceAtMost(room)
        state.dragBy(delta)
        return Offset(0f, delta)
    }
}

/** 顶部拖拽手柄：32×4 药丸，居中在 22dp 区域里。 */
@Composable
private fun SheetDragHandle() {
    val spacing = OxygenTheme.spacing
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(spacing.sheetHandleInset),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(width = spacing.sheetHandleWidth, height = spacing.sheetHandleHeight)
                .clip(RoundedCornerShape(spacing.sheetHandleHeight / 2))
                .background(OxygenTheme.colorScheme.outlineVariant),
        )
    }
}
