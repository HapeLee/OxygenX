// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

/** 浏览器里没有系统壁纸取色，恒返回 null，由调用方回退到种子色生成。 */
@Composable
actual fun rememberSystemDynamicColorScheme(darkTheme: Boolean): ColorScheme? = null
