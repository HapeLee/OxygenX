// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.BasicText
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
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.Icon
import io.oxygenx.ui.basic.TextField
import io.oxygenx.ui.basic.TextFieldDefaults

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

    DemoCard {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.gapLarge)) {
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
