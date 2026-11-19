// Material Symbols (https://fonts.google.com/icons) - Apache License 2.0
// Copyright 2026 Google LLC
// SPDX-License-Identifier: Apache-2.0
//
// Generated: https://fonts.gstatic.com/render/v1/Material+Symbols+Outlined/24dp/arrow_forward_ios.kt?var=opsz,wght,FILL,GRAD@24,300,0,0
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
internal val ArrowForwardIosGlyph: ImageVector
  get() {
    if (_ArrowForwardIosGlyph != null) {
      return _ArrowForwardIosGlyph!!
    }
    _ArrowForwardIosGlyph =
      ImageVector.Builder(
          name = "arrow_forward_ios",
          defaultWidth = 24.dp,
          defaultHeight = 24.dp,
          viewportWidth = 24f,
          viewportHeight = 24f,
          autoMirror = true,
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
            moveTo(8.02f, 21.65f)
            lineTo(6.6f, 20.23f)
            lineTo(14.83f, 12f)
            lineTo(6.6f, 3.77f)
            lineTo(8.02f, 2.35f)
            lineTo(17.67f, 12f)
            lineTo(8.02f, 21.65f)
            close()
          }
        }
        .build()
    return _ArrowForwardIosGlyph!!
  }

private var _ArrowForwardIosGlyph: ImageVector? = null
