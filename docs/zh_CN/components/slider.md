# Slider

非官方 / draft。导入 `io.oxygenx.ui.basic.Slider`。值由调用方持有。

## 用法

```kotlin
var value by remember { mutableFloatStateOf(0.5f) }
Slider(
    value = value,
    onValueChange = { value = it },
    valueRange = 0f..1f,
    steps = 3,
    onValueChangeFinished = { save(value) },
)
```

## 参数与默认值

| 参数 | 默认值 / 行为 |
| --- | --- |
| value / onValueChange | 必需的有限值与更新回调 |
| modifier / enabled | Modifier / true |
| valueRange | 0f..1f；端点有限且有序 |
| steps | 0 连续；正数表示中间档位数，3 对应 5 个值 |
| colors / motion | SliderDefaults.colors() / SliderDefaults.Motion |
| interactionSource | null；内部 remember |
| onValueChangeFinished | null；触摸结束、键盘有效步进或无障碍提交 |
| track / thumb | null；可选的 SliderScope 槽位 |

## 输入契约

RTL 镜像轨道、触摸坐标和自定义拇指位置。左右箭头跟随物理方向，上下箭头增减。Home/End 选择区间端点，Page Up/Down 调整约 10% 的间隔数。连续滑块的键盘调节使用 100 个间隔。

无障碍 SetProgress 会吸附并夹紧值；值未变返回 false，有变化时两个回调各触发一次。禁用态不处理输入。键盘焦点以拇指圆环显示。

## 配色与动效

轨道、拇指、档位点和波纹颜色来自 SliderDefaults.colors()。拇指直接跟随输入，motion 控制按压波纹。自定义轨道收到逻辑 fraction，应自行处理 RTL 绘制，普通 start 对齐布局会自然镜像。默认轨道与自定义拇指位置由组件镜像。

## 回归

catalog 包含带当前值和提交值的 RTL 示例。Desktop 测试覆盖触摸、键盘、禁用态、无障碍提交、自定义拇指位置，以及四张明暗 × LTR/RTL PNG golden。
