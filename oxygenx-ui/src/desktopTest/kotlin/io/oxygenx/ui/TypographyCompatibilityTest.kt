// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui

import androidx.compose.ui.text.TextStyle
import io.oxygenx.theme.OxygenTypography
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TypographyCompatibilityTest {
    @Test
    fun legacyConstructorCopyAndDefaultDispatcherRemainCallable() {
        val original = OxygenTypography()
        val type = OxygenTypography::class.java
        val oldFields = arrayOf(
            original.displayLarge, original.appbarTitle, original.dialogTitle, original.dialogAction,
            original.appBarSubtitle, original.titleMedium, original.listItemTitle, original.clockTime,
            original.inputText, original.bodyLarge, original.bodyMedium, original.labelMedium, original.caption,
        )
        val types = Array(13) { TextStyle::class.java }
        val constructed = type.getConstructor(*types).newInstance(*oldFields)
        assertEquals(original.bodyLarge, constructed.bodyLarge)
        val copied = type.getMethod("copy", *types).invoke(original, *oldFields)
        assertEquals(original, copied)
        val dispatcher = type.declaredMethods.single { it.name == "copy\$default" && it.parameterCount == 16 }
        assertEquals(original, dispatcher.invoke(null, *arrayOf(original, *arrayOfNulls<Any>(13), 8191, null)))
    }

    @Test
    fun newRolesParticipateInEqualityAndLegacyCopyKeepsThem() {
        val original = OxygenTypography()
        val custom = original.copy(extras = original.extras.copy(headlineMedium = original.headlineMedium.copy(color = androidx.compose.ui.graphics.Color.Red)))
        assertNotEquals(original, custom)
        assertEquals(custom.headlineMedium, custom.copy(bodyLarge = custom.bodyLarge).headlineMedium)
    }
}
