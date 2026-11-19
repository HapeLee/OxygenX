// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.overlay

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import io.oxygenx.motion.OxygenAnimation
import io.oxygenx.motion.OxygenMotionDefaults
import io.oxygenx.motion.OxygenMotionSpec
import io.oxygenx.motion.rememberOxygenAnimation
import io.oxygenx.theme.LocalContentColor
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.theme.ProvideContentStyle
import io.oxygenx.theme.cornerMenu
import io.oxygenx.theme.generated.GeneratedTokens
import io.oxygenx.ui.basic.IndicationDefaults
import io.oxygenx.ui.basic.Surface
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

private val LocalMenuInteractive = staticCompositionLocalOf { true }

@Stable
internal class DropdownMenuAnimation(
    val scale: OxygenAnimation,
    val fade: OxygenAnimation,
    val present: State<Boolean>,
)

@Composable
internal fun rememberDropdownMenuAnimation(
    expanded: Boolean,
    motion: OxygenMotionSpec,
    fadeMotion: OxygenMotionSpec,
): DropdownMenuAnimation {
    val scale = rememberOxygenAnimation(motion)
    val fade = rememberOxygenAnimation(fadeMotion)
    val present = remember { mutableStateOf(expanded) }
    LaunchedEffect(expanded, motion, fadeMotion) {
        if (!expanded && !present.value) return@LaunchedEffect
        if (expanded) present.value = true
        coroutineScope {
            launch { scale.animateTo(if (expanded) 1f else 0f) }
            launch { fade.animateTo(if (expanded) 1f else 0f) }
        }
        if (!expanded) present.value = false
    }
    return remember(scale, fade, present) { DropdownMenuAnimation(scale, fade, present) }
}

/** 经典 M3 菜单的尺寸与浮层样式；颜色、排版与图标尺寸继续读取 OxygenTheme。 */
object DropdownMenuDefaults {
    val MaxWidth: Dp = GeneratedTokens.SIZE_MENU_MAX_WIDTH.dp
    val IconGap: Dp = GeneratedTokens.SPACE_MENU_ICON_GAP.dp
    val WindowMargin: Dp = GeneratedTokens.SPACE_MENU_WINDOW_MARGIN.dp
    val ShadowElevation: Dp = GeneratedTokens.ELEVATION_MENU.dp

    val ItemContentPadding: PaddingValues
        @Composable get() = PaddingValues(
            horizontal = OxygenTheme.spacing.menuItemPaddingHorizontal,
        )
}

/**
 * 下拉菜单：贴着锚点下方弹出的浮层，点外部 / 返回键关闭。
 *
 * 用法是把 [DropdownMenu] 放在锚点所在的 `Box` 里——它用 `Popup` 相对**父布局**定位，
 * 所以父 `Box` 就是它对齐的锚点（与 M3 同口径）：
 *
 * ```kotlin
 * Box {
 *     Button(onClick = { expanded = true }) { Text("排序") }
 *     DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
 *         DropdownMenuItem(text = { Text("按名称") }, onClick = { … })
 *     }
 * }
 * ```
 *
 * 经典 M3 小圆角 / 轻投影 / 无描边，长菜单可滚动；宽度按内容限制在 112–280dp。
 * 下方空间不够时翻到锚点上方，支持 RTL 对齐与窗口边缘回退。
 *
 * - Spec ID: `CMP-DROPDOWN-MENU`
 * - Baseline: `Material3 1.5.0-beta01 classic DropdownMenu / framework reference / 2026-11-18`
 * - Reference: `docs/spec/dropdown-m3-plan.md`
 * - Motion: `MOT-ENTER-002` / `MOT-ENTER-003`，锚点缩放与淡入淡出，可中断反向。
 * - Status: `draft`
 *
 * @param expanded 是否展开；由调用方持有。
 * @param onDismissRequest 点外部或返回键时回调，调用方负责把 [expanded] 置 false。
 * @param modifier 外部 Modifier，作用在菜单面板上。
 * @param offset 相对锚点左下角的偏移，默认紧贴锚点下沿。
 * @param content 菜单项槽，按显示顺序放 [DropdownMenuItem]。
 */
@Composable
fun DropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        motion = OxygenMotionDefaults.menuScale(),
        modifier = modifier,
        offset = offset,
        content = content,
    )
}

/** 显式动效重载；旧签名继续使用默认 M3 菜单动效。关闭动画完成后才移除 Popup。 */
@Composable
fun DropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    motion: OxygenMotionSpec,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    fadeMotion: OxygenMotionSpec = OxygenMotionDefaults.menuFade(),
    content: @Composable ColumnScope.() -> Unit,
) {
    val animation = rememberDropdownMenuAnimation(expanded, motion, fadeMotion)
    var origin by remember { mutableStateOf(TransformOrigin.Center) }
    if (!expanded && !animation.present.value) return
    val density = LocalDensity.current
    val windowHeight = with(density) { LocalWindowInfo.current.containerSize.height.toDp() }
    val spacing = OxygenTheme.spacing
    val minVisibleHeight = spacing.menuItemHeight + spacing.menuPaddingVertical * 2
    val maxHeight = if (windowHeight > 0.dp) {
        (windowHeight - DropdownMenuDefaults.WindowMargin * 2)
            .coerceAtLeast(minOf(windowHeight, minVisibleHeight))
    } else {
        Dp.Infinity
    }
    Popup(
        popupPositionProvider = remember(density, offset) {
            DropdownMenuPositionProvider(density, offset) { anchor, menu -> origin = menuTransformOrigin(anchor, menu) }
        },
        onDismissRequest = { if (expanded) onDismissRequest() },
        properties = PopupProperties(focusable = true),
    ) {
        CompositionLocalProvider(LocalMenuInteractive provides expanded) {
            DropdownMenuContent(
                modifier.heightIn(max = maxHeight).graphicsLayer {
                    val progress = animation.scale.value
                    val size = GeneratedTokens.EFFECT_MENU_CLOSED_SCALE + (1f - GeneratedTokens.EFFECT_MENU_CLOSED_SCALE) * progress
                    scaleX = size
                    scaleY = size
                    alpha = animation.fade.value.coerceIn(0f, 1f)
                    transformOrigin = origin
                },
                content,
            )
        }
    }
}

/** Popup 与截图回归共用同一个面板，避免测试另画一份样式。 */
@Composable
internal fun DropdownMenuContent(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val spacing = OxygenTheme.spacing
    val shape = OxygenTheme.shapes.cornerMenu
    Surface(
        modifier = modifier
            .widthIn(min = spacing.menuMinWidth, max = maxOf(spacing.menuMinWidth, DropdownMenuDefaults.MaxWidth))
            .shadow(DropdownMenuDefaults.ShadowElevation, shape, clip = false),
        color = OxygenTheme.colorScheme.surfaceContainer,
        shape = shape,
    ) {
        ProvideContentStyle(
            contentColor = OxygenTheme.colorScheme.onSurface,
            textStyle = OxygenTheme.typography.labelLarge,
        ) {
            Column(
                Modifier.width(IntrinsicSize.Max)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = spacing.menuPaddingVertical),
                content = content,
            )
        }
    }
}

/**
 * 菜单项：整行可点，左侧可放图标，右侧可放勾选 / 快捷键提示。
 *
 * - Spec ID: `CMP-DROPDOWN-MENU`
 * - Baseline: `Material3 1.5.0-beta01 classic DropdownMenu / framework reference / 2026-11-18`
 * - Reference: `docs/spec/dropdown-m3-plan.md`
 * - Motion: 按压只有水波纹（可被 `LocalRippleEnabled` 关闭）。
 * - Status: `draft`
 *
 * @param text 文案槽（已下发 `type.label.medium` 与内容色）。
 * @param onClick 点击回调；`enabled = false` 时不触发。
 * @param modifier 外部 Modifier。
 * @param leadingIcon 左侧图标槽。
 * @param trailingIcon 右侧图标槽。
 * @param enabled 是否可用。
 */
@Composable
fun DropdownMenuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
) {
    DropdownMenuItem(
        text = text,
        onClick = onClick,
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        enabled = enabled,
        contentPadding = DropdownMenuDefaults.ItemContentPadding,
        interactionSource = null,
    )
}

/** 自定义内距与交互源重载；原有参数签名保留。内距在最小触摸高度内，自然支持大字体。 */
@Composable
fun DropdownMenuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    val spacing = OxygenTheme.spacing
    val colors = OxygenTheme.colorScheme
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val interactive = LocalMenuInteractive.current
    val iconColor = if (enabled) colors.onSurfaceVariant else colors.onSurfaceDisabled
    ProvideContentStyle(
        contentColor = if (enabled) colors.onSurface else colors.onSurfaceDisabled,
        textStyle = OxygenTheme.typography.labelLarge,
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .widthIn(min = spacing.menuMinWidth, max = maxOf(spacing.menuMinWidth, DropdownMenuDefaults.MaxWidth))
                .heightIn(min = spacing.menuItemHeight)
                .clickable(
                    interactionSource = source,
                    indication = IndicationDefaults.ripple(),
                    enabled = enabled,
                    role = Role.Button,
                    onClick = { if (interactive) onClick() },
                )
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(DropdownMenuDefaults.IconGap),
        ) {
            if (leadingIcon != null) {
                CompositionLocalProvider(LocalContentColor provides iconColor) {
                    Box(Modifier.widthIn(min = OxygenTheme.icons.medium)) { leadingIcon() }
                }
            }
            CompositionLocalProvider(
                LocalContentColor provides if (enabled) colors.onSurface else colors.onSurfaceDisabled,
            ) {
                Column(modifier = Modifier.weight(1f)) { text() }
            }
            if (trailingIcon != null) {
                CompositionLocalProvider(LocalContentColor provides iconColor) {
                    Box(Modifier.widthIn(min = OxygenTheme.icons.medium)) { trailingIcon() }
                }
            }
        }
    }
}

/**
 * 菜单定位：默认贴锚点下沿，下方放不下就翻到上方，横向夹在窗口内。
 *
 * `anchorBounds` 由 `Popup` 传入（即 [DropdownMenu] 的父布局），所以调用方把菜单
 * 放进锚点所在的 `Box` 即可，不需要额外传坐标。
 */
internal class DropdownMenuPositionProvider(
    private val density: Density,
    private val offset: DpOffset,
    private val onPosition: (IntRect, IntRect) -> Unit = { _, _ -> },
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val dx = with(density) { offset.x.roundToPx() }
        val dy = with(density) { offset.y.roundToPx() }
        val maxX = (windowSize.width - popupContentSize.width).coerceAtLeast(0)
        val xCandidates = if (layoutDirection == LayoutDirection.Ltr) {
            listOf(anchorBounds.left + dx, anchorBounds.right - popupContentSize.width + dx)
        } else {
            listOf(anchorBounds.right - popupContentSize.width - dx, anchorBounds.left - dx)
        }
        val x = xCandidates.firstOrNull { it in 0..maxX } ?: xCandidates.first().coerceIn(0, maxX)
        val remainingHeight = (windowSize.height - popupContentSize.height).coerceAtLeast(0)
        val margin = minOf(with(density) { DropdownMenuDefaults.WindowMargin.roundToPx() }, remainingHeight / 2)
        val maxY = remainingHeight - margin
        val yCandidates = listOf(anchorBounds.bottom + dy, anchorBounds.top - popupContentSize.height + dy)
        val y = yCandidates.firstOrNull { it in margin..maxY } ?: yCandidates.first().coerceIn(margin, maxY)
        val result = IntOffset(x, y)
        onPosition(anchorBounds, IntRect(result, popupContentSize))
        return result
    }
}

/** 以菜单和锚点的交集中心为轴，完全在上下方时分别从近边展开。 */
internal fun menuTransformOrigin(anchor: IntRect, menu: IntRect): TransformOrigin {
    fun pivot(aStart: Int, aEnd: Int, start: Int, end: Int): Float = when {
        start >= aEnd -> 0f
        end <= aStart -> 1f
        end <= start -> 0.5f
        else -> (((maxOf(aStart, start) + minOf(aEnd, end)) / 2f - start) / (end - start)).coerceIn(0f, 1f)
    }
    return TransformOrigin(
        pivot(anchor.left, anchor.right, menu.left, menu.right),
        pivot(anchor.top, anchor.bottom, menu.top, menu.bottom),
    )
}
