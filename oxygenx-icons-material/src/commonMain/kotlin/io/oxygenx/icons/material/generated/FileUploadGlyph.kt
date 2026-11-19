// Material Symbols (https://fonts.google.com/icons) - Apache License 2.0
// Copyright 2026 Google LLC
// SPDX-License-Identifier: Apache-2.0
//
// Generated: https://fonts.gstatic.com/render/v1/Material+Symbols+Outlined/24dp/file_upload.kt?var=opsz,wght,FILL,GRAD@24,300,0,0
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
internal val FileUploadGlyph: ImageVector
  get() {
    if (_FileUploadGlyph != null) {
      return _FileUploadGlyph!!
    }
    _FileUploadGlyph =
      ImageVector.Builder(
          name = "file_upload",
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
            moveTo(11.25f, 15.79f)
            verticalLineTo(7.39f)
            lineTo(8.78f, 9.85f)
            lineTo(7.73f, 8.77f)
            lineTo(12f, 4.5f)
            lineToRelative(4.27f, 4.27f)
            lineTo(15.22f, 9.85f)
            lineTo(12.75f, 7.39f)
            verticalLineToRelative(8.4f)
            horizontalLineToRelative(-1.5f)
            close()
            moveTo(6.31f, 19.5f)
            quadToRelative(-0.76f, 0f, -1.28f, -0.52f)
            reflectiveQuadTo(4.5f, 17.69f)
            verticalLineTo(14.98f)
            horizontalLineTo(6f)
            verticalLineToRelative(2.71f)
            quadToRelative(0f, 0.12f, 0.1f, 0.21f)
            reflectiveQuadTo(6.31f, 18f)
            horizontalLineTo(17.69f)
            quadToRelative(0.12f, 0f, 0.21f, -0.1f)
            reflectiveQuadTo(18f, 17.69f)
            verticalLineTo(14.98f)
            horizontalLineToRelative(1.5f)
            verticalLineToRelative(2.71f)
            quadToRelative(0f, 0.76f, -0.52f, 1.28f)
            reflectiveQuadTo(17.69f, 19.5f)
            horizontalLineTo(6.31f)
            close()
          }
        }
        .build()
    return _FileUploadGlyph!!
  }

private var _FileUploadGlyph: ImageVector? = null
