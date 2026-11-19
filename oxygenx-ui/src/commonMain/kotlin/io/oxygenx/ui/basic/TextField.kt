// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.basic

import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldDecorator
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.toTextFieldBuffer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.lerp
import io.oxygenx.motion.OxygenMotionDefaults
import io.oxygenx.motion.OxygenMotionSpec
import io.oxygenx.motion.rememberOxygenAnimation
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.theme.ProvideContentStyle

/**
 * 状态输入框：直接使用 Foundation 的编辑状态，不在 String 与 TextFieldState 间同步。
 *
 * 通用参数名称、类型与 M3 state-based TextField 对齐。标签保留 Oxygen 固定上浮布局，
 * 不提供 Material 专属 labelPosition / TextFieldLabelScope；额外暴露 [motion]。
 * 输入 / 输出变换、选区、键盘动作、滚动与行数限制全部交给 BasicTextField。
 *
 * - Spec ID: `CMP-TEXTFIELD`
 * - Baseline: `OnePlus 9R (LE2100) / ColorOS 11 / Settings 11 / 2026-11-14`
 * - Reference: `catalog/reference/CMP-TEXTFIELD/`
 * - Motion: `MOT-STATE-006`（聚焦指示线，E0 / draft）
 * - Status: `draft`
 *
 * @param state 唯一编辑状态；通过 state.edit 或 snapshotFlow 进行程序化编辑与观察。
 * @param modifier 输入框 Modifier。
 * @param enabled 是否可交互。
 * @param readOnly 可聚焦和选择，但不接受用户编辑。
 * @param textStyle 输入排版；显式 style.color 优先于 colors。
 * @param label 固定在输入行上方的标签槽，不参与浮动动画。
 * @param placeholder 输出变换后的显示内容为空时展示。
 * @param leadingIcon 行首图标。
 * @param trailingIcon 行尾图标。
 * @param prefix 文本前缀槽。
 * @param suffix 文本后缀槽。
 * @param supportingText 辅助说明；错误态默认下发错误色。
 * @param isError 错误视觉状态；可通过 Modifier 补充本地化错误语义。
 * @param inputTransformation 用户输入变换；不应用于程序化 state.edit。
 * @param outputTransformation 只变换显示内容，不改原始文本或选区。
 * @param keyboardOptions 软键盘配置。
 * @param onKeyboardAction IME 动作处理，null 使用 Foundation 默认行为。
 * @param lineLimits 单行 / 多行限制，替代 singleLine、minLines、maxLines。
 * @param onTextLayout 布局结果查询回调，签名与 Foundation / M3 一致。
 * @param scrollState 单行横向 / 多行纵向滚动状态。
 * @param shape 容器形状。
 * @param colors 输入文字、指示线与状态配色。
 * @param contentPadding 容器内边距。
 * @param interactionSource 外部交互源；null 时内部创建。
 * @param motion 聚焦动效，可替换并支持中断反向。
 */
@Composable
fun TextField(
    state: TextFieldState,
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
    inputTransformation: InputTransformation? = null,
    outputTransformation: OutputTransformation? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
    onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)? = null,
    scrollState: ScrollState = rememberScrollState(),
    shape: Shape = TextFieldDefaults.shape,
    colors: TextFieldColors = TextFieldDefaults.colors(),
    contentPadding: PaddingValues = TextFieldDefaults.contentPadding,
    interactionSource: MutableInteractionSource? = null,
    motion: OxygenMotionSpec = TextFieldDefaults.Motion,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    BasicTextField(
        state = state,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle.copy(color = textStyle.color.takeOrElse { colors.resolveTextColor(enabled, focused) }),
        inputTransformation = inputTransformation,
        outputTransformation = outputTransformation,
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        lineLimits = lineLimits,
        onTextLayout = onTextLayout,
        interactionSource = source,
        cursorBrush = SolidColor(colors.resolveCursorColor(isError)),
        scrollState = scrollState,
        decorator = TextFieldDecorator { innerTextField ->
            // 临时 buffer 只预览输出结果；不会写回 state、选区或 IME composition。
            val displayHasContent = if (outputTransformation == null) {
                state.text.isNotEmpty()
            } else {
                val buffer = state.toTextFieldBuffer()
                with(outputTransformation) { buffer.transformOutput() }
                buffer.length > 0
            }
            TextFieldShell(
                hasContent = displayHasContent,
                isFocused = focused,
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
                motion = motion,
                innerTextField = innerTextField,
            )
        },
    )
}

/**
 * 值输入兼容重载：支持 `String` / [TextFieldValue]；新调用优先使用 TextFieldState 重载。
 *
 * 视觉按一加「设备名称」编辑框复刻：容器无填充、无描边，只在底部画指示线；
 * 未聚焦取 `color.input.indicator.unfocused`、聚焦变粗并取 `color.on.surface`
 * （即真机上的黑色），错误态取 `color.error`。容器形状由 [shape] 决定（默认
 * `shape.corner.input` 的小圆角矩形），用于裁剪与后续填充色。
 *
 * label 固定在输入行上方，不实现 M3 的浮动标签过渡，
 * 因此状态仍为 `draft`。
 *
 * - Spec ID: `CMP-TEXTFIELD`
 * - Baseline: `OnePlus 9R (LE2100) / ColorOS 11 / Settings 11 / 2026-11-14`
 * - Reference: `catalog/reference/CMP-TEXTFIELD/`
 * - Motion: `MOT-STATE-006`（指示线粗细与颜色的过渡，E0 / draft）
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
 * @param supportingText 输入框下方的辅助说明；默认随错误态变色，文案由调用方提供。
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
 * @param motion 聚焦指示线动效；可替换规格，聚焦反转时从当前位置接续。
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
    motion: OxygenMotionSpec = TextFieldDefaults.Motion,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val isFocused = source.collectIsFocusedAsState().value

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle.copy(color = textStyle.color.takeOrElse { colors.resolveTextColor(enabled, isFocused) }),
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
                hasContent = visualTransformation.filter(androidx.compose.ui.text.AnnotatedString(value)).text.isNotEmpty(),
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
                motion = motion,
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
    motion: OxygenMotionSpec = TextFieldDefaults.Motion,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val isFocused = source.collectIsFocusedAsState().value

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle.copy(color = textStyle.color.takeOrElse { colors.resolveTextColor(enabled, isFocused) }),
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
                hasContent = visualTransformation.filter(value.annotatedString).text.isNotEmpty(),
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
                motion = motion,
                innerTextField = innerTextField,
            )
        },
    )
}

/** 三个重载共享固定标签、指示线与内容槽位契约。 */
@Composable
private fun TextFieldShell(
    hasContent: Boolean,
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
    motion: OxygenMotionSpec,
    innerTextField: @Composable () -> Unit,
) {
    val spacing = OxygenTheme.spacing
    val focusProgress = rememberOxygenAnimation(motion, initialValue = if (isFocused && enabled) 1f else 0f)
    LaunchedEffect(isFocused, enabled, focusProgress) {
        focusProgress.animateTo(if (isFocused && enabled) 1f else 0f)
    }
    val unfocusedIndicator = colors.resolveIndicatorColor(enabled, isError, focused = false)
    val focusedIndicator = colors.resolveIndicatorColor(enabled, isError, focused = true)
    val secondaryColor = if (enabled) OxygenTheme.colorScheme.onSurfaceVariant else colors.disabledTextColor
    val textColor = colors.resolveTextColor(enabled, isFocused)
    val labelColor = when {
        !enabled -> colors.disabledTextColor
        isError -> colors.errorIndicatorColor
        isFocused -> colors.focusedTextColor
        else -> secondaryColor
    }
    val supportingColor = if (enabled && isError) colors.errorIndicatorColor else secondaryColor
    val trailingColor = if (enabled && isError) colors.errorIndicatorColor else secondaryColor

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
                Box(modifier = Modifier.padding(end = spacing.gapSmall)) {
                    ProvideContentStyle(contentColor = secondaryColor) { leadingIcon() }
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                if (label != null) {
                    ProvideContentStyle(contentColor = labelColor, textStyle = OxygenTheme.typography.bodySmall) { label() }
                    Spacer(modifier = Modifier.height(spacing.gapXs))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (prefix != null) {
                        ProvideContentStyle(contentColor = textColor, textStyle = TextFieldDefaults.textStyle()) { prefix() }
                        Spacer(modifier = Modifier.width(spacing.gapXs))
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        if (!hasContent && placeholder != null) {
                            ProvideContentStyle(contentColor = secondaryColor, textStyle = OxygenTheme.typography.bodyMedium) { placeholder() }
                        }
                        innerTextField()
                    }
                    if (suffix != null) {
                        Spacer(modifier = Modifier.width(spacing.gapXs))
                        ProvideContentStyle(contentColor = textColor, textStyle = TextFieldDefaults.textStyle()) { suffix() }
                    }
                }
            }
            if (trailingIcon != null) {
                Box(modifier = Modifier.padding(start = spacing.gapSmall)) {
                    ProvideContentStyle(contentColor = trailingColor) { trailingIcon() }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(spacing.inputIndicatorThickness)
                .drawBehind {
                    // 指示线向输入区内加粗，布局高度固定；每帧只重绘，不触发重新测量。
                    val progress = focusProgress.value.coerceIn(0f, 1f)
                    val thickness = lerp(
                        spacing.inputIndicatorThickness,
                        spacing.inputIndicatorThicknessFocused,
                        progress,
                    ).toPx()
                    drawRect(
                        color = lerp(unfocusedIndicator, focusedIndicator, progress),
                        topLeft = Offset(0f, size.height - thickness),
                        size = Size(size.width, thickness),
                    )
                },
        )
        if (supportingText != null) {
            Box(modifier = Modifier.padding(top = spacing.gapXs)) {
                ProvideContentStyle(contentColor = supportingColor, textStyle = OxygenTheme.typography.bodySmall) { supportingText() }
            }
        }
    }
}

object TextFieldDefaults {
    val Motion: OxygenMotionSpec
        @Composable get() = OxygenMotionDefaults.inputFocus()

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
    fun textStyle(): TextStyle = OxygenTheme.typography.bodyLarge

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
