// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.icons.icon.OxygenIcons
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Icon
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.navigation.NavigationRail
import io.oxygenx.ui.navigation.NavigationRailItem

private val RailItems = listOf("文件", "图库", "设置")

fun LazyListScope.navigationRailSection() {
    item(key = "cmp-navigation-rail") {
        SmallTitle(text = "NavigationRail / 侧边导航条", specId = "CMP-NAVIGATION-RAIL")
        DemoCard {
            NavigationRailVariants()
        }
    }
}

@Composable
private fun NavigationRailVariants() {
    val colors = OxygenTheme.colorScheme
    var selected by remember { mutableIntStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
        BasicText(
            text = "侧边窄栏（80dp）：图标在上、文案在下，选中只换内容色不铺底（与底栏同一套语义）；" +
                "用于平板 / 桌面，header 槽可以放悬浮按钮",
            modifier = Modifier.fillMaxWidth(),
            style = OxygenTheme.typography.caption.copy(color = colors.onSurfaceVariant),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(OxygenTheme.spacing.appBarCollapseDistance),
        ) {
            NavigationRail {
                NavigationRailItem(
                    selected = selected == 0,
                    onClick = { selected = 0 },
                    icon = { Icon(imageVector = OxygenIcons.Info, contentDescription = null) },
                    label = { Text("文件", maxLines = 1) },
                )
                NavigationRailItem(
                    selected = selected == 1,
                    onClick = { selected = 1 },
                    icon = { Icon(imageVector = OxygenIcons.Grid, contentDescription = null) },
                    label = { Text("图库", maxLines = 1) },
                )
                NavigationRailItem(
                    selected = selected == 2,
                    onClick = { selected = 2 },
                    icon = { Icon(imageVector = OxygenIcons.Settings, contentDescription = null) },
                    label = { Text("设置", maxLines = 1) },
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(colors.surfaceContainer)
                    .padding(OxygenTheme.spacing.cardPadding),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = "当前页：${RailItems[selected]}",
                    style = OxygenTheme.typography.bodyMedium.copy(color = colors.onSurface),
                )
            }
        }
    }
}
