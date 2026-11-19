// Material Symbols (https://fonts.google.com/icons) - Apache License 2.0
// Copyright 2026 Google LLC
// SPDX-License-Identifier: Apache-2.0
//
// Generated: https://fonts.gstatic.com/render/v1/Material+Symbols+Outlined/24dp/fullscreen.kt?var=opsz,wght,FILL,GRAD@24,300,0,0
// Axes: opsz=24, wght=300, FILL=0, GRAD=0 (Outlined style)
// Do not edit by hand; run python tools/material_symbols.py --fetch.

package io.oxygenx.icons.material.generated

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
internal val FullscreenGlyph: ImageVector
  get() {
    if (_FullscreenGlyph != null) {
      return _FullscreenGlyph!!
    }
    _FullscreenGlyph =
      ImageVector.Builder(
          name = "fullscreen",
          defaultWidth = 24.dp,
          defaultHeight = 24.dp,
          viewportWidth = 24f,
          viewportHeight = 24f,
        )
        .apply {
          path(
            fill = SolidColor(Color.Black),
            fillAlpha = 1f,
            stroke = null,
            strokeAlpha = 1f,
            strokeLineWidth = 1f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Bevel,
            strokeLineMiter = 1f,
            pathFillType = PathFillType.Companion.NonZero,
          ) {
            moveTo(3.5f, 20.5f)
            verticalLineTo(15.79f)
            horizontalLineTo(5f)
            verticalLineTo(19f)
            horizontalLineTo(8.21f)
            verticalLineToRelative(1.5f)
            horizontalLineTo(3.5f)
            close()
            moveToRelative(12.3f, 0f)
            verticalLineTo(19f)
            horizontalLineToRelative(3.21f)
            verticalLineTo(15.79f)
            horizontalLineToRelative(1.5f)
            verticalLineTo(20.5f)
            horizontalLineTo(15.8f)
            close()
            moveTo(3.5f, 8.21f)
            verticalLineTo(3.5f)
            horizontalLineTo(8.21f)
            verticalLineTo(5f)
            horizontalLineTo(5f)
            verticalLineTo(8.21f)
            horizontalLineTo(3.5f)
            close()
            moveToRelative(15.51f, 0f)
            verticalLineTo(5f)
            horizontalLineTo(15.8f)
            verticalLineTo(3.5f)
            horizontalLineToRelative(4.71f)
            verticalLineTo(8.21f)
            horizontalLineToRelative(-1.5f)
            close()
          }
        }
        .build()
    return _FullscreenGlyph!!
  }

private var _FullscreenGlyph: ImageVector? = null
