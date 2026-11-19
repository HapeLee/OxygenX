// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.basic.TextField
import io.oxygenx.ui.basic.TextFieldDefaults
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class StateTextFieldTest {
    @Test
    fun userFilterPreservesSelectionAndDoesNotFilterProgrammaticEdits() = runComposeUiTest {
        val state = TextFieldState(initialText = "12", initialSelection = TextRange(1))
        val digitsOnly = InputTransformation { if (asCharSequence().any { !it.isDigit() }) revertAllChanges() }
        setContent {
            OxygenTheme { TextField(state, Modifier.testTag("input"), inputTransformation = digitsOnly) }
        }
        onNodeWithTag("input").performTextInput("3")
        runOnIdle {
            assertEquals("132", state.text.toString())
            assertEquals(TextRange(2), state.selection)
        }
        onNodeWithTag("input").performTextInput("x")
        runOnIdle {
            assertEquals("132", state.text.toString())
            assertEquals(TextRange(2), state.selection)
        }
        runOnIdle {
            state.edit {
                replace(0, length, "programmatic")
                selection = TextRange(4)
            }
        }
        runOnIdle {
            assertEquals("programmatic", state.text.toString())
            assertEquals(TextRange(4), state.selection)
        }
    }

    @Test
    fun outputFormattingChangesLayoutWithoutMutatingRawState() = runComposeUiTest {
        val state = TextFieldState("123")
        var query: (() -> TextLayoutResult?)? = null
        val format = OutputTransformation {
            replace(0, 0, "[")
            append("]")
        }
        setContent {
            OxygenTheme {
                TextField(state, outputTransformation = format, onTextLayout = { query = it })
            }
        }
        runOnIdle {
            assertEquals("[123]", query?.invoke()?.layoutInput?.text?.text)
            assertEquals("123", state.text.toString())
            assertEquals(TextRange(3), state.selection)
        }
    }

    @Test
    fun placeholderUsesTransformedDisplayEmptiness() = runComposeUiTest {
        val rawEmpty = TextFieldState("")
        val rawFilled = TextFieldState("123")
        setContent {
            OxygenTheme {
                Column {
                    TextField(
                        rawEmpty,
                        outputTransformation = OutputTransformation { append("display") },
                        placeholder = { Text("hidden", Modifier.testTag("hidden-hint")) },
                    )
                    TextField(
                        rawFilled,
                        outputTransformation = OutputTransformation { replace(0, length, "") },
                        placeholder = { Text("visible", Modifier.testTag("visible-hint")) },
                    )
                }
            }
        }
        onNodeWithTag("hidden-hint", useUnmergedTree = true).assertDoesNotExist()
        onNodeWithTag("visible-hint", useUnmergedTree = true).assertExists()
        runOnIdle {
            assertEquals("", rawEmpty.text.toString())
            assertEquals("123", rawFilled.text.toString())
        }
    }

    @Test
    fun imeActionCallsHandlerOnce() = runComposeUiTest {
        val state = TextFieldState("done")
        var actions = 0
        setContent {
            OxygenTheme {
                TextField(
                    state,
                    Modifier.testTag("input"),
                    lineLimits = TextFieldLineLimits.SingleLine,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    onKeyboardAction = { actions++ },
                )
            }
        }
        onNodeWithTag("input").performImeAction()
        runOnIdle { assertEquals(1, actions) }
    }

    @Test
    fun lineLimitsUseCallerOwnedScrollState() = runComposeUiTest {
        val state = TextFieldState((1..15).joinToString("\n") { "Line $it" })
        val scroll = ScrollState(0)
        setContent {
            OxygenTheme {
                TextField(
                    state,
                    Modifier.width(240.dp),
                    scrollState = scroll,
                    lineLimits = TextFieldLineLimits.MultiLine(minHeightInLines = 1, maxHeightInLines = 2),
                )
            }
        }
        runOnIdle { assertTrue(scroll.maxValue > 0) }
    }

    @Test
    fun disabledAndReadOnlyFieldsDoNotExposeEditingAction() = runComposeUiTest {
        setContent {
            OxygenTheme {
                Column {
                    TextField(TextFieldState("disabled"), Modifier.testTag("disabled"), enabled = false)
                    TextField(TextFieldState("readonly"), Modifier.testTag("readonly"), readOnly = true)
                }
            }
        }
        onNodeWithTag("disabled").assertIsNotEnabled()
        onNodeWithTag("readonly").assert(SemanticsMatcher.keyNotDefined(SemanticsActions.SetText))
        onNodeWithTag("disabled").assert(SemanticsMatcher.keyNotDefined(SemanticsActions.SetText))
    }

    @Test
    fun slotsReceiveErrorAndPlaceholderStylesAndExplicitTextColorWins() = runComposeUiTest {
        var label = Color.Unspecified
        var hint = Color.Unspecified
        var supporting = Color.Unspecified
        var text = Color.Unspecified
        var expectedHint = Color.Unspecified
        var query: (() -> TextLayoutResult?)? = null
        setContent {
            OxygenTheme {
                expectedHint = OxygenTheme.colorScheme.onSurfaceVariant
                TextField(
                    TextFieldState(""),
                    isError = true,
                    textStyle = TextStyle(color = Color.Green),
                    colors = TextFieldDefaults.colors(errorIndicatorColor = Color.Red),
                    label = { Text("Label", onTextLayout = { label = it.layoutInput.style.color }) },
                    placeholder = { Text("Hint", onTextLayout = { hint = it.layoutInput.style.color }) },
                    supportingText = { Text("Error", onTextLayout = { supporting = it.layoutInput.style.color }) },
                    onTextLayout = { query = it },
                )
            }
        }
        runOnIdle {
            text = query?.invoke()?.layoutInput?.style?.color ?: Color.Unspecified
            assertEquals(Color.Green, text)
            assertEquals(Color.Red, label)
            assertEquals(Color.Red, supporting)
            assertEquals(expectedHint, hint)
        }
    }
}
