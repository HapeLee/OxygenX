// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.overlay

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class DropdownMenuPositionTest {
    @Test
    fun offsetAndStartAlignmentRespectLayoutDirection() {
        val provider = DropdownMenuPositionProvider(Density(1f), DpOffset(10.dp, 4.dp))
        val anchor = IntRect(120, 100, 180, 148)
        val window = IntSize(400, 500)
        assertEquals(IntOffset(130, 152), provider.calculatePosition(anchor, window, LayoutDirection.Ltr, IntSize(112, 64)))
        assertEquals(IntOffset(58, 152), provider.calculatePosition(anchor, window, LayoutDirection.Rtl, IntSize(112, 64)))
    }

    @Test
    fun edgesFlipAboveAndTinyWindowsNeverProduceNegativePositions() {
        val provider = DropdownMenuPositionProvider(Density(1f), DpOffset(10.dp, 4.dp))
        assertEquals(
            IntOffset(130, 308),
            provider.calculatePosition(IntRect(120, 400, 180, 448), IntSize(400, 500), LayoutDirection.Ltr, IntSize(112, 96)),
        )
        assertEquals(
            IntOffset(288, 152),
            provider.calculatePosition(IntRect(380, 100, 398, 148), IntSize(400, 500), LayoutDirection.Ltr, IntSize(112, 64)),
        )
        assertEquals(
            IntOffset.Zero,
            provider.calculatePosition(IntRect(30, 20, 60, 40), IntSize(64, 50), LayoutDirection.Rtl, IntSize(112, 64)),
        )
    }

    @Test
    fun transformOriginPointsAtTheAnchorEdge() {
        val anchor = IntRect(120, 100, 180, 148)
        val below = menuTransformOrigin(anchor, IntRect(120, 148, 300, 348))
        val above = menuTransformOrigin(anchor, IntRect(20, 0, 200, 100))
        assertEquals(0f, below.pivotFractionY)
        assertEquals(1f, above.pivotFractionY)
        assertEquals(1f / 6f, below.pivotFractionX)
        assertEquals(130f / 180f, above.pivotFractionX)
    }
}
