// Material Symbols (https://fonts.google.com/icons) - Apache License 2.0
// Copyright 2026 Google LLC
// SPDX-License-Identifier: Apache-2.0
//
// Generated: https://fonts.gstatic.com/render/v1/Material+Symbols+Outlined/24dp/refresh.kt?var=opsz,wght,FILL,GRAD@24,300,0,0
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
internal val RefreshGlyph: ImageVector
  get() {
    if (_RefreshGlyph != null) {
      return _RefreshGlyph!!
    }
    _RefreshGlyph =
      ImageVector.Builder(
          name = "refresh",
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
            moveTo(12.04f, 19.5f)
            quadTo(8.9f, 19.5f, 6.72f, 17.32f)
            reflectiveQuadTo(4.54f, 12f)
            reflectiveQuadTo(6.72f, 6.68f)
            reflectiveQuadTo(12.04f, 4.5f)
            quadToRelative(1.75f, 0f, 3.32f, 0.78f)
            reflectiveQuadToRelative(2.6f, 2.2f)
            verticalLineTo(4.5f)
            horizontalLineToRelative(1.5f)
            verticalLineToRelative(6.12f)
            horizontalLineTo(13.35f)
            verticalLineTo(9.12f)
            horizontalLineTo(17.3f)
            quadTo(16.51f, 7.67f, 15.1f, 6.83f)
            reflectiveQuadTo(12.04f, 6f)
            quadTo(9.54f, 6f, 7.79f, 7.75f)
            reflectiveQuadTo(6.04f, 12f)
            reflectiveQuadToRelative(1.75f, 4.25f)
            reflectiveQuadTo(12.04f, 18f)
            quadToRelative(1.93f, 0f, 3.47f, -1.1f)
            reflectiveQuadTo(17.69f, 14f)
            horizontalLineToRelative(1.58f)
            quadToRelative(-0.68f, 2.45f, -2.68f, 3.97f)
            reflectiveQuadTo(12.04f, 19.5f)
            close()
          }
        }
        .build()
    return _RefreshGlyph!!
  }

private var _RefreshGlyph: ImageVector? = null
