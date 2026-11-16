// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.basic

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import io.oxygenx.theme.OxygenTheme

/**
 * 按 `icon.*` 令牌与主题色绘制 `oxygenx-icons` 的矢量图标。
 *
 * foundation 不提供 Material 的 `Icon`，这里用 `Image` + `ColorFilter.tint` 等价实现，
 * 并按 `LocalLayoutDirection` 镜像方向性图标。
 *
 * - Spec ID: `CMP-ICON`
 * - Baseline: `OxygenOS <version> / <device> / <date>`
 * - Reference: `catalog/reference/CMP-ICON/`
 * - Status: `draft`
 *
 * @param imageVector 图标几何。
 * @param contentDescription 无障碍描述；纯装饰图标传 `null`。
 * @param modifier 外部 Modifier。
 * @param tint 着色；`Color.Unspecified` 时按内容色解析（`LocalContentColor` → `color.on.surface`）。
 * @param size 绘制尺寸，默认 `icon.size.medium`。
 * @param mirrorInRtl 方向性图标在 RTL 下是否需要镜像。
 */
@Composable
fun Icon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
    size: Dp = OxygenTheme.icons.medium,
    mirrorInRtl: Boolean = false,
) {
    val mirror = mirrorInRtl && LocalLayoutDirection.current == LayoutDirection.Rtl
    Image(
        painter = rememberVectorPainter(imageVector),
        contentDescription = null,
        modifier = modifier
            .size(size)
            .graphicsLayer { if (mirror) scaleX = -1f }
            .semantics {
                if (contentDescription != null) this.contentDescription = contentDescription
            },
        contentScale = ContentScale.Fit,
        colorFilter = ColorFilter.tint(resolveContentColor(tint)),
    )
}
