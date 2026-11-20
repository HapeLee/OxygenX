# 参考凭据目录

P1 阶段这里是**占位索引**，尚无实际录屏或测量数据。所有 `design/tokens/*.yaml` 中 `source` 指向本文件，仅用于验证令牌生成与校验管线；这些令牌全部是 `E0` / `draft`。

## 系统资源表提取（`E3` 来源）

系统自带的设置应用可以直接读资源表拿到确切数值，比逐像素取色更可靠。本仓库只记录**推导后的数值**，不入库、不再分发任何原始资源文件。

| 项 | 值 |
| --- | --- |
| 来源包 | `com.android.settings` 11（versionCode 30，AOSP Settings + OnePlus `op_*` 资源） |
| 提取设备 | OnePlus 9R (LE2100) / ColorOS 11 / Android 11 |
| 工具 | `aapt2 dump resources` + `aapt2 dump xmltree --file res/...`（`$ANDROID_HOME/build-tools/<ver>/aapt2`） |

已提取并落成令牌的数值：

- 开关：`op_switch_track_material` 高 17dp、圆角 8.5dp；`op_switch_thumb_material` 17dp 圆形；`op_switch_ripple_radius` = 18dp。
  轨道开启 = `colorAccent` @16%（浅）/ @35%（深）；轨道关闭 = 中性色 @12%；轨道禁用 = @4% / @8%。
  拇指开启 = `colorAccent`；拇指关闭 = 18% 黑 / 30% 白；拇指禁用 = 5% 黑 / 12% 白。
- 设置行：`op_preference_material` 行高 64dp、行内边距 20dp、图标 36dp、行内上下内边距 12dp；
  `op_switch_preference_material` 与 `op_preference_checkable_two_target` 的行尾开关单元格均为 45dp 方框。
- 文字：行标题 `op_text_size_h6` 16sp、摘要 `op_text_size_body1` 13sp。
- 分割线：`op_control_divider_color` = 8% 黑 / 10% 白；`op_control_divider_height_standard` = 2px。
- 单选 / 复选：单选框真机 44–48px（16dp），未选中 4px（1.5dp）描边 #969696，选中为强调色实心圆加 11px（4dp）白点；复选框向量 `op_checkbox` 在 63 视口内为 51 的圆角方块（圆角 8），对勾线宽约 7% 方块宽。
- 步进滑块：真机「显示大小」页 5 档对应 5 个 10px（4dp）深色圆点，位于各档位置。
- 滑块：轨道高 `op_seekbar_track_background_height_material` 4dp、圆角半径同为 4dp；进度高 `seekbar_track_progress_height_material` 2dp（material 变体为 4dp）；拇指 `op_seekbar_thumb_material` / `op_ic_brightness_thumb` 15dp 圆形、`colorAccent` 填充加 1 单位描边；空轨 `op_seek_bar_disabled_color_light` = #1E000000（12% 黑）。
  实测交叉验证（450dpi 亮度滑块）：轨道 11px（4dp）、拇指 42px（15dp）、空轨 #DDDDDD 叠 #FAFAFA；拖动时拇指下出现直径 90px（32dp）的 12% 黑圆形水波纹，与开关按压波纹同一色值。
- 顶栏与文字：`op_app_bar_collapsing_height` 245dp、`op_app_bar_distance` 171dp、`op_app_bar_margin_top_without_tab` 40px（2x → 20dp）、`op_app_bar_expand_text_size` 96px / zh 86px（2x → 48sp / 43sp）、`op_app_bar_collapsed_text_size` 42px、`op_text_size_h6` 16sp、`op_text_size_body1` 13sp。
  实测交叉验证（450dpi）：标准顶栏在状态栏下方 206px = 73.2dp（≈20 + 54），大顶栏展开 747px = 265.6dp（≈20 + 245），折叠标题 18sp、大标题 43sp。

### 2026-11-20 顶栏复核（Settings 11 + 图库 4.0.182）

把顶栏的样式链完整展开后，几处旧记录需要更正 / 补充：

- 标准顶栏：`op_control_appbar_size_standard` = **74dp**；标题 `OnePlus.TextAppearance.Toolbar.Title` → `op_control_text_style_h5` → `op_text_size_h5` = **18sp**（此前记的 `op_app_bar_collapsed_text_size` 42px 并不在这条链上，别按 21sp 理解）；副标题 `expandedSubtitleTextAppearance` → `op_control_text_style_body1` → `op_text_size_body1` = **13sp**；标题左内距 `op_control_margin_screen_left3` = **20dp**；操作图标 `op_control_icon_size_button` = **21dp**。
- 大顶栏：`Widget.Design.CollapsingAppbarLayout.WithoutTab` 的 `android:height` = `op_app_bar_collapsing_height` 245dp、`collapsedAppbarHeight` = `op_control_appbar_size_standard` 74dp、`expandedTitleMarginTop` = `op_app_bar_margin_top_without_tab` 40px（÷2 = **20dp**）；展开标题 `expandedTitleTextAppearance` → `op_appbar_title_style_h1` → `op_app_bar_expand_text_size` = 96px（÷2 = **48sp**，该 dimen 没有 zh 变体）。
- 横划顶栏（`com.oneplus.gallery` 4.0.182）：`design_tab_bar_height` = **20dp** 是 TabBarView 独立成行的高度，`op_app_bar_collapsing_height_with_tab` = 225dp = 245 − 20，即**指示器带从大顶栏里扣、总高不变**；`design_tab_bar_indicator_height` = 4px（÷2 = **2dp**）。

复现方式：`aapt2 dump resources <apk> > resources.txt` 后按资源名检索；颜色选择器与 drawable 用 `aapt2 dump xmltree --file res/color|drawable/<name>.xml` 展开。

## 目录约定（P2 起生效）

```
catalog/reference/<spec-id>/
├── README.md         采集设备、系统版本、targetBaseline、帧率、录制方式、许可说明
├── clip.webm         最短可复核片段
├── frames/           关键帧编号与截图
└── measurements.csv  时间点 → 位移 / 缩放 / 不透明度采样，以及拟合方法
```

## 合规约束

- 只保存用于本地验收的最小片段；不进入公开发布物与应用制品。
- 不得包含个人信息、通知内容、账号数据或受版权保护的完整素材。
- 不复制或再分发专有资源；本目录只承载「可观察行为」的测量凭据。
- 维护者有权要求移除任何凭据文件。

## 证据升级

`E0 → E2/E3` 时，在同一改动中：补 `reference`、把令牌的 `source` 指向该目录、`evidence` 升到实测等级、`status` 改为 `verified`，并同步 catalog 对比页与 golden 截图。详见 `docs/spec/motion.md` 与 `docs/spec/workflow.md`。
