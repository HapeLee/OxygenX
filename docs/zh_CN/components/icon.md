# Icon

Unofficial / draft. Import `io.oxygenx.ui.basic.Icon`.

## 用法

```kotlin
Icon(OxygenIcons.Check, contentDescription = "Done")
Icon(MaterialSymbols.LibraryBooks, contentDescription = "Bookshelf")
Icon(imageVector = multicolorVector, contentDescription = null, tint = Color.Unspecified)
Icon(painter = painter, contentDescription = "Cover", tint = Color.Unspecified)
Icon(bitmap = bitmap, contentDescription = null)
```

## 参数与默认值

| 参数 | 默认值 / 行为 |
| --- | --- |
| imageVector / painter / bitmap | 必需资源，三个重载各选一个 |
| contentDescription | 必需可空；有含义时提供本地化描述，装饰图标传 null |
| modifier | Modifier |
| tint | OxygenTheme.contentColor；显式 Unspecified 不染色 |
| size | OxygenTheme.icons.medium；三个重载统一按令牌尺寸绘制 |
| mirrorInRtl | false；矢量自身 autoMirror 仍生效，true 可让其他资源在 RTL 中镜像 |

## 图标集合

依赖 `io.oxygenx:oxygenx-icons-material`（版本由 BOM 管理），导入 `io.oxygenx.icons.material.MaterialSymbols`。128 个图标统一 Google Material Symbols Outlined / opsz=24 / wght=300 / FILL=0 / GRAD=0，Apache-2.0。`MaterialSymbols.All` 用于浏览全集，正常页面按单个属性取用以便裁剪。

14 个 `OxygenIcons` 兼容名称保留，造型由 Rounded 统一为 Outlined；扩展库重导出同一实例，例如 `OxygenIcons.Back === MaterialSymbols.ArrowBackIosNew`、`Clock === Schedule`。核心组件仍只依赖小模块。方向性矢量自带 autoMirror，默认即可适配 RTL；同时传 mirrorInRtl=true 不会重复翻转。自定义非自动镜像资源仍可显式开启。

## 内容色与原色

省略 tint 时继承槽位内容色，禁用按钮也会自动使用禁用色。显式传 Color.Unspecified 会移除颜色滤镜，保留矢量路径、Painter 或位图本来的颜色。Color.Transparent 仍是显式着色，会隐藏图形。

这与 Text 的 Color.Unspecified 含义不同。图标不从文本 style.color 取色。默认图标尺寸会约束资源，通用图片展示可使用 Foundation Image。

## 语义与动效

有描述的图标通过 Foundation Image 暴露图像角色与描述；装饰图标不增加描述。交互请包在 IconButton 中。Icon 本身无动效；矢量优先使用自带的 autoMirror，其他方向资源通过 mirrorInRtl 开启 RTL 镜像。

## 迁移与回归

旧代码若显式传 tint = Color.Unspecified 来继承内容色，请删除该参数，或改传 OxygenTheme.contentColor。Vector 的其他参数顺序保留，新增重载按资源类型区分。

Desktop 测试覆盖默认 / 显式颜色、三类资源保留原色、RTL、2× 字体不放大图标、装饰与图像语义。明暗 Text/Icon PNG 提供静态回归；不代表 OxygenOS 视觉验收。
