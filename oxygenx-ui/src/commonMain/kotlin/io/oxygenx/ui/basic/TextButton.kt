// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.basic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.theme.ProvideContentStyle

/**
 * 文字按钮：无容器、48dp 高、文字居中，用强调色。
 *
 * 用于弹窗的「确定 / 取消」——一加这两个按钮是通栏、等高、垂直排列的纯文字按钮，
 * 文案取 `type.dialog.action`（真机「确定」墨迹 76×36px、实心像素 907 个 @450dpi，
 * 16sp/500 只有 85×40px / 472 个，折合 14sp/700）。按压层是方角通栏矩形，圆角交给
 * 宿主卡片裁，因此中间行按下时不会出现胶囊形高亮。
 *
 * 内容走 [content] 槽位：槽位内已下发按钮文字色与 `type.dialog.action`，写
 * `TextButton(onClick = { dismiss() }) { Text("确定") }` 即可。
 *
 * - Spec ID: `CMP-TEXTBUTTON`
 * - Baseline: `OnePlus 9R (LE2100) / ColorOS 11 / Settings 11 / 2026-11-14`
 * - Reference: `catalog/reference/CMP-TEXTBUTTON/`
 * - Status: `draft`
 *
 * @param onClick 点击回调；`enabled = false` 时不触发。
 * @param modifier 外部 Modifier。
 * @param enabled 是否可交互。
 * @param colors 文字颜色集合。
 * @param interactionSource 按压态来源（M3 口径）；不传则内部自建。
 * @param content 按钮内容，整体居中；内容色与排版已经由按钮下发。
 */
@Composable
fun TextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: TextButtonColors = TextButtonDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    // M3 口径：外部可观察按压态；不传则内部自己 remember 一个。
    val source = interactionSource ?: remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(TextButtonDefaults.Height)
            .clip(TextButtonDefaults.Shape)
            .clickable(
                interactionSource = source,
                indication = IndicationDefaults.ripple(),
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        ProvideContentStyle(
            contentColor = colors.resolveContentColor(enabled),
            textStyle = OxygenTheme.typography.labelLarge,
        ) {
            content()
        }
    }
}

object TextButtonDefaults {
    /** 按钮高度（与真机弹窗按钮 135px @450dpi = 48dp 一致）。 */
    val Height: Dp
        @Composable get() = OxygenTheme.spacing.buttonHeight

    /**
     * 按压层的裁剪形状：真机「确定」按下时是一条**方角**通栏矩形（录屏抽帧实测
     * x=23..1056、y=2039..2175 @450dpi），圆角只由宿主卡片在底边自己裁。这里不再
     * 套 `shape.corner.card`，否则中间行的按压层会变成胶囊状。
     */
    val Shape: Shape
        get() = RectangleShape

    @Composable
    fun colors(
        contentColor: Color = OxygenTheme.colorScheme.primary,
        disabledContentColor: Color = OxygenTheme.colorScheme.onSurfaceDisabled,
    ): TextButtonColors = remember(contentColor, disabledContentColor) {
        TextButtonColors(
            contentColor = contentColor,
            disabledContentColor = disabledContentColor,
        )
    }
}

@Immutable
data class TextButtonColors(
    val contentColor: Color,
    val disabledContentColor: Color,
) {
    @Stable
    internal fun resolveContentColor(enabled: Boolean): Color = if (enabled) contentColor else disabledContentColor
}
