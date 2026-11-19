# Text

非官方 / draft。导入 `io.oxygenx.ui.basic.Text`，支持 String 和 AnnotatedString。

## 用法

```kotlin
Text("Styled", style = OxygenTheme.typography.bodyMedium.copy(color = Color.Red))
Text("Override", style = TextStyle(color = Color.Red), color = Color.Blue)
```

## 参数与默认值

| 参数 | 默认值 / 行为 |
| --- | --- |
| text | 必需的 String 或 AnnotatedString |
| modifier | Modifier |
| style | OxygenTheme.textStyle |
| color | Unspecified；显式 color → style.color → LocalContentColor → onSurface |
| fontSize / fontWeight / fontStyle / letterSpacing / lineHeight | null；继承 style |
| maxLines / minLines | Int.MAX_VALUE / 1 |
| softWrap / overflow | true / Clip |
| textAlign / onTextLayout | null；继承样式对齐 / 无回调 |
| autoSize | null；Foundation 原生自动字号 |
| inlineContent | emptyMap()；仅 AnnotatedString 重载 |
| fontFamily / textDecoration | null；继承 style |

## 行内内容与自动字号

```kotlin
val paragraph = buildAnnotatedString {
    append("Status ")
    appendInlineContent("icon", "[done]")
}
Text(
    text = paragraph,
    inlineContent = mapOf(
        "icon" to InlineTextContent(
            Placeholder(1.em, 1.em, PlaceholderVerticalAlign.Center),
        ) { Icon(OxygenIcons.Check, contentDescription = "Done") },
    ),
)
Text(
    "A long title",
    modifier = Modifier.fillMaxWidth(),
    maxLines = 1,
    autoSize = TextAutoSize.StepBased(
        minFontSize = OxygenTheme.typography.caption.fontSize,
        maxFontSize = OxygenTheme.typography.displayLarge.fontSize,
    ),
)
```

InlineTextContent、appendInlineContent 和 TextAutoSize 来自 Foundation，Placeholder 来自 UI text。整块占位需要交互时，子组件应撑满占位区域。未提供的 id 保留替代文本。自动字号受父布局约束，并优先于固定字号，可能增加测量次数；默认 null 保留固定字号。

## 配色与动效

保留样式颜色，显式 color 优先。按钮和顶栏槽位会下发各自的文本样式与内容色。Text 自身没有 motion 参数。

## 回归

Desktop 契约测试检查样式颜色与显式覆盖；组件矩阵检查 1×、2× 字体下按钮多行内容不裁切。

新增回归覆盖行内测量 / 点击、缺失 id 回退、两种文本自动字号、RTL / 大字体与字体装饰覆盖；明暗 PNG 保护静态布局。新增参数请使用命名调用，更新后重新编译依赖。
