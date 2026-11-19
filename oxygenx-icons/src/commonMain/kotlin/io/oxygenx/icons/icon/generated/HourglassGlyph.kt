// Material Symbols (https://fonts.google.com/icons) - Apache License 2.0
// Copyright 2026 Google LLC
// SPDX-License-Identifier: Apache-2.0
//
// Generated: https://fonts.gstatic.com/render/v1/Material+Symbols+Outlined/24dp/hourglass_empty.kt?var=opsz,wght,FILL,GRAD@24,300,0,0
// Axes: opsz=24, wght=300, FILL=0, GRAD=0 (Outlined style)
// Do not edit by hand; run python tools/material_symbols.py --fetch.

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
internal val HourglassGlyph: ImageVector
  get() {
    if (_HourglassGlyph != null) {
      return _HourglassGlyph!!
    }
    _HourglassGlyph =
      ImageVector.Builder(
          name = "hourglass_empty",
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
            moveTo(7.9f, 20f)
            horizontalLineTo(16.1f)
            verticalLineTo(17f)
            quadToRelative(0f, -1.71f, -1.19f, -2.92f)
            reflectiveQuadTo(12f, 12.87f)
            reflectiveQuadTo(9.1f, 14.08f)
            reflectiveQuadTo(7.9f, 17f)
            verticalLineToRelative(3f)
            close()
            moveToRelative(7f, -10.08f)
            quadTo(16.1f, 8.71f, 16.1f, 7f)
            verticalLineTo(4f)
            horizontalLineTo(7.9f)
            verticalLineTo(7f)
            quadToRelative(0f, 1.71f, 1.19f, 2.92f)
            reflectiveQuadTo(12f, 11.13f)
            reflectiveQuadTo(14.9f, 9.92f)
            close()
            moveTo(4.5f, 21.5f)
            verticalLineTo(20f)
            horizontalLineTo(6.4f)
            verticalLineTo(17f)
            quadToRelative(0f, -1.69f, 0.92f, -3.07f)
            reflectiveQuadTo(9.78f, 12f)
            quadTo(8.25f, 11.45f, 7.33f, 10.07f)
            reflectiveQuadTo(6.4f, 7f)
            verticalLineTo(4f)
            horizontalLineTo(4.5f)
            verticalLineTo(2.5f)
            horizontalLineToRelative(15f)
            verticalLineTo(4f)
            horizontalLineTo(17.6f)
            verticalLineTo(7f)
            quadToRelative(0f, 1.69f, -0.92f, 3.07f)
            reflectiveQuadTo(14.22f, 12f)
            quadToRelative(1.53f, 0.54f, 2.45f, 1.93f)
            reflectiveQuadTo(17.6f, 17f)
            verticalLineToRelative(3f)
            horizontalLineToRelative(1.9f)
            verticalLineToRelative(1.5f)
            horizontalLineTo(4.5f)
            close()
          }
        }
        .build()
    return _HourglassGlyph!!
  }

private var _HourglassGlyph: ImageVector? = null
