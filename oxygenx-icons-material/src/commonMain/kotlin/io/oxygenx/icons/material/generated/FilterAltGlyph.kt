// Material Symbols (https://fonts.google.com/icons) - Apache License 2.0
// Copyright 2026 Google LLC
// SPDX-License-Identifier: Apache-2.0
//
// Generated: https://fonts.gstatic.com/render/v1/Material+Symbols+Outlined/24dp/filter_alt.kt?var=opsz,wght,FILL,GRAD@24,300,0,0
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
internal val FilterAltGlyph: ImageVector
  get() {
    if (_FilterAltGlyph != null) {
      return _FilterAltGlyph!!
    }
    _FilterAltGlyph =
      ImageVector.Builder(
          name = "filter_alt",
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
            moveTo(11.38f, 19.5f)
            quadToRelative(-0.38f, 0f, -0.63f, -0.25f)
            reflectiveQuadTo(10.5f, 18.62f)
            verticalLineTo(12.83f)
            lineTo(4.9f, 5.72f)
            quadTo(4.61f, 5.33f, 4.82f, 4.92f)
            reflectiveQuadTo(5.51f, 4.5f)
            horizontalLineTo(18.49f)
            quadToRelative(0.49f, 0f, 0.69f, 0.42f)
            reflectiveQuadTo(19.1f, 5.72f)
            lineToRelative(-5.6f, 7.11f)
            verticalLineToRelative(5.79f)
            quadToRelative(0f, 0.38f, -0.25f, 0.63f)
            reflectiveQuadTo(12.62f, 19.5f)
            horizontalLineTo(11.38f)
            close()
            moveTo(12f, 12.3f)
            lineTo(16.95f, 6f)
            horizontalLineTo(7.05f)
            lineTo(12f, 12.3f)
            close()
            moveToRelative(0f, 0f)
            close()
          }
        }
        .build()
    return _FilterAltGlyph!!
  }

private var _FilterAltGlyph: ImageVector? = null
