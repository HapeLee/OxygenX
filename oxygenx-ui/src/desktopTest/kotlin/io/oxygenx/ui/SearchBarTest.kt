// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.theme.lightColorScheme
import io.oxygenx.ui.basic.SearchBar
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class SearchBarTest {
    @Test
    fun searchBarDefaultsUseCompactHeightInsetsAndSurfaceColor() = runComposeUiTest {
        val colors = lightColorScheme()
        setContent {
            OxygenTheme(colorScheme = colors) {
                SearchBar(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.width(320.dp).testTag("search"),
                    placeholder = "Find books",
                    leadingIcon = { Box(Modifier.size(24.dp).testTag("leading")) },
                    trailingIcon = { Box(Modifier.size(24.dp).testTag("trailing")) },
                )
            }
        }

        val search = onNodeWithTag("search")
        val bar = search.getUnclippedBoundsInRoot()
        val start = onNodeWithTag("leading").getUnclippedBoundsInRoot()
        val end = onNodeWithTag("trailing").getUnclippedBoundsInRoot()
        val hint = onNodeWithText("Find books", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertEquals(48.dp, bar.bottom - bar.top)
        assertEquals(12.dp, start.left - bar.left)
        assertEquals(12.dp, bar.right - end.right)
        val center = (bar.top + bar.bottom) / 2
        assertEquals(center, (start.top + start.bottom) / 2)
        assertEquals(center.value, ((hint.top + hint.bottom) / 2).value, 1f)

        val pixels = search.captureToImage().toPixelMap()
        assertEquals(colors.surfaceContainerLow, pixels[pixels.width / 2, pixels.height / 2])
    }
}
