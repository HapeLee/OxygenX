// Material Symbols (https://fonts.google.com/icons) - Apache License 2.0
// Copyright 2026 Google LLC
// SPDX-License-Identifier: Apache-2.0
//
// Generated: https://fonts.gstatic.com/render/v1/Material+Symbols+Outlined/24dp/report_problem.kt?var=opsz,wght,FILL,GRAD@24,300,0,0
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
internal val ReportProblemGlyph: ImageVector
  get() {
    if (_ReportProblemGlyph != null) {
      return _ReportProblemGlyph!!
    }
    _ReportProblemGlyph =
      ImageVector.Builder(
          name = "report_problem",
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
            moveTo(1.87f, 20.5f)
            lineTo(12f, 3f)
            lineTo(22.13f, 20.5f)
            horizontalLineTo(1.87f)
            close()
            moveTo(4.45f, 19f)
            horizontalLineToRelative(15.1f)
            lineTo(12f, 6f)
            lineTo(4.45f, 19f)
            close()
            moveToRelative(8.12f, -1.43f)
            quadTo(12.81f, 17.34f, 12.81f, 17f)
            reflectiveQuadTo(12.57f, 16.43f)
            reflectiveQuadTo(12f, 16.19f)
            reflectiveQuadToRelative(-0.57f, 0.23f)
            reflectiveQuadTo(11.19f, 17f)
            reflectiveQuadToRelative(0.23f, 0.57f)
            reflectiveQuadTo(12f, 17.81f)
            reflectiveQuadToRelative(0.57f, -0.23f)
            close()
            moveTo(11.25f, 15.19f)
            horizontalLineToRelative(1.5f)
            verticalLineToRelative(-5f)
            horizontalLineToRelative(-1.5f)
            verticalLineToRelative(5f)
            close()
            moveTo(12f, 12.5f)
            close()
          }
        }
        .build()
    return _ReportProblemGlyph!!
  }

private var _ReportProblemGlyph: ImageVector? = null
