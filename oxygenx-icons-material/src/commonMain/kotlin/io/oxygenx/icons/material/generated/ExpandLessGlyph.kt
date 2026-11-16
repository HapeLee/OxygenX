// Material Symbols (https://fonts.google.com/icons) - Apache License 2.0
// Copyright 2026 Google LLC
// SPDX-License-Identifier: Apache-2.0
//
// Generated: https://fonts.gstatic.com/render/v1/Material+Symbols+Outlined/24dp/expand_less.kt?var=opsz,wght,FILL,GRAD@24,300,0,0
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
internal val ExpandLessGlyph: ImageVector
  get() {
    if (_ExpandLessGlyph != null) {
      return _ExpandLessGlyph!!
    }
    _ExpandLessGlyph =
      ImageVector.Builder(
          name = "expand_less",
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
            moveTo(7.4f, 15.04f)
            lineTo(6.35f, 13.98f)
            lineTo(12f, 8.33f)
            lineToRelative(5.65f, 5.65f)
            lineTo(16.6f, 15.04f)
            lineTo(12f, 10.44f)
            lineToRelative(-4.6f, 4.6f)
            close()
          }
        }
        .build()
    return _ExpandLessGlyph!!
  }

private var _ExpandLessGlyph: ImageVector? = null
