// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.material3

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Material3ColorSchemeTest {
    @Test
    fun directRolesKeepTheirValues() {
        // 直连角色必须原值搬运：外部 App 的自定义配色不应该在桥接层被改写。
        val m3 = lightColorScheme()
        val mapped = m3.toOxygenColorScheme()

        assertEquals(m3.primary, mapped.primary)
        assertEquals(m3.primaryContainer, mapped.primaryContainer)
        assertEquals(m3.secondaryContainer, mapped.secondaryContainer)
        assertEquals(m3.surfaceContainerHighest, mapped.surfaceContainerHighest)
        assertEquals(m3.errorContainer, mapped.errorContainer)
        assertEquals(m3.onErrorContainer, mapped.onErrorContainer)
        assertEquals(m3.outlineVariant, mapped.outlineVariant)
        assertEquals(m3.scrim, mapped.scrim)
    }

    @Test
    fun darknessFollowsSurfaceLuminance() {
        // M3 的 ColorScheme 不携带 isDark，桥接层按 surface 亮度推断。
        assertFalse(lightColorScheme().toOxygenColorScheme().isDark)
        assertTrue(darkColorScheme().toOxygenColorScheme().isDark)
    }

    @Test
    fun extendedRolesAreSynthesisedFromOnSurface() {
        val light = lightColorScheme().toOxygenColorScheme()
        val dark = darkColorScheme().toOxygenColorScheme()

        // 主题色之上派生：开关轨道跟随 primary，深浅两档透明度不同。
        assertEquals(light.primary.red, light.switchTrackOn.red)
        // 容差按 Color 的存储精度给：sRGB 色值每个通道只存 8 bit，0.12f 读回来是 31/255。
        assertEquals(0.16f, light.switchTrackOn.alpha, absoluteTolerance = ALPHA_TOLERANCE)
        assertEquals(0.35f, dark.switchTrackOn.alpha, absoluteTolerance = ALPHA_TOLERANCE)
        // 禁用文字 / 未聚焦指示线一律由 onSurface 叠透明度。
        assertEquals(0.38f, light.onSurfaceDisabled.alpha, absoluteTolerance = ALPHA_TOLERANCE)
        assertEquals(0.12f, light.inputIndicatorUnfocused.alpha, absoluteTolerance = ALPHA_TOLERANCE)
        // 单选内点两套主题都是白。
        assertEquals(Color.White, light.controlDot)
        assertEquals(Color.White, dark.controlDot)
    }

    private companion object {
        /** 8 bit 通道的量化步长约 0.0039，容差取两倍留余量。 */
        const val ALPHA_TOLERANCE = 0.008f
    }
}
