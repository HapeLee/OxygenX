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
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Icon
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.navigation.SecondaryTabRow
import io.oxygenx.ui.navigation.Tab
import io.oxygenx.ui.navigation.TabRow

private val FixedTabs = listOf("推荐", "分类", "收藏", "历史")

private val ScrollableTabs = listOf(
    "全部", "图片", "视频", "音乐", "文档", "压缩包", "安装包", "其他", "最近", "回收站",
)

fun LazyListScope.tabRowSection() {
    item(key = "cmp-tabrow") {
        SmallTitle(text = "TabRow / 页签", specId = "CMP-TABROW")
        DemoCard {
            FixedTabDemo()
        }
    }
    item(key = "cmp-tabrow-scrollable") {
        SmallTitle(text = "ScrollableTabRow / 可滚动页签", specId = "CMP-TABROW")
        DemoCard {
            ScrollableTabDemo()
        }
    }
    item(key = "cmp-tabrow-secondary") {
        SmallTitle(text = "SecondaryTabRow / 二级页签", specId = "CMP-TABROW-SECONDARY")
        DemoCard {
            SecondaryTabDemo()
        }
    }
}

@Composable
private fun FixedTabDemo() {
    var selected by remember { mutableIntStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
        Label("固定模式：页签等分整行宽度，指示器宽度等于页签宽度、相邻首尾相接")
        TabRow {
            FixedTabs.forEachIndexed { index, label ->
                Tab(
                    selected = selected == index,
                    onClick = { selected = index },
                    text = { Text(text = label, maxLines = 1) },
                )
            }
        }
        Label("当前选中：${FixedTabs[selected]}（每页签是独立的 Tab，可自行加图标）")
    }
}

@Composable
private fun ScrollableTabDemo() {
    var selected by remember { mutableIntStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
        Label("可滚动模式：每个页签至少 72dp，超出宽度可横划，选中页签会自动滚进视口")
        TabRow(scrollable = true) {
            ScrollableTabs.forEachIndexed { index, label ->
                Tab(
                    selected = selected == index,
                    onClick = { selected = index },
                    text = { Text(text = label, maxLines = 1) },
                )
            }
        }
        Label("当前选中：${ScrollableTabs[selected]}")
    }
}

@Composable
private fun SecondaryTabDemo() {
    var selected by remember { mutableIntStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
        Label("secondary：指示器与选中文字取 color.secondary（墨色，与横划顶栏选中色同源），与主色按钮同屏时不抢焦点")
        SecondaryTabRow {
            FixedTabs.forEachIndexed { index, label ->
                Tab(
                    selected = selected == index,
                    onClick = { selected = index },
                    // 图标槽：给了图标时图标在上、文案在下（M3 Tab 形态）。
                    icon = if (index == 0) {
                        { Icon(imageVector = OxygenIcons.Grid, contentDescription = null) }
                    } else {
                        null
                    },
                    text = { Text(text = label, maxLines = 1) },
                )
            }
        }
        Label("当前选中：${FixedTabs[selected]}（第 1 个页签演示了 icon 槽）")
    }
}

@Composable
private fun Label(text: String) {
    BasicText(
        text = text,
        style = OxygenTheme.typography.caption.copy(color = OxygenTheme.colorScheme.onSurfaceVariant),
    )
}
