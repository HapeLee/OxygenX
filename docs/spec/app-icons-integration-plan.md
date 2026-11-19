# AppIcons 图标接入（2026-11-18）

状态：本轮接入与自动化验收已完成 / draft；仅 Google Apache-2.0 公共矢量，不含厂商素材。

## 决定

- 全集统一 Material Symbols Outlined，opsz=24、wght=300、FILL=0、GRAD=0。
- 14 个核心图标保留 `OxygenIcons` 名称与小模块，重新按同一 Outlined 参数生成。
- 扩展模块依赖核心模块，按官方符号名重导出相同对象；物理矢量只存一份。
- 既有 100 个扩展图标保持名称，按 MD3 `AppIcons` 的真实缺口增补。
- 方向性矢量采用 Compose autoMirror，Icon 显式镜像时避免重复。播放器上下首不镜像。
- 生成器记录官方来源与源文件 / 生成物 SHA-256；离线检查漂移，显式联网更新锁。

## 工作与验收

1. 生成器 / 清单、核心图标和扩展 facade，ABI 仅增补。
2. catalog、双语 Icon 文档、规范同步；图标渲染与 RTL 回归。
3. Desktop / Android / Wasm 门禁；更新本地未发布 0.0.1 分发快照。
4. MD3 `AppIcons` 操作 / 播放器 / 五个主目的地 Oxygen 分支，保留其他引擎。
5. MD3 自动测试、Core / Comic 构建与快照 SHA-256 验证；不做真机或性能验收声明。

## 库侧验证

- `generateTokens` → `spotlessCheck` → Desktop / Android 编译 → `check` 全通过。
- Desktop 113 项零失败，包含图标模块 2 项、明暗图集 2 项和 RTL 像素回归 1 项；Wasm 浏览器测试通过。
- API dump 独立执行；扩展 API 增加 28 个属性、核心 14 个属性不变，API check 通过。
- 两张新图集已目视检查，既有八张基线未修改。生成器 2 项与发布脚本 15 项、14 对文档通过。
- 签名 catalog、33 份 Maven publication、0.0.1 分发包已更新。阅读项目 vendor SHA-256：`bbc7d0c0a232fe6f347aced8e424362dfe1894de439ccbb55f466f344f91378a`。
- 日志：`build/app-icons-api-compile.log`、`build/app-icons-render.log`、`build/app-icons-library-final.log`。无远端发布、真机或性能验收。

## 宿主结果

- MD3 AppIcons 的 27 个操作 / 播放器入口、五个主目的地进入 Oxygen 分支。
- Core 39 项（图标 2 + 主题 37）、Comic 图标 2 项全部零失败 / 零跳过；覆盖五引擎切换、选中态、实际 AppIcon 的 RTL 像素与 32 图标完整尺寸，自动渲染预览已目视检查。
- Core / Comic 编译、Debug APK、架构 / AAR 元数据 / 重复类门禁通过，vendor 哈希再次核验。
- 宿主测试首次无关类扫描过慢而中止；使用仅存在于忽略构建目录的定向 class include 脚本，保留每类独立 JVM。最终两份日志和范围见目标工程 `docs/dev/oxygenx-integration.md`。
- 本轮未重复宿主全量 Lint，未进行真机、IME、性能或远端发布验收。现有 draft / API experimental 状态保持。
