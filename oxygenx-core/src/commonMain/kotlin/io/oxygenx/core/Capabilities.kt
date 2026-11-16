// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.core

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * 平台能力声明。组件不直接判断平台，而是读取本对象并走预定义的降级路径。
 */
@Immutable
data class OxygenCapabilities(
    val blur: Boolean = false,
    val renderEffect: Boolean = false,
    val haptics: Boolean = false,
    val highRefreshRate: Boolean = false,
    val sharedElementTransition: Boolean = false,
)

val LocalOxygenCapabilities = staticCompositionLocalOf { OxygenCapabilities() }
