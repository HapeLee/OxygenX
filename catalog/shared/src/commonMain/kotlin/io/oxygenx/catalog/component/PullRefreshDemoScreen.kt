// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

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
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.catalog.ui.catalogPageSpacing
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.feedback.PullToRefreshBox
import io.oxygenx.ui.layout.Scaffold
import io.oxygenx.ui.navigation.TopAppBar
import io.oxygenx.ui.settings.SettingsItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** 模拟一次网络刷新；属于演示脚本，不是动效令牌。 */
private const val SIMULATED_REFRESH_MILLIS = 1200L

/**
 * 下拉刷新整页演示：页面自己的列表就是滚动源，下拉时内容整体跟手下移、圆环从顶部冒出，
 * 超过阈值松手触发一次模拟刷新，刷新结束后追加若干行。
 */
@Composable
fun PullRefreshDemoScreen(onBack: () -> Unit) {
    val spacing = OxygenTheme.spacing
    val page = catalogPageSpacing(fullBleed = true)
    val scope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    var itemCount by remember { mutableIntStateOf(24) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PullToRefresh / 下拉刷新", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                onNavigateBack = onBack,
                navigationContentDescription = "返回",
            )
        },
        containerColor = OxygenTheme.colorScheme.surfaceContainer,
    ) { insets ->
        PullToRefreshBox(
            isRefreshing = refreshing,
            onRefresh = {
                refreshing = true
                scope.launch {
                    delay(SIMULATED_REFRESH_MILLIS)
                    itemCount += 6
                    refreshing = false
                }
            },
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = page.contentPadding(bottomInset = insets.calculateBottomPadding()),
                verticalArrangement = Arrangement.spacedBy(page.blockGap),
            ) {
                item(key = "cmp-pull-refresh") {
                    SmallTitle(
                        text = "PullToRefresh / 下拉刷新",
                        specId = "CMP-PULL-REFRESH",
                        modifier = Modifier.padding(horizontal = spacing.gutter),
                    )
                    BasicText(
                        text = "下拉超过 64dp 松手触发一次刷新；刷新中圆环常驻旋转，结束后列表末尾追加 6 行。" +
                            "当前共 $itemCount 行。",
                        modifier = Modifier.padding(horizontal = spacing.gutter),
                        style = OxygenTheme.typography.caption.copy(
                            color = OxygenTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                }
                items(itemCount) { index ->
                    SettingsItem(
                        onClick = {},
                        title = "列表项 ${index + 1}",
                        summary = "第 ${index + 1} 行，用于验证下拉与列表嵌套滚动的先后顺序",
                        showChevron = true,
                    )
                }
            }
        }
    }
}
