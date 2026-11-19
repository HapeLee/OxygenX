// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.material3

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import io.oxygenx.theme.OxygenTypography
import kotlin.test.Test
import kotlin.test.assertEquals

class TypographyInteropTest {
    @Test
    fun allFifteenRolesPreserveCompleteCustomTextStyles() {
        val source = Typography().copy(
            headlineMedium = Typography().headlineMedium.copy(fontSize = 31.sp, fontFamily = FontFamily.Monospace),
            labelSmall = Typography().labelSmall.copy(fontSize = 10.sp, letterSpacing = 2.sp),
        )
        val target = source.toOxygenTypography()
        assertEquals(source.displayLarge, target.displayLarge, "displayLarge")
        assertEquals(source.displayMedium, target.displayMedium, "displayMedium")
        assertEquals(source.displaySmall, target.displaySmall, "displaySmall")
        assertEquals(source.headlineLarge, target.headlineLarge, "headlineLarge")
        assertEquals(source.headlineMedium, target.headlineMedium, "headlineMedium")
        assertEquals(source.headlineSmall, target.headlineSmall, "headlineSmall")
        assertEquals(source.titleLarge, target.titleLarge, "titleLarge")
        assertEquals(source.titleMedium, target.titleMedium, "titleMedium")
        assertEquals(source.titleSmall, target.titleSmall, "titleSmall")
        assertEquals(source.bodyLarge, target.bodyLarge, "bodyLarge")
        assertEquals(source.bodyMedium, target.bodyMedium, "bodyMedium")
        assertEquals(source.bodySmall, target.bodySmall, "bodySmall")
        assertEquals(source.labelLarge, target.labelLarge, "labelLarge")
        assertEquals(source.labelMedium, target.labelMedium, "labelMedium")
        assertEquals(source.labelSmall, target.labelSmall, "labelSmall")
    }

    @Test
    fun defaultScaleMatchesThePinnedMaterialTypeMetrics() {
        val actual = OxygenTypography()
        val styles = listOf(actual.displayLarge, actual.displayMedium, actual.displaySmall, actual.headlineLarge, actual.headlineMedium, actual.headlineSmall, actual.titleLarge, actual.titleMedium, actual.titleSmall, actual.bodyLarge, actual.bodyMedium, actual.bodySmall, actual.labelLarge, actual.labelMedium, actual.labelSmall)
        val expected = listOf(
            listOf(57.0f, 64.0f, 400.0f, -0.2f),
            listOf(45.0f, 52.0f, 400.0f, 0.0f),
            listOf(36.0f, 44.0f, 400.0f, 0.0f),
            listOf(32.0f, 40.0f, 400.0f, 0.0f),
            listOf(28.0f, 36.0f, 400.0f, 0.0f),
            listOf(24.0f, 32.0f, 400.0f, 0.0f),
            listOf(22.0f, 28.0f, 400.0f, 0.0f),
            listOf(16.0f, 24.0f, 500.0f, 0.2f),
            listOf(14.0f, 20.0f, 500.0f, 0.1f),
            listOf(16.0f, 24.0f, 400.0f, 0.5f),
            listOf(14.0f, 20.0f, 400.0f, 0.2f),
            listOf(12.0f, 16.0f, 400.0f, 0.4f),
            listOf(14.0f, 20.0f, 500.0f, 0.1f),
            listOf(12.0f, 16.0f, 500.0f, 0.5f),
            listOf(11.0f, 16.0f, 500.0f, 0.5f),
        )
        styles.zip(expected).forEach { (style, metrics) ->
            assertEquals(metrics[0].sp, style.fontSize)
            assertEquals(metrics[1].sp, style.lineHeight)
            assertEquals(metrics[2].toInt(), style.fontWeight?.weight)
            assertEquals(metrics[3].sp, style.letterSpacing)
        }
    }
}
