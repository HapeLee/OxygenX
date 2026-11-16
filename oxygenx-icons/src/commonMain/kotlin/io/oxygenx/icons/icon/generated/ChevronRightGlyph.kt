// Material Symbols (https://fonts.google.com/icons) - Apache License 2.0
// Copyright 2026 Google LLC
// SPDX-License-Identifier: Apache-2.0
//
// Generated: https://fonts.gstatic.com/render/v1/Material+Symbols+Rounded/24dp/chevron_right.kt
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
internal val ChevronRightGlyph: ImageVector
  get() {
    if (_ChevronRightGlyph != null) {
      return _ChevronRightGlyph!!
    }
    _ChevronRightGlyph =
      ImageVector.Builder(
          name = "chevron_right",
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
            moveTo(12.95f, 12f)
            lineTo(8.87f, 7.93f)
            quadTo(8.67f, 7.72f, 8.66f, 7.4f)
            reflectiveQuadTo(8.87f, 6.87f)
            reflectiveQuadTo(9.4f, 6.66f)
            reflectiveQuadTo(9.93f, 6.87f)
            lineToRelative(4.49f, 4.49f)
            quadToRelative(0.14f, 0.14f, 0.2f, 0.3f)
            reflectiveQuadTo(14.68f, 12f)
            reflectiveQuadToRelative(-0.06f, 0.34f)
            reflectiveQuadToRelative(-0.2f, 0.3f)
            lineTo(9.93f, 17.13f)
            quadTo(9.72f, 17.33f, 9.4f, 17.34f)
            reflectiveQuadTo(8.87f, 17.13f)
            reflectiveQuadTo(8.66f, 16.6f)
            reflectiveQuadTo(8.87f, 16.07f)
            lineTo(12.95f, 12f)
            close()
          }
        }
        .build()
    return _ChevronRightGlyph!!
  }

private var _ChevronRightGlyph: ImageVector? = null
