// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.overlay

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import kotlin.coroutines.cancellation.CancellationException

internal actual val SheetDismissOnBackPress: Boolean = false

@Composable
internal actual fun ConfigureDialogWindow() {
    val view = LocalView.current
    DisposableEffect(view) {
        // Dialog 的内容挂在平台窗口里，往上找一层就是 DialogWindowProvider。
        val window = (view.parent as? DialogWindowProvider)?.window
        // 记下主题里原本的遮罩强度，退出时还原，别污染宿主自己的 Dialog。
        val previous = window?.attributes?.dimAmount
        window?.let {
            it.setDimAmount(0f)
            WindowCompat.setDecorFitsSystemWindows(it, false)
        }
        onDispose {
            if (window == null) return@onDispose
            if (previous != null) window.setDimAmount(previous)
            WindowCompat.setDecorFitsSystemWindows(window, true)
        }
    }
}

@Composable
internal actual fun SheetPredictiveBackHandler(
    enabled: Boolean,
    onProgress: (Float) -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit,
) {
    PredictiveBackHandler(enabled = enabled) { events ->
        try {
            // 每个事件带一行进比例：面板直接按它跟手位移。
            events.collect { event -> onProgress(event.progress) }
            onBack()
        } catch (cancellation: CancellationException) {
            onCancel()
            throw cancellation
        }
    }
}
