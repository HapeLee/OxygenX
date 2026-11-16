// Material Symbols (https://fonts.google.com/icons) - Apache License 2.0
// Copyright 2026 Google LLC
// SPDX-License-Identifier: Apache-2.0
//
// Generated: https://fonts.gstatic.com/render/v1/Material+Symbols+Rounded/24dp/close.kt
// Axes: opsz=24, wght=300, FILL=0, GRAD=0, ROND=50
// Do not edit by hand; re-download from the URL above.

package io.oxygenx.icons.icon.generated

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
internal val CloseGlyph: ImageVector
  get() {
    if (_CloseGlyph != null) {
      return _CloseGlyph!!
    }
    _CloseGlyph =
      ImageVector.Builder(
          name = "close",
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
            moveTo(12f, 13.05f)
            lineTo(6.93f, 18.13f)
            quadTo(6.72f, 18.33f, 6.4f, 18.34f)
            reflectiveQuadTo(5.87f, 18.13f)
            reflectiveQuadTo(5.66f, 17.6f)
            reflectiveQuadTo(5.87f, 17.07f)
            lineTo(10.95f, 12f)
            lineTo(5.87f, 6.93f)
            quadTo(5.67f, 6.72f, 5.66f, 6.4f)
            reflectiveQuadTo(5.87f, 5.87f)
            reflectiveQuadTo(6.4f, 5.66f)
            reflectiveQuadTo(6.93f, 5.87f)
            lineTo(12f, 10.95f)
            lineTo(17.07f, 5.87f)
            quadTo(17.28f, 5.67f, 17.6f, 5.66f)
            reflectiveQuadToRelative(0.53f, 0.21f)
            reflectiveQuadTo(18.34f, 6.4f)
            reflectiveQuadTo(18.13f, 6.93f)
            lineTo(13.05f, 12f)
            lineToRelative(5.07f, 5.07f)
            quadToRelative(0.21f, 0.21f, 0.21f, 0.52f)
            reflectiveQuadToRelative(-0.21f, 0.53f)
            reflectiveQuadTo(17.6f, 18.34f)
            reflectiveQuadTo(17.07f, 18.13f)
            lineTo(12f, 13.05f)
            close()
          }
        }
        .build()
    return _CloseGlyph!!
  }

private var _CloseGlyph: ImageVector? = null
