// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.icons.icon.OxygenIcons
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Icon
import io.oxygenx.ui.basic.Text

/** CMP-TEXT / CMP-ICON：行内内容、自动字号，以及三类图标资源的默认色 / 原色对照。 */
fun LazyListScope.textIconSection() {
    item(key = "cmp-material-type-scale") {
        SmallTitle("Typography / M3 基础排版", "CMP-TEXT")
        DemoCard {
            val type = OxygenTheme.typography
            val roles = listOf(
                "displayLarge" to type.displayLarge, "displayMedium" to type.displayMedium, "displaySmall" to type.displaySmall,
                "headlineLarge" to type.headlineLarge, "headlineMedium" to type.headlineMedium, "headlineSmall" to type.headlineSmall,
                "titleLarge" to type.titleLarge, "titleMedium" to type.titleMedium, "titleSmall" to type.titleSmall,
                "bodyLarge" to type.bodyLarge, "bodyMedium" to type.bodyMedium, "bodySmall" to type.bodySmall,
                "labelLarge" to type.labelLarge, "labelMedium" to type.labelMedium, "labelSmall" to type.labelSmall,
            )
            Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapLarge)) {
                roles.forEach { (name, style) ->
                    Text("$name · ${style.fontSize.value}/${style.lineHeight.value}", style = type.bodySmall)
                    Text("Aa 书源", style = style)
                }
            }
        }
    }
    item(key = "cmp-icon-contract") {
        SmallTitle("Icon / 内容色与原色", "CMP-ICON")
        DemoCard { IconSources() }
    }
    item(key = "cmp-text-capabilities") {
        SmallTitle("Text / 排版与行内内容", "CMP-TEXT")
        DemoCard { TextCapabilities() }
    }
}

@Composable
private fun IconSources() {
    val colors = OxygenTheme.colorScheme
    val vector = remember(colors.primary, colors.secondary) {
        ImageVector.Builder("OxygenX two-color sample", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(colors.primary)) {
                moveTo(0f, 0f)
                lineTo(12f, 0f)
                lineTo(12f, 24f)
                lineTo(0f, 24f)
                close()
            }
            path(fill = SolidColor(colors.secondary)) {
                moveTo(12f, 0f)
                lineTo(24f, 0f)
                lineTo(24f, 24f)
                lineTo(12f, 24f)
                close()
            }
        }.build()
    }
    val bitmap = remember(colors.primary, colors.secondary) {
        ImageBitmap(24, 24).also {
            val canvas = Canvas(it)
            canvas.drawRect(0f, 0f, 12f, 24f, Paint().apply { color = colors.primary })
            canvas.drawRect(12f, 0f, 24f, 24f, Paint().apply { color = colors.secondary })
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
        Text("默认继承内容色；显式 Unspecified 保留双色。资源为现场绘制，不使用外部素材。")
        Row(horizontalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gutter)) {
            Icon(vector, "默认单色")
            Icon(vector, "矢量原色", tint = Color.Unspecified)
            Icon(bitmap, "位图原色", tint = Color.Unspecified)
            Icon(BitmapPainter(bitmap), "Painter 原色", tint = Color.Unspecified)
        }
        Icon(OxygenIcons.Back, "随 RTL 镜像的方向图标", mirrorInRtl = true)
    }
}

@Composable
private fun TextCapabilities() {
    val typography = OxygenTheme.typography
    val text = remember {
        buildAnnotatedString {
            append("行内 ")
            appendInlineContent("check", "[完成]")
            append(" 图标随文本一起排版")
        }
    }
    val inline = remember {
        mapOf(
            "check" to InlineTextContent(Placeholder(1.em, 1.em, PlaceholderVerticalAlign.Center)) {
                Icon(OxygenIcons.Check, "完成")
            },
        )
    }
    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gutter)) {
        Text(text, inlineContent = inline)
        Text("有限宽度内自动缩小字号", style = typography.caption)
        Text(
            "OxygenX Compose Multiplatform",
            Modifier.fillMaxWidth(),
            style = typography.displayLarge,
            maxLines = 1,
            autoSize = TextAutoSize.StepBased(minFontSize = typography.caption.fontSize, maxFontSize = typography.displayLarge.fontSize),
        )
        Text("FontFamily.Monospace", fontFamily = FontFamily.Monospace, textDecoration = TextDecoration.Underline)
        Text("未提供行内 id 时保留替代文本", modifier = Modifier.fillMaxWidth(), style = typography.caption)
    }
}
