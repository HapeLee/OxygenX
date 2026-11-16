// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.SettingsDivider
import io.oxygenx.catalog.ui.SettingsRow
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Card
import io.oxygenx.ui.basic.CardDefaults

fun LazyListScope.cardSection() {
    item(key = "cmp-card") {
        SmallTitle(text = "Card / 卡片", specId = "CMP-CARD")
        DemoCard {
            CardVariants()
        }
    }
}

@Composable
private fun CardVariants() {
    val colors = OxygenTheme.colorScheme
    var clicks by remember { mutableIntStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
        Card(modifier = Modifier.fillMaxWidth()) {
            BasicText(
                text = "基础卡片：圆角来自 shape.corner.card，细边框取顶栏分割线同款" +
                    "（color.outline.variant + size.divider.thickness），无投影。",
                style = OxygenTheme.typography.bodyMedium.copy(color = colors.onSurface),
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = { clicks++ },
        ) {
            BasicText(
                text = "可点击卡片，点击次数：$clicks",
                style = OxygenTheme.typography.bodyMedium.copy(color = colors.onSurface),
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(),
        ) {
            SettingsRow(title = "通栏列表卡片", subtitle = "内容内边距交给行自行控制")
            SettingsDivider()
            SettingsRow(title = "行尾文本", trailingText = "示例")
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,
            border = null,
            colors = CardDefaults.colors(containerColor = colors.surfaceContainerHighest),
        ) {
            BasicText(
                text = "方角无描边卡片：shape 传 RectangleShape、border 传 null，" +
                    "底色可直接覆盖；层级不再靠那圈线。",
                style = OxygenTheme.typography.bodyMedium.copy(color = colors.onSurface),
            )
        }
    }
}
