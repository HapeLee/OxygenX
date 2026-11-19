// Material Symbols (https://fonts.google.com/icons) - Apache License 2.0
// Copyright 2026 Google LLC
// SPDX-License-Identifier: Apache-2.0
//
// Generated: https://fonts.gstatic.com/render/v1/Material+Symbols+Outlined/24dp/star_rate.kt?var=opsz,wght,FILL,GRAD@24,300,0,0
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
internal val StarRateGlyph: ImageVector
  get() {
    if (_StarRateGlyph != null) {
      return _StarRateGlyph!!
    }
    _StarRateGlyph =
      ImageVector.Builder(
          name = "star_rate",
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
            moveTo(9.6f, 15.65f)
            lineTo(12f, 13.8f)
            lineToRelative(2.4f, 1.85f)
            lineTo(13.5f, 12.6f)
            lineTo(15.75f, 11f)
            horizontalLineToRelative(-2.8f)
            lineTo(12f, 7.9f)
            lineTo(11.05f, 11f)
            horizontalLineTo(8.25f)
            lineToRelative(2.25f, 1.6f)
            lineTo(9.6f, 15.65f)
            close()
            moveTo(6.76f, 19.67f)
            lineTo(8.73f, 13.21f)
            lineTo(3.56f, 9.5f)
            horizontalLineTo(9.97f)
            lineTo(12f, 2.73f)
            lineTo(14.03f, 9.5f)
            horizontalLineToRelative(6.41f)
            lineToRelative(-5.17f, 3.71f)
            lineToRelative(1.97f, 6.47f)
            lineTo(12f, 15.68f)
            lineTo(6.76f, 19.67f)
            close()
            moveTo(12f, 11.77f)
            close()
          }
        }
        .build()
    return _StarRateGlyph!!
  }

private var _StarRateGlyph: ImageVector? = null
