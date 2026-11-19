// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.oxygenx.theme.LocalContentColor
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Icon
import kotlin.test.Test
import kotlin.test.assertEquals

internal fun twoColorVector(): ImageVector = ImageVector.Builder(
    name = "test-two-colors",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
).apply {
    path(fill = SolidColor(Color.Red)) {
        moveTo(0f, 0f)
        lineTo(12f, 0f)
        lineTo(12f, 24f)
        lineTo(0f, 24f)
        close()
    }
    path(fill = SolidColor(Color.Blue)) {
        moveTo(12f, 0f)
        lineTo(24f, 0f)
        lineTo(24f, 24f)
        lineTo(12f, 24f)
        close()
    }
}.build()

internal fun twoColorBitmap(): ImageBitmap = ImageBitmap(24, 24).also {
    val canvas = Canvas(it)
    canvas.drawRect(0f, 0f, 12f, 24f, Paint().apply { color = Color.Red })
    canvas.drawRect(12f, 0f, 24f, 24f, Paint().apply { color = Color.Blue })
}

@OptIn(ExperimentalTestApi::class)
class IconContractTest {
    @Test
    fun vectorDefaultInheritsColorAndExplicitTintOverridesIt() = runComposeUiTest {
        val vector = twoColorVector()
        setContent {
            OxygenTheme {
                CompositionLocalProvider(LocalContentColor provides Color.Green) {
                    Row {
                        Icon(vector, "ambient", Modifier.testTag("ambient"))
                        Icon(vector, "explicit", Modifier.testTag("explicit"), tint = Color.Magenta)
                    }
                }
            }
        }
        for ((tag, color) in listOf("ambient" to Color.Green, "explicit" to Color.Magenta)) {
            val pixels = onNodeWithTag(tag).captureToImage().toPixelMap()
            assertEquals(color, pixels[pixels.width / 4, pixels.height / 2])
            assertEquals(color, pixels[pixels.width * 3 / 4, pixels.height / 2])
        }
    }

    @Test
    fun unspecifiedPreservesVectorPainterAndBitmapColors() = runComposeUiTest {
        val vector = twoColorVector()
        val bitmap = twoColorBitmap()
        setContent {
            OxygenTheme {
                Row {
                    Icon(vector, null, Modifier.testTag("vector"), tint = Color.Unspecified)
                    Icon(bitmap, null, Modifier.testTag("bitmap"), tint = Color.Unspecified)
                    Icon(BitmapPainter(bitmap), null, Modifier.testTag("painter"), tint = Color.Unspecified)
                }
            }
        }
        for (tag in listOf("vector", "bitmap", "painter")) {
            val pixels = onNodeWithTag(tag).captureToImage().toPixelMap()
            assertEquals(Color.Red, pixels[pixels.width / 4, pixels.height / 2], tag)
            assertEquals(Color.Blue, pixels[pixels.width * 3 / 4, pixels.height / 2], tag)
        }
    }

    @Test
    fun painterMirrorsOnlyWhenRequestedInRtlAndKeepsTokenSize() = runComposeUiTest {
        val bitmap = twoColorBitmap()
        setContent {
            CompositionLocalProvider(LocalDensity provides Density(1f, 2f), LocalLayoutDirection provides LayoutDirection.Rtl) {
                OxygenTheme {
                    Row {
                        Icon(BitmapPainter(bitmap), null, Modifier.testTag("mirrored"), tint = Color.Unspecified, mirrorInRtl = true)
                        Icon(bitmap, null, Modifier.testTag("fixed"), tint = Color.Unspecified)
                    }
                }
            }
        }
        val mirrored = onNodeWithTag("mirrored").captureToImage().toPixelMap()
        val fixed = onNodeWithTag("fixed").captureToImage().toPixelMap()
        assertEquals(24, mirrored.width)
        assertEquals(24, mirrored.height)
        assertEquals(Color.Blue, mirrored[6, 12])
        assertEquals(Color.Red, fixed[6, 12])
    }

    @Test
    fun meaningfulIconsExposeImageRoleAndDecorativeOnesHaveNoDescription() = runComposeUiTest {
        val bitmap = twoColorBitmap()
        setContent {
            OxygenTheme {
                Row {
                    Icon(bitmap, "Cover", Modifier.testTag("meaningful"))
                    Icon(bitmap, null, Modifier.testTag("decorative"))
                }
            }
        }
        onNodeWithTag("meaningful").assertContentDescriptionEquals("Cover")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Image))
        onNodeWithTag("decorative").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.ContentDescription))
    }
}
