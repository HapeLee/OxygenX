// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.icons.icon.OxygenIcons
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Icon
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.overlay.Badge
import io.oxygenx.ui.overlay.BadgedBox

fun LazyListScope.badgeSection() {
    item(key = "cmp-badge") {
        SmallTitle(text = "Badge / 角标", specId = "CMP-BADGE")
        DemoCard {
            BadgeVariants()
        }
    }
}

@Composable
private fun BadgeVariants() {
    val colors = OxygenTheme.colorScheme
    val caption = OxygenTheme.typography.caption.copy(color = colors.onSurfaceVariant)
    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
        BasicText(
            text = "BadgedBox 把角标压在内容右上角：内容尺寸不变，角标可溢出边界；" +
                "角标本身不进语义树（数量应由宿主的 label 承担）",
            modifier = Modifier.fillMaxWidth(),
            style = caption,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapLarge),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BadgedBox(badge = { Badge() }) {
                Icon(imageVector = OxygenIcons.Search, contentDescription = "搜索")
            }
            BadgedBox(badge = { Badge { Text("3", maxLines = 1) } }) {
                Icon(imageVector = OxygenIcons.Settings, contentDescription = "设置")
            }
            BadgedBox(badge = { Badge { Text("99+", maxLines = 1) } }) {
                Icon(imageVector = OxygenIcons.Info, contentDescription = "通知")
            }
        }
        BasicText(
            text = "独立角标：不给内容时是 6dp 小圆点，给内容时是 16dp 高、随文字撑宽的胶囊",
            modifier = Modifier.fillMaxWidth(),
            style = caption,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Badge()
            Badge { Text("1", maxLines = 1) }
            Badge { Text("12", maxLines = 1) }
            Badge(
                containerColor = colors.error,
                contentColor = colors.onPrimary,
            ) {
                Text("!", maxLines = 1)
            }
        }
    }
}
