// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.basic.TextField
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class TextFieldGoldenTest {
    @Test
    fun lightSlots() = checkSlots(dark = false)

    @Test
    fun darkSlots() = checkSlots(dark = true)

    private fun checkSlots(dark: Boolean) = runComposeUiTest {
        val filled = TextFieldState("123")
        val empty = TextFieldState("")
        val disabled = TextFieldState("Disabled")
        setContent {
            CompositionLocalProvider(
                LocalDensity provides Density(1f),
                LocalLayoutDirection provides LayoutDirection.Ltr,
            ) {
                OxygenTheme(darkTheme = dark) {
                    Column(
                        Modifier.size(320.dp, 300.dp)
                            .background(OxygenTheme.colorScheme.surface)
                            .testTag("golden")
                            .padding(OxygenTheme.spacing.gutter),
                    ) {
                        TextField(
                            filled,
                            label = { Text("Number") },
                            prefix = { Text("#") },
                            suffix = { Text("kg") },
                            supportingText = { Text("Digits only") },
                            outputTransformation = OutputTransformation {
                                replace(0, 0, "[")
                                append("]")
                            },
                        )
                        TextField(
                            empty,
                            isError = true,
                            label = { Text("Name") },
                            placeholder = { Text("Required") },
                            supportingText = { Text("Enter a name") },
                        )
                        TextField(disabled, enabled = false, label = { Text("Unavailable") })
                    }
                }
            }
        }
        assertGolden(onNodeWithTag("golden").captureToImage(), "CMP-TEXTFIELD/${if (dark) "dark" else "light"}-slots.png")
    }
}
