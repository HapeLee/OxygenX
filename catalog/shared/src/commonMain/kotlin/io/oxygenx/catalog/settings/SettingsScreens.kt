// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import io.oxygenx.catalog.ui.catalogPageSpacing
import io.oxygenx.icons.icon.OxygenIcons
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Icon
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.layout.Scaffold
import io.oxygenx.ui.navigation.LargeTopAppBar
import io.oxygenx.ui.navigation.TopAppBar
import io.oxygenx.ui.navigation.TopAppBarAction
import io.oxygenx.ui.navigation.rememberTopAppBarState
import io.oxygenx.ui.settings.SettingsGroupTitle
import io.oxygenx.ui.settings.SettingsItem
import io.oxygenx.ui.settings.SettingsSplitItem
import io.oxygenx.ui.settings.SettingsSwitchItem

/**
 * 设置首页：大标题 + 通栏设置项列表（一加设置页的列表形态）。
 *
 * 一加的设置项不是卡片，而是铺满整宽的白底列表，组与组之间靠灰色小标题分隔，
 * 行与行之间没有分割线；WLAN 与蓝牙行是「左侧点击进入 + 右侧开关」的合并项。
 */
@Composable
fun SettingsHomeScreen(
    onBack: () -> Unit,
    onOpenDisplaySettings: () -> Unit,
    onOpenWlanSettings: () -> Unit,
    wlanEnabled: Boolean,
    onWlanEnabledChange: (Boolean) -> Unit,
) {
    val topBarState = rememberTopAppBarState()
    var bluetoothEnabled by remember { mutableStateOf(true) }
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("设置", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                subtitle = { Text("上海, 19:04, 周五, 11/13", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                onNavigateBack = onBack,
                navigationContentDescription = "返回",
                state = topBarState,
                actions = {
                    TopAppBarAction(onClick = {}) {
                        Icon(imageVector = OxygenIcons.Settings, contentDescription = "设置")
                    }
                },
            )
        },
        topBarState = topBarState,
        // 通栏列表整页白底，与一加的设置页一致。
        containerColor = OxygenTheme.colorScheme.surfaceContainer,
    ) { insets ->
        SettingsListContainer(bottomInset = insets.calculateBottomPadding()) {
            item(key = "group-connectivity") { SettingsGroupTitle("连接") }
            item(key = "settings-home-wlan") {
                // 左侧点进 WLAN 页面，右侧开关开关 WLAN，两者互不触发。
                SettingsSplitItem(
                    onClick = onOpenWlanSettings,
                    onCheckedChange = onWlanEnabledChange,
                    title = "WLAN",
                    summary = if (wlanEnabled) "已连接到 OxygenX-5G" else "WLAN 已关闭",
                    checked = wlanEnabled,
                )
            }
            item(key = "settings-home-bluetooth") {
                SettingsSwitchItem(
                    onCheckedChange = { bluetoothEnabled = it },
                    title = "蓝牙",
                    summary = if (bluetoothEnabled) "已开启，1 个设备已连接" else "已关闭",
                    checked = bluetoothEnabled,
                )
            }
            item(key = "settings-home-mobile") {
                SettingsItem(
                    onClick = {},
                    title = "移动网络",
                    summary = "已启用 5G",
                    showChevron = true,
                )
            }

            item(key = "group-device") { SettingsGroupTitle("设备") }
            item(key = "settings-home-display") {
                SettingsItem(
                    onClick = onOpenDisplaySettings,
                    title = "显示与亮度",
                    summary = "自动调节亮度、护眼模式",
                    showChevron = true,
                )
            }
            item(key = "settings-home-sound") {
                SettingsItem(onClick = {}, title = "声音与振动", showChevron = true)
            }
            item(key = "settings-home-battery") {
                SettingsItem(onClick = {}, title = "电池", value = "82%")
            }
            item(key = "settings-home-storage") {
                SettingsItem(onClick = {}, title = "存储", value = "已用 96 GB")
            }

            item(key = "group-security") { SettingsGroupTitle("安全") }
            item(key = "settings-home-privacy") {
                SettingsItem(onClick = {}, title = "隐私与安全", showChevron = true)
            }
            item(key = "settings-home-apps") {
                SettingsItem(onClick = {}, title = "应用管理", showChevron = true)
            }
            item(key = "settings-home-update") {
                SettingsItem(onClick = {}, title = "系统更新", showChevron = true)
            }
            item(key = "settings-home-gesture") {
                SettingsItem(onClick = {}, title = "快捷手势与负一屏", showChevron = true)
            }

            item(key = "group-about") { SettingsGroupTitle("关于") }
            item(key = "settings-home-about") {
                SettingsItem(
                    onClick = {},
                    title = "关于本机",
                    summary = "OxygenX Catalog · Unofficial",
                    showChevron = true,
                )
            }
        }
    }
}

/**
 * WLAN 页面：顶部是整行开关，下面按分组列出网络与二级设置项。
 *
 * 与设置首页共用同一个 `wlanEnabled` 状态，方便对照「合并项右侧开关」
 * 与「整行开关」两种形态。
 */
@Composable
fun WlanSettingsScreen(
    onBack: () -> Unit,
    wlanEnabled: Boolean,
    onWlanEnabledChange: (Boolean) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WLAN", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                onNavigateBack = onBack,
                navigationContentDescription = "返回",
            )
        },
        containerColor = OxygenTheme.colorScheme.surfaceContainer,
    ) { insets ->
        SettingsListContainer(bottomInset = insets.calculateBottomPadding()) {
            item(key = "wlan-master") {
                SettingsSwitchItem(
                    onCheckedChange = onWlanEnabledChange,
                    title = "使用 WLAN",
                    checked = wlanEnabled,
                )
            }
            if (wlanEnabled) {
                item(key = "wlan-group-available") { SettingsGroupTitle("可用网络") }
                item(key = "wlan-connected") {
                    SettingsItem(
                        onClick = {},
                        title = "OxygenX-5G",
                        summary = "已连接",
                        value = "已保存",
                        showChevron = true,
                    )
                }
                item(key = "wlan-guest") {
                    SettingsItem(
                        onClick = {},
                        title = "OxygenX-Guest",
                        summary = "已加密（WPA2）",
                        showChevron = true,
                    )
                }
                item(key = "wlan-add") {
                    SettingsItem(onClick = {}, title = "添加网络", showChevron = true)
                }
            } else {
                item(key = "wlan-off-hint") {
                    SettingsHint("要查看可用网络，请打开 WLAN。")
                }
            }

            item(key = "wlan-group-settings") { SettingsGroupTitle("WLAN 设置") }
            item(key = "wlan-preference") {
                SettingsItem(
                    onClick = {},
                    title = "WLAN 偏好设置",
                    summary = "不自动重新开启 WLAN",
                    showChevron = true,
                )
            }
            item(key = "wlan-saved") {
                SettingsItem(
                    onClick = {},
                    title = "已保存的网络",
                    summary = "1 个网络",
                    showChevron = true,
                )
            }
            item(key = "wlan-usage") {
                SettingsItem(onClick = {}, title = "WLAN 流量用量", showChevron = true)
            }
        }
    }
}

/**
 * 设置列表页：返回式顶栏（带底部分割线）+ 通栏设置行 + 行尾开关。
 */
@Composable
fun SettingsListScreen(onBack: () -> Unit) {
    var autoBrightness by remember { mutableStateOf(true) }
    var eyeComfort by remember { mutableStateOf(false) }
    var alwaysOn by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("显示与亮度", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                onNavigateBack = onBack,
                navigationContentDescription = "返回",
            )
        },
        containerColor = OxygenTheme.colorScheme.surfaceContainer,
    ) { insets ->
        SettingsListContainer(bottomInset = insets.calculateBottomPadding()) {
            item(key = "group-brightness") { SettingsGroupTitle("亮度") }
            item(key = "settings-display-auto") {
                SettingsSwitchItem(
                    onCheckedChange = { autoBrightness = it },
                    title = "自动调节亮度",
                    summary = "根据环境光调整屏幕亮度",
                    checked = autoBrightness,
                )
            }
            item(key = "settings-display-eye") {
                SettingsSwitchItem(
                    onCheckedChange = { eyeComfort = it },
                    title = "护眼模式",
                    summary = "降低屏幕蓝光，缓解视觉疲劳",
                    checked = eyeComfort,
                )
            }

            item(key = "group-screen") { SettingsGroupTitle("屏幕") }
            item(key = "settings-display-refresh") {
                SettingsItem(
                    onClick = {},
                    title = "屏幕刷新率",
                    summary = "智能切换，兼顾流畅与省电",
                    value = "120Hz",
                    showChevron = true,
                )
            }
            item(key = "settings-display-aod") {
                SettingsSwitchItem(
                    onCheckedChange = { alwaysOn = it },
                    title = "息屏显示",
                    summary = "轻触屏幕查看时间与通知",
                    checked = alwaysOn,
                )
            }
        }
    }
}

/** 通栏设置列表容器：白底铺满、左右不留内边距，行自行控制内边距。 */
@Composable
private fun SettingsListContainer(
    bottomInset: Dp,
    content: LazyListScope.() -> Unit,
) {
    val page = catalogPageSpacing(fullBleed = true)
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(OxygenTheme.colorScheme.surfaceContainer),
        contentPadding = page.contentPadding(bottomInset = bottomInset),
        content = content,
    )
}

/** 列表里的纯说明文字，例如「要查看可用网络，请打开 WLAN」。 */
@Composable
private fun SettingsHint(text: String) {
    BasicText(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = OxygenTheme.spacing.listItemInset,
                end = OxygenTheme.spacing.listItemInset,
                top = OxygenTheme.spacing.gapLarge,
            ),
        style = OxygenTheme.typography.bodyMedium.copy(
            color = OxygenTheme.colorScheme.onSurfaceVariant,
        ),
    )
}
