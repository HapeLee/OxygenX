// Material Symbols (https://fonts.google.com/icons) - Apache License 2.0
// Copyright 2026 Google LLC
// SPDX-License-Identifier: Apache-2.0
//
// Generated: https://fonts.gstatic.com/render/v1/Material+Symbols+Outlined/24dp/login.kt?var=opsz,wght,FILL,GRAD@24,300,0,0
// Axes: opsz=24, wght=300, FILL=0, GRAD=0 (Outlined style)
// Do not edit by hand; re-download from the URL above.

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
internal val LoginGlyph: ImageVector
  get() {
    if (_LoginGlyph != null) {
      return _LoginGlyph!!
    }
    _LoginGlyph =
      ImageVector.Builder(
          name = "login",
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
            moveTo(11.99f, 20.5f)
            verticalLineTo(19f)
            horizontalLineToRelative(6.7f)
            quadToRelative(0.12f, 0f, 0.21f, -0.1f)
            reflectiveQuadTo(19f, 18.69f)
            verticalLineTo(5.31f)
            quadTo(19f, 5.19f, 18.9f, 5.1f)
            reflectiveQuadTo(18.69f, 5f)
            horizontalLineToRelative(-6.7f)
            verticalLineTo(3.5f)
            horizontalLineToRelative(6.7f)
            quadToRelative(0.76f, 0f, 1.28f, 0.52f)
            reflectiveQuadTo(20.5f, 5.31f)
            verticalLineTo(18.69f)
            quadToRelative(0f, 0.76f, -0.52f, 1.28f)
            reflectiveQuadTo(18.69f, 20.5f)
            horizontalLineToRelative(-6.7f)
            close()
            moveTo(10.63f, 16.27f)
            lineTo(9.6f, 15.18f)
            lineToRelative(2.43f, -2.43f)
            horizontalLineTo(3.5f)
            verticalLineToRelative(-1.5f)
            horizontalLineToRelative(8.53f)
            lineTo(9.6f, 8.82f)
            lineTo(10.63f, 7.73f)
            lineTo(14.9f, 12f)
            lineToRelative(-4.27f, 4.27f)
            close()
          }
        }
        .build()
    return _LoginGlyph!!
  }

private var _LoginGlyph: ImageVector? = null
