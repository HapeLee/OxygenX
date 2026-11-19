// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.material3

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import io.oxygenx.theme.LocalContentColor
import io.oxygenx.theme.LocalTextStyle
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Button
import io.oxygenx.ui.basic.ButtonDefaults
import kotlin.test.Test
import kotlin.test.assertEquals
import androidx.compose.material3.LocalContentColor as MaterialContentColor
import androidx.compose.material3.LocalTextStyle as MaterialTextStyle
import androidx.compose.material3.Text as MaterialText

@OptIn(ExperimentalTestApi::class)
class Material3ContentTest {
    @Test
    fun rootBridgeReadsNestedSlotStylesAndUpdatesDisabledState() = runComposeUiTest {
        var enabled by mutableStateOf(true)
        var actualColor = Color.Unspecified
        var actualSize = 0.sp
        var expectedSize = 0.sp
        setContent {
            MaterialTheme {
                OxygenTheme {
                    expectedSize = OxygenTheme.typography.labelLarge.fontSize
                    ProvideMaterial3Content {
                        Button(
                            {},
                            enabled = enabled,
                            colors = ButtonDefaults.colors(contentColor = Color.Red, disabledContentColor = Color.Blue),
                        ) {
                            MaterialText("bridged", onTextLayout = {
                                actualColor = it.layoutInput.style.color
                                actualSize = it.layoutInput.style.fontSize
                            })
                        }
                    }
                }
            }
        }
        runOnIdle {
            assertEquals(Color.Red, actualColor)
            assertEquals(expectedSize, actualSize)
        }
        runOnIdle { enabled = false }
        runOnIdle { assertEquals(Color.Blue, actualColor) }
    }

    @Test
    fun explicitMaterialProviderWinsWithoutChangingOxygenLocals() = runComposeUiTest {
        var materialColor = Color.Unspecified
        var oxygenColor = Color.Unspecified
        var materialSize = 0.sp
        setContent {
            OxygenTheme {
                ProvideMaterial3Content {
                    CompositionLocalProvider(
                        LocalContentColor provides Color.Red,
                        LocalTextStyle provides TextStyle(fontSize = 29.sp),
                    ) {
                        CompositionLocalProvider(
                            MaterialContentColor provides Color.Green,
                            MaterialTextStyle provides TextStyle(fontSize = 17.sp),
                        ) {
                            oxygenColor = LocalContentColor.current
                            MaterialText("native", onTextLayout = {
                                materialColor = it.layoutInput.style.color
                                materialSize = it.layoutInput.style.fontSize
                            })
                        }
                    }
                }
            }
        }
        runOnIdle {
            assertEquals(Color.Green, materialColor)
            assertEquals(17.sp, materialSize)
            assertEquals(Color.Red, oxygenColor)
        }
    }
}
