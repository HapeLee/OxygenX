# Desktop 回归基线 / Desktop regression baselines

Unofficial / 非官方。这里的 PNG 是 OxygenX 自身的 Desktop 渲染回归基线，**不是** OxygenOS 真机参考，也不代表组件已 `verified`。

These PNG files are OxygenX Desktop rendering regression baselines. They are **not** OxygenOS device references and do not confer `verified` status.

`CMP-SLIDER`：240×48 px，density 1，value 0.25，明暗 × LTR/RTL 四种组合；不含文字、焦点或按压动画。每通道允许 2/255 差异，超过 0.5% 的像素变化会失败。失败图写入 `oxygenx-ui/build/golden-actual/`。

`CMP-SLIDER`: 240×48 px, density 1, value 0.25, light/dark × LTR/RTL. No text, focus, or pressed animation. Channel tolerance is 2/255; more than 0.5% changed pixels fails. Actual images are written to `oxygenx-ui/build/golden-actual/` on failure.

```powershell
.\gradlew.bat :oxygenx-ui:desktopTest --tests '*SliderGoldenTest'
# 仅在审阅视觉变更后更新 / Update only after reviewing the visual change:
.\gradlew.bat :oxygenx-ui:desktopTest --tests '*SliderGoldenTest' -PoxygenxUpdateGoldens=true
```

更新后必须检查四张图片，再不带更新参数重跑测试；不得通过自动更新基线掩盖回归。

Inspect all four images after updating, then rerun without the update flag. Do not automatically accept baseline changes to hide regressions.

`CMP-TEXTFIELD`：320×300 px，density 1，明暗两张 Windows Desktop 基线。覆盖状态输入显示转换、标签 / 前后缀 / 辅助说明、错误占位及禁用样式；不含焦点、光标与 IME。字体来自 Desktop 环境，不能作为跨平台字形真值。容差与失败图目录同上。

`CMP-TEXTFIELD`: 320×300 px, density 1, light/dark Windows Desktop baselines. Covers formatted state input, labels/prefixes/suffixes/supporting text, error placeholders and disabled styling. Excludes focus, cursor and IME. Desktop fonts do not establish cross-platform glyph fidelity. Tolerances and failure output follow the same rules above.

```powershell
.\gradlew.bat :oxygenx-ui:desktopTest --tests '*TextFieldGoldenTest'
# 仅在审阅视觉变更后更新 / Update only after reviewing the visual change:
.\gradlew.bat :oxygenx-ui:desktopTest --tests '*TextFieldGoldenTest' -PoxygenxUpdateGoldens=true
```

更新后检查两张图片，再不带更新参数重跑。两组测试共用 `GoldenAssertions.kt`，容差保持一致。

Inspect both images after updating, then rerun without the update flag. Both suites share `GoldenAssertions.kt` and the same tolerances.

`CMP-TEXT-ICON`：320×180 px，density 1，明暗两张 Windows Desktop 基线；覆盖默认内容色、矢量 / 位图原色、Painter 显式色、行内占位组件与自动字号。字体环境与像素容差同上；不包含动画或平台 IME。

`CMP-TEXT-ICON`: 320×180 px, density 1, light/dark Windows Desktop baselines. Covers ambient tint, original vector/bitmap colors, explicit Painter tint, inline placeholders and automatic text sizing. Font-environment limits and pixel tolerances are unchanged; excludes animation and platform IME.

```powershell
.\gradlew.bat :oxygenx-ui:desktopTest --tests '*TextIconGoldenTest'
# 审阅后才更新 / Update only after visual review:
.\gradlew.bat :oxygenx-ui:desktopTest --tests '*TextIconGoldenTest' -PoxygenxUpdateGoldens=true
```

更新后检查两张图片，再不带更新参数重跑。

Inspect both updated images, then rerun without the update flag.

## Material Symbols

`ICONS-MATERIAL/light.png` / `dark.png` 覆盖全部 128 个 Outlined / wght=300 图标，按 `MaterialSymbols.All` 顺序每行 16 个，包含共用的 14 个核心矢量。下面的定向命令仅生成这两张基线；常规 `check` 只比较，不自动更新。另有自动 / 显式 RTL 镜像逐像素回归。这些是库回归，不是真机或 OxygenOS 视觉验收。

`ICONS-MATERIAL/light.png` / `dark.png` cover all 128 Outlined / wght=300 symbols,
in `MaterialSymbols.All` order (16 per row). They include all 14 shared core vectors.
Generate only these new baselines with `:oxygenx-ui:desktopTest --tests '*MaterialSymbolsGoldenTest*' -PoxygenxUpdateGoldens=true`;
normal `check` compares them without updating. Auto-mirror and explicit RTL mirroring are also checked pixel by pixel.
These are library regressions, not OxygenOS fidelity or device acceptance.

## DropdownMenu

`CMP-DROPDOWN-MENU/light.png` / `dark.png` 为 312×240px、density=1 的经典 M3 风格菜单回归。覆盖无图标 / 前后图标与禁用态，使用 Popup 的同一内部面板绘制；周围留白包含 3dp 投影。只可用 `:oxygenx-ui:desktopTest --tests '*DropdownMenuGoldenTest*' -PoxygenxUpdateGoldens=true` 定向更新，审图后不带更新参数重跑。

`CMP-DROPDOWN-MENU/light.png` / `dark.png` are 312×240px, density=1 classic M3 style menu regressions. They cover plain labels, leading/trailing icons and disabled state using Popup's shared panel renderer; outer space includes the 3dp shadow. Update only these images using `:oxygenx-ui:desktopTest --tests '*DropdownMenuGoldenTest*' -PoxygenxUpdateGoldens=true`, review them, then rerun without the update flag.
