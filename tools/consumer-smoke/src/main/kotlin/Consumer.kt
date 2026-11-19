// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.consumer

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import io.oxygenx.motion.OxygenMotionDefaults
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.theme.OxygenTypography
import io.oxygenx.ui.basic.Button
import io.oxygenx.ui.basic.Icon
import io.oxygenx.ui.basic.Slider
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.basic.TextField
import io.oxygenx.ui.overlay.DropdownMenu
import io.oxygenx.ui.overlay.DropdownMenuItem

/** 只依赖已发布 BOM + UI 坐标，避免仓库内 project 依赖掩盖元数据问题。 */
@Composable
fun Consumer() {
    val typography = OxygenTypography(titleSmall = OxygenTypography().titleSmall.copy(fontFamily = FontFamily.Monospace))
    OxygenTheme(darkTheme = true, typography = typography) {
        Text("Page title", style = typography.displayMedium)
        Text("Bar title", style = typography.titleLarge)
        Box {
            DropdownMenu(false, {}) { DropdownMenuItem(text = { Text("Old API") }, onClick = {}) }
            DropdownMenu(false, {}, motion = OxygenMotionDefaults.menuScale()) {
                DropdownMenuItem(text = { Text("Animated API") }, onClick = {})
            }
        }
        Button(onClick = {}) { Text("Save") }
        DropdownMenuItem(text = { Text("Menu") }, onClick = {})
        DropdownMenuItem(
            text = { Text("Custom menu") }, onClick = {},
            contentPadding = PaddingValues(horizontal = 20.dp),
            interactionSource = remember { MutableInteractionSource() },
        )
        Slider(value = 0.5f, onValueChange = {}, onValueChangeFinished = {})
        TextField(value = "", onValueChange = {})
        TextField(
            state = rememberTextFieldState("123"),
            inputTransformation = InputTransformation { if (length > 5) revertAllChanges() },
            outputTransformation = OutputTransformation { append(" units") },
            lineLimits = TextFieldLineLimits.SingleLine,
            onKeyboardAction = { performDefaultAction -> performDefaultAction() },
            onTextLayout = { getResult -> getResult() },
        )
        val bitmap = ImageBitmap(24, 24)
        Icon(bitmap, null, tint = Color.Unspecified)
        Icon(BitmapPainter(bitmap), null)
        Text("Auto", autoSize = TextAutoSize.StepBased(8.sp, 20.sp), fontFamily = FontFamily.Monospace, textDecoration = TextDecoration.Underline)
        Text(
            buildAnnotatedString { appendInlineContent("icon", "[image]") },
            inlineContent = mapOf("icon" to InlineTextContent(Placeholder(1.em, 1.em, PlaceholderVerticalAlign.Center)) { Icon(bitmap, null) }),
        )
    }
}
