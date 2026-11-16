// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.android

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.oxygenx.catalog.CatalogApp
import io.oxygenx.core.LocalOxygenCapabilities
import io.oxygenx.core.rememberPlatformCapabilities

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // catalog 自己决定明暗（默认跟随系统），这里只镜像它解析出的结果：
            // 初值先按系统 uiMode 取值，避免第一帧系统栏样式与实际主题相反。
            val systemDark = (
                resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK
                ) == Configuration.UI_MODE_NIGHT_YES
            var darkTheme by remember { mutableStateOf(systemDark) }
            // targetSdk 36 下系统会强制边到边。这里按应用内主题重新应用系统栏样式：
            // 浅色主题用浅底深色图标，深色主题用深底浅色图标，透明 scrim 让内容延伸到系统栏之后。
            // 必须走 enableEdgeToEdge(SystemBarStyle)，WindowCompat 的设置会被它覆盖。
            LaunchedEffect(darkTheme) {
                val style = if (darkTheme) {
                    SystemBarStyle.dark(Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                }
                enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
            }
            CompositionLocalProvider(
                LocalOxygenCapabilities provides rememberPlatformCapabilities(),
            ) {
                CatalogApp(onDarkThemeChange = { darkTheme = it })
            }
        }
    }
}
