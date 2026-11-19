// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.icons.material

import io.oxygenx.icons.icon.OxygenIcons
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class MaterialSymbolsTest {
    @Test
    fun completeCollectionHasUniqueNamesAndUsableGeometry() {
        val all = MaterialSymbols.All
        assertEquals(128, all.size)
        assertEquals(all.size, all.map { it.name }.toSet().size)
        for (icon in all) {
            assertEquals(24f, icon.viewportWidth, icon.name)
            assertEquals(24f, icon.viewportHeight, icon.name)
            assertTrue(icon.root.size > 0, icon.name)
        }
        assertTrue(MaterialSymbols.ArrowBackIosNew.autoMirror)
        assertTrue(MaterialSymbols.List.autoMirror)
        assertTrue(MaterialSymbols.LibraryBooks.autoMirror)
        assertFalse(MaterialSymbols.SkipNext.autoMirror)
        assertFalse(MaterialSymbols.SkipPrevious.autoMirror)
    }

    @Test
    fun coreCompatibilityNamesReuseExactlyTheSameVectors() {
        val pairs = listOf(
            OxygenIcons.Back to MaterialSymbols.ArrowBackIosNew,
            OxygenIcons.ChevronRight to MaterialSymbols.ChevronRight,
            OxygenIcons.Check to MaterialSymbols.Check,
            OxygenIcons.Settings to MaterialSymbols.Settings,
            OxygenIcons.Clock to MaterialSymbols.Schedule,
            OxygenIcons.Grid to MaterialSymbols.GridView,
            OxygenIcons.Info to MaterialSymbols.Info,
            OxygenIcons.Alarm to MaterialSymbols.Alarm,
            OxygenIcons.WorldClock to MaterialSymbols.Public,
            OxygenIcons.Hourglass to MaterialSymbols.HourglassEmpty,
            OxygenIcons.Stopwatch to MaterialSymbols.Timer,
            OxygenIcons.Add to MaterialSymbols.Add,
            OxygenIcons.Search to MaterialSymbols.Search,
            OxygenIcons.Close to MaterialSymbols.Close,
        )
        for ((core, extended) in pairs) assertSame(core, extended)
    }
}
