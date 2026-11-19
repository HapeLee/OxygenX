// Material Symbols (https://fonts.google.com/icons) - Apache License 2.0
// Copyright 2026 Google LLC
// SPDX-License-Identifier: Apache-2.0
//
// Generated: https://fonts.gstatic.com/render/v1/Material+Symbols+Outlined/24dp/menu.kt?var=opsz,wght,FILL,GRAD@24,300,0,0
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
internal val MenuGlyph: ImageVector
  get() {
    if (_MenuGlyph != null) {
      return _MenuGlyph!!
    }
    _MenuGlyph =
      ImageVector.Builder(
          name = "menu",
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
            moveTo(3.5f, 17.63f)
            verticalLineToRelative(-1.5f)
            horizontalLineToRelative(17f)
            verticalLineToRelative(1.5f)
            horizontalLineTo(3.5f)
            close()
            moveToRelative(0f, -4.88f)
            verticalLineToRelative(-1.5f)
            horizontalLineToRelative(17f)
            verticalLineToRelative(1.5f)
            horizontalLineTo(3.5f)
            close()
            moveToRelative(0f, -4.88f)
            verticalLineTo(6.37f)
            horizontalLineToRelative(17f)
            verticalLineToRelative(1.5f)
            horizontalLineTo(3.5f)
            close()
          }
        }
        .build()
    return _MenuGlyph!!
  }

private var _MenuGlyph: ImageVector? = null
