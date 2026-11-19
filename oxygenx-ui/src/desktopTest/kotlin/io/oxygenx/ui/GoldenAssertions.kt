// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal fun assertGolden(bitmap: ImageBitmap, relativePath: String) {
    val pixels = bitmap.toPixelMap()
    val actual = BufferedImage(pixels.width, pixels.height, BufferedImage.TYPE_INT_ARGB)
    for (y in 0 until pixels.height) {
        for (x in 0 until pixels.width) actual.setRGB(x, y, pixels[x, y].toArgb())
    }
    val expectedFile = File(System.getProperty("oxygenx.goldenDir"), relativePath)
    if (System.getProperty("oxygenx.updateGoldens").toBoolean()) {
        expectedFile.parentFile.mkdirs()
        ImageIO.write(actual, "png", expectedFile)
        return
    }
    val actualFile = File(System.getProperty("oxygenx.actualGoldenDir"), relativePath)
    actualFile.parentFile.mkdirs()
    ImageIO.write(actual, "png", actualFile)
    assertTrue(expectedFile.isFile, "Missing golden $expectedFile; review and generate it explicitly")
    val expected = ImageIO.read(expectedFile)
    assertEquals(expected.width, actual.width, relativePath)
    assertEquals(expected.height, actual.height, relativePath)
    var changed = 0
    for (y in 0 until actual.height) {
        for (x in 0 until actual.width) {
            val before = expected.getRGB(x, y)
            val after = actual.getRGB(x, y)
            if (listOf(0, 8, 16, 24).any { shift ->
                    abs(((before ushr shift) and 255) - ((after ushr shift) and 255)) > 2
                }
            ) {
                changed++
            }
        }
    }
    assertTrue(changed <= actual.width * actual.height * 0.005, "$relativePath: $changed changed pixels; actual: $actualFile")
}
