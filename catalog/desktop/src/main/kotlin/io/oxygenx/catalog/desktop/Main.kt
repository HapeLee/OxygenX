// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.oxygenx.catalog.CatalogApp

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "OxygenX Catalog",
    ) {
        // 明暗由 CatalogApp 自己解析（默认跟随系统），桌面端没有系统栏需要同步。
        CatalogApp()
    }
}
