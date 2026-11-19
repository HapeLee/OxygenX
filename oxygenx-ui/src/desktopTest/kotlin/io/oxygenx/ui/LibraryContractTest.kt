// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.theme.darkColorScheme
import io.oxygenx.ui.basic.Slider
import io.oxygenx.ui.basic.Text
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class LibraryContractTest {
    @Test
    fun nestedTypographyOverridePreservesCustomDarkColors() = runComposeUiTest {
        val outer = darkColorScheme().copy(primary = Color.Magenta)
        var actual = outer.copy(primary = Color.Transparent)
        setContent {
            OxygenTheme(colorScheme = outer) {
                OxygenTheme(typography = OxygenTheme.typography) {
                    val colors = OxygenTheme.colorScheme
                    SideEffect { actual = colors }
                }
            }
        }
        runOnIdle { assertEquals(outer, actual) }
    }

    @Test
    fun textPreservesStyleColorUnlessExplicitColorOverridesIt() = runComposeUiTest {
        var styleColor = Color.Unspecified
        var explicitColor = Color.Unspecified
        setContent {
            OxygenTheme {
                Text("style", style = TextStyle(color = Color.Red), onTextLayout = {
                    styleColor = it.layoutInput.style.color
                })
                Text("explicit", style = TextStyle(color = Color.Red), color = Color.Blue, onTextLayout = {
                    explicitColor = it.layoutInput.style.color
                })
            }
        }
        runOnIdle {
            assertEquals(Color.Red, styleColor)
            assertEquals(Color.Blue, explicitColor)
        }
    }

    @Test
    fun accessibilityProgressCommitsOnceAndIgnoresUnchangedValue() = runComposeUiTest {
        var actual = 0.25f
        var finished = 0
        setContent {
            var value by remember { mutableFloatStateOf(actual) }
            OxygenTheme {
                Slider(
                    value,
                    {
                        value = it
                        actual = it
                    },
                    Modifier.testTag("slider"),
                    steps = 3,
                    onValueChangeFinished = { finished++ },
                )
            }
        }
        onNodeWithTag("slider").performSemanticsAction(SemanticsActions.SetProgress) { assertTrue(it(0.75f)) }
        runOnIdle {
            assertEquals(0.75f, actual)
            assertEquals(1, finished)
        }
        onNodeWithTag("slider").performSemanticsAction(SemanticsActions.SetProgress) { assertFalse(it(0.75f)) }
        runOnIdle { assertEquals(1, finished) }
    }

    @Test
    fun keyboardDirectionRespectsRtlAndCommitsEachStep() = runComposeUiTest {
        var actual = 0.5f
        var finished = 0
        setContent {
            var value by remember { mutableFloatStateOf(actual) }
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                OxygenTheme {
                    Slider(
                        value,
                        {
                            value = it
                            actual = it
                        },
                        Modifier.testTag("slider"),
                        steps = 3,
                        onValueChangeFinished = { finished++ },
                    )
                }
            }
        }
        onNodeWithTag("slider").performSemanticsAction(SemanticsActions.RequestFocus) { assertTrue(it()) }
        onNodeWithTag("slider").performKeyInput { pressKey(Key.DirectionRight) }
        runOnIdle {
            assertEquals(0.25f, actual)
            assertEquals(1, finished)
        }
        onNodeWithTag("slider").performKeyInput { pressKey(Key.MoveEnd) }
        runOnIdle {
            assertEquals(1f, actual)
            assertEquals(2, finished)
        }
    }

    @Test
    fun rtlTouchAndCustomThumbUseTheSameMirroredPosition() = runComposeUiTest {
        var actual = 0.25f
        var finished = 0
        setContent {
            var value by remember { mutableFloatStateOf(actual) }
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                OxygenTheme {
                    Slider(
                        value = value,
                        onValueChange = {
                            value = it
                            actual = it
                        },
                        modifier = Modifier.width(240.dp).testTag("slider"),
                        onValueChangeFinished = { finished++ },
                        thumb = { Box(Modifier.size(20.dp).testTag("thumb")) },
                    )
                }
            }
        }
        val slider = onNodeWithTag("slider").fetchSemanticsNode().boundsInRoot
        val thumb = onNodeWithTag("thumb", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertTrue(thumb.center.x > slider.center.x)
        onNodeWithTag("slider").performTouchInput { click(Offset(width * 0.2f, height / 2f)) }
        runOnIdle {
            assertTrue(actual > 0.75f)
            assertEquals(1, finished)
        }
        val moved = onNodeWithTag("thumb", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertTrue(moved.center.x < slider.center.x)
    }

    @Test
    fun disabledSliderDoesNotRespondToTouch() = runComposeUiTest {
        var callbacks = 0
        setContent {
            OxygenTheme {
                Slider(
                    0.5f,
                    { callbacks++ },
                    Modifier.testTag("slider"),
                    enabled = false,
                    onValueChangeFinished = { callbacks++ },
                )
            }
        }
        onNodeWithTag("slider").performTouchInput { click() }
        runOnIdle { assertEquals(0, callbacks) }
    }
}
