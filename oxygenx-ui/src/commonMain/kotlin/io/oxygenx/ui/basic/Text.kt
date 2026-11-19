// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.basic

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import io.oxygenx.theme.OxygenTheme

/**
 * 文本：本项目的排版入口，等价于 M3 的 `Text`。
 *
 * 排版与颜色都支持继承，与 M3 同口径：
 * - 颜色解析顺序：显式 [color] → [style] 中的颜色 → 当前内容色 → `colorScheme.onSurface`。
 * - 样式解析顺序：显式 [style] → 当前文本样式（`OxygenTheme.textStyle`，即容器用 `ProvideTextStyle`
 *   下发的档位）→ 主题默认 `type.body.medium`。因此按钮 / 顶栏这类组件的内容槽位里写
 *   `Text("确定")` 就会自动是 `label.medium` / `appbar.title`，不需要逐处传 `style`。
 * - [fontSize] / [fontWeight] / [fontStyle] / [letterSpacing] / [lineHeight] 用于只改一个维度的场合，
 *   传 `null` 表示沿用样式里的值。
 *
 * - Spec ID: `CMP-TEXT`
 * - Baseline: `OxygenOS <version> / <device> / <date>`
 * - Reference: `catalog/reference/CMP-TEXT/`
 * - Motion: 无
 * - Status: `draft`
 *
 * @param text 文本内容。
 * @param modifier 外部 Modifier。
 * @param style 排版样式，默认取当前文本样式（主题内是 `type.body.medium`）。
 * @param color 显式颜色；`Color.Unspecified` 时先取 [style] 颜色，再取内容色。
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
 * @param autoSize 自动字号策略；null 保留固定字号，非 null 时由 Foundation 在约束内测量。
 * @param fontFamily 字体覆盖；null 沿用 style。
 * @param textDecoration 装饰覆盖；null 沿用 style。
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
    autoSize: TextAutoSize? = null,
    fontFamily: FontFamily? = null,
    textDecoration: TextDecoration? = null,
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
        autoSize = autoSize,
        fontFamily = fontFamily,
        textDecoration = textDecoration,
        onTextLayout = onTextLayout,
    )
}

/** 富文本版本；[inlineContent] 交给 Foundation 放置占位组件，未提供的 id 保留替代文本。 */
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
    autoSize: TextAutoSize? = null,
    fontFamily: FontFamily? = null,
    textDecoration: TextDecoration? = null,
    inlineContent: Map<String, InlineTextContent> = emptyMap(),
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) {
    val resolved = style.copy(
        color = resolveContentColor(color, style.color),
        textAlign = textAlign ?: style.textAlign,
        fontSize = fontSize ?: style.fontSize,
        fontWeight = fontWeight ?: style.fontWeight,
        fontStyle = fontStyle ?: style.fontStyle,
        letterSpacing = letterSpacing ?: style.letterSpacing,
        lineHeight = lineHeight ?: style.lineHeight,
        fontFamily = fontFamily ?: style.fontFamily,
        textDecoration = textDecoration ?: style.textDecoration,
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
        autoSize = autoSize,
        inlineContent = inlineContent,
    )
}

/**
 * 内容色解析：显式色 → 样式色 → `LocalContentColor` → `onSurface`。
 *
 * 图标的 Unspecified 表示不染色，与文本的未指定颜色含义不同。
 */
@Composable
internal fun resolveContentColor(explicit: Color, styleColor: Color = Color.Unspecified): Color {
    if (explicit.isSpecified) return explicit
    if (styleColor.isSpecified) return styleColor
    val content = OxygenTheme.contentColor
    return if (content.isSpecified) content else OxygenTheme.colorScheme.onSurface
}
