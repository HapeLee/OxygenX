// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import io.oxygenx.catalog.ui.catalogPageSpacing
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.layout.Scaffold
import io.oxygenx.ui.navigation.TopAppBar

/**
 * 单个组件的演示页：自带 `Scaffold` 与返回式标准顶栏，
 * 内容仍由 `CatalogSection.content` 提供（所以每个组件在自己的屏幕里显示，
 * 而不是全部堆在索引页的一张长列表上）。
 *
 * `CatalogSection.screen` 非空时不再套这层外壳：演示对象本身就是页面外壳
 * （例如顶栏演示要让屏幕自己的顶栏就是被演示的组件），由它自己渲染整页。
 */
@Composable
fun CatalogSectionScreen(
    section: CatalogSection,
    onBack: () -> Unit,
) {
    val screen = section.screen
    if (screen != null) {
        screen(onBack)
    } else {
        SectionScaffold(section = section, onBack = onBack)
    }
}

@Composable
private fun SectionScaffold(
    section: CatalogSection,
    onBack: () -> Unit,
) {
    // 页面级边距统一取 catalogPageSpacing：通栏页左右为 0，行内距由组件 token 决定。
    val page = catalogPageSpacing(fullBleed = section.fullBleed)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(section.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                onNavigateBack = onBack,
                navigationContentDescription = "返回",
            )
        },
    ) { insets ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = page.contentPadding(bottomInset = insets.calculateBottomPadding()),
            verticalArrangement = Arrangement.spacedBy(page.blockGap),
        ) {
            section.content.invoke(this)
        }
    }
}
