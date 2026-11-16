// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.catalog.ui.catalogPageSpacing
import io.oxygenx.icons.icon.OxygenIcons
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Button
import io.oxygenx.ui.basic.Icon
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.layout.Scaffold
import io.oxygenx.ui.navigation.PagerTopBar
import io.oxygenx.ui.navigation.TopAppBarAction
import io.oxygenx.ui.navigation.rememberTopAppBarState

private val PagerTitles = listOf("照片", "相册", "发现")
private val PagerSubtitles = listOf("共 24 项 · 已同步", "5 本相册", "智能分类")

private val NoOp: () -> Unit = {}

/**
 * 横划顶栏演示页：屏幕自己的顶栏就是 [PagerTopBar]，下面是三页横划容器。
 *
 * 复刻一加图库首页：顶部三段指示器跟着横划切换，大标题随手指横移、划到一半整块淡出；
 * 右侧操作区固定不动。页面没有返回键（图库是顶层页面），用系统返回手势回到索引页。
 */
@Composable
fun PagerTopBarDemoScreen(onBack: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { PagerTitles.size })
    // 与大顶栏同一套滚动联动：上滑收拢成 98dp 的窄栏，下滑到顶再展开。
    val topBarState = rememberTopAppBarState()
    Scaffold(
        topBarState = topBarState,
        topBar = {
            PagerTopBar(
                pagerState = pagerState,
                title = { page -> Text(PagerTitles[page], maxLines = 1) },
                subtitle = { page ->
                    Text(PagerSubtitles[page], maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                state = topBarState,
                // 操作区按页重组：照片页「搜索 / 设置」、相册页「新建相册 / 搜索」、发现页只有「设置」。
                actions = { page ->
                    when (page) {
                        0 -> {
                            TopAppBarAction(onClick = NoOp) {
                                Icon(OxygenIcons.Search, "搜索")
                            }
                            TopAppBarAction(onClick = NoOp) {
                                Icon(OxygenIcons.Settings, "设置")
                            }
                        }

                        1 -> {
                            TopAppBarAction(onClick = NoOp) {
                                Icon(OxygenIcons.Add, "新建相册")
                            }
                            TopAppBarAction(onClick = NoOp) {
                                Icon(OxygenIcons.Search, "搜索")
                            }
                        }

                        else -> TopAppBarAction(onClick = NoOp) {
                            Icon(OxygenIcons.Settings, "设置")
                        }
                    }
                },
            )
        },
    ) { insets ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = insets.calculateBottomPadding()),
        ) { page ->
            when (page) {
                0 -> PhotosPage()
                1 -> AlbumsPage()
                else -> DiscoverPage(onBack = onBack)
            }
        }
    }
}

/** 第 1 页「照片」：按日期分组的方格占位图。 */
@Composable
private fun PhotosPage() {
    val colors = OxygenTheme.colorScheme
    val spacing = OxygenTheme.spacing
    val page = catalogPageSpacing()
    val gridState = rememberLazyGridState()
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        state = gridState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = page.contentPadding(),
        horizontalArrangement = Arrangement.spacedBy(spacing.gapXs),
        verticalArrangement = Arrangement.spacedBy(spacing.gapXs),
    ) {
        items(count = 24) { index ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(
                        color = colors.surfaceDisabled,
                        shape = RoundedCornerShape(spacing.gapSmall),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = "${index + 1}",
                    style = OxygenTheme.typography.caption.copy(color = colors.onSurfaceDisabled),
                )
            }
        }
    }
}

/** 第 2 页「相册」：两列相册卡片 + 名称与条目数。 */
@Composable
private fun AlbumsPage() {
    val colors = OxygenTheme.colorScheme
    val spacing = OxygenTheme.spacing
    val page = catalogPageSpacing()
    val albums = listOf(
        "所有照片及视频" to "24 项",
        "屏幕截图" to "8 项",
        "相机" to "1 项",
        "Pictures" to "15 项",
        "下载" to "2 项",
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = page.contentPadding(),
        verticalArrangement = Arrangement.spacedBy(spacing.gapLarge),
    ) {
        item(key = "albums-grid") {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.gapLarge)) {
                albums.chunked(2).forEach { rowAlbums ->
                    Row(horizontalArrangement = Arrangement.spacedBy(spacing.gapSmall)) {
                        rowAlbums.forEach { (name, count) ->
                            Column(modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .background(
                                            color = colors.surfaceContainer,
                                            shape = OxygenTheme.shapes.cornerCard,
                                        ),
                                )
                                BasicText(
                                    text = name,
                                    style = OxygenTheme.typography.listItemTitle.copy(
                                        color = colors.onSurface,
                                    ),
                                )
                                BasicText(
                                    text = count,
                                    style = OxygenTheme.typography.caption.copy(
                                        color = colors.onSurfaceVariant,
                                    ),
                                )
                            }
                        }
                        if (rowAlbums.size == 1) {
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

/** 第 3 页「发现」：智能分类行 + 组件说明。 */
@Composable
private fun DiscoverPage(onBack: () -> Unit) {
    val colors = OxygenTheme.colorScheme
    val spacing = OxygenTheme.spacing
    val page = catalogPageSpacing()
    val rows = listOf(
        "人物" to "按人脸自动聚类",
        "故事" to "照片足够多时自动生成",
        "地点" to "按拍摄位置归类",
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = page.contentPadding(),
        verticalArrangement = Arrangement.spacedBy(page.blockGap),
    ) {
        item(key = "pager-appbar-spec") {
            SmallTitle(text = "PagerTopBar / 横划顶栏", specId = "CMP-APPBAR-PAGER")
        }
        item(key = "pager-appbar-rows") {
            DemoCard {
                rows.forEach { (title, summary) ->
                    Column {
                        BasicText(
                            text = title,
                            style = OxygenTheme.typography.listItemTitle.copy(
                                color = colors.onSurface,
                            ),
                        )
                        BasicText(
                            text = summary,
                            style = OxygenTheme.typography.caption.copy(
                                color = colors.onSurfaceVariant,
                            ),
                        )
                    }
                }
            }
        }
        item(key = "pager-appbar-note") {
            DemoCard {
                BasicText(
                    text = "指示器段数取标题数量，当前页那段用主色、其余用禁用色；大标题随" +
                        "横划按页宽 1:1 平移并在过半时淡出。顶栏本身也能横划——手指落在标题区" +
                        "拖动就能换页，松手按「过半 / 甩动」吸附；标题不吃左右内边距，滑出时" +
                        "一直贴到屏幕边缘才被栏体裁掉。右上角操作区按页切换，并跟标题一样按页宽" +
                        "滑入滑出、过半淡出（照片页搜索 / 设置、相册页新建相册 / 搜索、发现页只有" +
                        "设置）。本页没有返回键（图库是顶层页面），用系统返回手势或下面的按钮回索引页。",
                    style = OxygenTheme.typography.bodyMedium.copy(color = colors.onSurfaceVariant),
                )
                Button(onClick = onBack) { Text("返回索引页", maxLines = 1) }
            }
        }
        item(key = "pager-appbar-gap") {
            Box(modifier = Modifier.padding(bottom = spacing.gapLarge))
        }
    }
}
