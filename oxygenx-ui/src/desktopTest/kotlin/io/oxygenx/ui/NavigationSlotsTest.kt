// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.navigation.NavigationBar
import io.oxygenx.ui.navigation.NavigationBarItem
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class NavigationSlotsTest {
    @Test
    fun slotItemsKeepSelectionDisabledStateAndSingleDispatch() = runComposeUiTest {
        val selected = mutableIntStateOf(0)
        var writes = 0
        setContent {
            OxygenTheme {
                NavigationBar {
                    repeat(3) { index ->
                        NavigationBarItem(
                            selected = selected.intValue == index,
                            onClick = {
                                selected.intValue = index
                                writes++
                            },
                            enabled = index != 2,
                            label = { Text("Tab $index") },
                            icon = { Box(Modifier.size(24.dp)) },
                        )
                    }
                }
            }
        }
        onNodeWithText("Tab 0").assertIsSelected()
        onNodeWithText("Tab 1").performClick().assertIsSelected()
        onNodeWithText("Tab 2").assertIsNotEnabled()
        runOnIdle { assertEquals(1, writes) }
    }
}
