// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
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
import io.oxygenx.icons.material.MaterialSymbols
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Icon
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class MaterialSymbolsGoldenTest {
    @Test
    fun lightCollection() = checkCollection(dark = false)

    @Test
    fun darkCollection() = checkCollection(dark = true)

    private fun checkCollection(dark: Boolean) = runComposeUiTest {
        setContent {
            CompositionLocalProvider(LocalDensity provides Density(1f)) {
                OxygenTheme(darkTheme = dark) {
                    Column(Modifier.background(OxygenTheme.colorScheme.surface).testTag("collection")) {
                        MaterialSymbols.All.chunked(16).forEach { row ->
                            Row {
                                row.forEach { icon ->
                                    Icon(icon, icon.name, Modifier.size(40.dp), size = 40.dp)
                                }
                            }
                        }
                    }
                }
            }
        }
        assertGolden(
            onNodeWithTag("collection").captureToImage(),
            "ICONS-MATERIAL/${if (dark) "dark" else "light"}.png",
        )
    }

    @Test
    fun autoMirroredVectorAndExplicitMirroringNeverCancelEachOther() = runComposeUiTest {
        setContent {
            CompositionLocalProvider(LocalDensity provides Density(1f)) {
                OxygenTheme {
                    Row {
                        Icon(MaterialSymbols.ArrowBack, null, Modifier.testTag("ltr"))
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Icon(MaterialSymbols.ArrowBack, null, Modifier.testTag("rtl"))
                            Icon(MaterialSymbols.ArrowBack, null, Modifier.testTag("explicit"), mirrorInRtl = true)
                        }
                    }
                }
            }
        }
        val ltr = onNodeWithTag("ltr").captureToImage().toPixelMap()
        val rtl = onNodeWithTag("rtl").captureToImage().toPixelMap()
        val explicit = onNodeWithTag("explicit").captureToImage().toPixelMap()
        for (y in 0 until ltr.height) {
            for (x in 0 until ltr.width) {
                assertEquals(ltr[x, y], rtl[ltr.width - 1 - x, y])
                assertEquals(rtl[x, y], explicit[x, y])
            }
        }
    }
}
