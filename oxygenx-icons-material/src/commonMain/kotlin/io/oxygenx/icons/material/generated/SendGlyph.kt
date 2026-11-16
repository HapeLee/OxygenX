// Material Symbols (https://fonts.google.com/icons) - Apache License 2.0
// Copyright 2026 Google LLC
// SPDX-License-Identifier: Apache-2.0
//
// Generated: https://fonts.gstatic.com/render/v1/Material+Symbols+Outlined/24dp/send.kt?var=opsz,wght,FILL,GRAD@24,300,0,0
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
internal val SendGlyph: ImageVector
  get() {
    if (_SendGlyph != null) {
      return _SendGlyph!!
    }
    _SendGlyph =
      ImageVector.Builder(
          name = "send",
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
            moveTo(3.5f, 19.25f)
            verticalLineTo(4.75f)
            lineTo(20.71f, 12f)
            lineTo(3.5f, 19.25f)
            close()
            moveTo(5f, 17f)
            lineTo(16.85f, 12f)
            lineTo(5f, 7f)
            verticalLineToRelative(3.69f)
            lineTo(10.42f, 12f)
            lineTo(5f, 13.31f)
            verticalLineTo(17f)
            close()
            moveToRelative(0f, 0f)
            verticalLineTo(12f)
            verticalLineTo(7f)
            verticalLineToRelative(3.69f)
            verticalLineToRelative(2.62f)
            verticalLineTo(17f)
            close()
          }
        }
        .build()
    return _SendGlyph!!
  }

private var _SendGlyph: ImageVector? = null
