# 交付流程与门禁

一次「完成」不是写完一个 `.kt` 文件。组件、令牌、动效、测试、示例、文档、基线是一整批交付物。

> **阶段说明**：门禁 1–4 已落地并跑通（含 Android `assembleDebug` 与真机 `installDebug`）；CI 自动化、catalog 逐帧对比、性能基准（5–6）、golden 与文档站（7）尚未建立。在对应模块/脚本落地前，不得把未执行项写成通过。

## 1. 实现状态

| 状态 | 含义 | 允许出现的位置 |
| --- | --- | --- |
| `draft` | 结构可用，参数含 `E0/E1` 占位或无完整 reference | 仅 `draft` 分支与 catalog 中带可见标记 |
| `in-review` | 证据与实现齐备，等待逐帧/性能验收 | PR 中 |
| `verified` | 通过 §5 全部门禁 | 公开文档、发布物 |

状态只能按 `draft → in-review → verified` 前进；降级必须记录原因并同步更新文档与 catalog 标记。

## 2. 新增组件（完整清单）

先分配 `Spec ID`、锁定 `targetBaseline`、准备 reference，再动手写代码。交付必须同时覆盖：

1. `oxygenx-ui/.../{Name}.kt`：实现 + KDoc（`Spec ID` / `Baseline` / `Motion` / `Status`）。
2. `design/tokens/*.yaml`：所需的静态令牌（能复用就不新增）+ 生成物。
3. `design/tokens/motion.yaml` + 动效登记表：所用/新增的 `MOT-*` 规格。
4. `oxygenx-ui/src/commonTest/...`：默认渲染、变体、禁用、回调、RTL、大字体、动效中断/反向。
5. `catalog/shared/.../component/{Name}Section.kt`：§3 演示矩阵全部维度。
6. `CatalogPage.kt` 注册：字母序 import + 展示顺序调用 + 导航 key。
7. 文档站交互 demo 注册（复用 catalog 演示代码）。
8. `docs/components/{slug}.md` + `docs/zh_CN/components/{slug}.md`：章节一一对应。
9. 组件概览表（中英各一行）+ 侧边栏（中英各一条）。
10. `catalog/golden/<spec-id>/` 关键帧基线（明暗 + 必要状态）。
11. 性能基准记录（帧耗时分布、重组次数、动效实测时长、设备与刷新率）。

缺失任一项 → 状态保持 `draft`，PR 中逐条说明原因与补齐计划，不得标记完成。

## 3. 新增 / 修改动效

除 §2 中与动效相关的条目外，还需：

- `catalog/reference/<spec-id>/`：采集说明、最短片段、关键帧、`measurements.csv` 与拟合方法。
- `catalog/shared/.../motion/{SpecId}Comparison.kt` 并排对比页 + 注册。
- golden 帧序列与性能基准同步更新；若改动影响共享规格，列出所有受影响的 `Spec ID` 并逐一回归。
- 曲线变化必须在 PR 中给出「改前 / 改后 / reference」三条曲线，不能只描述手感。

## 4. 修改既有实现

改 API、默认值、令牌或动效时，同步更新：

- 组件 KDoc 与 `ComponentDefaults` / `OxygenMotionDefaults`；
- 所有调用点（库内 + catalog + 文档站 demo）；
- 中英文档页的 Properties 表、Defaults / Colors / Motion 章节与代码示例；
- 受影响的 golden 截图与性能基准；
- 破坏性变更：PR 中给出前后签名对照与迁移步骤，并在 catalog 保留旧行为的对照示例一个版本周期。

**修 bug** 流程：在 catalog 复现 → 固化为回归用例（含 golden 或动效帧）→ 在库模块修复 → 受影响平台分别验证 → 同步文档（若行为承诺有变）。

## 5. 验证门禁（顺序固定）

| # | 检查 | 命令 / 方式 |
| --- | --- | --- |
| 1 | 令牌生成与校验 | `./gradlew :oxygenx-theme:generateTokens`（生成物无 diff） |
| 2 | 格式 | `./gradlew spotlessCheck`（仅报错时 `spotlessApply`） |
| 3 | 编译 | `./gradlew compileKotlinDesktop` + Android 编译 |
| 4 | 测试与门禁 | `./gradlew check` |
| 5 | catalog 人工验收 | Android 与 Desktop 逐项对照 §3 演示矩阵；动效走对比模式 |
| 6 | 性能 | `./gradlew :benchmark:runMotionBenchmark`，帧耗时 P95 不超预算 |
| 7 | 文档 | 双语章节一一对应、链接与注册项完整 |

前一步失败不得跳到后一步，也不得以「本地没环境」为由把结果写成通过；确实无法执行时，在 PR 中明确列出未执行项及其风险。CI（P1 起）对 1–4、7 做自动校验，5–6 的记录作为 PR 附件。

## 6. Git 提交

遵循 `AGENT.md` §10：`<scope>: <summary>`，主题 ≤ 72 字符、句末无句号、提交前跑 `spotlessCheck`。组件与其 catalog / 文档 / golden / 基准配套改动放进**同一个** `ui:`（或 `motion:`）提交；纯规范改动用 `docs:`，且必须同步 `AGENT.md` 摘要。

## 7. PR 自检模板

```markdown
## 变更
- Spec ID: CMP-XXX / MOT-XXX-NNN
- targetBaseline: OxygenOS <version> / <device> / <date>
- Reference: catalog/reference/<spec-id>/（证据等级 E?）
- Status: draft | in-review | verified

## 交付清单
- [ ] 组件源码 + KDoc
- [ ] 设计/动效令牌 + 生成物
- [ ] 单元测试
- [ ] catalog Section + 注册
- [ ] 动效对比页（如适用）
- [ ] 中英文档页 + 概览表 + 侧边栏
- [ ] golden 截图
- [ ] 性能基准记录

## 验证
- [ ] 1–7 门禁（列出未执行项及原因）
- [ ] 受影响平台：Android / Desktop / Web
- [ ] 明暗主题、RTL、大字体
```

## 8. 规范本身变更

见 [README.md](./README.md) §7：动机、影响面、迁移成本 + 同步 `AGENT.md`、模板、catalog 校验项与 CI 门禁；不得放宽已 `verified` 实现的门槛，不得「先合并后补文档」。
