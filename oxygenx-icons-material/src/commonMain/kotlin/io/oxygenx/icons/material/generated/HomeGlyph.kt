// Material Symbols (https://fonts.google.com/icons) - Apache License 2.0
// Copyright 2026 Google LLC
// SPDX-License-Identifier: Apache-2.0
//
// Generated: https://fonts.gstatic.com/render/v1/Material+Symbols+Outlined/24dp/home.kt?var=opsz,wght,FILL,GRAD@24,300,0,0
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
internal val HomeGlyph: ImageVector
  get() {
    if (_HomeGlyph != null) {
      return _HomeGlyph!!
    }
    _HomeGlyph =
      ImageVector.Builder(
          name = "home",
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
            moveTo(6f, 19f)
            horizontalLineTo(9.35f)
            verticalLineTo(13.06f)
            horizontalLineToRelative(5.31f)
            verticalLineTo(19f)
            horizontalLineTo(18f)
            verticalLineTo(10f)
            lineTo(12f, 5.48f)
            lineTo(6f, 10f)
            verticalLineToRelative(9f)
            close()
            moveTo(4.5f, 20.5f)
            verticalLineTo(9.25f)
            lineTo(12f, 3.61f)
            lineToRelative(7.5f, 5.64f)
            verticalLineTo(20.5f)
            horizontalLineTo(13.15f)
            verticalLineTo(14.56f)
            horizontalLineTo(10.85f)
            verticalLineTo(20.5f)
            horizontalLineTo(4.5f)
            close()
            moveTo(12f, 12.24f)
            close()
          }
        }
        .build()
    return _HomeGlyph!!
  }

private var _HomeGlyph: ImageVector? = null
