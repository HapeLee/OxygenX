// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.icons.icon.OxygenIcons
import io.oxygenx.icons.material.MaterialSymbols
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.ExtendedFloatingActionButton
import io.oxygenx.ui.basic.FabDefaults
import io.oxygenx.ui.basic.FloatingActionButton
import io.oxygenx.ui.basic.LargeFloatingActionButton
import io.oxygenx.ui.basic.SmallFloatingActionButton

fun LazyListScope.fabSection() {
    item(key = "cmp-fab") {
        SmallTitle(text = "FAB / 悬浮按钮", specId = "CMP-FAB")
        DemoCard {
            DefaultFabDemo()
        }
    }
    item(key = "cmp-fab-sizes") {
        SmallTitle(text = "三档尺寸", specId = "CMP-FAB")
        DemoCard {
            FabSizesDemo()
        }
    }
    item(key = "cmp-fab-variants") {
        SmallTitle(text = "tonal 配色 / 禁用 / 扩展型", specId = "CMP-FAB")
        DemoCard {
            FabVariantsDemo()
        }
    }
}

@Composable
private fun DefaultFabDemo() {
    var count by remember { mutableIntStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
        Label("56dp 正圆、强调色底、图标居中；按压水波纹被圆裁住，不溢出圆外")
        Row(
            horizontalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FloatingActionButton(
                onClick = { count += 1 },
                icon = OxygenIcons.Add,
                contentDescription = "新建",
            )
            Label("已点击 $count 次")
        }
    }
}

@Composable
private fun FabSizesDemo() {
    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
        Label("小号 40dp / 常规 56dp / 大号 96dp（大号图标同步放大到 36dp）")
        Row(
            horizontalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapLarge),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SmallFloatingActionButton(
                onClick = {},
                icon = OxygenIcons.Add,
                contentDescription = "新建（小号）",
            )
            FloatingActionButton(
                onClick = {},
                icon = OxygenIcons.Add,
                contentDescription = "新建",
            )
            LargeFloatingActionButton(
                onClick = {},
                icon = OxygenIcons.Add,
                contentDescription = "新建（大号）",
            )
        }
    }
}

@Composable
private fun FabVariantsDemo() {
    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
        Label("tonal：底色改用 secondary 容器色（M3 的 primaryContainer 在本调色板没有对应角色）")
        FloatingActionButton(
            onClick = {},
            icon = MaterialSymbols.Share,
            contentDescription = "分享",
            colors = FabDefaults.tonalColors(),
        )
        Label("禁用：底色与图标都取 disabled 令牌，点击不触发")
        FloatingActionButton(
            onClick = {},
            icon = MaterialSymbols.Delete,
            contentDescription = "删除",
            enabled = false,
        )
        Label("扩展型：图标 + 文案的药丸，高度仍是 56dp")
        ExtendedFloatingActionButton(
            onClick = {},
            icon = MaterialSymbols.Edit,
            text = "编辑",
            contentDescription = "编辑",
        )
    }
}

@Composable
private fun Label(text: String) {
    BasicText(
        text = text,
        style = OxygenTheme.typography.caption.copy(color = OxygenTheme.colorScheme.onSurfaceVariant),
    )
}
