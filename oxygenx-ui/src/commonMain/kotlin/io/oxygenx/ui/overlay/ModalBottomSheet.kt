// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.overlay

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.window.DialogProperties
import io.oxygenx.motion.OxygenMotionDefaults
import io.oxygenx.motion.OxygenMotionSpec
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.theme.ProvideContentStyle
import io.oxygenx.theme.generated.GeneratedTokens
import kotlinx.coroutines.launch
import androidx.compose.ui.window.Dialog as PlatformDialog

/** 面板的停靠点。 */
enum class SheetValue {
    /** 完全收起，面板在屏幕之外。 */
    Hidden,

    /** 半屏停靠。面板比半屏矮、或 `skipPartiallyExpanded = true` 时没有这一档。 */
    PartiallyExpanded,

    /** 全屏展开：面板顶边贴在容器顶边（面板比容器高时就是贴顶）。 */
    Expanded,
}

/**
 * 面板的停靠状态：位移与吸附交给 foundation 的 [AnchoredDraggableState]。
 *
 * 这一层与 M3 `SheetState` 同构——锚点算法、位置阈值（下一档距离的一半）、速度阈值
 * （[VelocityThreshold]）、松手后的吸附与惯性都沿用 M3 的口径，只是锚点由本组件的
 * 「容器高 / 面板高」两个量算出，不依赖任何 Material 组件。自己只多做了三件事：
 *
 * - 把锚点从「像素位移」翻译成 [SheetValue]，并在尺寸变化时整体替换（[updateAnchors]）；
 * - [currentValue] 给业务读停靠档，[requireOffset] 给渲染读位移；
 * - 入场 / 收起的动效规格走本项目的 `spec.dialog.slide`，与弹窗同一套滑入滑出。
 */
@Stable
class SheetState internal constructor(
    val skipPartiallyExpanded: Boolean = false,
    val confirmValueChange: (SheetValue) -> Boolean = { true },
    internal val motion: OxygenMotionSpec,
) {
    /**
     * 位移真源（px）：`Hidden` 为容器高、`Expanded` 为容器高减面板高，数值越大越靠下。
     * 锚点没算出来之前不能读，读 [requireOffset] 之外的内部值会拿到 `NaN`。
     *
     * 位置阈值与吸附弹簧不挂在 state 上（foundation 1.12 起那套构造已废弃），
     * 而是由 `Modifier.anchoredDraggable(flingBehavior = AnchoredDraggableDefaults.flingBehavior(...))`
     * 传入，见 [ModalBottomSheet]。
     */
    // `confirmValueChange` 在 foundation 1.12 里标了废弃（建议改成「不把非法档位放进锚点集合」），
    // 但本组件刻意保留 M3 的语义：拦下之后回到原档，而不是让那一档根本不存在。
    @Suppress("DEPRECATION")
    internal val anchored = AnchoredDraggableState(
        initialValue = SheetValue.Hidden,
        // 锚点先留空，等容器与面板尺寸都量到再整体替换（同 M3 的 `initAnchors`）。
        anchors = DraggableAnchors { },
        confirmValueChange = confirmValueChange,
    )

    /** 当前（或正在动画去往的）停靠档。 */
    val currentValue: SheetValue get() = anchored.currentValue

    /** 目标停靠档；拖动途中可能已经指向下一档。 */
    val targetValue: SheetValue get() = anchored.targetValue

    /** 是否已经落定过的停靠档；用来判断「收起动画真的跑完了」。 */
    internal val settledValue: SheetValue get() = anchored.settledValue

    /** 是否可见（不处于收起态）。 */
    val isVisible: Boolean get() = currentValue != SheetValue.Hidden

    /** 锚点是否已经算过；没算过之前不能入场，否则面板会先闪一下。 */
    internal var positioned by mutableStateOf(false)
        private set

    /** 当前位移（px）。调试与测试用；业务读 [currentValue] 就够。 */
    fun requireOffset(): Float = anchored.requireOffset()

    /** 入场：支持半屏时停在半屏，否则直接全屏。 */
    suspend fun show() = animateTo(partialTarget)

    /** 收起。返回后宿主可以安全移除面板。 */
    suspend fun hide() = animateTo(SheetValue.Hidden)

    /** 展开到全屏。 */
    suspend fun expand() = animateTo(SheetValue.Expanded)

    /** 半屏停靠；不支持半屏时等价于 [expand]。 */
    suspend fun partialExpand() = animateTo(partialTarget)

    /** 拖动残量（px，正数向下）；内容滚不动的部分交给面板时用。 */
    internal fun dispatchRawDelta(delta: Float): Float = if (positioned) anchored.dispatchRawDelta(delta) else 0f

    /** 预测式返回的实际起点：手势开始前的位移，用来把进度换算成位移。 */
    private var backBaseOffset: Float? = null

    /**
     * 返回手势进度（0..1）：面板从「手势开始时的那一档」线性跟到收起位。
     *
     * 用位移而不是锚点做插值，是为了手势中途取消能原路弹回，不会先跳到相邻档。
     */
    internal fun onBackProgress(progress: Float) {
        if (!positioned) return
        val hidden = anchored.anchors.positionOf(SheetValue.Hidden)
        if (hidden.isNaN()) return
        val base = backBaseOffset ?: anchored.offset.also { backBaseOffset = it }
        val target = base + (hidden - base) * progress.coerceIn(0f, 1f)
        anchored.dispatchRawDelta(target - anchored.offset)
    }

    /** 返回手势被取消（手指划回）：弹回手势开始时的档位。 */
    internal suspend fun onBackCancelled() {
        backBaseOffset = null
        animateTo(currentValue)
    }

    /** 返回手势确认：清掉基准，收起动画由调用方发起。 */
    internal fun onBackCommitted() {
        backBaseOffset = null
    }

    /** 按「容器高 / 面板高」重算锚点；尺寸没变就什么都不做，免得把展开态拽回收起态。 */
    internal fun updateAnchors(containerHeight: Float, sheetHeight: Float) {
        if (containerHeight <= 0f || sheetHeight <= 0f) return
        if (positioned && containerHeight == lastContainerHeight && sheetHeight == lastSheetHeight) return
        lastContainerHeight = containerHeight
        lastSheetHeight = sheetHeight

        val expanded = (containerHeight - sheetHeight).coerceAtLeast(0f)
        val hidden = containerHeight
        val partial = (containerHeight * (1f - PARTIAL_EXPANDED_RATIO)).coerceIn(expanded, hidden)
        // 面板比半屏矮、或半屏与相邻档距离太近时，不提供中间档（与 M3 的 `initAnchors` 同条件）。
        val hasPartial = !skipPartiallyExpanded &&
            partial - expanded > MIN_DETENT_GAP &&
            hidden - partial > MIN_DETENT_GAP
        val next = DraggableAnchors {
            SheetValue.Hidden at hidden
            SheetValue.Expanded at expanded
            if (hasPartial) SheetValue.PartiallyExpanded at partial
        }
        // 停靠值不在新锚点里（例如半屏档消失）就回到收起，位移跟着锚点走。
        val keep = if (next.hasPositionFor(currentValue)) currentValue else SheetValue.Hidden
        anchored.updateAnchors(next, keep)
        positioned = true
    }

    private suspend fun animateTo(value: SheetValue) {
        val target = if (anchored.anchors.hasPositionFor(value)) value else SheetValue.Expanded
        if (!anchored.anchors.hasPositionFor(target)) return
        // 收起走 `motion.exit`（与弹窗同一套滑出），其余用弹簧，拖动松手才有惯性。
        val spec: AnimationSpec<Float> = if (target == SheetValue.Hidden) motion.exit else settleSpring
        anchored.animateTo(target, spec)
    }

    private val partialTarget: SheetValue
        get() = if (anchored.anchors.hasPositionFor(SheetValue.PartiallyExpanded)) {
            SheetValue.PartiallyExpanded
        } else {
            SheetValue.Expanded
        }

    private var lastContainerHeight = -1f
    private var lastSheetHeight = -1f

    private companion object {
        /** 半屏停靠点取容器高度的一半（与 M3 同值）。 */
        const val PARTIAL_EXPANDED_RATIO = 0.5f

        /** 两档之间小于这个距离（px）就不算一个有效停靠点。 */
        const val MIN_DETENT_GAP = 1f
    }
}

/** 位置阈值：走完到下一档距离的一半就翻档（M3 `SwipeableDefaults.PositionalThreshold` 同值）。 */
private const val POSITIONAL_THRESHOLD_FRACTION = 0.5f

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
 * 实现**按 M3 的骨架**搭：窗口是全屏 `Dialog`（`usePlatformDefaultWidth = false`），位移与吸附
 * 是 foundation 的 [AnchoredDraggableState] + `Modifier.anchoredDraggable`（M3 `ModalBottomSheet`
 * 的同一套内核），锚点由「容器高 / 面板高」算出，位置阈值取下一档距离的一半；内容与面板的
 * 仲裁走 nested-scroll 残量。视觉一律用本项目的令牌（圆角、遮罩、手柄），不引入任何 Material
 * 组件。
 *
 * 与 M3 的两点差异：①遮罩是本组件自绘的（平台窗口那层是常数，跟不了面板位置），因此窗口上要
 * 把主题的 `backgroundDimAmount` 归零并关掉系统栏内缩，见 [ConfigureDialogWindow]；
 * ②返回键走**预测式返回**：Android 14+ 的手势进度会驱动面板跟手回退，取消则弹回原档，
 * 见 [SheetPredictiveBackHandler]。
 *
 * 与 [io.oxygenx.ui.basic.Dialog] 的分工：Dialog 是贴底的「弹窗卡片」（左右留 8dp、
 * 20dp 圆角、内容由调用方排）；底部面板通栏贴底、只有上面两个角是圆角。
 *
 * 语义（与 M3 `ModalBottomSheet` 一致，只有参数形态按本项目风格收敛）：
 * - 停靠：「半屏 ↔ 全屏 ↔ 收起」三档，`skipPartiallyExpanded = true` 退化成两档；
 * - 入场即 `show()`，收起走 `sheetState.hide()`，**`hide()` 返回后宿主再移除面板**；
 * - 点遮罩、返回键（含预测式返回手势）都会先播完下滑再回调 [onDismissRequest]；
 * - 内容可滚动时内容先滚，滚到顶继续下拉才拖面板（走 nested-scroll 残量）；
 *   拖拽手柄和不可滚动的区域直接拖面板；
 * - [SheetState.confirmValueChange] 可以拦下某次切换（例如有未保存输入时不许关）。
 *
 * - Spec ID: `CMP-BOTTOM-SHEET`
 * - Baseline: `Material 3 modal bottom sheet / OnePlus 9R (LE2100) / 2026-11-16`
 * - Reference: `catalog/reference/CMP-BOTTOM-SHEET/`
 * - Motion: `MOT-ENTER-001`（`spec.dialog.slide`）+ `spring.dialog`（拖动松手），可通过 [sheetState] 覆盖。
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

    // 本次挂载内是否真的展开过。不能用 state 上的「曾经入场过」判断：宿主一般把面板包在
    // `if (open)` 里，而 [sheetState] 是跨挂载保留的，重新打开时那个标记已经是 true。
    var openedHere by remember { mutableStateOf(false) }

    // 所有收起路径（遮罩 / 返回键 / 内容里调 hide）最终都会落到 Hidden，
    // 这里统一补一次通知：即便调用方忘了在 hide() 之后回调，面板也不会「关不掉」。
    // 判据用 `settledValue` 而不是 `currentValue`：后者在动画开始时就已经指向目标档。
    LaunchedEffect(sheetState.settledValue) {
        if (sheetState.settledValue == SheetValue.Hidden) {
            if (openedHere) currentOnDismiss()
        } else {
            openedHere = true
        }
    }

    // 窗口用全屏 Dialog（M3 同款）：与锚点无关、天生铺满整屏，返回键 / IME / 无障碍都由
    // 平台窗口接管。遮罩仍由本组件自绘——平台那层是常数，跟不了面板位置，见 [ConfigureDialogWindow]。
    PlatformDialog(
        onDismissRequest = { dismiss() },
        properties = DialogProperties(
            // Android 上把返回键让给预测式返回；其余平台直接由窗口关闭。
            dismissOnBackPress = SheetDismissOnBackPress,
            // 遮罩点击自己接管，平台只认「内容之外」的点击。
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        ConfigureDialogWindow()
        // 必须组合在 Dialog **里面**：平台 Dialog 是 ComponentDialog，它有自己的
        // OnBackPressedDispatcherOwner，返回事件只发给对话框窗口这一层；放到 Dialog 外面
        // 就会注册到 Activity 的 dispatcher 上，对话框在前台时完全收不到返回手势。
        SheetPredictiveBackHandler(
            enabled = sheetState.positioned &&
                sheetState.isVisible &&
                !sheetState.anchored.isAnimationRunning,
            onProgress = { progress -> sheetState.onBackProgress(progress) },
            onCancel = { scope.launch { sheetState.onBackCancelled() } },
            onBack = {
                sheetState.onBackCommitted()
                dismiss()
            },
        )
        BoxWithConstraints(modifier = modifier.fillMaxSize()) {
            val maxSheetHeight = maxHeight * 0.9f
            val containerHeightPx = with(density) { maxHeight.toPx() }

            LaunchedEffect(containerHeightPx, sheetHeightPx, sheetState) {
                sheetState.updateAnchors(containerHeightPx, sheetHeightPx)
                // 两样尺寸都到手才入场。判据是「当前是不是收起」，不是「是否曾经入场过」：
                // 后者跨挂载保留，第二次挂载时永远为 true，面板就再也不会入场。
                if (sheetState.positioned && sheetState.currentValue == SheetValue.Hidden) {
                    sheetState.show()
                }
            }

            // 遮罩：透明度跟着面板位置走，面板越靠上遮罩越实。
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val anchors = sheetState.anchored.anchors
                        val hidden = anchors.positionOf(SheetValue.Hidden)
                        val expanded = anchors.positionOf(SheetValue.Expanded)
                        val span = hidden - expanded
                        alpha = if (!sheetState.positioned || span.isNaN() || span <= 0f) {
                            0f
                        } else {
                            // 1 = 全展开（最实），0 = 收起：按两个锚点之间归一，不用绝对位移。
                            (1f - (sheetState.requireOffset() - expanded) / span).coerceIn(0f, 1f)
                        }
                    }
                    .background(scrimColor)
                    .pointerInput(Unit) { detectTapGestures { dismiss() } },
            )

            val nestedScroll = remember(sheetState) { sheetNestedScroll(sheetState) }
            // 松手怎么吸：位置阈值（到下一档距离的一半）+ 吸附弹簧，都交给框架的 fling 行为，
            // 这也是 foundation 1.12 起推荐的写法（state 上的那套阈值参数已废弃）。
            val flingBehavior = AnchoredDraggableDefaults.flingBehavior(
                state = sheetState.anchored,
                positionalThreshold = { distance -> distance * POSITIONAL_THRESHOLD_FRACTION },
                animationSpec = settleSpring,
            )
            val sheetModifier = Modifier
                // 位移的真源是「面板顶边相对容器顶边」的位置（见 [SheetState.anchored]），
                // 所以这里必须贴顶对齐再向下位移；贴底对齐会被推到屏幕之外。
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .heightIn(max = maxSheetHeight)
                .offset {
                    val current = if (sheetState.positioned) sheetState.requireOffset() else containerHeightPx
                    IntOffset(0, current.toInt())
                }
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
                // 与 M3 同一套：拖动位移、位置阈值、松手吸附都在框架里。
                .anchoredDraggable(
                    state = sheetState.anchored,
                    orientation = Orientation.Vertical,
                    flingBehavior = flingBehavior,
                )
                .nestedScroll(nestedScroll)

            ProvideContentStyle(
                contentColor = OxygenTheme.colorScheme.onSurface,
                textStyle = OxygenTheme.typography.bodyMedium,
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
 * 残量交给 [AnchoredDraggableState.dispatchRawDelta]，越界由框架夹在两端锚点之间。
 */
private fun sheetNestedScroll(state: SheetState) = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        // 手指向上（available.y < 0）且面板还没展开到位：先展开面板。
        if (source != NestedScrollSource.UserInput || available.y >= 0f) return Offset.Zero
        return Offset(0f, state.dispatchRawDelta(available.y))
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset {
        // 手指向下（available.y > 0）且内容已经到顶：残量用来收面板。
        if (source != NestedScrollSource.UserInput || available.y <= 0f) return Offset.Zero
        return Offset(0f, state.dispatchRawDelta(available.y))
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
