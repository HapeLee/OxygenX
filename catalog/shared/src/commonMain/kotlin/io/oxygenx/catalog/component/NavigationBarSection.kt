// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.icons.icon.OxygenIcons
import io.oxygenx.icons.material.MaterialSymbols
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.navigation.NavigationBar
import io.oxygenx.ui.navigation.NavigationBarDefaults
import io.oxygenx.ui.navigation.NavigationBarItem

private val FileManagerTabs = listOf(
    NavigationBarItem(label = "最近", icon = MaterialSymbols.History),
    NavigationBarItem(label = "文件", icon = MaterialSymbols.Description),
    NavigationBarItem(label = "收藏", icon = MaterialSymbols.Favorite),
    NavigationBarItem(label = "我的", icon = MaterialSymbols.AccountCircle),
)

private val SimpleTabs = listOf(
    NavigationBarItem(label = "概览", icon = MaterialSymbols.Dashboard),
    NavigationBarItem(label = "列表", icon = MaterialSymbols.List),
    NavigationBarItem(label = "设置", icon = OxygenIcons.Settings),
)

fun LazyListScope.navigationBarSection() {
    item(key = "cmp-navigation-bar") {
        SmallTitle(text = "NavigationBar / 底栏导航", specId = "CMP-NAVIGATION-BAR")
        DemoCard {
            FileManagerNavigationBarDemo()
        }
    }
    item(key = "cmp-navigation-bar-simple") {
        SmallTitle(text = "三栏底栏", specId = "CMP-NAVIGATION-BAR")
        DemoCard {
            SimpleNavigationBarDemo()
        }
    }
    item(key = "cmp-navigation-bar-states") {
        SmallTitle(text = "禁用栏 / 配色变体", specId = "CMP-NAVIGATION-BAR")
        DemoCard {
            NavigationBarStatesDemo()
        }
    }
}

/** 文件管理应用的形态：四个一级入口，55dp 等分，选中栏取强调色。 */
@Composable
private fun FileManagerNavigationBarDemo() {
    var selected by remember { mutableIntStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
        Label("文件管理这类应用把一级入口放底栏：四栏等分、图标压字、选中只换内容色；按下时从按点扩散半径等于栏高的大圆，溢出底栏的部分被裁掉")
        NavigationBar(
            items = FileManagerTabs,
            selectedIndex = selected,
            onSelect = { selected = it },
        )
        Label("当前分栏：${FileManagerTabs[selected].label}")
    }
}

@Composable
private fun SimpleNavigationBarDemo() {
    var selected by remember { mutableIntStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
        Label("三栏是下限：栏宽随数量等分，文案单行省略，分割线与顶栏同一条细线")
        NavigationBar(
            items = SimpleTabs,
            selectedIndex = selected,
            onSelect = { selected = it },
        )
        Label("当前分栏：${SimpleTabs[selected].label}")
    }
}

@Composable
private fun NavigationBarStatesDemo() {
    var selected by remember { mutableIntStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
        Label("中间栏 enabled = false：取 disabled 色、不响应点击，其余栏照常切换")
        NavigationBar(
            items = listOf(
                FileManagerTabs[0],
                FileManagerTabs[1].copy(enabled = false),
                FileManagerTabs[2],
            ),
            selectedIndex = selected,
            onSelect = { selected = it },
        )
        Label("选中色换成墨色（color.secondary）：页面上已有强调色主操作时用这一档")
        NavigationBar(
            items = SimpleTabs,
            selectedIndex = selected,
            onSelect = { selected = it },
            colors = NavigationBarDefaults.colors(
                selectedContent = OxygenTheme.colorScheme.secondary,
            ),
        )
        Label("槽位 API：宿主保留自己的导航分派与图标内容")
        NavigationBar {
            SimpleTabs.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = selected == index,
                    onClick = { selected = index },
                    label = { io.oxygenx.ui.basic.Text(item.label) },
                    icon = { io.oxygenx.ui.basic.Icon(item.icon, contentDescription = null) },
                )
            }
        }
    }
}

@Composable
private fun Label(text: String) {
    BasicText(
        text = text,
        style = OxygenTheme.typography.caption.copy(color = OxygenTheme.colorScheme.onSurfaceVariant),
    )
}
