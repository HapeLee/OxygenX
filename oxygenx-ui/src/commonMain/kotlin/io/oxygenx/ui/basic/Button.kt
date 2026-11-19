// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.theme.ProvideContentStyle

/**
 * 填充式按钮：强调色底 + 反色文字；触摸态只有 M3 水波纹，没有按压缩放。
 *
 * 内容走 [content] 槽位（M3 口径的 `RowScope`）：槽位内下发按钮的内容色与
 * `type.label.medium` 排版，因此 `Button(onClick = …) { Text("确定") }` 就是正确外观；
 * 需要「图标 + 文案」或加载态时直接在槽位里排，不必绕开组件自己拼容器。
 *
 * - Spec ID: `CMP-BUTTON`
 * - Baseline: `OxygenOS <version> / <device> / <date>`
 * - Reference: `catalog/reference/CMP-BUTTON/`
 * - Motion: 无。触摸反馈只走水波纹，过冲弹簧等交互动效后续作为新规格引入。
 * - Status: `draft`
 *
 * @param onClick 点击回调；`enabled = false` 时不触发。
 * @param modifier 外部 Modifier。
 * @param enabled 是否可交互；禁用时使用 `color.surface.disabled` / `color.on.surface.disabled`。
 * @param colors 颜色集合，默认取主题强调色；反白实心按钮用 [ButtonDefaults.secondaryColors]。
 * @param shape 按钮外形，默认 `shape.corner.button`；整段半圆的药丸形用 [ButtonDefaults.PillShape]。
 * @param contentPadding 内容内边距，默认左右 [ButtonDefaults.HorizontalPadding]；纯图标按钮传 `PaddingValues()`。
 * @param interactionSource 按压态来源（M3 口径），可在外部观察 / 串联；不传则内部自建。
 * @param content 按钮内容，水平排列、整体居中；内容色与排版已经由按钮下发。高度是下限
 *   （[ButtonDefaults.Height]）而非固定值，换行或两行内容不会被裁。
 */
@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.colors(),
    shape: RoundedCornerShape = ButtonDefaults.Shape,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    // M3 口径：外部可观察按压态；不传则内部自己 remember 一个。
    val source = interactionSource ?: remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            // 下限而非固定高度：槽位里换行或放两行内容时高度跟着长，不会被 56dp 裁掉。
            .heightIn(min = ButtonDefaults.Height)
            .defaultMinSize(minWidth = ButtonDefaults.MinWidth)
            .clip(shape)
            .background(colors.resolveContainerColor(enabled))
            .clickable(
                interactionSource = source,
                indication = IndicationDefaults.ripple(),
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(contentPadding),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProvideContentStyle(
            contentColor = colors.resolveContentColor(enabled),
            textStyle = OxygenTheme.typography.labelLarge,
        ) {
            content()
        }
    }
}

object ButtonDefaults {
    val Height: Dp
        @Composable get() = OxygenTheme.spacing.buttonHeight

    val MinWidth: Dp
        @Composable get() = OxygenTheme.spacing.controlMinWidth

    val HorizontalPadding: Dp
        @Composable get() = OxygenTheme.spacing.buttonHorizontalPadding

    /** 默认内容内边距：左右各 [HorizontalPadding]，上下为 0（高度由 [Height] 下限撑开）。 */
    val ContentPadding: PaddingValues
        @Composable get() = PaddingValues(horizontal = HorizontalPadding)

    val Shape: RoundedCornerShape
        @Composable get() = OxygenTheme.shapes.cornerButton

    /** 整段半圆的药丸外形：圆角恒定等于按钮高度的一半，与高度无关地保持「全圆端」。 */
    val PillShape: RoundedCornerShape
        @Composable get() = OxygenTheme.shapes.pill

    /** secondary 实心按钮：底色取 `color.secondary`（墨色），文字取 `color.on.secondary`。 */
    @Composable
    fun secondaryColors(
        containerColor: Color = OxygenTheme.colorScheme.secondary,
        contentColor: Color = OxygenTheme.colorScheme.onSecondary,
        disabledContainerColor: Color = OxygenTheme.colorScheme.surfaceDisabled,
        disabledContentColor: Color = OxygenTheme.colorScheme.onSurfaceDisabled,
    ): ButtonColors = colors(
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
    ): ButtonColors = remember(
        containerColor,
        contentColor,
        disabledContainerColor,
        disabledContentColor,
    ) {
        ButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        )
    }
}

@Immutable
data class ButtonColors(
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
