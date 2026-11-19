// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.basic

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import io.oxygenx.theme.LocalContentColor
import io.oxygenx.theme.LocalTextStyle
import io.oxygenx.theme.OxygenTheme

/**
 * 芯片：整段半圆的圆形胶囊。未选中为 `color.outline.variant` 细线描边 + `onSurfaceVariant`
 * 文字，选中为 `color.secondary` 实心胶囊 + `onSecondary` 文字。
 *
 * 参数按 M3 Chip 系列（Assist / Filter / Input / Suggestion）的公共契约组织：[selected]
 * 表达筛选态，[leadingIcon] 是领航图标槽（Filter 选中时可换成勾选图标），[trailingIcon]
 * 是尾随操作槽（Input 的移除按钮），[colors] 提供 secondary / primary / tonal 三档配色，
 * [shape] 默认 `shapes.pill`。组件**无状态**：选中、图标显隐、
 * 列表增删都由调用方在 [onClick] 里维护，便于单选组与多选组复用同一个组件。
 *
 * 未选中态的文字不取描边色：`outlineVariant` 是 8% 半透明叠加层，画文字会不可读，故按 M3
 * 复用 `onSurfaceVariant` 保证 4.5:1 对比度；只有边框与顶栏细线同色。
 *
 * - Spec ID: `CMP-CHIP`
 * - Baseline: `OxygenOS <version> / <device> / <date>`（E0 草案，暂无真机参考）
 * - Reference: `catalog/reference/CMP-CHIP/`
 * - Motion: 无。触摸反馈只走水波纹，过冲弹簧等交互动效后续作为新规格引入。
 * - Status: `draft`
 *
 * @param onClick 点击回调；`enabled = false` 时不触发。
 * @param label 标签内容槽：内容色与 `type.label.medium` 排版已经下发，单行省略由调用方
 *   的 `Text(maxLines = 1, overflow = Ellipsis)` 决定。
 * @param modifier 外部 Modifier；点击区撑到 `space.touch.target.min`（48dp 无障碍下限），
 *   视觉高度仍是 `size.chip.height`，波纹由内层胶囊裁剪。
 * @param selected 是否选中；只影响配色，不持有状态。
 * @param enabled 是否可交互；禁用时取禁用容器 / 禁用文字 / 细线边框。
 * @param colors 颜色集合，默认 secondary 系列；primary / tonal 见
 *   [ChipDefaults.primaryColors] / [ChipDefaults.tonalColors]。
 * @param shape 外形，默认 `shapes.pill`（圆角恒为短边一半）。
 * @param border 描边，默认按配色解析（未选中细线、选中 / 长按无描边）；`BorderStroke(0.dp, …)` 可去掉。
 * @param interactionSource 交互状态源，供外部观察按压；`null` 时内部创建。
 * @param leadingIcon 领航图标槽，用 [ChipDefaults.Icon] 可自动跟随当前内容色。
 * @param trailingIcon 尾随图标槽，M3 InputChip 的移除按钮放这里。
 */
@Composable
fun Chip(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    colors: ChipColors = ChipDefaults.colors(),
    shape: RoundedCornerShape = ChipDefaults.Shape,
    border: BorderStroke = ChipDefaults.border(colors.resolveBorderColor(enabled, selected)),
    interactionSource: MutableInteractionSource? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val spacing = OxygenTheme.spacing
    val containerColor = colors.resolveContainerColor(enabled, selected)
    val contentColor = colors.resolveContentColor(enabled, selected)

    CompositionLocalProvider(
        LocalChipContentColor provides contentColor,
        LocalContentColor provides contentColor,
        LocalTextStyle provides LocalTextStyle.current.merge(OxygenTheme.typography.labelLarge),
    ) {
        // 视觉高度是 size.chip.height，触摸目标由外层 Box 撑到 space.touch.target.min。
        // clickable 放外层、波纹通过 indication 放内层，波纹才会被胶囊的 clip 裁住。
        Box(
            modifier = modifier
                .height(spacing.touchTargetMin)
                .clickable(
                    interactionSource = source,
                    indication = null,
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                modifier = Modifier
                    .height(spacing.chipHeight)
                    .clip(shape)
                    .background(containerColor)
                    .border(width = border.width, brush = border.brush, shape = shape)
                    .indication(source, IndicationDefaults.ripple())
                    .padding(
                        start = if (leadingIcon != null) spacing.gapSmall else spacing.cardPadding,
                        end = if (trailingIcon != null) spacing.gapSmall else spacing.cardPadding,
                    ),
                horizontalArrangement = Arrangement.spacedBy(spacing.gapSmall),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (leadingIcon != null) leadingIcon()
                label()
                if (trailingIcon != null) trailingIcon()
            }
        }
    }
}

/** 让 [ChipDefaults.Icon] 能自动跟随 chip 的选中 / 禁用内容色。 */
internal val LocalChipContentColor = compositionLocalOf { Color.Unspecified }

object ChipDefaults {
    /** chip 视觉高度（`size.chip.height`），不是触摸目标高度。 */
    val Height: Dp
        @Composable get() = OxygenTheme.spacing.chipHeight

    /** 未选中描边宽度（`size.chip.border.width`）。 */
    val BorderWidth: Dp
        @Composable get() = OxygenTheme.spacing.chipBorderWidth

    /** 圆形胶囊；按形状语义定义，没有对应的圆角令牌。 */
    val Shape: RoundedCornerShape
        @Composable get() = OxygenTheme.shapes.pill

    /** 默认描边：厚度 `size.chip.border.width`，颜色由配色解析（选中为透明，即无描边）。 */
    @Composable
    fun border(color: Color): BorderStroke {
        val width = OxygenTheme.spacing.chipBorderWidth
        return remember(color, width) { BorderStroke(width, color) }
    }

    /**
     * 领航 / 尾随图标：着色自动取当前 chip 内容色，尺寸取 `icon.size.small`。
     * 在 chip 外调用时回退到 `onSurfaceVariant`。
     */
    @Composable
    fun Icon(imageVector: ImageVector, contentDescription: String? = null) {
        val local = LocalChipContentColor.current
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = if (local == Color.Unspecified) OxygenTheme.colorScheme.onSurfaceVariant else local,
            size = OxygenTheme.icons.small,
        )
    }

    /** primary 系列：选中为一加红 `primary` 胶囊 + `onPrimary` 文字。 */
    @Composable
    fun primaryColors(): ChipColors = colors(
        selectedContainerColor = OxygenTheme.colorScheme.primary,
        selectedContentColor = OxygenTheme.colorScheme.onPrimary,
    )

    /** M3 FilterChip 的 tonal 画法：选中为 `secondaryContainer` + `onSecondaryContainer`。 */
    @Composable
    fun tonalColors(): ChipColors = colors(
        selectedContainerColor = OxygenTheme.colorScheme.secondaryContainer,
        selectedContentColor = OxygenTheme.colorScheme.onSecondaryContainer,
    )

    /**
     * 默认 secondary 系列：未选中透明底 + `outlineVariant` 细线边框 + `onSurfaceVariant`
     * 文字，选中 `secondary` 底 + `onSecondary` 文字。
     */
    @Composable
    fun colors(
        selectedContainerColor: Color = OxygenTheme.colorScheme.secondary,
        selectedContentColor: Color = OxygenTheme.colorScheme.onSecondary,
        unselectedContainerColor: Color = Color.Transparent,
        unselectedContentColor: Color = OxygenTheme.colorScheme.onSurfaceVariant,
        unselectedBorderColor: Color = OxygenTheme.colorScheme.outlineVariant,
        disabledContainerColor: Color = OxygenTheme.colorScheme.surfaceDisabled,
        disabledContentColor: Color = OxygenTheme.colorScheme.onSurfaceDisabled,
        disabledBorderColor: Color = OxygenTheme.colorScheme.outlineVariant,
    ): ChipColors = remember(
        selectedContainerColor,
        selectedContentColor,
        unselectedContainerColor,
        unselectedContentColor,
        unselectedBorderColor,
        disabledContainerColor,
        disabledContentColor,
        disabledBorderColor,
    ) {
        ChipColors(
            selectedContainerColor = selectedContainerColor,
            selectedContentColor = selectedContentColor,
            unselectedContainerColor = unselectedContainerColor,
            unselectedContentColor = unselectedContentColor,
            unselectedBorderColor = unselectedBorderColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
            disabledBorderColor = disabledBorderColor,
        )
    }
}

@Immutable
data class ChipColors(
    val selectedContainerColor: Color,
    val selectedContentColor: Color,
    val unselectedContainerColor: Color,
    val unselectedContentColor: Color,
    val unselectedBorderColor: Color,
    val disabledContainerColor: Color,
    val disabledContentColor: Color,
    val disabledBorderColor: Color,
) {
    @Stable
    internal fun resolveContainerColor(enabled: Boolean, selected: Boolean): Color = when {
        !enabled -> disabledContainerColor
        selected -> selectedContainerColor
        else -> unselectedContainerColor
    }

    @Stable
    internal fun resolveContentColor(enabled: Boolean, selected: Boolean): Color = when {
        !enabled -> disabledContentColor
        selected -> selectedContentColor
        else -> unselectedContentColor
    }

    @Stable
    internal fun resolveBorderColor(enabled: Boolean, selected: Boolean): Color = when {
        !enabled -> disabledBorderColor
        selected -> Color.Transparent
        else -> unselectedBorderColor
    }
}
