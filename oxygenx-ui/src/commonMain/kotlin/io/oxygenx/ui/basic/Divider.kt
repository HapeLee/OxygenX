// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import io.oxygenx.theme.OxygenTheme

/**
 * 水平分割线：默认取 `size.divider.thickness` 与 `color.outline.variant`。
 *
 * 命名对齐 M3 的 `HorizontalDivider` / `VerticalDivider`（`Divider` 在 M3 1.2 后已拆分），
 * 但厚度走本项目令牌，不取 M3 的 1dp 固定值。
 *
 * - Spec ID: `CMP-DIVIDER`
 * - Status: `draft`
 *
 * @param modifier 外部 Modifier。
 * @param thickness 线宽，默认 `size.divider.thickness`。
 * @param color 颜色，默认 `color.outline.variant`。
 */
@Composable
fun HorizontalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = OxygenTheme.spacing.dividerThickness,
    color: Color = OxygenTheme.colorScheme.outlineVariant,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
            .background(color),
    )
}

/**
 * 垂直分割线：参数含义同 [HorizontalDivider]，高度由外部通过 `fillMaxHeight()` 或固定值决定。
 */
@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = OxygenTheme.spacing.dividerThickness,
    color: Color = OxygenTheme.colorScheme.outlineVariant,
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(thickness)
            .background(color),
    )
}
