// Material Symbols (https://fonts.google.com/icons) - Apache License 2.0
// Copyright 2026 Google LLC
// SPDX-License-Identifier: Apache-2.0
//
// Generated: https://fonts.gstatic.com/render/v1/Material+Symbols+Outlined/24dp/skip_previous.kt?var=opsz,wght,FILL,GRAD@24,300,0,0
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
internal val SkipPreviousGlyph: ImageVector
  get() {
    if (_SkipPreviousGlyph != null) {
      return _SkipPreviousGlyph!!
    }
    _SkipPreviousGlyph =
      ImageVector.Builder(
          name = "skip_previous",
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
            moveTo(6.12f, 17.31f)
            verticalLineTo(6.69f)
            horizontalLineToRelative(1.5f)
            verticalLineTo(17.31f)
            horizontalLineTo(6.12f)
            close()
            moveToRelative(11.77f, 0f)
            lineTo(9.92f, 12f)
            lineTo(17.88f, 6.69f)
            verticalLineTo(17.31f)
            close()
            moveTo(16.38f, 12f)
            close()
            moveToRelative(0f, 2.5f)
            verticalLineToRelative(-5f)
            lineTo(12.62f, 12f)
            lineToRelative(3.77f, 2.5f)
            close()
          }
        }
        .build()
    return _SkipPreviousGlyph!!
  }

private var _SkipPreviousGlyph: ImageVector? = null
