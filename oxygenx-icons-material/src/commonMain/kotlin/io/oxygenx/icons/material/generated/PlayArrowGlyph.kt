// Material Symbols (https://fonts.google.com/icons) - Apache License 2.0
// Copyright 2026 Google LLC
// SPDX-License-Identifier: Apache-2.0
//
// Generated: https://fonts.gstatic.com/render/v1/Material+Symbols+Outlined/24dp/play_arrow.kt?var=opsz,wght,FILL,GRAD@24,300,0,0
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
internal val PlayArrowGlyph: ImageVector
  get() {
    if (_PlayArrowGlyph != null) {
      return _PlayArrowGlyph!!
    }
    _PlayArrowGlyph =
      ImageVector.Builder(
          name = "play_arrow",
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
            moveTo(8.5f, 18.1f)
            verticalLineTo(5.9f)
            lineTo(18.08f, 12f)
            lineTo(8.5f, 18.1f)
            close()
            moveTo(10f, 12f)
            close()
            moveToRelative(0f, 3.35f)
            lineTo(15.27f, 12f)
            lineTo(10f, 8.65f)
            verticalLineToRelative(6.7f)
            close()
          }
        }
        .build()
    return _PlayArrowGlyph!!
  }

private var _PlayArrowGlyph: ImageVector? = null
