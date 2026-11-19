// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.overlay

import androidx.compose.runtime.Composable

internal actual val SheetDismissOnBackPress: Boolean = true

@Composable
internal actual fun ConfigureDialogWindow() = Unit

@Composable
internal actual fun SheetPredictiveBackHandler(
    enabled: Boolean,
    onProgress: (Float) -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit,
) = Unit
