// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.oxygenx.theme.LocalContentColor
import io.oxygenx.theme.LocalTextStyle
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.theme.ProvideContentStyle
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.overlay.DropdownMenu
import io.oxygenx.ui.overlay.DropdownMenuContent
import io.oxygenx.ui.overlay.DropdownMenuItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class DropdownMenuContractTest {
    @Test
    fun closingKeepsThePopupUntilItsExitAnimationCompletes() = runComposeUiTest {
        val expanded = mutableStateOf(true)
        setContent {
            OxygenTheme {
                Box(Modifier.width(240.dp)) {
                    DropdownMenu(expanded.value, { expanded.value = false }) {
                        DropdownMenuItem(text = { Text("Animated") }, onClick = {})
                    }
                }
            }
        }
        waitForIdle()
        mainClock.autoAdvance = false
        runOnIdle { expanded.value = false }
        mainClock.advanceTimeByFrame()
        onNodeWithText("Animated").assertExists()
        mainClock.advanceTimeBy(1_000)
        onNodeWithText("Animated").assertDoesNotExist()
    }

    @Test
    fun suppliedInteractionSourceReceivesOnePressAndRelease() = runComposeUiTest {
        val source = MutableInteractionSource()
        val events = mutableListOf<androidx.compose.foundation.interaction.Interaction>()
        setContent {
            OxygenTheme {
                LaunchedEffect(source) { source.interactions.collect { events += it } }
                DropdownMenuItem(
                    text = { Text("Action") },
                    onClick = {},
                    modifier = Modifier.width(240.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    interactionSource = source,
                )
            }
        }
        onNodeWithText("Action").performClick()
        runOnIdle {
            assertEquals(1, events.filterIsInstance<PressInteraction.Press>().size)
            assertEquals(1, events.filterIsInstance<PressInteraction.Release>().size)
            assertTrue(events.none { it is PressInteraction.Cancel })
        }
    }

    @Test
    fun boundedWidthAndLargeFontGrowRowsInsteadOfClippingText() = runComposeUiTest {
        setContent {
            CompositionLocalProvider(LocalDensity provides Density(1f, 2f)) {
                OxygenTheme {
                    Box(Modifier.width(400.dp)) {
                        DropdownMenuContent(Modifier.heightIn(max = 320.dp).testTag("panel")) {
                            DropdownMenuItem(
                                text = { Text("A very long menu option\nSecond line") },
                                onClick = {},
                                modifier = Modifier.testTag("large"),
                            )
                        }
                    }
                }
            }
        }
        onNodeWithTag("panel").assertWidthIsEqualTo(280.dp)
        onNodeWithTag("large").assertHeightIsAtLeast(80.dp)
    }

    @Test
    fun disabledSlotsUseTheDisabledColorAndCustomPaddingIsHonored() = runComposeUiTest {
        var expected = androidx.compose.ui.graphics.Color.Unspecified
        var actual = androidx.compose.ui.graphics.Color.Unspecified
        var iconColor = androidx.compose.ui.graphics.Color.Unspecified
        var clicks = 0
        setContent {
            OxygenTheme {
                expected = OxygenTheme.colorScheme.onSurfaceDisabled
                Column(Modifier.width(240.dp)) {
                    DropdownMenuItem(
                        text = {
                            val color = LocalContentColor.current
                            SideEffect { actual = color }
                            Text("Disabled")
                        },
                        onClick = { clicks++ },
                        enabled = false,
                        leadingIcon = {
                            val color = LocalContentColor.current
                            SideEffect { iconColor = color }
                            Text("+")
                        },
                    )
                    DropdownMenuItem(
                        text = { Text("Padded", Modifier.testTag("padded-text")) },
                        onClick = { clicks++ },
                        modifier = Modifier.testTag("padded-row"),
                        contentPadding = PaddingValues(start = 32.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
                    )
                }
            }
        }
        onNodeWithText("Disabled").assertIsNotEnabled().performClick()
        runOnIdle {
            assertEquals(expected, actual)
            assertEquals(expected, iconColor)
            assertEquals(0, clicks)
        }
        val row = onNodeWithTag("padded-row").fetchSemanticsNode().boundsInRoot
        val text = onNodeWithTag("padded-text", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertEquals(32f, text.left - row.left)
        onNodeWithText("Padded").performClick()
        runOnIdle { assertEquals(1, clicks) }
    }

    @Test
    fun itemOwnsItsCompactLabelStyleEvenUnderAnUnrelatedParentStyle() = runComposeUiTest {
        var actual = TextStyle.Default
        var expected = TextStyle.Default
        setContent {
            OxygenTheme {
                expected = OxygenTheme.typography.labelLarge
                ProvideContentStyle(textStyle = TextStyle(fontSize = 30.sp)) {
                    DropdownMenuItem(
                        text = {
                            val style = LocalTextStyle.current
                            SideEffect { actual = style }
                            Text("Menu")
                        },
                        onClick = {},
                        modifier = Modifier.width(240.dp),
                    )
                }
            }
        }
        runOnIdle {
            assertEquals(expected.fontSize, actual.fontSize)
            assertEquals(expected.lineHeight, actual.lineHeight)
        }
    }

    @Test
    fun longMenuCanScrollToItsLastItemAndCallsTheHostOnce() = runComposeUiTest {
        val expanded = mutableStateOf(true)
        var clicks = 0
        setContent {
            OxygenTheme {
                Box(Modifier.width(280.dp)) {
                    DropdownMenu(expanded.value, { expanded.value = false }, Modifier.heightIn(max = 200.dp).testTag("menu")) {
                        repeat(40) { index ->
                            DropdownMenuItem(text = { Text("Option $index") }, onClick = {
                                clicks++
                                expanded.value = false
                            })
                        }
                    }
                }
            }
        }
        onAllNodes(hasScrollAction()).assertCountEquals(1)
        onNodeWithText("Option 39").performScrollTo().performClick()
        runOnIdle { assertEquals(1, clicks) }
        onNodeWithText("Option 39").assertDoesNotExist()
    }
}
