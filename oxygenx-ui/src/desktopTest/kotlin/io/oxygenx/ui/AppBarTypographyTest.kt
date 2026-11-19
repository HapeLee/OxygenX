// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.oxygenx.theme.LocalTextStyle
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.theme.OxygenTypography
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.navigation.LargeTopAppBar
import io.oxygenx.ui.navigation.PagerTopBar
import io.oxygenx.ui.navigation.TopAppBar
import io.oxygenx.ui.navigation.rememberTopAppBarState
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class AppBarTypographyTest {
    @Test
    fun compactTitlesUseCalibratedMetricsAndKeepThemeFont() = runComposeUiTest {
        val custom = mutableStateOf(false)
        val defaults = OxygenTypography()
        val compact = defaults.titleLarge.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 21.sp,
            lineHeight = 29.sp,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold,
        )
        val typography = OxygenTypography(
            displayMedium = defaults.displayMedium.copy(fontFamily = FontFamily.Serif, fontSize = 43.sp, lineHeight = 51.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Light),
            titleLarge = compact,
        )
        var standard = TextStyle.Default
        var expanded = TextStyle.Default
        var collapsed = TextStyle.Default
        setContent {
            OxygenTheme(typography = if (custom.value) typography else defaults) {
                Column(Modifier.width(360.dp)) {
                    TopAppBar(title = {
                        val style = LocalTextStyle.current
                        SideEffect { standard = style }
                        Text("Standard")
                    })
                    LargeTopAppBar(title = {
                        val style = LocalTextStyle.current
                        SideEffect { expanded = style }
                        Text("Expanded")
                    })
                    LargeTopAppBar(
                        title = {
                            val style = LocalTextStyle.current
                            SideEffect { collapsed = style }
                            Text("Collapsed")
                        },
                        state = rememberTopAppBarState(initialCollapsedFraction = 1f),
                    )
                }
            }
        }
        runOnIdle {
            assertEquals(18.sp, standard.fontSize)
            assertEquals(24.sp, standard.lineHeight)
            assertEquals(FontWeight.Normal, standard.fontWeight)
            assertEquals(45.sp, expanded.fontSize)
            assertEquals(52.sp, expanded.lineHeight)
            assertEquals(18.sp, collapsed.fontSize)
            assertEquals(24.sp, collapsed.lineHeight)
            assertEquals(FontWeight.Normal, collapsed.fontWeight)
            custom.value = true
        }
        runOnIdle {
            fun attributes(style: TextStyle) = listOf(style.fontSize, style.lineHeight, style.fontFamily, style.fontWeight, style.letterSpacing)
            val calibrated = compact.copy(fontSize = 18.sp, lineHeight = 24.sp)
            assertEquals(attributes(calibrated), attributes(standard))
            assertEquals(attributes(typography.displayMedium), attributes(expanded))
            assertEquals(attributes(calibrated), attributes(collapsed))
        }
    }

    @Test
    fun pagerTitlesUseTheCalibratedCompactMetrics() = runComposeUiTest {
        val collapse = mutableStateOf(false)
        var actual = TextStyle.Default
        setContent {
            OxygenTheme {
                PagerTopBar(
                    pagerState = rememberPagerState { 2 },
                    title = {
                        val style = LocalTextStyle.current
                        SideEffect { actual = style }
                        Text("Page $it")
                    },
                    state = rememberTopAppBarState(initialCollapsedFraction = if (collapse.value) 1f else 0f),
                    modifier = Modifier.width(360.dp),
                )
            }
        }
        runOnIdle {
            assertEquals(45.sp, actual.fontSize)
            collapse.value = true
        }
        runOnIdle { assertEquals(18.sp, actual.fontSize) }
    }
}
