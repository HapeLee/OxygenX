// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Button
import io.oxygenx.ui.basic.Checkbox
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.basic.TextField
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ComponentMatrixTest {
    @Test
    fun controlsSupportThemesDirectionsAndFontScales() = runComposeUiTest {
        var dark by mutableStateOf(false)
        var rtl by mutableStateOf(false)
        var fontScale by mutableFloatStateOf(1f)
        var enabled by mutableStateOf(true)
        var clicks = 0
        setContent {
            CompositionLocalProvider(
                LocalLayoutDirection provides if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr,
                LocalDensity provides Density(1f, fontScale),
            ) {
                OxygenTheme(darkTheme = dark) {
                    var checked by remember { mutableStateOf(false) }
                    Column(Modifier.width(280.dp)) {
                        Button({ clicks++ }, Modifier.testTag("button"), enabled = enabled) {
                            Text("Save\n保存", Modifier.testTag("label"))
                        }
                        Checkbox(checked, { checked = it }, Modifier.testTag("check"), enabled = enabled)
                        TextField("", {}, Modifier.testTag("disabled-input"), enabled = false)
                    }
                }
            }
        }
        for (isDark in listOf(false, true)) {
            for (isRtl in listOf(false, true)) {
                for (scale in listOf(1f, 2f)) {
                    runOnIdle {
                        dark = isDark
                        rtl = isRtl
                        fontScale = scale
                        enabled = true
                    }
                    onNodeWithTag("button").assertIsEnabled().performClick()
                    val button = onNodeWithTag("button").fetchSemanticsNode().boundsInRoot
                    val label = onNodeWithTag("label", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
                    assertTrue(button.height >= 48f)
                    assertTrue(label.top >= button.top && label.bottom <= button.bottom, "Text cropped at fontScale=$scale")
                    onNodeWithTag("check").assertIsOff().performClick().assertIsOn().performClick().assertIsOff()
                    onNodeWithTag("disabled-input").assertIsNotEnabled()
                    runOnIdle { enabled = false }
                    onNodeWithTag("button").assertIsNotEnabled()
                    onNodeWithTag("check").assertIsNotEnabled()
                }
            }
        }
        runOnIdle { assertEquals(8, clicks) }
    }

    @Test
    fun textFieldReceivesInputThroughItsPublicContract() = runComposeUiTest {
        setContent {
            var value by remember { mutableStateOf("") }
            OxygenTheme {
                TextField(value, { value = it }, Modifier.testTag("input"), singleLine = true)
            }
        }
        onNodeWithTag("input").performTextInput("OxygenX")
        onNodeWithTag("input").assertTextEquals("OxygenX")
    }
}
