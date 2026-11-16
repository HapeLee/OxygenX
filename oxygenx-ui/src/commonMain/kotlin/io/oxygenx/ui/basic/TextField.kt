// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.basic

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.lerp
import io.oxygenx.theme.OxygenTheme

/**
 * 输入框：参数与 M3 `TextField` 逐一对应（含 `String` / [TextFieldValue] 两个重载）。
 *
 * 视觉按一加「设备名称」编辑框复刻：容器无填充、无描边，只在底部画指示线；
 * 未聚焦取 `color.input.indicator.unfocused`、聚焦变粗并取 `color.on.surface`
 * （即真机上的黑色），错误态取 `color.error`。容器形状由 [shape] 决定（默认
 * `shape.corner.input` 的小圆角矩形），用于裁剪与后续填充色。
 *
 * label 采用 M3 的固定上浮布局（始终位于输入行上方），浮动过渡动画尚未实现，
 * 因此状态仍为 `draft`。
 *
 * - Spec ID: `CMP-TEXTFIELD`
 * - Baseline: `OnePlus 9R (LE2100) / ColorOS 11 / Settings 11 / 2026-11-14`
 * - Reference: `catalog/reference/CMP-TEXTFIELD/`
 * - Motion: `MOT-STATE-002`（指示线粗细与颜色的过渡）
 * - Status: `draft`
 *
 * @param value 当前文本。
 * @param onValueChange 文本变化回调。
 * @param modifier 外部 Modifier。
 * @param enabled 是否可交互；禁用时指示线与文字取禁用色。
 * @param readOnly 只读时仍可聚焦与选择，但不接受输入。
 * @param textStyle 输入文字样式。
 * @param label 输入行上方的标签；传 `null` 表示不显示。
 * @param placeholder 内容为空时的占位文字。
 * @param leadingIcon 行首图标。
 * @param trailingIcon 行尾图标。
 * @param prefix 文字前缀。
 * @param suffix 文字后缀。
 * @param supportingText 输入框下方的辅助说明，错误态由调用方切换文案与颜色。
 * @param isError 是否错误态。
 * @param visualTransformation 输入内容变换（如密码遮罩）。
 * @param keyboardOptions 软键盘配置。
 * @param keyboardActions 软键盘动作。
 * @param singleLine 是否单行。
 * @param maxLines 最大行数。
 * @param minLines 最小行数。
 * @param interactionSource 交互状态源，用于外部观察聚焦；`null` 时内部创建。
 * @param shape 容器形状。
 * @param contentPadding 容器内边距，默认 [TextFieldDefaults.contentPadding]。
 * @param colors 颜色集合。
 */
@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextFieldDefaults.textStyle(),
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = TextFieldDefaults.shape,
    contentPadding: PaddingValues = TextFieldDefaults.contentPadding,
    colors: TextFieldColors = TextFieldDefaults.colors(),
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val isFocused = source.collectIsFocusedAsState().value

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle.copy(color = colors.resolveTextColor(enabled, isFocused)),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        visualTransformation = visualTransformation,
        interactionSource = source,
        cursorBrush = SolidColor(colors.resolveCursorColor(isError)),
        decorationBox = { innerTextField ->
            TextFieldShell(
                value = value,
                isFocused = isFocused,
                enabled = enabled,
                isError = isError,
                label = label,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                prefix = prefix,
                suffix = suffix,
                supportingText = supportingText,
                shape = shape,
                contentPadding = contentPadding,
                colors = colors,
                innerTextField = innerTextField,
            )
        },
    )
}

/**
 * [TextFieldValue] 重载：保留选区与组合区间，参数含义与 [TextField] 一致。
 *
 * @param value 当前文本与选区。
 * @param onValueChange 文本或选区变化回调。
 */
@Composable
fun TextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextFieldDefaults.textStyle(),
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = TextFieldDefaults.shape,
    contentPadding: PaddingValues = TextFieldDefaults.contentPadding,
    colors: TextFieldColors = TextFieldDefaults.colors(),
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val isFocused = source.collectIsFocusedAsState().value

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle.copy(color = colors.resolveTextColor(enabled, isFocused)),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        visualTransformation = visualTransformation,
        interactionSource = source,
        cursorBrush = SolidColor(colors.resolveCursorColor(isError)),
        decorationBox = { innerTextField ->
            TextFieldShell(
                value = value.text,
                isFocused = isFocused,
                enabled = enabled,
                isError = isError,
                label = label,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                prefix = prefix,
                suffix = suffix,
                supportingText = supportingText,
                shape = shape,
                contentPadding = contentPadding,
                colors = colors,
                innerTextField = innerTextField,
            )
        },
    )
}

/** 指示线 + 标签 + 图标 + 前后缀的共同布局，两个重载共用。 */
@Composable
private fun TextFieldShell(
    value: String,
    isFocused: Boolean,
    enabled: Boolean,
    isError: Boolean,
    label: @Composable (() -> Unit)?,
    placeholder: @Composable (() -> Unit)?,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    prefix: @Composable (() -> Unit)?,
    suffix: @Composable (() -> Unit)?,
    supportingText: @Composable (() -> Unit)?,
    shape: Shape,
    contentPadding: PaddingValues,
    colors: TextFieldColors,
    innerTextField: @Composable () -> Unit,
) {
    val spacing = OxygenTheme.spacing
    val focusProgress by animateFloatAsState(
        targetValue = if (isFocused) 1f else 0f,
        label = "oxygenInputFocus",
    )
    val indicatorColor = lerp(
        colors.resolveIndicatorColor(enabled, isError, focused = false),
        colors.resolveIndicatorColor(enabled, isError, focused = true),
        focusProgress,
    )
    val indicatorThickness = lerp(
        spacing.inputIndicatorThickness,
        spacing.inputIndicatorThicknessFocused,
        focusProgress,
    )
    val hasContent = value.isNotEmpty()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            // 一加的编辑框只有 40dp 高，用最小高度 + 垂直内边距：无 label 时正好 40dp，
            // 有 label 时由 label 行自然撑开，不再套 M3 的固定 56dp。
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = spacing.inputHeight)
                .clip(shape)
                .background(colors.resolveContainerColor(enabled))
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leadingIcon != null) {
                Box(modifier = Modifier.padding(end = spacing.gapSmall)) { leadingIcon() }
            }
            Column(modifier = Modifier.weight(1f)) {
                if (label != null) {
                    label()
                    Spacer(modifier = Modifier.height(spacing.gapXs))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (prefix != null) {
                        prefix()
                        Spacer(modifier = Modifier.width(spacing.gapXs))
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        if (!hasContent && placeholder != null) {
                            placeholder()
                        }
                        innerTextField()
                    }
                    if (suffix != null) {
                        Spacer(modifier = Modifier.width(spacing.gapXs))
                        suffix()
                    }
                }
            }
            if (trailingIcon != null) {
                Box(modifier = Modifier.padding(start = spacing.gapSmall)) { trailingIcon() }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(indicatorThickness)
                .background(indicatorColor),
        )
        if (supportingText != null) {
            Box(modifier = Modifier.padding(top = spacing.gapXs)) { supportingText() }
        }
    }
}

object TextFieldDefaults {
    /** 容器高度。 */
    val Height: Dp
        @Composable get() = OxygenTheme.spacing.inputHeight

    /** 小圆角矩形形状。 */
    val shape: Shape
        @Composable get() = OxygenTheme.shapes.cornerInput

    /**
     * 容器内边距。一加的编辑框文字贴底（真机「设备名称」输入行 111px 高、墨迹距指示线
     * 仅 17px），弹窗里把上下内边距换成「顶部占满、底部为 0」即可复刻。
     */
    val contentPadding: PaddingValues
        @Composable get() = PaddingValues(
            horizontal = OxygenTheme.spacing.cardPadding,
            vertical = OxygenTheme.spacing.gapSmall,
        )

    /** 输入文字默认样式。 */
    @Composable
    fun textStyle(): TextStyle = OxygenTheme.typography.inputText

    /** 占位文字样式：按 M3 复用 `color.on.surface.variant`（与正文次要色同一角色）。 */
    @Composable
    fun placeholderStyle(): TextStyle = OxygenTheme.typography.bodyMedium.copy(
        color = OxygenTheme.colorScheme.onSurfaceVariant,
    )

    @Composable
    fun colors(
        textColor: Color = OxygenTheme.colorScheme.onSurface,
        focusedTextColor: Color = OxygenTheme.colorScheme.onSurface,
        disabledTextColor: Color = OxygenTheme.colorScheme.onSurfaceDisabled,
        cursorColor: Color = OxygenTheme.colorScheme.primary,
        errorCursorColor: Color = OxygenTheme.colorScheme.error,
        indicatorColor: Color = OxygenTheme.colorScheme.inputIndicatorUnfocused,
        focusedIndicatorColor: Color = OxygenTheme.colorScheme.onSurface,
        disabledIndicatorColor: Color = OxygenTheme.colorScheme.outlineVariant,
        errorIndicatorColor: Color = OxygenTheme.colorScheme.error,
        containerColor: Color = Color.Transparent,
        disabledContainerColor: Color = Color.Transparent,
    ): TextFieldColors = remember(
        textColor,
        focusedTextColor,
        disabledTextColor,
        cursorColor,
        errorCursorColor,
        indicatorColor,
        focusedIndicatorColor,
        disabledIndicatorColor,
        errorIndicatorColor,
        containerColor,
        disabledContainerColor,
    ) {
        TextFieldColors(
            textColor = textColor,
            focusedTextColor = focusedTextColor,
            disabledTextColor = disabledTextColor,
            cursorColor = cursorColor,
            errorCursorColor = errorCursorColor,
            indicatorColor = indicatorColor,
            focusedIndicatorColor = focusedIndicatorColor,
            disabledIndicatorColor = disabledIndicatorColor,
            errorIndicatorColor = errorIndicatorColor,
            containerColor = containerColor,
            disabledContainerColor = disabledContainerColor,
        )
    }
}

@Immutable
data class TextFieldColors(
    val textColor: Color,
    val focusedTextColor: Color,
    val disabledTextColor: Color,
    val cursorColor: Color,
    val errorCursorColor: Color,
    val indicatorColor: Color,
    val focusedIndicatorColor: Color,
    val disabledIndicatorColor: Color,
    val errorIndicatorColor: Color,
    val containerColor: Color,
    val disabledContainerColor: Color,
) {
    @Stable
    internal fun resolveTextColor(enabled: Boolean, focused: Boolean): Color = when {
        !enabled -> disabledTextColor
        focused -> focusedTextColor
        else -> textColor
    }

    @Stable
    internal fun resolveCursorColor(isError: Boolean): Color = if (isError) errorCursorColor else cursorColor

    @Stable
    internal fun resolveIndicatorColor(enabled: Boolean, isError: Boolean, focused: Boolean): Color = when {
        !enabled -> disabledIndicatorColor
        isError -> errorIndicatorColor
        focused -> focusedIndicatorColor
        else -> indicatorColor
    }

    @Stable
    internal fun resolveContainerColor(enabled: Boolean): Color = if (enabled) containerColor else disabledContainerColor
}
