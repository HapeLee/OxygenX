// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import io.oxygenx.catalog.scaffold.ScaffoldDemoScreen
import io.oxygenx.catalog.settings.SettingsHomeScreen
import io.oxygenx.catalog.settings.SettingsListScreen
import io.oxygenx.catalog.settings.WlanSettingsScreen
import io.oxygenx.material3.toOxygenColorScheme
import io.oxygenx.theme.OxygenTheme

/** catalog 的路由键：实现 nav3 的 [NavKey]，由返回栈驱动页面切换。 */
sealed interface CatalogScreen : NavKey {
    data object Index : CatalogScreen

    /** 单个组件的独立页面；[id] 是 `CatalogSection.id`（即小写 `Spec ID`）。 */
    data class Section(val id: String) : CatalogScreen

    data object SettingsHome : CatalogScreen

    data object SettingsList : CatalogScreen

    /** WLAN 页面：演示「整行开关」与「点击 + 开关」两种行形态的联动。 */
    data object WlanSettings : CatalogScreen

    data object ScaffoldDemo : CatalogScreen
}

/**
 * Catalog 根节点：只负责主题与页面路由，组件演示全部在
 * [CatalogPage] 的 Section 注册表里。
 *
 * 主题默认**跟随系统**（[CatalogThemeMode.System] + `isSystemInDarkTheme()`），
 * 「关于」页可以固定成浅色 / 深色；实际生效的明暗通过 [onDarkThemeChange] 回传给平台入口，
 * Android 用它重设系统栏样式（`enableEdgeToEdge`）。
 *
 * 路由交给 nav3 的 [NavDisplay]：索引入口是一个页面，每个组件在自己的
 * [CatalogSectionScreen] 里显示（自带 `Scaffold` + 返回式顶栏），
 * 整页预览（设置、Scaffold 能力）也是独立 entry。
 *
 * 返回栈用 `mutableStateListOf` + `remember` 承载：`rememberNavBackStack` 要求
 * `NavKey` 带 `@Serializable`，catalog 只做演示，不需要进程重建后恢复栈。
 * entry 的 `contentKey` 默认是 `"$key:${key::class}"` 字符串，可保存，故
 * `SaveableStateHolder` 装饰器在 Android 上也不会拿到不可保存的键。
 *
 * @param onDarkThemeChange 解析后的明暗主题变化回调；平台入口用它同步系统栏。
 */
@Composable
fun CatalogApp(onDarkThemeChange: (Boolean) -> Unit = {}) {
    val systemDarkTheme = isSystemInDarkTheme()
    var themeMode by remember { mutableStateOf(CatalogThemeMode.System) }
    // 画面上真正生效的明暗：跟随系统时取平台值，选浅色 / 深色时固定。
    val darkTheme = themeMode.isDark(systemDarkTheme)
    // 回传是副作用，不能直接写在组合里；SideEffect 在每次成功组合后执行一次。
    SideEffect { onDarkThemeChange(darkTheme) }

    // WLAN 状态由路由层持有：设置首页的合并项与 WLAN 页面共用同一份。
    var wlanEnabled by remember { mutableStateOf(true) }
    val backStack = remember { mutableStateListOf<CatalogScreen>(CatalogScreen.Index) }
    val push: (CatalogScreen) -> Unit = remember(backStack) { { screen -> backStack.add(screen) } }
    val pop: () -> Unit = {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }
    val sections = remember(push) { catalogSections(onOpen = push) }

    // M3 配色桥接开关：「关于」页切换。打开后整个 catalog 走 Material 3 基线色板
    // 经 `toOxygenColorScheme()` 映射出来的色板，用来肉眼核对 38 个角色有没有漏。
    var m3ColorScheme by remember { mutableStateOf(false) }
    val bridgedColorScheme = remember(m3ColorScheme, darkTheme) {
        if (m3ColorScheme) {
            (if (darkTheme) darkColorScheme() else lightColorScheme()).toOxygenColorScheme()
        } else {
            null
        }
    }

    OxygenTheme(darkTheme = darkTheme, colorScheme = bridgedColorScheme) {
        NavDisplay(
            backStack = backStack,
            onBack = pop,
            entryProvider = entryProvider {
                entry<CatalogScreen.Index> {
                    CatalogPage(
                        themeMode = themeMode,
                        systemDarkTheme = systemDarkTheme,
                        onThemeModeChange = { themeMode = it },
                        m3ColorScheme = m3ColorScheme,
                        onM3ColorSchemeChange = { m3ColorScheme = it },
                        sections = sections,
                        onOpenSection = { push(CatalogScreen.Section(it.id)) },
                    )
                }

                entry<CatalogScreen.Section> { route ->
                    val section = sections.firstOrNull { it.id == route.id }
                    if (section != null) {
                        CatalogSectionScreen(section = section, onBack = pop)
                    }
                }

                entry<CatalogScreen.SettingsHome> {
                    SettingsHomeScreen(
                        onBack = pop,
                        onOpenDisplaySettings = { push(CatalogScreen.SettingsList) },
                        onOpenWlanSettings = { push(CatalogScreen.WlanSettings) },
                        wlanEnabled = wlanEnabled,
                        onWlanEnabledChange = { wlanEnabled = it },
                    )
                }

                entry<CatalogScreen.SettingsList> { SettingsListScreen(onBack = pop) }

                entry<CatalogScreen.WlanSettings> {
                    WlanSettingsScreen(
                        onBack = pop,
                        wlanEnabled = wlanEnabled,
                        onWlanEnabledChange = { wlanEnabled = it },
                    )
                }

                entry<CatalogScreen.ScaffoldDemo> { ScaffoldDemoScreen(onBack = pop) }
            },
        )
    }
}
