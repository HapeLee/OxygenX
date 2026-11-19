# 动效规范

本项目以动效复刻为核心，因此本文是**最严格**的细则。所有数值来自 `design/tokens/motion.yaml` 并可追溯到 reference；**本文件不给出现成的 OxygenOS 时长或弹簧参数**，因为那需要先有测量凭据。

## 1. 原则

1. **可追溯**：每个动效有 `Spec ID`、`targetBaseline`、`reference`、证据等级。没有凭据的参数只能是 `E0` 占位。
2. **参数化**：所有动效通过 `OxygenMotionSpec` 暴露给调用方与 catalog，不在组件内部封死。
3. **连续性**：任何动效在任意时刻可被打断、反向、重定向，且从当前值接续，不回到起点。
4. **可降级**：平台能力缺失时，按预定义规则降级，不崩溃、不跳帧。
5. **可测量**：catalog 提供并排对比与关键帧对照；性能有帧预算与基准，不接受主观「挺顺滑」。

## 2. 分类与 Spec ID

| 类别 | 覆盖范围 | ID 前缀 |
| --- | --- | --- |
| 状态反馈 | 按压、开关、选中、展开/折叠、涟漪与高亮 | `MOT-STATE-` |
| 进场 / 退场 | 弹窗、底部面板、Toast、Snackbar、菜单 | `MOT-ENTER-` |
| 容器 / 共享元素转场 | 应用启动、页面切换、共享元素、预测式返回 | `MOT-TRANS-` |
| 手势驱动 | 拖拽、滑动关闭、下拉、可拖动面板、惯性释放 | `MOT-GESTURE-` |
| 滚动与列表 | 置顶栏、吸顶、列表项插入/删除、过度滚动 | `MOT-SCROLL-` |
| 系统级场景 | 通知栏、快捷设置、音量面板、指纹、充电、AOD、最近任务 | `MOT-SYSTEM-` |
| 进度与加载 | 不确定进度、骨架、刷新 | `MOT-PROGRESS-` |
| 图标与插画 | 图标形变、路径动画、插画循环 | `MOT-ICON-` |

ID 形如 `MOT-TRANS-001`。新增动效先在 P2 建立的 `design/motion-registry.yaml` 登记表中占号，注明类别、`targetBaseline`、reference 路径与状态；ID 不复用。

## 3. 动效令牌

与静态设计令牌共用 `design/tokens/` 真源和 `generateTokens` 流水线（见 [design-tokens.md](./design-tokens.md)）。

### 3.1 令牌类型

| 类型 | 字段 | 说明 |
| --- | --- | --- |
| `duration` | `millis` | 仅用于「固定时长」动效；不得直接被组件引用，须经 `MotionSpec` 组合 |
| `easing` | `cubicBezier` | 命名曲线族（standard / decelerate / accelerate / emphasized） |
| `spring` | `stiffness`、`dampingRatio`、`visibilityThreshold` | 物理参数，不做「弹性系数」式的无量纲近似 |
| `stagger` | `delayMillis`、`order` | 编排错峰 |
| `motionSpec` | 组合上述令牌 + `enter` / `exit` 对称定义 | 组件实际消费的单位 |

组件**只消费 `motionSpec`**，不直接引用 duration/easing/spring 原子令牌，以保证语义一致。

### 3.2 YAML 结构示意

```yaml
# design/tokens/motion.yaml —— 结构示意，数值均为 E0 占位，不是已验证参数
tokens:
  - id: spring.snappy
    kind: spring
    stiffness: 400               # TODO(E0): 由逐帧位移曲线拟合后替换
    dampingRatio: 0.85
    visibilityThreshold: 0.001
    baseline: OxygenOS <version> / <device> / <date>
    source: catalog/reference/MOT-STATE-002/measurements.csv
    evidence: E2
    status: draft
  - id: spec.switch.toggle
    kind: motionSpec
    enterSpec: spring.snappy
    exitSpec: spring.snappy
    baseline: OxygenOS <version> / <device> / <date>
    source: catalog/reference/MOT-STATE-002/README.md
    evidence: E0
    status: draft
```

`motionSpec` 用 `enterSpec` / `exitSpec` 引用其他令牌 ID，不内联参数。生成器校验：两类字段存在、`verified` 具备 `E2+` 证据与有效 reference；引用完整性在 P2 引入 `OxygenMotion` 解析器时校验。令牌文件的扁平格式约束见 [design-tokens.md](./design-tokens.md) §3。

### 3.3 封装

- 统一走 `oxygenx-motion` 的 `OxygenMotion.*` 封装（`springOf(...)` / `easingOf(...)` / `motionSpecOf(...)`），组件不得直接调用 `tween()`、`spring()`、`keyframes()` 带字面量参数。
- `OxygenMotionDefaults.<name>()` 作为组件 `motion` 参数的默认值，与 `ComponentDefaults` 分开维护。
- 动画时间只由规格本身决定：**禁止**用 `Thread.sleep`、全局倍率或 `CompositionLocal` 改写动画时长。

## 4. 组件动效契约

- 每个会动的组件签名含 `motion: OxygenMotionSpec = OxygenMotionDefaults.<name>()`（参数位置见 [component-api.md](./component-api.md)）。
- `OxygenMotionSpec` 必须能被 catalog 实时调参（曲线、弹簧刚度/阻尼、错峰）并能被单测以固定时钟驱动。
- 一个 `Spec ID` 对应一份规格；多个组件共用同一动效时可以共享 `motionSpec` 令牌，但各自仍需登记 `Spec ID` 说明使用场景。
- 动画状态归组件所有（或由 `rememberXxxState` 托管），调用方通过 `motion` 覆盖表现，**不通过**传入 `AnimationState` 泄漏内部实现。

## 5. 实现纪律

### 可中断与连续

- 用 `Animatable` / 可 seek 的过渡管理单一真值；重复触发用 `animateTo`/`snapTo` 从当前值接续，禁止 `LaunchedEffect(key) { animate... }` 在每次变化时重建动画。
- 手势结束必须把释放速度传入弹簧（`initialVelocity`），不允许「松手后重新播一段固定动画」。
- 支持中断时反向（如预测式返回、上滑取消底部面板），并验证连续快速反向的边界。
- 共享元素转场的几何量（位置、尺寸、圆角、裁剪）在整个过程中连续；容器变换与内容淡入分层，不允许内容瞬间出现在终点。

### 生命周期

- 无限循环动画必须在离开组合时取消；`DisposableEffect` / `rememberCoroutineScope` 的 scope 由 Compose 管理，禁止自建全局 scope。
- `LaunchedEffect` 的 key 只包含 effect 体内真正读取的值；长生命周期闭包用 `rememberUpdatedState` 取最新值。

### 编排

- `enter` / `exit` 对称：同一 `motionSpec` 声明两者，禁止只定义一方而在调用处临时补 `tween`。
- 多元素动画通过 `stagger` 令牌错峰，不在组件里写 `delay(index * 30L)`。
- 进出场同时进行时（容器变换），明确 z 序与裁剪边界，避免叠加期出现重影或透底。

## 6. 平台能力降级

能力由 `LocalOxygenCapabilities` 暴露：`blur`、`renderEffect`、`haptics`、`highRefreshRate`、`sharedElementTransition` 等。

- 每个动效规格必须声明「能力不足时的替代表现」，替代品同样来自令牌（例如用不透明度/位移替代模糊过渡），不允许静默变成「无动画」。
- Android 是唯一 full-fidelity 平台；Desktop/Web 允许视觉近似，但**时序与曲线不得改变**，否则逐帧对比失去意义。
- 触觉反馈属于动效的一部分：在**点击回调**里触发，不在状态变化时触发；强度与类型同样走令牌，并受 capability 与系统设置约束。

## 7. 可访问性

- 动画只做增强：状态必须有静态可辨的差异（选中 / 禁用 / 进度各自有形状或颜色差异），动画不能是信息的唯一载体。
- 本项目**不实现**系统「减少动态效果」降级，也不提供时间倍率 / 慢放开关（`oxygenx-motion` 没有对应的 `CompositionLocal`）；逐帧核验改用 reference 录制片段与关键帧对照。
- 禁止频闪、高频亮度变化；循环动画须有可关闭路径。

## 8. 性能预算

验收以 **Android 真机**为准（优先 120 Hz 设备）；Desktop 只用于调试，不能替代真机帧率结论。

| 刷新率 | 单帧预算 | 要求 |
| --- | --- | --- |
| 60 Hz | 16.7 ms | 动画期间 P95 帧耗时不得超预算 |
| 90 Hz | 11.1 ms | 同上 |
| 120 Hz | 8.3 ms | 同上 |

实现约束：

- 动画阶段只改 layer 属性（`graphicsLayer { }` 的 **lambda 版本**、`drawWithCache` 等），避免在每帧触发重组或重新测量。
- 高频变化的值不要写进 Compose `State` 导致重组；派生值用 `derivedStateOf`。
- 避免 `minIntrinsicWidth` / `maxIntrinsicWidth` 引发整棵子树遍历；避免在动画路径上做 I/O、分配大对象或创建 `Path`/`Paint`。
- 基准放在 `benchmark/`：记录帧耗时分布、掉帧数、重组次数与动效实测时长；测量必须在稳定环境重复多次取分位数，报告写明设备与刷新率。

## 9. Reference 与验收

每个 `Spec ID` 对应一份 reference 记录（P1 起用 `catalog/reference/<spec-id>/` 目录）：

```
README.md        采集设备 / 系统版本 / targetBaseline / 帧率 / 录制方式 / 许可说明
clip.mp4|webm    最短可复核片段
frames/          关键帧编号与截图
measurements.csv 时间点 → 位移/缩放/不透明度等采样，及拟合方法
```

- **对比模式**：catalog 将参考片段与实现并排播放，提供时间轴、帧号、暂停与逐帧步进；差异以采样曲线呈现，不靠肉眼结论。
- **golden**：关键帧截图入 `catalog/golden/<spec-id>/`，更新必须与被批准的 reference 一起在同一提交中完成。
- **验收门槛**：`evidence >= E2` + 曲线测量报告 + golden 对比 + 性能基准，全部通过才可标 `verified`。`E0/E1` 的实现只能以 `draft` 存在于 catalog，并在 UI 上显式标注。

## 10. 禁止清单

- 组件里裸写时长、缓动、弹簧、delay 字面量；绕过 `OxygenMotion` 封装直连 `tween/spring`。
- 用 `LaunchedEffect` 每次重建动画、手势丢掉速度、状态跳变回起点。
- 只定义进入不定义退出，或降级时直接取消动画。
- 用全局倍率 / sleep / `CompositionLocal` 改写动画时长；用主观描述替代测量数据。
- 在 commonMain 调平台动画 API；用刷新率差异当作「测不准」的借口。
- 把 `E0` 占位参数悄悄标成 `verified` 或写进公开文档。

## 2026-11-18 菜单进出场

用户明确要求补菜单动画。MOT-ENTER-002 / 003 分别为从锚点缩放和透明度通道，使用 M3 标准 fast spatial / effects 弹簧类比；阈值为项目选择，E0 / draft。保持 Popup 至退出完成，反向不重置 Animatable，帧中仅写 graphicsLayer。手势按压反馈仍采用水波纹，不将进出场缩放误称为按压缩放。
