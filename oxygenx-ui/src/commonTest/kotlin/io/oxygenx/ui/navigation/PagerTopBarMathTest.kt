// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.navigation

import kotlin.test.Test
import kotlin.test.assertEquals

class PagerTopBarMathTest {
    @Test
    fun slowShortDragSnapsBackToCurrentPage() {
        // 拖了不到半页、松手时速度也不够：回当前页（与 pager 自己的吸附口径一致）。
        assertEquals(
            2,
            pagerDragSettlePage(
                currentPage = 2,
                pageCount = 3,
                offsetFraction = 0.3f,
                dragVelocity = 0f,
                flingThresholdPx = 1_000f,
            ),
        )
    }

    @Test
    fun leftFlingGoesToNextPage() {
        // 手指向左甩（速度为负）= pager 前进一页。
        assertEquals(
            2,
            pagerDragSettlePage(
                currentPage = 1,
                pageCount = 3,
                offsetFraction = 0f,
                dragVelocity = -2_000f,
                flingThresholdPx = 1_000f,
            ),
        )
    }

    @Test
    fun rightFlingGoesToPreviousPage() {
        assertEquals(
            1,
            pagerDragSettlePage(
                currentPage = 2,
                pageCount = 3,
                offsetFraction = 0f,
                dragVelocity = 2_000f,
                flingThresholdPx = 1_000f,
            ),
        )
    }

    @Test
    fun offsetAfterPageFlipDoesNotBounceBack() {
        // 翻过页后 `currentPage` 已经加一、offset 变号到 -0.4（还没到半页）：
        // 停在当前页，不会被「四舍五入」打回上一页。
        assertEquals(
            3,
            pagerDragSettlePage(
                currentPage = 3,
                pageCount = 5,
                offsetFraction = -0.4f,
                dragVelocity = 0f,
                flingThresholdPx = 1_000f,
            ),
        )
    }

    @Test
    fun flingAtEdgesStaysInsideRange() {
        assertEquals(
            0,
            pagerDragSettlePage(
                currentPage = 0,
                pageCount = 3,
                offsetFraction = 0f,
                dragVelocity = 2_000f,
                flingThresholdPx = 1_000f,
            ),
        )
        assertEquals(
            2,
            pagerDragSettlePage(
                currentPage = 2,
                pageCount = 3,
                offsetFraction = 0f,
                dragVelocity = -2_000f,
                flingThresholdPx = 1_000f,
            ),
        )
    }

    @Test
    fun emptyPagerDegradesToZero() {
        assertEquals(
            0,
            pagerDragSettlePage(
                currentPage = 4,
                pageCount = 0,
                offsetFraction = 0.4f,
                dragVelocity = -2_000f,
                flingThresholdPx = 1_000f,
            ),
        )
    }
}
