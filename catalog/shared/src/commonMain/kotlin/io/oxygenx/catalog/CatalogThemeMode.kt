// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog

/**
 * catalog 的明暗主题来源。
 *
 * 默认 [System]：跟随系统设置，桌面上换系统主题、Android 上切深色模式都会即时反映；
 * [Light] / [Dark] 是固定档，用于「同一屏并排核对明暗两套令牌」这类调试场景，
 * 不随系统变化。
 */
enum class CatalogThemeMode(val title: String) {
    System("跟随系统"),
    Light("浅色"),
    Dark("深色"),
    ;

    /** 解析出实际生效的明暗：[System] 取平台值，其余两档固定。 */
    fun isDark(systemDarkTheme: Boolean): Boolean = when (this) {
        System -> systemDarkTheme
        Light -> false
        Dark -> true
    }
}
