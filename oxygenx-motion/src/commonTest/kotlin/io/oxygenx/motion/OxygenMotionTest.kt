// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.motion

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

class OxygenMotionTest {
    @Test
    fun cubicBezierParsesFourControlPoints() {
        val easing = OxygenMotion.cubicBezier("0.2,0.0,0.0,1.0")
        assertTrue(easing.toString().isNotEmpty())
    }

    @Test
    fun cubicBezierRejectsIncompleteCurve() {
        assertFailsWith<IllegalArgumentException> {
            OxygenMotion.cubicBezier("0.2,0.0")
        }
    }

    @Test
    fun defaultsExposeStableSpecInstance() {
        assertSame(OxygenMotionDefaults.switchToggle(), OxygenMotionDefaults.switchToggle())
    }
}
