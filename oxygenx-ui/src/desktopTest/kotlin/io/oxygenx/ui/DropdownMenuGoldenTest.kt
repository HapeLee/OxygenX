// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import io.oxygenx.icons.material.MaterialSymbols
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Icon
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.overlay.DropdownMenuContent
import io.oxygenx.ui.overlay.DropdownMenuItem
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class DropdownMenuGoldenTest {
    @Test
    fun lightMenu() = checkMenu(false)

    @Test
    fun darkMenu() = checkMenu(true)

    private fun checkMenu(dark: Boolean) = runComposeUiTest {
        setContent {
            CompositionLocalProvider(LocalDensity provides Density(1f)) {
                OxygenTheme(darkTheme = dark) {
                    Box(Modifier.size(312.dp, 240.dp).background(OxygenTheme.colorScheme.surface).testTag("golden").padding(16.dp)) {
                        DropdownMenuContent {
                            DropdownMenuItem(text = { Text("Sort by name") }, onClick = {}, trailingIcon = { Icon(MaterialSymbols.Check, null) })
                            DropdownMenuItem(text = { Text("Edit") }, onClick = {}, leadingIcon = { Icon(MaterialSymbols.Edit, null) })
                            DropdownMenuItem(text = { Text("Delete") }, onClick = {}, leadingIcon = { Icon(MaterialSymbols.Delete, null) })
                            DropdownMenuItem(text = { Text("Unavailable") }, onClick = {}, enabled = false, leadingIcon = { Icon(MaterialSymbols.Info, null) })
                        }
                    }
                }
            }
        }
        assertGolden(onNodeWithTag("golden").captureToImage(), "CMP-DROPDOWN-MENU/${if (dark) "dark" else "light"}.png")
    }
}
