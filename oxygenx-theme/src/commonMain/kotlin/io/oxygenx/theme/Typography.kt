// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.oxygenx.theme.generated.GeneratedTokens
import kotlin.jvm.JvmName

/** 不可变的新增角色；参与 Typography 的 equality / copy，避免主题变更丢失。 */
@Immutable
data class OxygenTypographyExtras(
    val displayMedium: TextStyle,
    val displaySmall: TextStyle,
    val headlineMedium: TextStyle,
    val titleSmall: TextStyle,
    val labelSmall: TextStyle,
)

/** 完整 M3 角色，同时保留旧字段、构造和 JVM copy 入口。优先使用同名 M3 命名工厂。 */
@Immutable
data class OxygenTypography(
    val displayLarge: TextStyle,
    val appbarTitle: TextStyle,
    val dialogTitle: TextStyle,
    val dialogAction: TextStyle,
    val appBarSubtitle: TextStyle,
    val titleMedium: TextStyle,
    val listItemTitle: TextStyle,
    val clockTime: TextStyle,
    val inputText: TextStyle,
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val labelMedium: TextStyle,
    val caption: TextStyle,
    val extras: OxygenTypographyExtras = defaultExtras(displayLarge),
) {
    constructor(displayLarge: TextStyle, appbarTitle: TextStyle, dialogTitle: TextStyle, dialogAction: TextStyle, appBarSubtitle: TextStyle, titleMedium: TextStyle, listItemTitle: TextStyle, clockTime: TextStyle, inputText: TextStyle, bodyLarge: TextStyle, bodyMedium: TextStyle, labelMedium: TextStyle, caption: TextStyle) : this(displayLarge, appbarTitle, dialogTitle, dialogAction, appBarSubtitle, titleMedium, listItemTitle, clockTime, inputText, bodyLarge, bodyMedium, labelMedium, caption, defaultExtras(displayLarge))

    val displayMedium: TextStyle get() = extras.displayMedium
    val displaySmall: TextStyle get() = extras.displaySmall
    val headlineLarge: TextStyle get() = clockTime
    val headlineMedium: TextStyle get() = extras.headlineMedium
    val headlineSmall: TextStyle get() = dialogTitle
    val titleLarge: TextStyle get() = appbarTitle
    val titleSmall: TextStyle get() = extras.titleSmall
    val bodySmall: TextStyle get() = caption
    val labelLarge: TextStyle get() = dialogAction
    val labelSmall: TextStyle get() = extras.labelSmall

    // 旧二进制仍会调用 copy / copy$default 的 13 参数入口。
    @Deprecated("Binary compatibility bridge", level = DeprecationLevel.HIDDEN)
    @JvmName("copy")
    fun legacyCopy(
        displayLarge: TextStyle = this.displayLarge,
        appbarTitle: TextStyle = this.appbarTitle,
        dialogTitle: TextStyle = this.dialogTitle,
        dialogAction: TextStyle = this.dialogAction,
        appBarSubtitle: TextStyle = this.appBarSubtitle,
        titleMedium: TextStyle = this.titleMedium,
        listItemTitle: TextStyle = this.listItemTitle,
        clockTime: TextStyle = this.clockTime,
        inputText: TextStyle = this.inputText,
        bodyLarge: TextStyle = this.bodyLarge,
        bodyMedium: TextStyle = this.bodyMedium,
        labelMedium: TextStyle = this.labelMedium,
        caption: TextStyle = this.caption,
    ): OxygenTypography = copy(
        displayLarge = displayLarge,
        appbarTitle = appbarTitle,
        dialogTitle = dialogTitle,
        dialogAction = dialogAction,
        appBarSubtitle = appBarSubtitle,
        titleMedium = titleMedium,
        listItemTitle = listItemTitle,
        clockTime = clockTime,
        inputText = inputText,
        bodyLarge = bodyLarge,
        bodyMedium = bodyMedium,
        labelMedium = labelMedium,
        caption = caption,
        extras = extras,
    )
}

/** 参数名称与 M3 的 15 个基础角色一致。旧构造所需的语义参数仍可原样使用。 */
fun OxygenTypography(
    displayLarge: TextStyle = MaterialTypeDefaults.displayLarge,
    displayMedium: TextStyle = MaterialTypeDefaults.displayMedium,
    displaySmall: TextStyle = MaterialTypeDefaults.displaySmall,
    headlineLarge: TextStyle = MaterialTypeDefaults.headlineLarge,
    headlineMedium: TextStyle = MaterialTypeDefaults.headlineMedium,
    headlineSmall: TextStyle = MaterialTypeDefaults.headlineSmall,
    titleLarge: TextStyle = MaterialTypeDefaults.titleLarge,
    titleMedium: TextStyle = MaterialTypeDefaults.titleMedium,
    titleSmall: TextStyle = MaterialTypeDefaults.titleSmall,
    bodyLarge: TextStyle = MaterialTypeDefaults.bodyLarge,
    bodyMedium: TextStyle = MaterialTypeDefaults.bodyMedium,
    bodySmall: TextStyle = MaterialTypeDefaults.bodySmall,
    labelLarge: TextStyle = MaterialTypeDefaults.labelLarge,
    labelMedium: TextStyle = MaterialTypeDefaults.labelMedium,
    labelSmall: TextStyle = MaterialTypeDefaults.labelSmall,
): OxygenTypography = OxygenTypography(
    displayLarge = displayLarge,
    appbarTitle = titleLarge,
    dialogTitle = headlineSmall,
    dialogAction = labelLarge,
    appBarSubtitle = bodyMedium,
    titleMedium = titleMedium,
    listItemTitle = bodyLarge,
    clockTime = headlineLarge,
    inputText = bodyLarge,
    bodyLarge = bodyLarge,
    bodyMedium = bodyMedium,
    labelMedium = labelMedium,
    caption = bodySmall,
    extras = OxygenTypographyExtras(displayMedium, displaySmall, headlineMedium, titleSmall, labelSmall),
)

private fun defaultExtras(template: TextStyle): OxygenTypographyExtras = OxygenTypographyExtras(
    displayMedium = MaterialTypeDefaults.displayMedium.copy(fontFamily = template.fontFamily),
    displaySmall = MaterialTypeDefaults.displaySmall.copy(fontFamily = template.fontFamily),
    headlineMedium = MaterialTypeDefaults.headlineMedium.copy(fontFamily = template.fontFamily),
    titleSmall = MaterialTypeDefaults.titleSmall.copy(fontFamily = template.fontFamily),
    labelSmall = MaterialTypeDefaults.labelSmall.copy(fontFamily = template.fontFamily),
)

private object MaterialTypeDefaults {
    val displayLarge = TextStyle(
        fontFamily = fontFamilyOf(GeneratedTokens.TYPE_DISPLAY_LARGE_FAMILY),
        fontSize = GeneratedTokens.TYPE_DISPLAY_LARGE_SIZE.sp,
        lineHeight = GeneratedTokens.TYPE_DISPLAY_LARGE_LINE_HEIGHT.sp,
        fontWeight = FontWeight(GeneratedTokens.TYPE_DISPLAY_LARGE_WEIGHT),
        letterSpacing = GeneratedTokens.TYPE_DISPLAY_LARGE_LETTER_SPACING.sp,
    )
    val displayMedium = TextStyle(
        fontFamily = fontFamilyOf(GeneratedTokens.TYPE_DISPLAY_MEDIUM_FAMILY),
        fontSize = GeneratedTokens.TYPE_DISPLAY_MEDIUM_SIZE.sp,
        lineHeight = GeneratedTokens.TYPE_DISPLAY_MEDIUM_LINE_HEIGHT.sp,
        fontWeight = FontWeight(GeneratedTokens.TYPE_DISPLAY_MEDIUM_WEIGHT),
        letterSpacing = GeneratedTokens.TYPE_DISPLAY_MEDIUM_LETTER_SPACING.sp,
    )
    val displaySmall = TextStyle(
        fontFamily = fontFamilyOf(GeneratedTokens.TYPE_DISPLAY_SMALL_FAMILY),
        fontSize = GeneratedTokens.TYPE_DISPLAY_SMALL_SIZE.sp,
        lineHeight = GeneratedTokens.TYPE_DISPLAY_SMALL_LINE_HEIGHT.sp,
        fontWeight = FontWeight(GeneratedTokens.TYPE_DISPLAY_SMALL_WEIGHT),
        letterSpacing = GeneratedTokens.TYPE_DISPLAY_SMALL_LETTER_SPACING.sp,
    )
    val headlineLarge = TextStyle(
        fontFamily = fontFamilyOf(GeneratedTokens.TYPE_HEADLINE_LARGE_FAMILY),
        fontSize = GeneratedTokens.TYPE_HEADLINE_LARGE_SIZE.sp,
        lineHeight = GeneratedTokens.TYPE_HEADLINE_LARGE_LINE_HEIGHT.sp,
        fontWeight = FontWeight(GeneratedTokens.TYPE_HEADLINE_LARGE_WEIGHT),
        letterSpacing = GeneratedTokens.TYPE_HEADLINE_LARGE_LETTER_SPACING.sp,
    )
    val headlineMedium = TextStyle(
        fontFamily = fontFamilyOf(GeneratedTokens.TYPE_HEADLINE_MEDIUM_FAMILY),
        fontSize = GeneratedTokens.TYPE_HEADLINE_MEDIUM_SIZE.sp,
        lineHeight = GeneratedTokens.TYPE_HEADLINE_MEDIUM_LINE_HEIGHT.sp,
        fontWeight = FontWeight(GeneratedTokens.TYPE_HEADLINE_MEDIUM_WEIGHT),
        letterSpacing = GeneratedTokens.TYPE_HEADLINE_MEDIUM_LETTER_SPACING.sp,
    )
    val headlineSmall = TextStyle(
        fontFamily = fontFamilyOf(GeneratedTokens.TYPE_HEADLINE_SMALL_FAMILY),
        fontSize = GeneratedTokens.TYPE_HEADLINE_SMALL_SIZE.sp,
        lineHeight = GeneratedTokens.TYPE_HEADLINE_SMALL_LINE_HEIGHT.sp,
        fontWeight = FontWeight(GeneratedTokens.TYPE_HEADLINE_SMALL_WEIGHT),
        letterSpacing = GeneratedTokens.TYPE_HEADLINE_SMALL_LETTER_SPACING.sp,
    )
    val titleLarge = TextStyle(
        fontFamily = fontFamilyOf(GeneratedTokens.TYPE_TITLE_LARGE_FAMILY),
        fontSize = GeneratedTokens.TYPE_TITLE_LARGE_SIZE.sp,
        lineHeight = GeneratedTokens.TYPE_TITLE_LARGE_LINE_HEIGHT.sp,
        fontWeight = FontWeight(GeneratedTokens.TYPE_TITLE_LARGE_WEIGHT),
        letterSpacing = GeneratedTokens.TYPE_TITLE_LARGE_LETTER_SPACING.sp,
    )
    val titleMedium = TextStyle(
        fontFamily = fontFamilyOf(GeneratedTokens.TYPE_TITLE_MEDIUM_FAMILY),
        fontSize = GeneratedTokens.TYPE_TITLE_MEDIUM_SIZE.sp,
        lineHeight = GeneratedTokens.TYPE_TITLE_MEDIUM_LINE_HEIGHT.sp,
        fontWeight = FontWeight(GeneratedTokens.TYPE_TITLE_MEDIUM_WEIGHT),
        letterSpacing = GeneratedTokens.TYPE_TITLE_MEDIUM_LETTER_SPACING.sp,
    )
    val titleSmall = TextStyle(
        fontFamily = fontFamilyOf(GeneratedTokens.TYPE_TITLE_SMALL_FAMILY),
        fontSize = GeneratedTokens.TYPE_TITLE_SMALL_SIZE.sp,
        lineHeight = GeneratedTokens.TYPE_TITLE_SMALL_LINE_HEIGHT.sp,
        fontWeight = FontWeight(GeneratedTokens.TYPE_TITLE_SMALL_WEIGHT),
        letterSpacing = GeneratedTokens.TYPE_TITLE_SMALL_LETTER_SPACING.sp,
    )
    val bodyLarge = TextStyle(
        fontFamily = fontFamilyOf(GeneratedTokens.TYPE_BODY_LARGE_FAMILY),
        fontSize = GeneratedTokens.TYPE_BODY_LARGE_SIZE.sp,
        lineHeight = GeneratedTokens.TYPE_BODY_LARGE_LINE_HEIGHT.sp,
        fontWeight = FontWeight(GeneratedTokens.TYPE_BODY_LARGE_WEIGHT),
        letterSpacing = GeneratedTokens.TYPE_BODY_LARGE_LETTER_SPACING.sp,
    )
    val bodyMedium = TextStyle(
        fontFamily = fontFamilyOf(GeneratedTokens.TYPE_BODY_MEDIUM_FAMILY),
        fontSize = GeneratedTokens.TYPE_BODY_MEDIUM_SIZE.sp,
        lineHeight = GeneratedTokens.TYPE_BODY_MEDIUM_LINE_HEIGHT.sp,
        fontWeight = FontWeight(GeneratedTokens.TYPE_BODY_MEDIUM_WEIGHT),
        letterSpacing = GeneratedTokens.TYPE_BODY_MEDIUM_LETTER_SPACING.sp,
    )
    val bodySmall = TextStyle(
        fontFamily = fontFamilyOf(GeneratedTokens.TYPE_BODY_SMALL_FAMILY),
        fontSize = GeneratedTokens.TYPE_BODY_SMALL_SIZE.sp,
        lineHeight = GeneratedTokens.TYPE_BODY_SMALL_LINE_HEIGHT.sp,
        fontWeight = FontWeight(GeneratedTokens.TYPE_BODY_SMALL_WEIGHT),
        letterSpacing = GeneratedTokens.TYPE_BODY_SMALL_LETTER_SPACING.sp,
    )
    val labelLarge = TextStyle(
        fontFamily = fontFamilyOf(GeneratedTokens.TYPE_LABEL_LARGE_FAMILY),
        fontSize = GeneratedTokens.TYPE_LABEL_LARGE_SIZE.sp,
        lineHeight = GeneratedTokens.TYPE_LABEL_LARGE_LINE_HEIGHT.sp,
        fontWeight = FontWeight(GeneratedTokens.TYPE_LABEL_LARGE_WEIGHT),
        letterSpacing = GeneratedTokens.TYPE_LABEL_LARGE_LETTER_SPACING.sp,
    )
    val labelMedium = TextStyle(
        fontFamily = fontFamilyOf(GeneratedTokens.TYPE_LABEL_MEDIUM_FAMILY),
        fontSize = GeneratedTokens.TYPE_LABEL_MEDIUM_SIZE.sp,
        lineHeight = GeneratedTokens.TYPE_LABEL_MEDIUM_LINE_HEIGHT.sp,
        fontWeight = FontWeight(GeneratedTokens.TYPE_LABEL_MEDIUM_WEIGHT),
        letterSpacing = GeneratedTokens.TYPE_LABEL_MEDIUM_LETTER_SPACING.sp,
    )
    val labelSmall = TextStyle(
        fontFamily = fontFamilyOf(GeneratedTokens.TYPE_LABEL_SMALL_FAMILY),
        fontSize = GeneratedTokens.TYPE_LABEL_SMALL_SIZE.sp,
        lineHeight = GeneratedTokens.TYPE_LABEL_SMALL_LINE_HEIGHT.sp,
        fontWeight = FontWeight(GeneratedTokens.TYPE_LABEL_SMALL_WEIGHT),
        letterSpacing = GeneratedTokens.TYPE_LABEL_SMALL_LETTER_SPACING.sp,
    )
}
