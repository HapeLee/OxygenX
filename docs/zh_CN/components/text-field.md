# TextField

非官方 / draft。从 `io.oxygenx.ui.basic` 导入 TextField 与 TextFieldDefaults。优先使用 Foundation 原生 TextFieldState 重载；String 与 TextFieldValue 重载继续保留。

## 用法

从 `androidx.compose.foundation.text.input` 导入 rememberTextFieldState、InputTransformation 与 TextFieldLineLimits。state 持有文本、选区和 IME composition，不需要 onValueChange 回调或 String 来回同步。

```kotlin
val state = rememberTextFieldState(initialText = "")
TextField(
    state = state,
    inputTransformation = InputTransformation {
        if (length > 20) revertAllChanges()
    },
    lineLimits = TextFieldLineLimits.SingleLine,
    label = { Text("Name") },
    onKeyboardAction = { performDefaultAction -> performDefaultAction() },
)
```

## 状态输入参数与默认值

| 参数 | 默认值 / 行为 |
| --- | --- |
| state | 必需的 Foundation TextFieldState |
| modifier / enabled / readOnly | Modifier / true / false |
| textStyle | TextFieldDefaults.textStyle()；显式 style.color 优先 |
| label / placeholder / leadingIcon / trailingIcon / prefix / suffix / supportingText | null 内容槽位 |
| isError | false；错误视觉状态，需要时通过 Modifier 提供本地化错误语义 |
| inputTransformation / outputTransformation | null / null |
| keyboardOptions / onKeyboardAction | Default / null；使用 Foundation 键盘默认行为 |
| lineLimits | TextFieldLineLimits.Default |
| onTextLayout | null；Density.(getResult: () -> TextLayoutResult?) -> Unit |
| scrollState | rememberScrollState()；SingleLine 横向，其余纵向 |
| shape / colors / contentPadding | TextFieldDefaults |
| interactionSource | null；内部 remember |
| motion | TextFieldDefaults.Motion → OxygenMotionDefaults.inputFocus() |

## M3 对齐范围与差异

通用编辑参数的名称、类型和行为对齐 M3：输入/输出变换、键盘动作、行数限制、滚动状态及布局回调，全部直接交给 BasicTextField。

视觉 API 保留 Oxygen 特征：固定在输入行上方的标签、普通 composable 标签槽、Oxygen 的 TextFieldColors / 形状 / 内边距默认值，以及额外的 motion 参数。不暴露 Material 专属 labelPosition、TextFieldLabelScope，也不提供实际没有实现的浮动标签参数。

InputTransformation 只影响用户输入，不过滤程序化 state.edit。OutputTransformation 只改变显示；占位判断用临时 buffer 预览变换，不写回原始 state 或选区。有安全/密码输入需求时，应使用具备对应隐私行为的 SecureTextField；输出格式化不是安全特性。

## 配色、槽位与动效

三个重载共用槽位样式。标签和辅助说明下发 caption，占位文字下发 bodyMedium，前后缀下发 inputText。次要槽位用 onSurfaceVariant，禁用槽位用 disabledTextColor；错误标签、辅助说明和行尾图标使用 errorIndicatorColor。槽内 Text 的显式颜色与样式仍优先。

混用内容见[ProvideContentStyle 与 M3 桥接](../theme.md)。指示线的颜色和粗细由 MOT-STATE-006 / spec.input.focus 控制，参数仍为 E0 / draft。中断后从当前值接续；进度在绘制阶段读取，布局高度固定。

## 旧重载

String 和 TextFieldValue 保留 onValueChange、visualTransformation、keyboardActions、singleLine、minLines、maxLines，不通过复制文本模拟 TextFieldState。它们同样保留显式 textStyle.color，槽位默认样式与 state 重载一致。

## 回归

catalog 演示仅数字输入、括号显示变换、程序化编辑、原始状态及 Oxygen 按钮里的 M3 Text。Desktop 测试覆盖过滤、选区、显示与原始状态分离、变换后的占位判断、IME 回调、外部滚动状态、禁用/只读语义和槽位配色。明暗两张槽位 PNG 保护布局与默认配色。OnePlus 9R / ColorOS 11 真机已检查字母拒绝、数字输入、只改变显示的格式化和程序化重置；完整 IME composition、TalkBack 与性能验收仍未完成。
