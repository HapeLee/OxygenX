// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.basic

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import io.oxygenx.theme.OxygenTheme

/**
 * 文本：本项目的排版入口，等价于 M3 的 `Text`。
 *
 * 排版与颜色都支持继承，与 M3 同口径：
 * - 颜色解析顺序：显式 [color] → 当前内容色（由 `Surface` / `Card` / `Scaffold` 等容器下发）→ `colorScheme.onSurface`。
 * - 样式解析顺序：显式 [style] → 当前文本样式（`OxygenTheme.textStyle`，即容器用 `ProvideTextStyle`
 *   下发的档位）→ 主题默认 `type.body.medium`。因此按钮 / 顶栏这类组件的内容槽位里写
 *   `Text("确定")` 就会自动是 `label.medium` / `appbar.title`，不需要逐处传 `style`。
 * - [fontSize] / [fontWeight] / [fontStyle] / [letterSpacing] / [lineHeight] 用于只改一个维度的场合，
 *   传 `null` 表示沿用样式里的值。
 *
 * - Spec ID: `CMP-TEXT`
 * - Baseline: `OxygenOS <version> / <device> / <date>`
 * - Reference: `catalog/reference/CMP-TEXT/`
 * - Status: `draft`
 *
 * @param text 文本内容。
 * @param modifier 外部 Modifier。
 * @param style 排版样式，默认取当前文本样式（主题内是 `type.body.medium`）。
 * @param color 显式颜色；`Color.Unspecified` 时按内容色解析。
 * @param fontSize 字号覆盖；`null` 沿用 [style]。
 * @param fontWeight 字重覆盖；`null` 沿用 [style]。
 * @param fontStyle 字形倾斜覆盖；`null` 沿用 [style]。
 * @param letterSpacing 字距覆盖；`null` 沿用 [style]。
 * @param lineHeight 行高覆盖；`null` 沿用 [style]。
 * @param maxLines 最大行数。
 * @param minLines 最小行数（默认 1）。
 * @param overflow 溢出策略，默认直接裁切。
 * @param softWrap 是否软换行。
 * @param textAlign 水平对齐；`null` 沿用 [style] 的设置。
 * @param onTextLayout 布局结果回调，用于测量行数（例如「展开 / 收起」判定）。
 */
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = OxygenTheme.textStyle,
    color: Color = Color.Unspecified,
    fontSize: TextUnit? = null,
    fontWeight: FontWeight? = null,
    fontStyle: FontStyle? = null,
    letterSpacing: TextUnit? = null,
    lineHeight: TextUnit? = null,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    textAlign: TextAlign? = null,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) {
    Text(
        text = AnnotatedString(text),
        modifier = modifier,
        style = style,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        fontStyle = fontStyle,
        letterSpacing = letterSpacing,
        lineHeight = lineHeight,
        maxLines = maxLines,
        minLines = minLines,
        overflow = overflow,
        softWrap = softWrap,
        textAlign = textAlign,
        onTextLayout = onTextLayout,
    )
}

/** 富文本版本，参数含义同 [Text]。 */
@Composable
fun Text(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = OxygenTheme.textStyle,
    color: Color = Color.Unspecified,
    fontSize: TextUnit? = null,
    fontWeight: FontWeight? = null,
    fontStyle: FontStyle? = null,
    letterSpacing: TextUnit? = null,
    lineHeight: TextUnit? = null,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    textAlign: TextAlign? = null,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) {
    val resolved = style.copy(
        color = resolveContentColor(color),
        textAlign = textAlign ?: style.textAlign,
        fontSize = fontSize ?: style.fontSize,
        fontWeight = fontWeight ?: style.fontWeight,
        fontStyle = fontStyle ?: style.fontStyle,
        letterSpacing = letterSpacing ?: style.letterSpacing,
        lineHeight = lineHeight ?: style.lineHeight,
    )
    BasicText(
        text = text,
        modifier = modifier,
        style = resolved,
        maxLines = maxLines,
        minLines = minLines,
        overflow = overflow,
        softWrap = softWrap,
        onTextLayout = onTextLayout,
    )
}

/**
 * 内容色解析：显式色 → `LocalContentColor` → `onSurface`。
 *
 * `Icon` 与 `ChipDefaults.Icon` 也复用这一套顺序，避免各组件各写一份判断。
 */
@Composable
internal fun resolveContentColor(explicit: Color): Color {
    if (explicit.isSpecified) return explicit
    val content = OxygenTheme.contentColor
    return if (content.isSpecified) content else OxygenTheme.colorScheme.onSurface
}
