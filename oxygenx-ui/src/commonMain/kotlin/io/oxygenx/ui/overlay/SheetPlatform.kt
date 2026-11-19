// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.overlay

import androidx.compose.runtime.Composable

/**
 * 是否让平台窗口自己处理返回键。
 *
 * Android 上是 `false`：返回键交给 [SheetPredictiveBackHandler]，这样面板能跟着手势回退；
 * 其余平台是 `true`，让平台直接关掉窗口（桌面端的 Esc 走这条）。
 */
internal expect val SheetDismissOnBackPress: Boolean

/**
 * 把平台 Dialog 窗口调成「通栏 + 无自带遮罩」。
 *
 * - 遮罩：面板的暗层是自绘的——透明度要跟着面板位置连续变化，平台那层是常数，
 *   两层叠起来会变成 ~54% 黑，所以要把主题里的 `backgroundDimAmount` 归零；
 * - 通栏：`DialogProperties` 在 common 里没有 `decorFitsSystemWindows`，
 *   窗口默认会被系统栏内缩，遮罩盖不到状态栏，所以要在窗口上关掉它。
 *
 * 只有 Android 能改窗口属性，其余平台是 no-op。
 */
@Composable
internal expect fun ConfigureDialogWindow()

/**
 * 预测式返回：手势进行中持续回调进度（0..1），取消回调 [onCancel]，确认返回回调 [onBack]。
 *
 * 只有 Android（14+ 且宿主开启 `android:enableOnBackInvokedCallback="true"`）会给连续进度；
 * 其他平台是 no-op，返回由 [SheetDismissOnBackPress] 那条路处理。
 */
@Composable
internal expect fun SheetPredictiveBackHandler(
    enabled: Boolean,
    onProgress: (Float) -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit,
)
