// Material Symbols (https://fonts.google.com/icons) - Apache License 2.0
// Copyright 2026 Google LLC
// SPDX-License-Identifier: Apache-2.0
//
// Generated: https://fonts.gstatic.com/render/v1/Material+Symbols+Outlined/24dp/add.kt?var=opsz,wght,FILL,GRAD@24,300,0,0
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
internal val AddGlyph: ImageVector
  get() {
    if (_AddGlyph != null) {
      return _AddGlyph!!
    }
    _AddGlyph =
      ImageVector.Builder(
          name = "add",
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
            moveTo(11.25f, 12.75f)
            horizontalLineTo(5.5f)
            verticalLineToRelative(-1.5f)
            horizontalLineToRelative(5.75f)
            verticalLineTo(5.5f)
            horizontalLineToRelative(1.5f)
            verticalLineToRelative(5.75f)
            horizontalLineTo(18.5f)
            verticalLineToRelative(1.5f)
            horizontalLineTo(12.75f)
            verticalLineTo(18.5f)
            horizontalLineToRelative(-1.5f)
            verticalLineTo(12.75f)
            close()
          }
        }
        .build()
    return _AddGlyph!!
  }

private var _AddGlyph: ImageVector? = null
