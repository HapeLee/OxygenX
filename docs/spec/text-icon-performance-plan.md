# Text / Icon 接入与 Android 性能入口

2026-11-17；Unofficial / 非官方；组件保持 draft。

1. Icon 默认继承内容色，显式 Unspecified 不染色；新增 Painter / ImageBitmap 重载，保留尺寸和 RTL 镜像契约。
2. Text 直接透传 Foundation autoSize / inlineContent，补字体与装饰覆盖，不改变内容样式优先级。
3. 先固定像素、语义、行内测量和自动字号回归；同步 catalog、明暗 golden、双语文档、迁移和 ABI。
4. 新增 Android Baseline Profile 生成与启动 Macrobenchmark 模块，使用非 debug 目标、UI 状态等待与真实设备；分别记录编译、生成和测量结果。
5. 全量门禁和独立消费者验证；性能测量不代表 OxygenOS 逐帧校准、库级 profile 覆盖或全部组件性能达标。

进度：Text / Icon API、8 项行为回归、2 张新 PNG、catalog 和双语文档已落地。Desktop 合计 100 项、0 失败；全量 check（含 Wasm、ABI、Android lint）、Android / Desktop / benchmark APK 构建已通过。OnePlus 9R 真机已目视检查；小米 API 37 的 profile 生成与启动对照均已实际跑通。


## 真机采集记录

- OnePlus 9R / LE2100 / ColorOS 11：安装并打开 Text / Icon 页，层级与截图确认三类资源原色、默认内容色、行内图标、单行自动字号和字体装饰。证据在忽略的 build/text-icon-device.xml 与 PNG。
- 小米 2211133C / Android 17 API 37：先关闭 Android CLI 的 UIAutomation server；默认采集返回空 profile。读取到 dalvik.vm.ps-min-first-save-ms=8000 后，在采集命令增加 profileSaveDelayMs=10000，生成成功（1m28s）。该等待不进入启动测量。
- 规则通过 Gradle 生成，位于 catalog/android/src/release/generated/baselineProfiles；baseline 与 startup 各约 1.55 MB，仅覆盖 catalog 启动。未手工编辑生成规则。
- 冷启动对照成功（2m41s）：两种模式各 10 次，2 项测试通过，生成器在 Macrobenchmark 模式按规则跳过。无预编译中位数 401.2915885 ms（389.984114–411.272031），Partial / Require 为 333.8120315 ms（324.180208–354.681875）；该样本中位数缩短约 16.8%。TTID 与 TTFD 本次相同。
- 条件：小米 2211133C / fuxi，Android 17 API 37，OS4.0.27.0.XEOCNXM，ART 370399999，120 Hz 配置，AC 供电 / 电量 100%，测量前电池温度 31.1°C。JSON 报告 cpuLocked=true；不是跨设备或长期稳定性结论，未建立性能门槛。
- 原始 benchmark JSON 复制到 `benchmark/results/2026-11-17-xiaomi-startup.json`；20 份 Perfetto trace、完整日志、XML 和设备状态在忽略的 benchmark/build 与 build/text-icon-*。
- R8 编译提示 134 条 startup synthetic method 未匹配，未手改生成规则或压下警告。Require 安装与测量通过，但不宣称 15154 条采集规则全部匹配或全组件覆盖。


## 接入与剩余边界

- 发布 0.1.0-local.3 到本地仓库；tools/consumer-smoke 仅 BOM + UI，成功构建原有状态输入及新增 Painter / Bitmap、自动字号、行内内容 API 的 Android APK（28s）。
- 已同步十一对双语页面、迁移、API dumps、28 个 catalog 入口及 CI 的 benchmark APK 编译步骤。
- 组件继续 draft：完整 TalkBack、跨平台字形校准、Desktop GUI 人工演示、Android golden、逐帧 / 全组件滚动与重组性能及远端 CI 尚未验收。
