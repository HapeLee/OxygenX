// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.oxygenx.motion.OxygenMotionDefaults
import io.oxygenx.motion.OxygenMotionSpec
import io.oxygenx.motion.rememberOxygenAnimation
import io.oxygenx.theme.LocalContentColor
import io.oxygenx.theme.OxygenTheme

/**
 * 开关：轨道 + 拇指，拇指位移走 `spec.switch.toggle`；按压反馈是以拇指为圆心的
 * 圆形波纹（可溢出轨道），不用铺满触摸盒的水波纹。
 *
 * [onCheckedChange] 传 `null` 即「纯展示开关」：只画轨道与拇指，不接收交互、不进入语义树。
 * 用于外层已经承担点击的场合（`SettingsSwitchItem` 的整行开关、`SettingsSplitItem` 的
 * 分割线右侧单元格），避免出现两个可点击的开关节点；此时可用 [pressed] 跟随外层的按压态。
 *
 * - Spec ID: `CMP-SWITCH`
 * - Baseline: `OnePlus 9R (LE2100) / ColorOS 11 / Settings / 2026-11-13`
 * - Reference: `catalog/reference/CMP-SWITCH/`
 * - Motion: `MOT-STATE-002`（拇指位移与波纹扩散），可通过 [motion] 覆盖。
 * - Status: `draft`
 *
 * @param checked 当前是否开启；受控状态由调用方持有。
 * @param onCheckedChange 请求切换时回调；`null` 表示纯展示（不接收点击、不进语义树）。
 * @param modifier 外部 Modifier；纯展示时只作用于视觉本身，不再撑 48dp 触摸盒。
 * @param enabled 是否可交互，禁用态仍保留可读语义。
 * @param pressed 纯展示时的外层按压态；交互态下忽略（内部自采）。
 * @param colors 轨道、拇指与波纹颜色集合。
 * @param motion 拇指位移与波纹扩散共用的动效规格。
 * @param thumbContent 拇指内容槽（M3 `thumbContent`）：如勾选 / 关闭图标，内容色已按开关态下发。
 */
@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    pressed: Boolean = false,
    colors: SwitchColors = SwitchDefaults.colors(),
    motion: OxygenMotionSpec = SwitchDefaults.Motion,
    thumbContent: (@Composable () -> Unit)? = null,
) {
    val change = onCheckedChange
    if (change == null) {
        SwitchVisual(
            checked = checked,
            enabled = enabled,
            pressed = pressed && enabled,
            colors = colors,
            motion = motion,
            modifier = modifier,
            thumbContent = thumbContent,
        )
        return
    }

    val interactionSource = remember { MutableInteractionSource() }
    val innerPressed by interactionSource.collectIsPressedAsState()
    Box(
        modifier = modifier
            // 轨道按 34dp 复刻，触摸目标仍撑到无障碍下限。
            .widthIn(min = OxygenTheme.spacing.touchTargetMin)
            .height(OxygenTheme.spacing.touchTargetMin)
            .toggleable(
                value = checked,
                interactionSource = interactionSource,
                // 波纹在拇指处自绘；48dp 触摸盒上的水波纹会铺满整块区域。
                indication = null,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = change,
            ),
        contentAlignment = Alignment.Center,
    ) {
        SwitchVisual(
            checked = checked,
            enabled = enabled,
            pressed = innerPressed,
            colors = colors,
            motion = motion,
            thumbContent = thumbContent,
        )
    }
}

/**
 * 轨道、拇指与按压波纹的实际绘制。
 *
 * 容器是「轨道宽 × max(轨道高, 波纹直径)」的画布：波纹比轨道高，必须靠容器留出
 * 空间，否则会被父级 tighten 的约束压扁；层属性统一写在 `graphicsLayer` 最外层，
 * 这样 `clip` / `background` 都在图层内，位移才真的作用于可见像素。
 */
@Composable
private fun SwitchVisual(
    checked: Boolean,
    enabled: Boolean,
    pressed: Boolean,
    colors: SwitchColors,
    motion: OxygenMotionSpec,
    modifier: Modifier = Modifier,
    thumbContent: (@Composable () -> Unit)? = null,
) {
    val trackWidth = SwitchDefaults.TrackWidth
    val trackHeight = SwitchDefaults.TrackHeight
    val thumbSize = SwitchDefaults.ThumbSize
    val rippleRadius = SwitchDefaults.RippleRadius

    val thumb = rememberOxygenAnimation(spec = motion, initialValue = if (checked) 1f else 0f)
    LaunchedEffect(checked, thumb) { thumb.animateTo(if (checked) 1f else 0f) }

    val ripple = rememberOxygenAnimation(spec = motion)
    LaunchedEffect(pressed, ripple) { ripple.animateTo(if (pressed) 1f else 0f) }

    val travel = switchThumbTravel(trackWidth, trackHeight, thumbSize)
    val rippleDiameter = rippleRadius * 2f
    val visualWidth = if (rippleDiameter > trackWidth) rippleDiameter else trackWidth
    val visualHeight = if (rippleDiameter > trackHeight) rippleDiameter else trackHeight
    val density = LocalDensity.current
    // 拇指中心相对轨道中心的静态偏移，加上随进度变化的行程即为实际位移。
    val thumbOriginPx = with(density) {
        switchThumbCenterOffset(trackWidth, trackHeight, thumbSize, progress = 0f).toPx()
    }
    val travelPx = with(density) { travel.toPx() }
    val rippleStartScale = thumbSize / rippleDiameter
    // 轨道与拇指颜色随位移进度插值：颜色在绘制阶段读动画值，只重绘不重组。
    val trackOffColor = colors.resolveTrackColor(checked = false, enabled = enabled)
    val trackOnColor = colors.resolveTrackColor(checked = true, enabled = enabled)
    val thumbOffColor = colors.resolveThumbColor(checked = false, enabled = enabled)
    val thumbOnColor = colors.resolveThumbColor(checked = true, enabled = enabled)

    Box(
        modifier = modifier.size(width = visualWidth, height = visualHeight),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(width = trackWidth, height = trackHeight)
                .clip(SwitchDefaults.Shape)
                .drawBehind {
                    drawRect(lerp(trackOffColor, trackOnColor, thumb.value))
                },
        )
        Box(
            modifier = Modifier
                .size(rippleDiameter)
                .graphicsLayer {
                    translationX = thumbOriginPx + thumb.value * travelPx
                    val scale = rippleStartScale + (1f - rippleStartScale) * ripple.value
                    scaleX = scale
                    scaleY = scale
                    // 波纹色自带按压强度 alpha，图层 alpha 只负责按下 / 抬起的淡入淡出。
                    alpha = ripple.value
                }
                .clip(CircleShape)
                .background(colors.resolveRippleColor(enabled)),
        )
        Box(
            modifier = Modifier
                .size(thumbSize)
                .graphicsLayer { translationX = thumbOriginPx + thumb.value * travelPx }
                .clip(CircleShape)
                .drawBehind {
                    drawCircle(lerp(thumbOffColor, thumbOnColor, thumb.value))
                },
        )
        if (thumbContent != null) {
            // 拇指内容按开关态取内容色：开是 onPrimary（拇指强调色底），关是 onSurfaceVariant。
            val thumbContentColor = when {
                !enabled -> OxygenTheme.colorScheme.onSurfaceDisabled
                checked -> OxygenTheme.colorScheme.onPrimary
                else -> OxygenTheme.colorScheme.onSurfaceVariant
            }
            Box(
                modifier = Modifier
                    .size(thumbSize)
                    .graphicsLayer { translationX = thumbOriginPx + thumb.value * travelPx }
                    .clip(CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                CompositionLocalProvider(LocalContentColor provides thumbContentColor) {
                    thumbContent()
                }
            }
        }
    }
}

/**
 * 拇指行程：轨道内可移动的水平距离，扣掉拇指与轨道之间的两侧内边距。
 * 纯函数，便于对 34/17/17 与 34/17/13 这类组合做回归。
 */
internal fun switchThumbTravel(trackWidth: Dp, trackHeight: Dp, thumbSize: Dp): Dp {
    val inset = (trackHeight - thumbSize) / 2f
    val insetTotal = if (inset > 0.dp) inset * 2f else 0.dp
    val travel = trackWidth - thumbSize - insetTotal
    return if (travel > 0.dp) travel else 0.dp
}

/**
 * 拇指圆心相对轨道中心的水平偏移：`progress = 0` 贴左、`progress = 1` 贴右。
 * 波纹圆心与拇指圆心共用这个值。
 */
internal fun switchThumbCenterOffset(
    trackWidth: Dp,
    trackHeight: Dp,
    thumbSize: Dp,
    progress: Float,
): Dp {
    val inset = if (trackHeight > thumbSize) (trackHeight - thumbSize) / 2f else 0.dp
    val travel = switchThumbTravel(trackWidth, trackHeight, thumbSize)
    val clamped = progress.coerceIn(0f, 1f)
    return thumbSize / 2f - trackWidth / 2f + inset + travel * clamped
}

object SwitchDefaults {
    val TrackWidth: Dp
        @Composable get() = OxygenTheme.spacing.switchTrackWidth

    val TrackHeight: Dp
        @Composable get() = OxygenTheme.spacing.switchTrackHeight

    val ThumbSize: Dp
        @Composable get() = OxygenTheme.spacing.switchThumbSize

    /** 按压波纹半径：真机为拇指直径，圆心是拇指中心。 */
    val RippleRadius: Dp
        @Composable get() = OxygenTheme.spacing.switchRippleRadius

    val Shape: RoundedCornerShape
        @Composable get() = OxygenTheme.shapes.pill

    val Motion: OxygenMotionSpec
        @Composable get() = OxygenMotionDefaults.switchToggle()

    @Composable
    fun colors(
        // 一加开关与 M3 默认相反：开启是浅强调色轨道 + 实心强调色拇指，关闭是中性灰轨道 + 灰拇指。
        // on 态两色由 primary 派生（见 ColorScheme.kt），动态取色时开关会跟随主色。
        trackOn: Color = OxygenTheme.colorScheme.switchTrackOn,
        trackOff: Color = OxygenTheme.colorScheme.surfaceContainerHighest,
        thumbOn: Color = OxygenTheme.colorScheme.primary,
        thumbOff: Color = OxygenTheme.colorScheme.switchThumbOff,
        ripple: Color = OxygenTheme.colorScheme.statePressed,
        trackDisabled: Color = OxygenTheme.colorScheme.switchTrackDisabled,
        thumbDisabled: Color = OxygenTheme.colorScheme.switchThumbDisabled,
    ): SwitchColors = remember(
        trackOn,
        trackOff,
        thumbOn,
        thumbOff,
        ripple,
        trackDisabled,
        thumbDisabled,
    ) {
        SwitchColors(
            trackOn = trackOn,
            trackOff = trackOff,
            thumbOn = thumbOn,
            thumbOff = thumbOff,
            ripple = ripple,
            trackDisabled = trackDisabled,
            thumbDisabled = thumbDisabled,
        )
    }
}

@Immutable
data class SwitchColors(
    val trackOn: Color,
    val trackOff: Color,
    val thumbOn: Color,
    val thumbOff: Color,
    val ripple: Color,
    val trackDisabled: Color,
    val thumbDisabled: Color,
) {
    @Stable
    internal fun resolveTrackColor(checked: Boolean, enabled: Boolean): Color = when {
        !enabled -> trackDisabled
        checked -> trackOn
        else -> trackOff
    }

    @Stable
    internal fun resolveThumbColor(checked: Boolean, enabled: Boolean): Color = when {
        !enabled -> thumbDisabled
        checked -> thumbOn
        else -> thumbOff
    }

    @Stable
    internal fun resolveRippleColor(enabled: Boolean): Color = if (enabled) ripple else Color.Transparent
}
