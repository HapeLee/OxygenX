# State input and content styling / 状态输入与内容样式

Unofficial / 非官方。2026-11-17；实现保持 `draft`。

1. 新增 Foundation 原生 `TextFieldState` 重载，通用参数名称 / 类型对齐 M3；固定标签视觉保留，不伪造 Material 专属标签 API。
2. 统一 Oxygen 内容色、文本样式合并与容器回退，补齐输入框槽位默认样式。
3. 可选 `oxygenx-material3` 提供 Oxygen → M3 内容样式桥接；核心模块不依赖 Material 组件。
4. 先复现回归用例，再实现；同步 catalog、双语文档、ABI、独立接入构建和 Android 验证。

1. Add a native Foundation TextFieldState overload with M3-compatible common parameter names/types. Keep the fixed Oxygen label layout; do not emulate unsupported Material label APIs.
2. Normalize content color, text-style merging and container fallback; provide input-slot defaults.
3. Add an optional Oxygen → M3 content-style bridge in oxygenx-material3 without introducing Material components into core modules.
4. Establish regressions first, then update the implementation, catalog, bilingual docs, ABI, isolated consumer and Android validation.

进度 / Progress: 已实现并本地验证 / Implemented and locally validated.

| 交付 / Delivery | 结果 / Result |
| --- | --- |
| Foundation 原生状态输入 / Native Foundation state input | 三种重载共存，通用编辑参数对齐 M3；固定标签 API 差异已文档化 / Three overloads coexist; common editing parameters align with M3; fixed-label differences documented |
| 内容样式 / Content styling | computed 默认值、嵌套主题继承、merge、容器回退与槽位样式回归通过 / Computed defaults, nested-theme inheritance, merging, container fallback and slot contracts passed |
| 可选 M3 桥接 / Optional M3 bridge | 根级包裹后能读取后代 Oxygen 按钮样式与禁用态，更近层 M3 provider 优先 / Root wrapper follows descendant Oxygen button styles and disabled state; nearer M3 providers win |
| 格式、编译、ABI / Format, compile, ABI | spotlessCheck、Desktop 编译、Android assembleDebug、单独 apiDump 后的 check 全通过 / All passed; apiDump run separately from check |
| 自动化 / Automation | Desktop 90 项，0 失败；check 包含 Wasm 浏览器测试、ABI 与 Android lint / 90 Desktop tests, zero failures; check also covers Wasm browser tests, ABI and Android lint |
| 图像 / Images | 原有 Slider 四张及新增 TextField 明暗两张 PNG 通过；新增图片已目视检查 / Four existing Slider PNGs and two new light/dark TextField PNGs passed; new images visually reviewed |
| 独立接入 / Isolated integration | 发布 0.1.0-local.2，consumer-smoke 只依赖 BOM + UI，状态输入 API assembleDebug 成功 / Published 0.1.0-local.2; BOM + UI-only consumer builds state API successfully |
| Android / Device | OnePlus 9R LE2100 / ColorOS 11：字母被拒绝、数字接受、原始状态与格式化显示分离、state.edit 重置为 456、M3 Text 继承按钮样式 / Alphabetic input rejected, digits accepted, raw/display separation, state.edit reset to 456 and M3 Text button styling confirmed |
| 文档 / Docs | 九对双语页面及本地链接检查通过，迁移与 catalog 同步 / Nine bilingual page pairs and links passed; migration and catalog updated |

本地记录位于忽略的 `build/state-input-*.log`、`build/state-input-*.xml` 与 `build/state-input-reset.png`。Android CLI 的 layout server 在该设备返回无法识别响应，改用系统 UIAutomator 层级与截图检查，未将工具故障计为应用测试通过。

Local logs and device evidence live in ignored `build/state-input-*` files. The Android CLI layout server returned an unrecognized response on this device; system UIAutomator hierarchy and screenshots were used instead. The tool failure is not counted as an application test pass.

仍未验收 / Pending acceptance: 完整 IME composition / TalkBack、Android golden、Desktop GUI 人工演示、OxygenOS 逐帧校准、性能基准与远端 CI。当前没有 benchmark 模块，不以编译或静态截图替代性能结果；组件保持 draft。

Full IME composition / TalkBack, Android goldens, manual Desktop GUI demonstration, OxygenOS frame calibration, performance benchmarks and remote CI remain pending. No benchmark module exists; compilation and static screenshots do not establish performance. Components remain draft.
