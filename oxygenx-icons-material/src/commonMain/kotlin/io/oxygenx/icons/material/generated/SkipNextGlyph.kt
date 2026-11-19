// Material Symbols (https://fonts.google.com/icons) - Apache License 2.0
// Copyright 2026 Google LLC
// SPDX-License-Identifier: Apache-2.0
//
// Generated: https://fonts.gstatic.com/render/v1/Material+Symbols+Outlined/24dp/skip_next.kt?var=opsz,wght,FILL,GRAD@24,300,0,0
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
internal val SkipNextGlyph: ImageVector
  get() {
    if (_SkipNextGlyph != null) {
      return _SkipNextGlyph!!
    }
    _SkipNextGlyph =
      ImageVector.Builder(
          name = "skip_next",
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
            moveTo(16.38f, 17.31f)
            verticalLineTo(6.69f)
            horizontalLineToRelative(1.5f)
            verticalLineTo(17.31f)
            horizontalLineToRelative(-1.5f)
            close()
            moveToRelative(-10.27f, 0f)
            verticalLineTo(6.69f)
            lineTo(14.08f, 12f)
            lineTo(6.12f, 17.31f)
            close()
            moveTo(7.62f, 12f)
            close()
            moveToRelative(0f, 2.5f)
            lineTo(11.38f, 12f)
            lineTo(7.62f, 9.5f)
            verticalLineToRelative(5f)
            close()
          }
        }
        .build()
    return _SkipNextGlyph!!
  }

private var _SkipNextGlyph: ImageVector? = null
