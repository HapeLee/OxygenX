// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.settings.SettingsGroupTitle
import io.oxygenx.ui.settings.SettingsItem
import io.oxygenx.ui.settings.SettingsSplitItem
import io.oxygenx.ui.settings.SettingsSwitchItem

fun LazyListScope.settingsItemSection() {
    item(key = "cmp-settings-item") {
        Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
            SettingsSectionTitle(text = "SettingsItem / 点击设置项", specId = "CMP-SETTINGS-ITEM")
            SettingsItemVariants()
        }
    }
}

fun LazyListScope.settingsSwitchItemSection() {
    item(key = "cmp-settings-switch") {
        Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
            SettingsSectionTitle(text = "SettingsSwitchItem / 开关设置项", specId = "CMP-SETTINGS-SWITCH")
            SettingsSwitchItemVariants()
        }
    }
}

fun LazyListScope.settingsSplitItemSection() {
    item(key = "cmp-settings-split") {
        Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
            SettingsSectionTitle(text = "SettingsSplitItem / 点击 + 开关", specId = "CMP-SETTINGS-SPLIT")
            SettingsSplitItemVariants()
        }
    }
}

fun LazyListScope.settingsGroupTitleSection() {
    item(key = "cmp-settings-group-title") {
        Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
            SettingsSectionTitle(text = "SettingsGroupTitle / 分组标题", specId = "CMP-SETTINGS-GROUP-TITLE")
            SettingsGroupTitleVariants()
        }
    }
}

@Composable
private fun SettingsItemVariants() {
    var clicks by remember { mutableIntStateOf(0) }
    Column(modifier = Modifier.fillMaxWidth()) {
        SettingsItem(
            onClick = { clicks++ },
            title = "进入二级页的行",
            summary = "行尾带箭头，点击次数 $clicks",
            showChevron = true,
        )
        SettingsItem(
            onClick = { clicks++ },
            title = "行尾数值",
            value = "$clicks 次",
        )
        SettingsItem(
            onClick = {},
            title = "禁用态",
            summary = "仍保留语义，但不响应点击",
            showChevron = true,
            enabled = false,
        )
        DemoNote("整页通栏列表用同一套行：行与行之间没有分割线，靠分组标题分组。")
    }
}

@Composable
private fun SettingsSwitchItemVariants() {
    var wifi by remember { mutableStateOf(true) }
    var bluetooth by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        SettingsSwitchItem(
            onCheckedChange = { wifi = it },
            title = "整行切换",
            summary = "点文字或开关任意位置都切换",
            checked = wifi,
        )
        SettingsSwitchItem(
            onCheckedChange = { bluetooth = it },
            title = "关闭态",
            checked = bluetooth,
        )
        SettingsSwitchItem(
            onCheckedChange = {},
            title = "禁用态",
            summary = "文字与开关同时变灰",
            checked = true,
            enabled = false,
        )
        DemoNote("开关按压时以拇指为圆心扩散圆形波纹，波纹可以溢出轨道。")
    }
}

@Composable
private fun SettingsSplitItemVariants() {
    var wlan by remember { mutableStateOf(true) }
    var entered by remember { mutableIntStateOf(0) }
    Column(modifier = Modifier.fillMaxWidth()) {
        SettingsSplitItem(
            onClick = { entered++ },
            onCheckedChange = { wlan = it },
            title = "WLAN",
            summary = if (wlan) "已连接 OxygenX-5G" else "已关闭",
            checked = wlan,
        )
        SettingsSplitItem(
            onClick = {},
            onCheckedChange = {},
            title = "禁用态",
            summary = "左右两个点击区都不响应",
            checked = false,
            enabled = false,
        )
        DemoNote("左侧点击进入二级页（已点击 $entered 次），右侧开关只切换开关本体，两者互不触发。")
    }
}

@Composable
private fun SettingsGroupTitleVariants() {
    Column(modifier = Modifier.fillMaxWidth()) {
        SettingsGroupTitle("连接")
        SettingsItem(onClick = {}, title = "WLAN", value = "已连接")
        SettingsItem(onClick = {}, title = "蓝牙", value = "已开启")
        SettingsGroupTitle("设备")
        SettingsItem(onClick = {}, title = "显示与亮度", showChevron = true)
    }
}

/**
 * 通栏页里只有「说明文字」和分块标题需要左右内距，
 * 设置行本身不加，让它延伸到屏幕左右边缘（文字由组件的行内边距定位）。
 */
@Composable
private fun SettingsSectionTitle(text: String, specId: String) {
    SmallTitle(
        text = text,
        specId = specId,
        modifier = Modifier.padding(horizontal = OxygenTheme.spacing.gutter),
    )
}

@Composable
private fun DemoNote(text: String) {
    BasicText(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = OxygenTheme.spacing.gutter),
        style = OxygenTheme.typography.caption.copy(
            color = OxygenTheme.colorScheme.onSurfaceVariant,
        ),
    )
}
