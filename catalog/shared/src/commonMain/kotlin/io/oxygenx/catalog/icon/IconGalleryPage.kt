// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.icon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.catalog.ui.catalogPageSpacing
import io.oxygenx.icons.material.MaterialSymbols
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Icon

/**
 * 一行放几个图标：符号名最长 23 字符（`check_box_outline_blank`），三列时每格约 105dp，
 * caption 字号下两行正好放得下，四列会普遍截断成省略号。
 */
private const val ICON_GRID_COLUMNS = 3

/** 按首字母分组后的图标；[letter] 是大写首字母。 */
private class IconLetterGroup(
    val letter: String,
    val icons: List<ImageVector>,
)

/**
 * 按官方符号名首字母分组，顺序即字母序（`MaterialSymbols.All` 本身按属性名排好）。
 *
 * 用首字母而不是语义分类：属性名与官方符号名一一对应，首字母是唯一不依赖人工判断的
 * 切分方式；语义分类会随图标增补不断失衡，还要重新分配。
 */
private val ICON_LETTER_GROUPS: List<IconLetterGroup> by lazy {
    MaterialSymbols.All
        .groupBy { it.name.first().uppercaseChar().toString() }
        .map { (letter, icons) -> IconLetterGroup(letter = letter, icons = icons) }
}

/**
 * 图标库页：索引页第 2 页的内容（顶栏与横划容器由 `CatalogPage` 提供，这里只管网格）。
 *
 * 这一页是 `:oxygenx-icons-material` 的可视验收面：100 个图标全部由渲染接口生成，
 * 只有铺出来才能看出某个 glyph 有没有画错、字号 / 细度是否一致。
 */
@Composable
fun IconGalleryPage() {
    val spacing = OxygenTheme.spacing
    val page = catalogPageSpacing()
    LazyVerticalGrid(
        columns = GridCells.Fixed(ICON_GRID_COLUMNS),
        modifier = Modifier.fillMaxSize(),
        contentPadding = page.contentPadding(),
        horizontalArrangement = Arrangement.spacedBy(spacing.gapSmall),
        verticalArrangement = Arrangement.spacedBy(spacing.gapLarge),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }, key = "icons-material-intro") {
            Column(verticalArrangement = Arrangement.spacedBy(page.blockGap)) {
                SmallTitle(text = "Material Symbols / 图标库", specId = "ICONS-MATERIAL")
                DemoCard {
                    BasicText(
                        text = "常用 Material Symbols ${MaterialSymbols.All.size} 个：Outlined 样式、" +
                            "wght=300，按字体官网「使用人数」取前 100，已排除 OxygenIcons 里的 14 个" +
                            "精选符号。网格里写的就是官方符号名，代码里写成 MaterialSymbols.PascalCase。",
                        style = OxygenTheme.typography.bodyMedium.copy(
                            color = OxygenTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                }
            }
        }
        ICON_LETTER_GROUPS.forEach { group ->
            item(span = { GridItemSpan(maxLineSpan) }, key = "icons-material-letter-${group.letter}") {
                BasicText(
                    text = group.letter,
                    style = OxygenTheme.typography.listItemTitle.copy(
                        color = OxygenTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
            items(items = group.icons, key = { it.name }) { icon ->
                IconTile(icon = icon)
            }
        }
    }
}

/** 单个图标格：图标居中、符号名在下，两行内截断。 */
@Composable
private fun IconTile(icon: ImageVector) {
    val spacing = OxygenTheme.spacing
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.gapXs),
    ) {
        Icon(imageVector = icon, contentDescription = icon.name)
        BasicText(
            text = icon.name,
            style = OxygenTheme.typography.caption.copy(
                color = OxygenTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
