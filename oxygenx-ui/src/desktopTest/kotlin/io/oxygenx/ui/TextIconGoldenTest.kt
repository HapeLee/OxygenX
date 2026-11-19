// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Icon
import io.oxygenx.ui.basic.Text
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class TextIconGoldenTest {
    @Test
    fun lightCapabilities() = checkCapabilities(dark = false)

    @Test
    fun darkCapabilities() = checkCapabilities(dark = true)

    private fun checkCapabilities(dark: Boolean) = runComposeUiTest {
        val vector = twoColorVector()
        val bitmap = twoColorBitmap()
        val text = buildAnnotatedString {
            append("Inline ")
            appendInlineContent("icon", "[image]")
            append(" content")
        }
        setContent {
            CompositionLocalProvider(LocalDensity provides Density(1f)) {
                OxygenTheme(darkTheme = dark) {
                    val type = OxygenTheme.typography
                    Column(
                        Modifier.size(320.dp, 180.dp).background(OxygenTheme.colorScheme.surface).testTag("golden").padding(OxygenTheme.spacing.gutter),
                        verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gutter),
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gutter)) {
                            Icon(vector, null)
                            Icon(vector, null, tint = Color.Unspecified)
                            Icon(bitmap, null, tint = Color.Unspecified)
                            Icon(BitmapPainter(bitmap), null, tint = OxygenTheme.colorScheme.primary)
                        }
                        Text(
                            text,
                            inlineContent = mapOf(
                                "icon" to InlineTextContent(Placeholder(1.em, 1.em, PlaceholderVerticalAlign.Center)) {
                                    Icon(bitmap, "inline", tint = Color.Unspecified)
                                },
                            ),
                        )
                        Text(
                            "Automatically sized title",
                            Modifier.fillMaxWidth(),
                            style = type.displayLarge,
                            maxLines = 1,
                            autoSize = TextAutoSize.StepBased(type.caption.fontSize, type.displayLarge.fontSize),
                        )
                    }
                }
            }
        }
        assertGolden(onNodeWithTag("golden").captureToImage(), "CMP-TEXT-ICON/${if (dark) "dark" else "light"}.png")
    }
}
