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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.icons.icon.OxygenIcons
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Icon
import io.oxygenx.ui.basic.IconButton
import io.oxygenx.ui.basic.LocalRippleEnabled
import io.oxygenx.ui.feedback.CircularProgressIndicator

/**
 * CMP-ICON-BUTTON 演示：基础形态、禁用态、内容槽（加载圈）与水波纹开关。
 * 只列基础形态，filled / tonal 变体库内不存在（需要底色时用 `Surface` 包一层）。
 */
fun LazyListScope.iconButtonSection() {
    item(key = "cmp-icon-button") {
        SmallTitle(text = "IconButton / 图标按钮", specId = "CMP-ICON-BUTTON")
        DemoCard {
            IconButtonVariants()
        }
    }
}

@Composable
private fun IconButtonVariants() {
    val colors = OxygenTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
        Caption("基础形态：48dp 触摸目标 + 24dp 图标 + 圆形水波纹；无障碍描述跟着 Icon 走")
        Row(horizontalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
            IconButton(onClick = {}) { Icon(OxygenIcons.Search, "搜索") }
            IconButton(onClick = {}) { Icon(OxygenIcons.Add, "添加") }
            IconButton(onClick = {}) { Icon(OxygenIcons.Settings, "设置") }
            IconButton(onClick = {}, enabled = false) { Icon(OxygenIcons.Close, "关闭") }
        }

        Caption("内容槽：放加载圈就是「正在处理」按钮，不需要新组件")
        Row(horizontalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
            IconButton(onClick = {}) {
                CircularProgressIndicator(
                    diameter = OxygenTheme.icons.medium,
                    strokeWidth = OxygenTheme.spacing.dividerThickness * 2,
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    imageVector = OxygenIcons.Check,
                    contentDescription = "完成",
                    tint = colors.primary,
                )
            }
        }

        Caption("LocalRippleEnabled = false：父级已经有涟漪时（可点击卡片 / 整行）避免叠两层")
        CompositionLocalProvider(LocalRippleEnabled provides false) {
            Row(horizontalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall)) {
                IconButton(onClick = {}) { Icon(OxygenIcons.Search, "搜索（无涟漪）") }
                IconButton(onClick = {}) { Icon(OxygenIcons.Settings, "设置（无涟漪）") }
            }
        }
        Caption("内容色可整体覆盖（contentColor 参数），默认启用 onSurface / 禁用 onSurfaceDisabled")
    }
}

@Composable
private fun Caption(text: String) {
    BasicText(
        text = text,
        modifier = Modifier.fillMaxWidth(),
        style = OxygenTheme.typography.caption.copy(color = OxygenTheme.colorScheme.onSurfaceVariant),
    )
}
