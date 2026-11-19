// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.ui.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.oxygenx.icons.icon.OxygenIcons
import io.oxygenx.theme.LocalContentColor
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.theme.ProvideContentStyle
import io.oxygenx.theme.generated.GeneratedTokens

/**
 * 搜索栏：药丸容器里的单行输入，左侧搜索图标、右侧动作槽，回车触发 [onSearch]。
 *
 * 与 [TextField] 的分工：`TextField` 是一加设置页的「下划线输入行」，用于表单；
 * 搜索栏是独立的紧凑药丸容器（48dp 高、两侧内边距 12dp、底色 `surfaceContainerLow`），
 * 用于列表页顶部。它不持有状态，`value` / `onValueChange` 由调用方传入。
 *
 * - Spec ID: `CMP-SEARCH-BAR`
 * - Baseline: `Legado Material 3 紧凑搜索输入框`
 * - Reference: `catalog/reference/CMP-SEARCH-BAR/`
 * - Motion: 无（展开 / 收起由宿主页面决定，本组件只画一行）。
 * - Status: `draft`
 *
 * @param value 当前输入。
 * @param onValueChange 输入变化回调。
 * @param modifier 外部 Modifier。
 * @param placeholder 空态提示文案。
 * @param onSearch 键盘搜索键回调；`null` 时搜索键只收起键盘。
 * @param enabled 是否可输入。
 * @param leadingIcon 左侧图标槽，`null` 时用搜索图标。
 * @param trailingIcon 右侧动作槽（清除 / 语音 / 筛选）。
 */
@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "搜索",
    onSearch: (() -> Unit)? = null,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    val spacing = OxygenTheme.spacing
    val colors = OxygenTheme.colorScheme
    val contentColor = if (enabled) colors.onSurface else colors.onSurfaceDisabled
    val iconColor = if (enabled) colors.onSurfaceVariant else colors.onSurfaceDisabled

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(spacing.searchBarHeight),
        color = colors.surfaceContainerLow,
        shape = OxygenTheme.shapes.pill,
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = GeneratedTokens.SPACE_SEARCH_BAR_PADDING_HORIZONTAL.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CompositionLocalProvider(LocalContentColor provides iconColor) {
                if (leadingIcon != null) {
                    leadingIcon()
                } else {
                    Icon(imageVector = OxygenIcons.Search, contentDescription = null)
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = spacing.gapSmall),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isEmpty()) {
                    ProvideContentStyle(
                        contentColor = iconColor,
                        textStyle = OxygenTheme.typography.bodyLarge,
                    ) {
                        Text(text = placeholder, maxLines = 1)
                    }
                }
                ProvideContentStyle(
                    contentColor = contentColor,
                    textStyle = OxygenTheme.typography.bodyLarge,
                ) {
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = enabled,
                        singleLine = true,
                        textStyle = OxygenTheme.typography.bodyLarge.copy(
                            color = resolveContentColor(contentColor),
                        ),
                        cursorBrush = SolidColor(colors.primary),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { onSearch?.invoke() }),
                    )
                }
            }
            if (trailingIcon != null) {
                CompositionLocalProvider(LocalContentColor provides iconColor) {
                    trailingIcon()
                }
            }
        }
    }
}
