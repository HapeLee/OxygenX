// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.icons.icon.OxygenIcons
import io.oxygenx.material3.ProvideMaterial3Content
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Button
import io.oxygenx.ui.basic.Icon
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.basic.TextField
import io.oxygenx.ui.basic.TextFieldDefaults
import androidx.compose.material3.Text as MaterialText

fun LazyListScope.textFieldSection() {
    item(key = "cmp-textfield") {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapSmall),
        ) {
            SmallTitle(text = "TextField / 输入框", specId = "CMP-TEXTFIELD")
            TextFieldVariants()
        }
    }
}

@Composable
private fun TextFieldVariants() {
    val typography = OxygenTheme.typography
    val colors = OxygenTheme.colorScheme
    val spacing = OxygenTheme.spacing
    var name by remember { mutableStateOf("OnePlus 9R") }
    var empty by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("oxygenx") }
    var password by remember { mutableStateOf("123456") }
    var query by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val emailError = email.isNotEmpty() && !email.contains("@")
    val state = rememberTextFieldState(initialText = "123")
    val digitsOnly = remember { InputTransformation { if (asCharSequence().any { !it.isDigit() }) revertAllChanges() } }
    val bracketed = remember {
        OutputTransformation {
            replace(0, 0, "[")
            append("]")
        }
    }

    DemoCard {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.gapLarge)) {
            DemoLabeledField(title = "State-based / 仅数字输入，括号只改变显示") {
                TextField(
                    state = state,
                    inputTransformation = digitsOnly,
                    outputTransformation = bracketed,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    label = { Text("编号") },
                    supportingText = { Text("原始值：${state.text}；通过 state.edit 编辑不经过用户输入过滤") },
                )
                ProvideMaterial3Content {
                    Button(onClick = { state.edit { replace(0, length, "456") } }) {
                        MaterialText("M3 Text 继承 Oxygen 按钮样式 / 重置为 456")
                    }
                }
            }
            DemoLabeledField(title = "主题继承 / 样式颜色回归") {
                OxygenTheme(typography = typography) {
                    Text("仅覆盖排版后，仍应继承当前主题", style = typography.bodyMedium)
                    Text("此行应为强调色", style = typography.bodyMedium.copy(color = colors.primary))
                }
            }
            DemoLabeledField(title = "已填值 · 对照一加「设备名称」编辑框") {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                )
            }
            DemoLabeledField(title = "占位文字") {
                TextField(
                    value = empty,
                    onValueChange = { empty = it },
                    singleLine = true,
                    placeholder = {
                        BasicText(
                            text = "请输入内容",
                            style = TextFieldDefaults.placeholderStyle(),
                        )
                    },
                )
            }
            DemoLabeledField(title = "label / leadingIcon / trailingIcon") {
                TextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    label = {
                        BasicText(
                            text = "搜索",
                            style = typography.caption.copy(color = colors.onSurfaceVariant),
                        )
                    },
                    placeholder = {
                        BasicText(
                            text = "输入关键词",
                            style = TextFieldDefaults.placeholderStyle(),
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = OxygenIcons.Search,
                            contentDescription = null,
                            tint = colors.onSurfaceVariant,
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = OxygenIcons.Check,
                            contentDescription = null,
                            tint = colors.primary,
                        )
                    },
                )
            }
            DemoLabeledField(title = "错误态 + supportingText") {
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    singleLine = true,
                    isError = emailError,
                    supportingText = {
                        BasicText(
                            text = if (emailError) "请输入有效的邮箱地址" else "用于找回密码",
                            style = typography.caption.copy(
                                color = if (emailError) colors.error else colors.onSurfaceVariant,
                            ),
                        )
                    },
                )
            }
            DemoLabeledField(title = "密码遮罩") {
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                )
            }
            DemoLabeledField(title = "禁用态") {
                TextField(
                    value = "禁用内容",
                    onValueChange = {},
                    singleLine = true,
                    enabled = false,
                )
            }
            DemoLabeledField(title = "多行（minLines = 3 / maxLines = 5）") {
                TextField(
                    value = note,
                    onValueChange = { note = it },
                    minLines = 3,
                    maxLines = 5,
                    placeholder = {
                        BasicText(
                            text = "多行备注",
                            style = TextFieldDefaults.placeholderStyle(),
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun DemoLabeledField(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapXs)) {
        BasicText(
            text = title,
            style = OxygenTheme.typography.caption.copy(
                color = OxygenTheme.colorScheme.onSurfaceVariant,
            ),
        )
        content()
    }
}
