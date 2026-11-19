# Snackbar

Unofficial / draft. Import SnackbarHost, SnackbarHostState and SnackbarDuration from `io.oxygenx.ui.overlay`.

## Usage

```kotlin
val host = remember { SnackbarHostState() }
val scope = rememberCoroutineScope()
Scaffold(snackbarHost = { SnackbarHost(host) }) {
    Button(onClick = {
        scope.launch {
            host.showSnackbar("Saved", withDismissAction = true)
        }
    }) { Text("Save") }
}
```

## Properties and defaults

| API | Default / behavior |
| --- | --- |
| showSnackbar.message | Required text |
| actionLabel / withDismissAction | null / false |
| duration | Short; Long and Indefinite also available |
| dismiss() | Completes the visible message with Dismissed |
| currentSnackbarData | Read-only current message or null |
| SnackbarHost.state / modifier | Required host state / Modifier |
| Snackbar.data / modifier | Required data / Modifier |
| containerColor / contentColor / actionColor | inverseSurface / inverseOnSurface / contentColor |

## Queue and cancellation

Calls are serialized. Cancelling the visible call clears its data in finally before the next call runs. Cancelling a queued call does not dismiss the visible message. Keep the host state and calling scope within the intended UI lifetime.

## Colors, accessibility and motion

Short/Long base durations are 4/10 seconds. A mounted host applies the accessibility manager's recommended timeout, including action/dismiss controls. The host exposes a polite live region and semantic dismiss action. Indefinite messages need an action, dismiss control or explicit cancellation.

This draft host does not animate entry/exit. Snackbar uses inverse surface colors; no motion parameter is offered for a transition it does not implement.

## Regression

The Scaffold capability catalog uses the public SnackbarHost and offers a cancellation button. Virtual-time tests cover cancellation, queueing, timeouts and accessibility timeout calculation.
