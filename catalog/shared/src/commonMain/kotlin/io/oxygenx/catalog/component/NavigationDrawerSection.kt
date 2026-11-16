// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.icons.icon.OxygenIcons
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Button
import io.oxygenx.ui.basic.Icon
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.navigation.ModalNavigationDrawer
import io.oxygenx.ui.navigation.NavigationDrawerItem

private val DrawerItems = listOf("文件", "图库", "回收站", "设置")

fun LazyListScope.navigationDrawerSection() {
    item(key = "cmp-navigation-drawer") {
        SmallTitle(text = "ModalNavigationDrawer / 侧边抽屉", specId = "CMP-NAVIGATION-DRAWER")
        DemoCard {
            NavigationDrawerVariants()
        }
    }
}

@Composable
private fun NavigationDrawerVariants() {
    val colors = OxygenTheme.colorScheme
    var open by remember { mutableStateOf(false) }
    var selected by remember { mutableIntStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
        BasicText(
            text = "从起始边滑出的模态抽屉 + 32% 黑遮罩；点遮罩关闭，选中项垫 secondaryContainer 药丸",
            modifier = Modifier.fillMaxWidth(),
            style = OxygenTheme.typography.caption.copy(color = colors.onSurfaceVariant),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(OxygenTheme.spacing.appBarCollapseDistance * 2),
        ) {
            ModalNavigationDrawer(
                open = open,
                onDismissRequest = { open = false },
                drawerContent = {
                    DrawerItems.forEachIndexed { index, label ->
                        NavigationDrawerItem(
                            selected = selected == index,
                            onClick = {
                                selected = index
                                open = false
                            },
                            label = { Text(label, maxLines = 1) },
                            icon = {
                                Icon(
                                    imageVector = OxygenIcons.ChevronRight,
                                    contentDescription = null,
                                )
                            },
                        )
                    }
                },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(OxygenTheme.spacing.cardPadding),
                    verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall),
                ) {
                    Text("当前页：${DrawerItems[selected]}", maxLines = 1)
                    Button(onClick = { open = true }) { Text("打开抽屉", maxLines = 1) }
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        BasicText(
                            text = "（抽屉覆盖在这块区域之上）",
                            style = OxygenTheme.typography.caption.copy(
                                color = colors.onSurfaceVariant,
                            ),
                        )
                    }
                }
            }
        }
    }
}
