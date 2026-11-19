// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Text
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class TextCapabilitiesTest {
    @Test
    fun inlineContentIsMeasuredAndInteractive() = runComposeUiTest {
        var layout: TextLayoutResult? = null
        var clicks = 0
        val text = buildAnnotatedString {
            append("A ")
            appendInlineContent("cover", "[cover]")
            append(" B")
        }
        setContent {
            OxygenTheme {
                Text(
                    text,
                    inlineContent = mapOf(
                        "cover" to InlineTextContent(Placeholder(1.em, 1.em, PlaceholderVerticalAlign.Center)) {
                            Box(Modifier.fillMaxSize().testTag("inline").clickable { clicks++ })
                        },
                    ),
                    onTextLayout = { layout = it },
                )
            }
        }
        onNodeWithTag("inline", useUnmergedTree = true).performClick()
        runOnIdle {
            assertEquals(1, clicks)
            val rect = requireNotNull(layout).placeholderRects.single()
            assertTrue(requireNotNull(rect).width > 0)
            assertTrue(rect.height > 0)
        }
    }

    @Test
    fun missingInlineIdKeepsAlternateText() = runComposeUiTest {
        var layout: TextLayoutResult? = null
        val text = buildAnnotatedString { appendInlineContent("missing", "alternate") }
        setContent { OxygenTheme { Text(text, onTextLayout = { layout = it }) } }
        runOnIdle {
            assertEquals("alternate", requireNotNull(layout).layoutInput.text.text)
            assertTrue(requireNotNull(layout).placeholderRects.isEmpty())
        }
    }

    @Test
    fun autoSizeFitsStringAndAnnotatedTextWithinConstraints() = runComposeUiTest {
        val layouts = mutableMapOf<String, TextLayoutResult>()
        setContent {
            OxygenTheme {
                Column {
                    Text(
                        "A long title",
                        Modifier.size(100.dp, 40.dp),
                        fontSize = 40.sp,
                        maxLines = 1,
                        autoSize = TextAutoSize.StepBased(8.sp, 40.sp, 1.sp),
                        onTextLayout = { layouts["string"] = it },
                    )
                    Text(
                        buildAnnotatedString { append("A long title") },
                        Modifier.size(100.dp, 40.dp),
                        fontSize = 40.sp,
                        maxLines = 1,
                        autoSize = TextAutoSize.StepBased(8.sp, 40.sp, 1.sp),
                        onTextLayout = { layouts["annotated"] = it },
                    )
                }
            }
        }
        runOnIdle {
            for (key in listOf("string", "annotated")) {
                val result = requireNotNull(layouts[key])
                assertFalse(result.hasVisualOverflow, key)
                assertTrue(result.layoutInput.style.fontSize.value < 40f, key)
                assertTrue(result.layoutInput.style.fontSize.value >= 8f, key)
            }
        }
    }

    @Test
    fun fontAndDecorationOverridesPreserveStyleColorInRtlLargeFonts() = runComposeUiTest {
        var layout: TextLayoutResult? = null
        setContent {
            CompositionLocalProvider(LocalDensity provides Density(1f, 2f), LocalLayoutDirection provides LayoutDirection.Rtl) {
                OxygenTheme {
                    Text(
                        "مرحبا",
                        style = TextStyle(color = Color.Red, fontSize = 15.sp, fontFamily = FontFamily.Serif, textDecoration = TextDecoration.LineThrough),
                        fontFamily = FontFamily.Monospace,
                        textDecoration = TextDecoration.Underline,
                        onTextLayout = { layout = it },
                    )
                }
            }
        }
        runOnIdle {
            val result = requireNotNull(layout)
            assertEquals(Color.Red, result.layoutInput.style.color)
            assertEquals(FontFamily.Monospace, result.layoutInput.style.fontFamily)
            assertEquals(TextDecoration.Underline, result.layoutInput.style.textDecoration)
            assertEquals(2f, result.layoutInput.density.fontScale)
            assertEquals(LayoutDirection.Rtl, result.layoutInput.layoutDirection)
        }
    }
}
