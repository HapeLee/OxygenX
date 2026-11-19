# TextField

Unofficial / draft. Import TextField and TextFieldDefaults from `io.oxygenx.ui.basic`. Prefer the native Foundation TextFieldState overload. String and TextFieldValue overloads remain available.

## Usage

Import rememberTextFieldState, InputTransformation and TextFieldLineLimits from `androidx.compose.foundation.text.input`. State owns text, selection and IME composition; no onValueChange callback or String synchronization is needed.

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

## State-based properties and defaults

| Property | Default / behavior |
| --- | --- |
| state | Required Foundation TextFieldState |
| modifier / enabled / readOnly | Modifier / true / false |
| textStyle | TextFieldDefaults.textStyle(); explicit style.color wins |
| label / placeholder / leadingIcon / trailingIcon / prefix / suffix / supportingText | null content slots |
| isError | false; visual error state; supply localized error semantics through Modifier when needed |
| inputTransformation / outputTransformation | null / null |
| keyboardOptions / onKeyboardAction | Default / null; native Foundation keyboard behavior |
| lineLimits | TextFieldLineLimits.Default |
| onTextLayout | null; Density.(getResult: () -> TextLayoutResult?) -> Unit |
| scrollState | rememberScrollState(); horizontal for SingleLine, vertical otherwise |
| shape / colors / contentPadding | TextFieldDefaults |
| interactionSource | null; internally remembered |
| motion | TextFieldDefaults.Motion → OxygenMotionDefaults.inputFocus() |

## M3 compatibility and differences

Common editing parameter names, types and behavior align with M3: transformations, keyboard action, line limits, scroll state and layout callback. They are passed to BasicTextField directly.

The visual API remains Oxygen-specific: fixed label above the editing row, a plain composable label slot, Oxygen TextFieldColors / shape / padding defaults, and an extra motion parameter. Material-only labelPosition and TextFieldLabelScope are not exposed. There is no floating-label behavior disguised behind unsupported parameters.

InputTransformation affects user edits, not programmatic state.edit calls. OutputTransformation changes display only; a temporary buffer determines placeholder visibility without committing changes to raw state or selection. Use SecureTextField for secure/password input when its privacy behavior is required; output formatting is not a security feature.

## Colors, slots and motion

All three overloads share slot styling. Label and supporting text receive caption typography, placeholder receives bodyMedium, prefix/suffix receive inputText. Secondary slots use onSurfaceVariant; disabled slots use disabledTextColor. Error label/supporting text/trailing icon use errorIndicatorColor. Explicit slot Text colors/styles remain higher priority.

Use [ProvideContentStyle and M3 interoperability](../theme.md) for mixed content. MOT-STATE-006 / spec.input.focus controls indicator color/thickness; parameters remain E0 / draft. Interruptions continue from the current value, with progress read during drawing and a fixed layout height.

## Legacy overloads

String and TextFieldValue retain onValueChange, visualTransformation, keyboardActions, singleLine, minLines and maxLines. They do not emulate TextFieldState by copying text. Their input text also respects explicit textStyle.color; their slots use the same defaults as the state overload.

## Regression

The catalog demonstrates digits-only input, bracketed output formatting, programmatic editing, raw state and an M3 Text inside an Oxygen Button. Desktop tests cover filtering, selection, display/raw separation, transformed placeholder visibility, IME callbacks, caller-owned scroll state, disabled/read-only semantics and slot colors. Light/dark slot PNGs guard layout and default colors. OnePlus 9R / ColorOS 11 device checks confirmed rejected alphabetic input, accepted digits, display-only formatting and programmatic reset. Full IME composition, TalkBack and performance acceptance remain pending.
