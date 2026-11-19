# 平台支持

非官方、实验版。编译通过、行为正确和真机还原度是独立承诺。

## 支持矩阵

| 平台 | 当前范围 | 待验证项目 |
| --- | --- | --- |
| Android，minSdk 26 | 主平台、APK catalog；ModalBottomSheet 预测式返回 | 完整 TalkBack 矩阵、逐帧校准与高刷基准 |
| Desktop JVM | Windows / JDK 21 的预览与离屏 UI 测试 | 其他桌面系统和设备输入差异 |
| WasmJs | 库编译与 common 逻辑浏览器测试 | Web 应用入口、浏览器无障碍与 UI 一致性 |
| iOS / macOS Native | 未配置 | 暂不承诺支持 |

## 回归覆盖

`./gradlew check` 包含 ABI 与现有测试。Desktop UI 测试覆盖主题继承、文本颜色、Slider 输入、代表性的 Button / Checkbox / TextField 行为和动画中断。明暗 × LTR/RTL × 字体倍率 1/2 是代表性覆盖，不等于所有组件均已覆盖。

Slider PNG golden 是固定密度且不含文字的 OxygenX 回归基线，不是 OxygenOS 参考。见 [golden 说明](../../catalog/golden/README.md)。

## CI 与发布

Windows CI 配置检查格式、编译、测试/ABI、双语文档和本地发布。远端状态需要实际 GitHub Actions 运行证明。已配置签名 GitHub 附件分发；Maven Central / GitHub Packages 托管和稳定 API 兼容策略尚未配置。

已新增签名 APK 与 Maven 仓库 zip 的手动 GitHub Release 工作流，具体激活与验证状态见[版本说明](versioning.md)。Maven Central、GitHub Packages、Native 目标与全组件验收仍未配置。
