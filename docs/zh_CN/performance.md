# Android 性能入口

非官方 / draft。benchmark 是 catalog 的 Android 测试模块，不随 UI 库发布。启动测量与组件逐帧 / 性能验收分别记账。

## 运行

```powershell
# Build the test APK without connecting a device
.\gradlew.bat :benchmark:assemble
# Select one unlocked physical Android 13+ device
$env:ANDROID_SERIAL = '<device-serial>'
.\gradlew.bat :catalog:android:generateBaselineProfile
.\gradlew.bat :benchmark:runStartupBenchmark
```

## 测量契约

BaselineProfileGenerator 收集 catalog 启动并等待真实目录节点，生成的规则写入 catalog/android/src 下的 generated/baselineProfiles，不能手工编造 profile。profile 只证明采集路径，不代表组件全部覆盖，也不属于库级 profile。

StartupBenchmarks 使用同一 benchmarkRelease APK，非 debug、R8 开启、允许 shell profiling、本地 debug 签名。分别以 CompilationMode.None 与 Partial(BaselineProfileMode.Require) 冷启动 10 次，记录初始显示与 ReportDrawn 后的完全显示时间。Require 在没有打包 profile 时失败。测试不会自动压下设备状态错误。

目录页的测试 tag 仅在 Android 入口映射为 resource id，UIAutomator 等待超时会失败，不以固定 sleep 判断启动完成。ProfileInstaller 只加在 catalog，核心库不依赖 Android 性能库。

部分厂商 ART 配置了首次保存延迟。若已确认目录页加载成功但生成 profile 为空，可在生成命令追加 `-Pandroid.testInstrumentationRunnerArguments.profileSaveDelayMs=10000`，等待首次保存窗口；只影响采集，不能放进启动测量。范围 0–60000 ms，默认 0。运行仪器测试前关闭占用 UIAutomation 的检查工具服务。

## 结果与限制

详细 JSON、Perfetto trace 和仪器测试报告位于 benchmark/build/outputs 与 benchmark/build/reports。生成 profile 后重新打包 benchmarkRelease，再比较相同 APK 的编译模式；不要拿 debug 与 release 的启动时间相减。

使用解锁真机，生成 profile 需要 Android 13+ 或支持的 rooted 设备。设备温度、系统负载、供电、刷新率与系统版本都影响数据，报告应保留这些条件。CI 只构建测试 APK，不在共享 runner 上宣称性能通过。

该入口尚不测滚动帧分布、组件重组次数、OxygenOS 动效拟合或所有库代码路径；runMotionBenchmark 仍未实现。

## 来源

采用官方 [生成 Baseline Profiles](https://developer.android.com/topic/performance/baselineprofiles/create-baselineprofile) 与 [Macrobenchmark 配置](https://developer.android.com/topic/performance/benchmarking/macrobenchmark-overview) 流程。版本统一固定在 gradle/libs.versions.toml。

2026-11-17 本地采集：小米 2211133C / Android 17 API 37 默认采集为空，配置 profileSaveDelayMs=10000 后生成 catalog 启动规则。该机首次保存延迟为 8000 ms。规则保留在 catalog/android/src/release/generated/baselineProfiles；冷启动测量单独记录在[工作日志](../spec/text-icon-performance-plan.md)。

同日冷启动对照成功：相同 R8 APK、两种模式各 10 次，中位数 401.3 ms → 333.8 ms（该样本约 -16.8%）。原始数据在 benchmark/results/2026-11-17-xiaomi-startup.json。部分 synthetic startup 方法未匹配，覆盖范围、供电 / 温度 / 系统条件和全部限制见工作日志；不能外推其他设备或全组件性能。
