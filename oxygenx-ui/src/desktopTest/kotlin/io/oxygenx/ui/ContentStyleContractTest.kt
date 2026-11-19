// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.oxygenx.theme.LocalContentColor
import io.oxygenx.theme.LocalTextStyle
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.theme.ProvideTextStyle
import io.oxygenx.ui.basic.Card
import io.oxygenx.ui.basic.CardDefaults
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.layout.Scaffold
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ContentStyleContractTest {
    @Test
    fun nestedThemePreservesExplicitSlotContentStyle() = runComposeUiTest {
        var actualColor = Color.Unspecified
        var actualSize = 0.sp
        setContent {
            OxygenTheme {
                CompositionLocalProvider(
                    LocalContentColor provides Color.Magenta,
                    LocalTextStyle provides TextStyle(fontSize = 31.sp),
                ) {
                    OxygenTheme(spacing = OxygenTheme.spacing) {
                        Text("nested", onTextLayout = {
                            actualColor = it.layoutInput.style.color
                            actualSize = it.layoutInput.style.fontSize
                        })
                    }
                }
            }
        }
        runOnIdle {
            assertEquals(Color.Magenta, actualColor)
            assertEquals(31.sp, actualSize)
        }
    }

    @Test
    fun unknownContainerColorInheritsAmbientContentColor() = runComposeUiTest {
        var cardColor = Color.Unspecified
        var scaffoldColor = Color.Unspecified
        setContent {
            OxygenTheme {
                CompositionLocalProvider(LocalContentColor provides Color.Green) {
                    Card(colors = CardDefaults.colors(containerColor = Color(0xFF123456))) {
                        Text("card", onTextLayout = { cardColor = it.layoutInput.style.color })
                    }
                    Scaffold(containerColor = Color(0xFF654321)) {
                        Text("scaffold", onTextLayout = { scaffoldColor = it.layoutInput.style.color })
                    }
                }
            }
        }
        runOnIdle {
            assertEquals(Color.Green, cardColor)
            assertEquals(Color.Green, scaffoldColor)
        }
    }

    @Test
    fun provideTextStyleMergesAndUpdatesAmbientAttributes() = runComposeUiTest {
        var size by mutableStateOf(19.sp)
        var actual = TextStyle.Default
        setContent {
            OxygenTheme {
                ProvideTextStyle(TextStyle(fontSize = size, color = Color.Red)) {
                    ProvideTextStyle(TextStyle(fontWeight = FontWeight.Bold)) {
                        Text("merged", onTextLayout = { actual = it.layoutInput.style })
                    }
                }
            }
        }
        runOnIdle {
            assertEquals(19.sp, actual.fontSize)
            assertEquals(Color.Red, actual.color)
            assertEquals(FontWeight.Bold, actual.fontWeight)
        }
        runOnIdle { size = 27.sp }
        runOnIdle { assertEquals(27.sp, actual.fontSize) }
    }
}
