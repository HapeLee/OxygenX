// Material Symbols (https://fonts.google.com/icons) - Apache License 2.0
// Copyright 2026 Google LLC
// SPDX-License-Identifier: Apache-2.0
//
// Generated: https://fonts.gstatic.com/render/v1/Material+Symbols+Outlined/24dp/search.kt?var=opsz,wght,FILL,GRAD@24,300,0,0
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
internal val SearchGlyph: ImageVector
  get() {
    if (_SearchGlyph != null) {
      return _SearchGlyph!!
    }
    _SearchGlyph =
      ImageVector.Builder(
          name = "search",
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
            moveTo(19.54f, 20.58f)
            lineTo(13.26f, 14.3f)
            quadToRelative(-0.75f, 0.62f, -1.72f, 0.97f)
            reflectiveQuadTo(9.52f, 15.62f)
            quadToRelative(-2.56f, 0f, -4.34f, -1.78f)
            reflectiveQuadTo(3.4f, 9.5f)
            reflectiveQuadTo(5.18f, 5.16f)
            reflectiveQuadTo(9.52f, 3.38f)
            reflectiveQuadToRelative(4.34f, 1.78f)
            reflectiveQuadTo(15.63f, 9.5f)
            quadToRelative(0f, 1.07f, -0.36f, 2.05f)
            reflectiveQuadToRelative(-0.96f, 1.7f)
            lineToRelative(6.28f, 6.28f)
            lineToRelative(-1.05f, 1.05f)
            close()
            moveTo(9.52f, 14.12f)
            quadToRelative(1.93f, 0f, 3.27f, -1.34f)
            reflectiveQuadTo(14.13f, 9.5f)
            reflectiveQuadTo(12.79f, 6.23f)
            reflectiveQuadTo(9.52f, 4.88f)
            reflectiveQuadTo(6.25f, 6.23f)
            reflectiveQuadTo(4.9f, 9.5f)
            reflectiveQuadToRelative(1.34f, 3.27f)
            reflectiveQuadToRelative(3.27f, 1.34f)
            close()
          }
        }
        .build()
    return _SearchGlyph!!
  }

private var _SearchGlyph: ImageVector? = null
