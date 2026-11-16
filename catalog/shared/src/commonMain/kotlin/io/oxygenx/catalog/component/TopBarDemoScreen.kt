// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.SettingsRow
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.catalog.ui.catalogPageSpacing
import io.oxygenx.icons.icon.OxygenIcons
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Button
import io.oxygenx.ui.basic.ButtonDefaults
import io.oxygenx.ui.basic.Icon
import io.oxygenx.ui.basic.Switch
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.layout.Scaffold
import io.oxygenx.ui.navigation.LargeTopAppBar
import io.oxygenx.ui.navigation.TopAppBar
import io.oxygenx.ui.navigation.TopAppBarAction
import io.oxygenx.ui.navigation.rememberTopAppBarState

private val NoOp: () -> Unit = {}

/**
 * 顶栏演示页：屏幕自己的顶栏就是被演示的组件，不再在页面里嵌一个顶栏预览。
 *
 * - 「标准顶栏 / 大顶栏」两个入口直接换掉页面的顶栏。
 * - 「返回键」「操作区」开关控制当前顶栏的显隐；关掉返回键后靠系统返回手势离开。
 * - 下面的列表就是滚动源，大顶栏随页面滚动真实收拢 / 展开。
 */
@Composable
fun TopBarDemoScreen(
    onBack: () -> Unit,
    onOpenSettingsHome: () -> Unit,
    onOpenDisplaySettings: () -> Unit,
) {
    val spacing = OxygenTheme.spacing
    var large by remember { mutableStateOf(false) }
    var navigation by remember { mutableStateOf(true) }
    var actions by remember { mutableStateOf(true) }
    var hideSubtitle by remember { mutableStateOf(true) }
    var showSubtitle by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()
    // 切换入口时换一份新的收拢状态，回到展开态；列表同时回到顶部。
    val topBarState = key(large) { rememberTopAppBarState() }
    LaunchedEffect(large) { listState.scrollToItem(0) }

    Scaffold(
        topBar = {
            if (large) {
                LargeTopAppBar(
                    title = { Text("TopBar / 顶栏", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    subtitle = { Text("大顶栏 · 向上滚动收拢成单行", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    showSubtitle = showSubtitle,
                    hideSubtitleOnCollapse = hideSubtitle,
                    onNavigateBack = if (navigation) onBack else null,
                    navigationContentDescription = if (navigation) "返回" else null,
                    state = topBarState,
                    actions = { if (actions) SettingsAction() },
                )
            } else {
                TopAppBar(
                    title = { Text("TopBar / 顶栏", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    subtitle = { Text("标准顶栏 · 标题与副标题整体居中", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    showSubtitle = showSubtitle,
                    onNavigateBack = if (navigation) onBack else null,
                    navigationContentDescription = if (navigation) "返回" else null,
                    actions = { if (actions) SettingsAction() },
                )
            }
        },
        topBarState = if (large) topBarState else null,
    ) { insets ->
        val page = catalogPageSpacing()
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = page.contentPadding(bottomInset = insets.calculateBottomPadding()),
            verticalArrangement = Arrangement.spacedBy(page.blockGap),
        ) {
            item(key = "cmp-appbar-spec") {
                SmallTitle(
                    text = if (large) "LargeTopBar / 大顶栏" else "TopBar / 顶栏",
                    specId = if (large) "CMP-APPBAR-LARGE" else "CMP-APPBAR",
                )
            }
            item(key = "cmp-appbar-options") {
                DemoCard {
                    Row(horizontalArrangement = Arrangement.spacedBy(spacing.gapSmall)) {
                        SegmentButton(
                            text = "标准顶栏",
                            selected = !large,
                            onClick = { large = false },
                        )
                        SegmentButton(
                            text = "大顶栏",
                            selected = large,
                            onClick = { large = true },
                        )
                    }
                    SettingsRow(
                        title = "返回键",
                        subtitle = if (large) {
                            "展开态顶部是 74dp 标准栏头；关掉后标题回到 24dp 起"
                        } else {
                            "关掉后标题从 20dp 内边距起始"
                        },
                        trailing = {
                            Switch(
                                checked = navigation,
                                onCheckedChange = { navigation = it },
                            )
                        },
                    )
                    SettingsRow(
                        title = "操作区",
                        subtitle = "展开态贴右下，收拢后贴右中",
                        trailing = {
                            Switch(
                                checked = actions,
                                onCheckedChange = { actions = it },
                            )
                        },
                    )
                    SettingsRow(
                        title = "收拢隐藏副标题",
                        subtitle = if (hideSubtitle) {
                            "收拢前半程淡出，标题单独在窄栏里居中"
                        } else {
                            "副标题保留，与标题作为一整块在窄栏里居中"
                        },
                        trailing = {
                            Switch(
                                checked = hideSubtitle,
                                onCheckedChange = { hideSubtitle = it },
                            )
                        },
                    )
                    SettingsRow(
                        title = "副标题",
                        subtitle = "副标题显隐（标准 / 大顶栏都生效）；收拢时的行为由上一项控制",
                        trailing = {
                            Switch(
                                checked = showSubtitle,
                                onCheckedChange = { showSubtitle = it },
                            )
                        },
                    )
                    BasicText(
                        text = if (large) {
                            "下面的列表就是滚动源：向上滚 171dp 收拢成单行，滚回顶部再展开。"
                        } else {
                            "标准顶栏不随滚动变化；切到「大顶栏」看收拢效果。"
                        },
                        modifier = Modifier.fillMaxWidth(),
                        style = OxygenTheme.typography.caption.copy(
                            color = OxygenTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(spacing.gapSmall)) {
                        Button(onClick = onOpenDisplaySettings) { Text("标准顶栏页", maxLines = 1) }
                        Button(onClick = onOpenSettingsHome) { Text("大顶栏页", maxLines = 1) }
                    }
                }
            }
            items(count = 12) { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(spacing.listRowMinHeight),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BasicText(
                        text = "内容行 ${index + 1}",
                        style = OxygenTheme.typography.bodyMedium.copy(
                            color = OxygenTheme.colorScheme.onSurface,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsAction() {
    TopAppBarAction(onClick = NoOp) {
        Icon(imageVector = OxygenIcons.Settings, contentDescription = "设置")
    }
}

@Composable
private fun SegmentButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = OxygenTheme.colorScheme
    Button(
        onClick = onClick,
        colors = if (selected) {
            ButtonDefaults.colors()
        } else {
            ButtonDefaults.colors(
                containerColor = colors.surface,
                contentColor = colors.onSurface,
            )
        },
    ) {
        Text(text = text, maxLines = 1)
    }
}
