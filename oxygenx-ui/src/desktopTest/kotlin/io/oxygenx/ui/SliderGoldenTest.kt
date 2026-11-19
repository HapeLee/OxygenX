// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
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
import io.oxygenx.ui.basic.Slider
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class SliderGoldenTest {
    @Test
    fun lightLtr() = checkSlider(dark = false, rtl = false)

    @Test
    fun lightRtl() = checkSlider(dark = false, rtl = true)

    @Test
    fun darkLtr() = checkSlider(dark = true, rtl = false)

    @Test
    fun darkRtl() = checkSlider(dark = true, rtl = true)

    private fun checkSlider(dark: Boolean, rtl: Boolean) = runComposeUiTest {
        setContent {
            CompositionLocalProvider(
                LocalDensity provides Density(1f),
                LocalLayoutDirection provides if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr,
            ) {
                OxygenTheme(darkTheme = dark) {
                    Box(Modifier.size(240.dp, 48.dp).background(OxygenTheme.colorScheme.surface).testTag("golden")) {
                        Slider(value = 0.25f, onValueChange = {})
                    }
                }
            }
        }
        val name = "${if (dark) "dark" else "light"}-${if (rtl) "rtl" else "ltr"}.png"
        assertGolden(onNodeWithTag("golden").captureToImage(), "CMP-SLIDER/$name")
    }
}
