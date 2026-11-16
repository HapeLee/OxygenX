// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.icons.icon.OxygenIcons
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Icon
import io.oxygenx.ui.basic.IconButton
import io.oxygenx.ui.basic.SearchBar
import io.oxygenx.ui.basic.Text

fun LazyListScope.searchBarSection() {
    item(key = "cmp-search-bar") {
        SmallTitle(text = "SearchBar / 搜索栏", specId = "CMP-SEARCH-BAR")
        DemoCard {
            SearchBarVariants()
        }
    }
}

@Composable
private fun SearchBarVariants() {
    val colors = OxygenTheme.colorScheme
    var query by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf("—") }
    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
        BasicText(
            text = "药丸容器（56dp、surfaceContainerHighest）：左侧搜索图标、右侧清空槽，" +
                "键盘搜索键触发 onSearch；不持有状态",
            modifier = Modifier.fillMaxWidth(),
            style = OxygenTheme.typography.caption.copy(color = colors.onSurfaceVariant),
        )
        SearchBar(
            value = query,
            onValueChange = { query = it },
            onSearch = { submitted = query.ifEmpty { "（空）" } },
            trailingIcon = if (query.isNotEmpty()) {
                {
                    IconButton(onClick = { query = "" }) {
                        Icon(imageVector = OxygenIcons.Close, contentDescription = "清空")
                    }
                }
            } else {
                null
            },
        )
        Text("上次搜索：$submitted", maxLines = 1)
        BasicText(
            text = "禁用态",
            modifier = Modifier.fillMaxWidth(),
            style = OxygenTheme.typography.caption.copy(color = colors.onSurfaceVariant),
        )
        SearchBar(
            value = "",
            onValueChange = {},
            enabled = false,
            placeholder = "搜索不可用",
        )
    }
}
