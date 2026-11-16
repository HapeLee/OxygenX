// Material Symbols (https://fonts.google.com/icons) - Apache License 2.0
// Copyright 2026 Google LLC
// SPDX-License-Identifier: Apache-2.0
//
// Generated: https://fonts.gstatic.com/render/v1/Material+Symbols+Outlined/24dp/arrow_back.kt?var=opsz,wght,FILL,GRAD@24,300,0,0
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
internal val ArrowBackGlyph: ImageVector
  get() {
    if (_ArrowBackGlyph != null) {
      return _ArrowBackGlyph!!
    }
    _ArrowBackGlyph =
      ImageVector.Builder(
          name = "arrow_back",
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
            moveTo(7.37f, 12.75f)
            lineToRelative(5.7f, 5.7f)
            lineTo(12f, 19.5f)
            lineTo(4.5f, 12f)
            lineTo(12f, 4.5f)
            lineToRelative(1.07f, 1.05f)
            lineToRelative(-5.7f, 5.7f)
            horizontalLineTo(19.5f)
            verticalLineToRelative(1.5f)
            horizontalLineTo(7.37f)
            close()
          }
        }
        .build()
    return _ArrowBackGlyph!!
  }

private var _ArrowBackGlyph: ImageVector? = null
