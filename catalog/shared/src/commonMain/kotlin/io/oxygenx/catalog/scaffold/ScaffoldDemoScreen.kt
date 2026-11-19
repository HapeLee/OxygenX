// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.scaffold

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.catalogPageSpacing
import io.oxygenx.icons.icon.OxygenIcons
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Button
import io.oxygenx.ui.basic.FloatingActionButton
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.layout.Scaffold
import io.oxygenx.ui.navigation.NavigationBar
import io.oxygenx.ui.navigation.NavigationBarItem
import io.oxygenx.ui.navigation.TopAppBar
import io.oxygenx.ui.overlay.SnackbarDuration
import io.oxygenx.ui.overlay.SnackbarHost
import io.oxygenx.ui.overlay.SnackbarHostState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Scaffold 能力验证页：顶栏 + 底栏 + 悬浮按钮 + Snackbar 宿主同时存在，
 * 用来确认各插槽的 insets 分配与叠放顺序（`CMP-SCAFFOLD`）。
 */
@Composable
fun ScaffoldDemoScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var created by remember { mutableIntStateOf(0) }
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var snackbarJob by remember { mutableStateOf<Job?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scaffold 能力", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                onNavigateBack = onBack,
                navigationContentDescription = "返回",
            )
        },
        bottomBar = {
            NavigationBar(
                items = demoTabs,
                selectedIndex = selectedTab,
                onSelect = { selectedTab = it },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    created += 1
                    snackbarJob?.cancel()
                    snackbarJob = scope.launch {
                        snackbarState.showSnackbar(
                            message = "已新建第 $created 项",
                            withDismissAction = true,
                            duration = SnackbarDuration.Indefinite,
                        )
                    }
                },
                icon = OxygenIcons.Add,
                contentDescription = "新建",
            )
        },
        snackbarHost = { SnackbarHost(snackbarState) },
    ) { insets ->
        val page = catalogPageSpacing()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = page.contentPadding(bottomInset = insets.calculateBottomPadding()),
            verticalArrangement = Arrangement.spacedBy(page.blockGap),
        ) {
            item(key = "scaffold-slots") {
                DemoCard {
                    BasicText(
                        text = "顶栏、底栏、悬浮按钮与 Snackbar 都由调用方组合，Scaffold 只负责" +
                            "尺寸与 insets：底栏自己承担底部安全区，内容不再重复让位。",
                        style = OxygenTheme.typography.bodyMedium.copy(
                            color = OxygenTheme.colorScheme.onSurface,
                        ),
                    )
                }
            }
            item(key = "snackbar-cancellation") {
                DemoCard {
                    Text("先点新建，再取消调用协程；消息应立即消失。再次新建仍应正常显示。")
                    Button(onClick = { snackbarJob?.cancel() }) { Text("取消 Snackbar 协程") }
                }
            }
            item(key = "scaffold-tab-content") {
                DemoCard {
                    BasicText(
                        text = "当前分栏：${demoTabs[selectedTab].label}",
                        style = OxygenTheme.typography.appbarTitle.copy(
                            color = OxygenTheme.colorScheme.onSurface,
                        ),
                    )
                    BasicText(
                        text = "切换底栏分栏，再点右下角「新建」看 Snackbar 与悬浮按钮的堆叠顺序。",
                        style = OxygenTheme.typography.bodyMedium.copy(
                            color = OxygenTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                }
            }
        }
    }
}

/** 底栏用的是公开组件 `NavigationBar`（`CMP-NAVIGATION-BAR`），这里只提供分栏数据。 */
private val demoTabs = listOf(
    NavigationBarItem(label = "概览", icon = OxygenIcons.Clock),
    NavigationBarItem(label = "组件", icon = OxygenIcons.Grid),
    NavigationBarItem(label = "关于", icon = OxygenIcons.Info),
)
