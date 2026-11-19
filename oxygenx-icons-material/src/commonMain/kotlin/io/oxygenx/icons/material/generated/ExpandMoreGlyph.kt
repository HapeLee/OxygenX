// Material Symbols (https://fonts.google.com/icons) - Apache License 2.0
// Copyright 2026 Google LLC
// SPDX-License-Identifier: Apache-2.0
//
// Generated: https://fonts.gstatic.com/render/v1/Material+Symbols+Outlined/24dp/expand_more.kt?var=opsz,wght,FILL,GRAD@24,300,0,0
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
internal val ExpandMoreGlyph: ImageVector
  get() {
    if (_ExpandMoreGlyph != null) {
      return _ExpandMoreGlyph!!
    }
    _ExpandMoreGlyph =
      ImageVector.Builder(
          name = "expand_more",
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
            moveTo(12f, 15.04f)
            lineTo(6.35f, 9.38f)
            lineTo(7.4f, 8.33f)
            lineToRelative(4.6f, 4.6f)
            lineToRelative(4.6f, -4.6f)
            lineToRelative(1.05f, 1.05f)
            lineTo(12f, 15.04f)
            close()
          }
        }
        .build()
    return _ExpandMoreGlyph!!
  }

private var _ExpandMoreGlyph: ImageVector? = null
