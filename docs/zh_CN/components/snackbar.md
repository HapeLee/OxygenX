# Snackbar

非官方 / draft。从 `io.oxygenx.ui.overlay` 导入 SnackbarHost、SnackbarHostState 和 SnackbarDuration。

## 用法

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

## 参数与默认值

| API | 默认值 / 行为 |
| --- | --- |
| showSnackbar.message | 必需文案 |
| actionLabel / withDismissAction | null / false |
| duration | Short；也支持 Long 和 Indefinite |
| dismiss() | 以 Dismissed 结束当前消息 |
| currentSnackbarData | 只读当前消息或 null |
| SnackbarHost.state / modifier | 必需状态 / Modifier |
| Snackbar.data / modifier | 必需数据 / Modifier |
| containerColor / contentColor / actionColor | inverseSurface / inverseOnSurface / contentColor |

## 排队与取消

调用串行排队。取消显示中的调用，会在 finally 清理数据后再放行下一条；取消队列中的调用不会关闭当前消息。宿主状态与调用协程应处于预期的 UI 生命周期内。

## 配色、无障碍与动效

Short/Long 基础停留时间是 4/10 秒。已挂载的宿主使用无障碍管理器建议超时，并计入操作按钮和关闭控件；宿主提供 polite live region 与语义关闭操作。Indefinite 消息需要操作按钮、关闭控件或显式取消。

当前 draft 宿主不实现进退场动效。Snackbar 使用反白容器配色，不为尚未实现的转场提供 motion 参数。

## 回归

Scaffold 能力页使用公开 SnackbarHost，并提供取消协程按钮。虚拟时间测试覆盖取消、排队、超时和无障碍超时计算。
