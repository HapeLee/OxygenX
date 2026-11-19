// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.basic

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import io.oxygenx.theme.OxygenTheme

/**
 * 按 `icon.*` 令牌与主题色绘制矢量图标，另支持 Painter / ImageBitmap。
 *
 * foundation 不提供 Material 的 `Icon`，这里用 `Image` + `ColorFilter.tint` 等价实现，
 * 并按 `LocalLayoutDirection` 镜像方向性图标。
 *
 * - Spec ID: `CMP-ICON`
 * - Baseline: `OxygenOS <version> / <device> / <date>`
 * - Reference: `catalog/reference/CMP-ICON/`
 * - Motion: 无
 * - Status: `draft`
 *
 * @param imageVector 图标几何。
 * @param contentDescription 无障碍描述；纯装饰图标传 `null`。
 * @param modifier 外部 Modifier。
 * @param tint 默认继承内容色；显式 `Color.Unspecified` 保留资源原色，不应用颜色滤镜。
 * @param size 绘制尺寸，默认 `icon.size.medium`。
 * @param mirrorInRtl 方向性图标在 RTL 下是否需要镜像。
 */
@Composable
fun Icon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = OxygenTheme.contentColor,
    size: Dp = OxygenTheme.icons.medium,
    mirrorInRtl: Boolean = false,
) {
    Icon(
        painter = rememberVectorPainter(imageVector),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
        size = size,
        mirrorInRtl = mirrorInRtl && !imageVector.autoMirror,
    )
}

/** 位图图标；内容色、不染色、尺寸与 RTL 行为同矢量 [Icon]。 */
@Composable
fun Icon(
    bitmap: ImageBitmap,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = OxygenTheme.contentColor,
    size: Dp = OxygenTheme.icons.medium,
    mirrorInRtl: Boolean = false,
) {
    Icon(
        painter = remember(bitmap) { BitmapPainter(bitmap) },
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
        size = size,
        mirrorInRtl = mirrorInRtl,
    )
}

/** Painter 图标；[tint] 为 Unspecified 时保留原色，默认尺寸仍来自图标令牌。 */
@Composable
fun Icon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = OxygenTheme.contentColor,
    size: Dp = OxygenTheme.icons.medium,
    mirrorInRtl: Boolean = false,
) {
    val mirror = mirrorInRtl && LocalLayoutDirection.current == LayoutDirection.Rtl
    val colorFilter = remember(tint) { if (tint == Color.Unspecified) null else ColorFilter.tint(tint) }
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier
            .size(size)
            .graphicsLayer { scaleX = if (mirror) -1f else 1f },
        contentScale = ContentScale.Fit,
        colorFilter = colorFilter,
    )
}
