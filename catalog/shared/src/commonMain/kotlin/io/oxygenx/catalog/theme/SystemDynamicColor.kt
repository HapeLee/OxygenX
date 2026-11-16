// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.dynamicColorScheme

/**
 * 平台动态取色（Material You）：有系统取色就用系统的，没有就返回 null 交给调用方回退。
 *
 * Android 12（API 31）起可以从壁纸取色；desktop / wasm 没有壁纸这个概念，
 * 直接返回 null。返回 null 时用 [fallbackDynamicScheme] 从种子色生成，两边的差异在
 * catalog 的说明文案里写清楚了。
 */
@Composable
expect fun rememberSystemDynamicColorScheme(darkTheme: Boolean): ColorScheme?

/**
 * 没有系统取色时的回退：用种子色跑一遍 Material 3 的动态取色算法。
 *
 * 这里**不是** M3 的基线紫板：同一个种子在深浅两套下会算出完整的一组角色，
 * 换种子就能看到整页换色——catalog 用它来验证「外部 App 自带色板 → Oxygen」这条链路。
 */
fun fallbackDynamicScheme(seedColor: Color, darkTheme: Boolean): ColorScheme = dynamicColorScheme(seedColor = seedColor, isDark = darkTheme)

/** catalog 的默认种子色（与品牌色无关的蓝，方便一眼看出「换了色板」）。 */
val CatalogSeedColor: Color = Color(0xFF0061A4)
