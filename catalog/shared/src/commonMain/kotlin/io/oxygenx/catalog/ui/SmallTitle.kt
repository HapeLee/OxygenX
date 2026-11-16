// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.oxygenx.theme.OxygenTheme

/**
 * Section 标题：显示组件名、`Spec ID` 与 `Status`，`draft` 示例必须在页面上可见。
 */
@Composable
fun SmallTitle(
    text: String,
    specId: String,
    modifier: Modifier = Modifier,
    status: String = "draft",
) {
    val colors = OxygenTheme.colorScheme
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall),
    ) {
        BasicText(
            text = text,
            style = OxygenTheme.typography.titleMedium.copy(color = colors.onSurface),
        )
        BasicText(
            text = "$specId · $status",
            style = OxygenTheme.typography.caption.copy(color = colors.onSurfaceVariant),
        )
    }
}
