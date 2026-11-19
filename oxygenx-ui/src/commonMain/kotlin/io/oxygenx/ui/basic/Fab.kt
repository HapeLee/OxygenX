// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import io.oxygenx.theme.OxygenTheme

/**
 * 悬浮操作按钮（FAB）：`56dp` 正圆，图标居中，按压只有圆形水波纹。
 *
 * 与 M3 的差异只有两处，都来自 OxygenOS 的形态：外形取**正圆**（`shapes.pill` 在正方形上
 * 就是圆，M3 默认是 16dp 圆角方形），并且没有阴影（本项目所有容器都是平面，层级靠颜色）。
 * 尺寸分档照 M3：小号 `40dp` / 常规 `56dp` / 大号 `96dp`，扩展型是「图标 + 文案」的药丸。
 * 放在页面右下角时用 `Scaffold` 的 `floatingActionButton` 槽，位置与安全区由它负责。
 *
 * - Spec ID: `CMP-FAB`
 * - Baseline: `OnePlus 9R (LE2100) / ColorOS 11 / Clock 5.3.0 / 2026-11-13`（`size.fab` 56dp）
 * - Reference: `catalog/reference/README.md`
 * - Motion: 无。触摸反馈只走水波纹。
 * - Status: `draft`
 *
 * @param onClick 点击回调；`enabled = false` 时不触发。
 * @param icon 图标，视觉尺寸 `icon.size.medium`（24dp）。
 * @param contentDescription 图标的无障碍描述；纯图标按钮必须给。
 * @param modifier 外部 Modifier。
 * @param enabled 是否可交互；禁用时用 `color.surface.disabled` / `color.on.surface.disabled`。
 * @param colors 颜色集合，默认取强调色；浅一档的 tonal 底用 [FabDefaults.tonalColors]。
 * @param shape 外形，默认 [FabDefaults.Shape]（正方形上即正圆）。
 * @param interactionSource 按压态来源（M3 口径）；不传则内部自建。
 */
@Composable
fun FloatingActionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: FabColors = FabDefaults.colors(),
    shape: RoundedCornerShape = FabDefaults.Shape,
    interactionSource: MutableInteractionSource? = null,
) {
    FabSurface(
        onClick = onClick,
        enabled = enabled,
        colors = colors,
        shape = shape,
        size = FabDefaults.Size,
        modifier = modifier,
        interactionSource = interactionSource,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = colors.resolveContentColor(enabled),
        )
    }
}

/** 小号 FAB：`40dp` 正圆，图标同为 24dp（M3 small FAB）。参数含义同 [FloatingActionButton]。 */
@Composable
fun SmallFloatingActionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: FabColors = FabDefaults.colors(),
    shape: RoundedCornerShape = FabDefaults.Shape,
    interactionSource: MutableInteractionSource? = null,
) {
    FabSurface(
        onClick = onClick,
        enabled = enabled,
        colors = colors,
        shape = shape,
        size = FabDefaults.SmallSize,
        modifier = modifier,
        interactionSource = interactionSource,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = colors.resolveContentColor(enabled),
        )
    }
}

/** 大号 FAB：`96dp` 正圆，图标放大到 `icon.size.large`（36dp，M3 large FAB）。参数含义同上。 */
@Composable
fun LargeFloatingActionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: FabColors = FabDefaults.colors(),
    shape: RoundedCornerShape = FabDefaults.Shape,
    interactionSource: MutableInteractionSource? = null,
) {
    FabSurface(
        onClick = onClick,
        enabled = enabled,
        colors = colors,
        shape = shape,
        size = FabDefaults.LargeSize,
        modifier = modifier,
        interactionSource = interactionSource,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = colors.resolveContentColor(enabled),
            size = FabDefaults.LargeIconSize,
        )
    }
}

/**
 * 扩展型 FAB：图标 + 文案，高度与常规 FAB 相同（56dp），左右各 16dp 内边距，外形是药丸。
 *
 * M3 的扩展型在收起 / 展开时有宽度动画；这里不做动画——按钮是否带文案由调用方在组合层
 * 决定，避免在按钮内部维护「展开状态」这种额外状态。
 *
 * @param text 文案，单行省略，样式 `type.label.medium`（M3 用 label large，此处沿用本项目的标签档）。
 */
@Composable
fun ExtendedFloatingActionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String? = null,
    colors: FabColors = FabDefaults.colors(),
    shape: RoundedCornerShape = FabDefaults.Shape,
    interactionSource: MutableInteractionSource? = null,
) {
    // M3 口径：外部可观察按压态；不传则内部自己 remember 一个。
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val spacing = OxygenTheme.spacing
    Row(
        modifier = modifier
            .height(FabDefaults.Size)
            .clip(shape)
            .background(colors.resolveContainerColor(enabled))
            .clickable(
                interactionSource = source,
                indication = IndicationDefaults.ripple(),
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = spacing.gutter),
        horizontalArrangement = Arrangement.spacedBy(spacing.gapSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = colors.resolveContentColor(enabled),
        )
        BasicText(
            text = text,
            style = OxygenTheme.typography.labelLarge.copy(
                color = colors.resolveContentColor(enabled),
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** 三种尺寸的圆形 FAB 共用的外壳：定尺寸、裁形状、铺底色、挂水波纹。 */
@Composable
private fun FabSurface(
    onClick: () -> Unit,
    enabled: Boolean,
    colors: FabColors,
    shape: RoundedCornerShape,
    size: Dp,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(colors.resolveContainerColor(enabled))
            // 水波纹被上面的 clip 裁成圆：FAB 的按压反馈是圆内扩散，不会溢到圆外。
            .clickable(
                interactionSource = source,
                indication = IndicationDefaults.ripple(),
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

object FabDefaults {
    val Size: Dp
        @Composable get() = OxygenTheme.spacing.fabSize

    val SmallSize: Dp
        @Composable get() = OxygenTheme.spacing.fabSmall

    val LargeSize: Dp
        @Composable get() = OxygenTheme.spacing.fabLarge

    val LargeIconSize: Dp
        @Composable get() = OxygenTheme.icons.large

    /** 正圆：FAB 是正方形，`pill`（圆角 = 短边一半）落在正方形上就是一个圆。 */
    val Shape: RoundedCornerShape
        @Composable get() = OxygenTheme.shapes.pill

    /** 浅一档的 tonal 底（M3 的 primaryContainer 在 OxygenOS 调色板里没有对应角色，借用 secondary 容器色）。 */
    @Composable
    fun tonalColors(
        containerColor: Color = OxygenTheme.colorScheme.secondaryContainer,
        contentColor: Color = OxygenTheme.colorScheme.onSecondaryContainer,
        disabledContainerColor: Color = OxygenTheme.colorScheme.surfaceDisabled,
        disabledContentColor: Color = OxygenTheme.colorScheme.onSurfaceDisabled,
    ): FabColors = colors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
    )

    @Composable
    fun colors(
        containerColor: Color = OxygenTheme.colorScheme.primary,
        contentColor: Color = OxygenTheme.colorScheme.onPrimary,
        disabledContainerColor: Color = OxygenTheme.colorScheme.surfaceDisabled,
        disabledContentColor: Color = OxygenTheme.colorScheme.onSurfaceDisabled,
    ): FabColors = remember(
        containerColor,
        contentColor,
        disabledContainerColor,
        disabledContentColor,
    ) {
        FabColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        )
    }
}

@Immutable
data class FabColors(
    val containerColor: Color,
    val contentColor: Color,
    val disabledContainerColor: Color,
    val disabledContentColor: Color,
) {
    @Stable
    internal fun resolveContainerColor(enabled: Boolean): Color = if (enabled) containerColor else disabledContainerColor

    @Stable
    internal fun resolveContentColor(enabled: Boolean): Color = if (enabled) contentColor else disabledContentColor
}
