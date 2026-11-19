# Text

Unofficial / draft. Import `io.oxygenx.ui.basic.Text`. Supports String and AnnotatedString.

## Usage

```kotlin
Text("Styled", style = OxygenTheme.typography.bodyMedium.copy(color = Color.Red))
Text("Override", style = TextStyle(color = Color.Red), color = Color.Blue)
```

## Properties and defaults

| Property | Default / behavior |
| --- | --- |
| text | Required String or AnnotatedString |
| modifier | Modifier |
| style | OxygenTheme.textStyle |
| color | Unspecified; explicit color → style.color → LocalContentColor → onSurface |
| fontSize / fontWeight / fontStyle / letterSpacing / lineHeight | null; inherit style |
| maxLines / minLines | Int.MAX_VALUE / 1 |
| softWrap / overflow | true / Clip |
| textAlign / onTextLayout | null; style alignment / no callback |
| autoSize | null; native Foundation automatic sizing |
| inlineContent | emptyMap(); AnnotatedString overload only |
| fontFamily / textDecoration | null; inherit style |

## Inline content and automatic sizing

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

InlineTextContent, appendInlineContent and TextAutoSize come from Foundation; Placeholder comes from UI text. Children should fill their placeholder when the full region is interactive. Missing ids keep alternate text. Auto sizing uses parent constraints and takes precedence over a fixed font size; it may perform extra measurements. Default null keeps fixed sizing.

## Colors and motion

Style color is preserved. An explicit color overrides it. Button and app-bar slots supply their own text style and content color. Text itself has no motion parameter.

## Regression

The Desktop contract test checks style color and explicit override. The component matrix checks multiline button content at 1× and 2× font scale.

Additional regressions cover inline measurement/clicks, missing-id fallback, auto sizing for both overloads, RTL/large fonts and font/decoration overrides. Light/dark PNGs guard static layout. Use named arguments for new properties and recompile dependents.
