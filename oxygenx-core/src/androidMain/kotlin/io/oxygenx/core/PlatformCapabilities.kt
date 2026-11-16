// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.core

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

/**
 * 在 Android 应用入口注入真实能力；组件只读 [LocalOxygenCapabilities] 并走降级路径。
 */
@Composable
fun rememberPlatformCapabilities(): OxygenCapabilities {
    val view = LocalView.current
    val refreshRate = remember(view) { view.display?.refreshRate ?: 60f }
    return remember(refreshRate) {
        OxygenCapabilities(
            blur = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
            renderEffect = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
            haptics = true,
            highRefreshRate = refreshRate >= 90f,
            sharedElementTransition = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE,
        )
    }
}
