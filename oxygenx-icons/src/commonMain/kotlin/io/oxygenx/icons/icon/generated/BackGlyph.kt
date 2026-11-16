// Material Symbols (https://fonts.google.com/icons) - Apache License 2.0
// Copyright 2026 Google LLC
// SPDX-License-Identifier: Apache-2.0
//
// Generated: https://fonts.gstatic.com/render/v1/Material+Symbols+Rounded/24dp/arrow_back_ios_new.kt
// Axes: opsz=24, wght=300, FILL=0, GRAD=0, ROND=50
// Centred variant: arrow_back_ios sits at x=1..11 of the 24dp grid (iOS style).
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
internal val BackGlyph: ImageVector
  get() {
    if (_BackGlyph != null) {
      return _BackGlyph!!
    }
    _BackGlyph =
      ImageVector.Builder(
          name = "arrow_back_ios_new",
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
            moveTo(9.18f, 12f)
            lineToRelative(7.53f, 7.53f)
            quadToRelative(0.3f, 0.3f, 0.29f, 0.7f)
            reflectiveQuadToRelative(-0.31f, 0.7f)
            reflectiveQuadTo(16f, 21.23f)
            reflectiveQuadToRelative(-0.7f, -0.3f)
            lineTo(7.63f, 13.28f)
            quadTo(7.36f, 13.01f, 7.23f, 12.67f)
            reflectiveQuadTo(7.09f, 12f)
            reflectiveQuadTo(7.23f, 11.33f)
            reflectiveQuadToRelative(0.4f, -0.61f)
            lineTo(15.3f, 3.05f)
            quadToRelative(0.3f, -0.3f, 0.71f, -0.29f)
            reflectiveQuadToRelative(0.71f, 0.31f)
            reflectiveQuadToRelative(0.3f, 0.7f)
            reflectiveQuadToRelative(-0.3f, 0.7f)
            lineTo(9.18f, 12f)
            close()
          }
        }
        .build()
    return _BackGlyph!!
  }

private var _BackGlyph: ImageVector? = null
