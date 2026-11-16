// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
package io.oxygenx.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import io.oxygenx.catalog.component.PagerTopBarDemoScreen
import io.oxygenx.catalog.component.PullRefreshDemoScreen
import io.oxygenx.catalog.component.TopBarDemoScreen
import io.oxygenx.catalog.component.badgeSection
import io.oxygenx.catalog.component.bottomBarSection
import io.oxygenx.catalog.component.bottomSheetSection
import io.oxygenx.catalog.component.buttonSection
import io.oxygenx.catalog.component.cardSection
import io.oxygenx.catalog.component.checkboxSection
import io.oxygenx.catalog.component.chipSection
import io.oxygenx.catalog.component.dialogSection
import io.oxygenx.catalog.component.dropdownMenuSection
import io.oxygenx.catalog.component.fabSection
import io.oxygenx.catalog.component.iconButtonSection
import io.oxygenx.catalog.component.navigationDrawerSection
import io.oxygenx.catalog.component.navigationRailSection
import io.oxygenx.catalog.component.overscrollSection
import io.oxygenx.catalog.component.progressSection
import io.oxygenx.catalog.component.radioButtonSection
import io.oxygenx.catalog.component.scaffoldSection
import io.oxygenx.catalog.component.searchBarSection
import io.oxygenx.catalog.component.settingsChoiceItemSection
import io.oxygenx.catalog.component.settingsGroupTitleSection
import io.oxygenx.catalog.component.settingsItemSection
import io.oxygenx.catalog.component.settingsSplitItemSection
import io.oxygenx.catalog.component.settingsSwitchItemSection
import io.oxygenx.catalog.component.sliderSection
import io.oxygenx.catalog.component.swipeToDismissSection
import io.oxygenx.catalog.component.switchSection
import io.oxygenx.catalog.component.tabRowSection
import io.oxygenx.catalog.component.textFieldSection
import io.oxygenx.catalog.component.tooltipSection
import io.oxygenx.catalog.icon.IconGalleryPage
import io.oxygenx.catalog.ui.DemoCard
import io.oxygenx.catalog.ui.SettingsRow
import io.oxygenx.catalog.ui.SmallTitle
import io.oxygenx.catalog.ui.catalogPageSpacing
import io.oxygenx.icons.material.MaterialSymbols
import io.oxygenx.theme.OxygenTheme
import io.oxygenx.ui.basic.RadioButton
import io.oxygenx.ui.basic.Switch
import io.oxygenx.ui.basic.Text
import io.oxygenx.ui.layout.Scaffold
import io.oxygenx.ui.navigation.PagerTopBar
import io.oxygenx.ui.navigation.rememberTopAppBarState
import io.oxygenx.ui.settings.SettingsGroupTitle
import io.oxygenx.ui.settings.SettingsItem

/**
 * 一个组件的演示登记项：索引页第 1 页按 [catalogSections] 的顺序列出，
 * 点进去后由 [CatalogSectionScreen] 在自己的脚手架里渲染 [content]。
 *
 * [id] 是页面 / 导航 key，一般取主 [specId] 的小写；一族组件合并成页时（顶栏、设置项）
 * 用族名，页内各分块仍各自用 `item(key = …)` 标自己的 `Spec ID`。
 *
 * 演示对象本身就是页面外壳（例如顶栏：要让屏幕自己的顶栏就是被演示的组件）时，
 * 用 [screen] 提供整页渲染，此时不再套标准 Section 外壳、也不使用 [content]。
 *
 * 通栏列表类组件（设置项这类行要延伸到屏幕左右边缘的）设 [fullBleed] = true：
 * 页面不给左右内边距、也不套卡片，文字离屏幕的距离由组件自身的行内边距决定。
 */
class CatalogSection(
    val id: String,
    val title: String,
    val specId: String,
    val status: String = "draft",
    val screen: (@Composable (onBack: () -> Unit) -> Unit)? = null,
    val fullBleed: Boolean = false,
    val content: LazyListScope.() -> Unit = {},
)

/**
 * Catalog 唯一注册入口：新增组件时在 [catalogSections] 里按顺序插一行，
 * 不允许出现未注册的 Section 文件。
 *
 * 每个 Section 现在是一个独立页面（索引页 → Section 页），页面外壳由
 * [CatalogSectionScreen] 统一提供，这里只负责内容与跳转回调。
 *
 * @param onOpen 打开整页预览（设置首页 / 显示与亮度 / Scaffold 能力页）。
 */
fun catalogSections(onOpen: (CatalogScreen) -> Unit): List<CatalogSection> = listOf(
    CatalogSection(
        id = "cmp-appbar",
        title = "TopBar / 顶栏",
        specId = "CMP-APPBAR",
        // 顶栏演示不套 Section 外壳：屏幕自己的顶栏就是被演示的组件。
        screen = { onBack ->
            TopBarDemoScreen(
                onBack = onBack,
                onOpenSettingsHome = { onOpen(CatalogScreen.SettingsHome) },
                onOpenDisplaySettings = { onOpen(CatalogScreen.SettingsList) },
            )
        },
    ),
    CatalogSection(
        id = "cmp-appbar-pager",
        title = "PagerTopBar / 横划顶栏",
        specId = "CMP-APPBAR-PAGER",
        // 同样整页接管：顶栏要贴在状态栏下方、下面直接接横划容器。
        screen = { onBack -> PagerTopBarDemoScreen(onBack = onBack) },
    ),
    CatalogSection(
        id = "cmp-scaffold",
        title = "Scaffold / 页面骨架",
        specId = "CMP-SCAFFOLD",
    ) {
        scaffoldSection(onOpenScaffoldDemo = { onOpen(CatalogScreen.ScaffoldDemo) })
    },
    CatalogSection(id = "cmp-bottom-bar", title = "BottomBar / 底栏", specId = "CMP-BOTTOM-BAR") {
        bottomBarSection()
    },
    CatalogSection(id = "cmp-button", title = "Button / 按钮", specId = "CMP-BUTTON") {
        buttonSection()
    },
    CatalogSection(id = "cmp-icon-button", title = "IconButton / 图标按钮", specId = "CMP-ICON-BUTTON") {
        iconButtonSection()
    },
    CatalogSection(id = "cmp-chip", title = "Chip / 芯片", specId = "CMP-CHIP") {
        chipSection()
    },
    CatalogSection(id = "cmp-badge", title = "Badge / 角标", specId = "CMP-BADGE") {
        badgeSection()
    },
    CatalogSection(id = "cmp-tooltip", title = "Tooltip / 文字提示", specId = "CMP-TOOLTIP") {
        tooltipSection()
    },
    CatalogSection(
        id = "cmp-dropdown-menu",
        title = "DropdownMenu / 下拉菜单",
        specId = "CMP-DROPDOWN-MENU",
    ) {
        dropdownMenuSection()
    },
    CatalogSection(
        id = "cmp-bottom-sheet",
        title = "ModalBottomSheet / 底部面板",
        specId = "CMP-BOTTOM-SHEET",
    ) {
        bottomSheetSection()
    },
    CatalogSection(
        id = "cmp-swipe-to-dismiss",
        title = "SwipeToDismissBox / 滑动删除",
        specId = "CMP-SWIPE-TO-DISMISS",
    ) {
        swipeToDismissSection()
    },
    CatalogSection(
        id = "cmp-navigation-rail",
        title = "NavigationRail / 侧边导航条",
        specId = "CMP-NAVIGATION-RAIL",
    ) {
        navigationRailSection()
    },
    CatalogSection(
        id = "cmp-navigation-drawer",
        title = "NavigationDrawer / 侧边抽屉",
        specId = "CMP-NAVIGATION-DRAWER",
    ) {
        navigationDrawerSection()
    },
    CatalogSection(id = "cmp-switch", title = "Switch / 开关", specId = "CMP-SWITCH") {
        switchSection()
    },
    CatalogSection(id = "cmp-textfield", title = "TextField / 输入框", specId = "CMP-TEXTFIELD") {
        textFieldSection()
    },
    CatalogSection(id = "cmp-search-bar", title = "SearchBar / 搜索栏", specId = "CMP-SEARCH-BAR") {
        searchBarSection()
    },
    CatalogSection(id = "cmp-dialog", title = "Dialog / 弹窗", specId = "CMP-DIALOG") {
        dialogSection()
    },
    CatalogSection(id = "cmp-fab", title = "FAB / 悬浮按钮", specId = "CMP-FAB") {
        fabSection()
    },
    CatalogSection(id = "cmp-slider", title = "Slider / 滑块", specId = "CMP-SLIDER") {
        sliderSection()
    },
    CatalogSection(id = "cmp-tabrow", title = "TabRow / 页签", specId = "CMP-TABROW") {
        tabRowSection()
    },
    CatalogSection(id = "cmp-progress", title = "Progress / 进度", specId = "CMP-PROGRESS") {
        progressSection()
    },
    CatalogSection(
        id = "cmp-pull-refresh",
        title = "PullToRefresh / 下拉刷新",
        specId = "CMP-PULL-REFRESH",
        // 整页接管：页面自己的列表就是下拉刷新的滚动源，嵌套滚动才真实。
        screen = { onBack -> PullRefreshDemoScreen(onBack = onBack) },
    ),
    CatalogSection(id = "cmp-overscroll", title = "Overscroll / 滚动过冲", specId = "CMP-OVERSCROLL") {
        overscrollSection()
    },
    CatalogSection(id = "cmp-card", title = "Card / 卡片", specId = "CMP-CARD") {
        cardSection()
    },
    CatalogSection(id = "cmp-radio", title = "RadioButton / 单选", specId = "CMP-RADIOBUTTON") {
        radioButtonSection()
    },
    CatalogSection(id = "cmp-checkbox", title = "Checkbox / 复选", specId = "CMP-CHECKBOX") {
        checkboxSection()
    },
    // 设置项是一族组件，合成一页按各自 Spec ID 分块，索引页只留一个入口。
    CatalogSection(
        id = "cmp-settings",
        title = "Settings / 设置项",
        specId = "CMP-SETTINGS-ITEM",
        // 氧 OS 的设置行通栏延伸到屏幕左右，文字靠组件行内距定位。
        fullBleed = true,
    ) {
        settingsItemSection()
        settingsSwitchItemSection()
        settingsSplitItemSection()
        settingsChoiceItemSection()
        settingsGroupTitleSection()
    },
)

/** 索引页的三页：段数 = 标题数 = pager 页数，顺序即页序。 */
private val INDEX_PAGES = listOf("组件", "图标", "关于")

/**
 * 索引页：屏幕自己的顶栏就是 [PagerTopBar]，下面横划三页——组件 / 图标 / 关于。
 *
 * 顶栏复用横划顶栏而不是标准顶栏，是为了让「浏览」这件事本身与图库（CMP-APPBAR-PAGER
 * 的真机基线）同构：三段指示器跟着手指走，大标题随页横移、过半淡出，上滑收拢成窄栏。
 * 三页各自是独立的滚动容器，收拢进度共用同一个 [rememberTopAppBarState]，所以翻页
 * 不会重置顶栏形态。
 *
 * 环境开关（明暗主题）放在第 3 页而不是组件页：它是调试入口，
 * 不属于「组件清单」，放进「关于」也顺带给了它一段说明位置。
 */
@Composable
fun CatalogPage(
    themeMode: CatalogThemeMode,
    systemDarkTheme: Boolean,
    onThemeModeChange: (CatalogThemeMode) -> Unit,
    m3ColorScheme: Boolean,
    onM3ColorSchemeChange: (Boolean) -> Unit,
    sections: List<CatalogSection>,
    onOpenSection: (CatalogSection) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { INDEX_PAGES.size })
    val topBarState = rememberTopAppBarState()
    val subtitles = listOf(
        "${sections.size} 个入口",
        "${MaterialSymbols.All.size} 个图标",
        "环境 · 许可 · 版本",
    )
    Scaffold(
        topBarState = topBarState,
        // 索引页是通栏列表，与设置页一致铺 surfaceContainer。
        containerColor = OxygenTheme.colorScheme.surfaceContainer,
        topBar = {
            PagerTopBar(
                pagerState = pagerState,
                title = { page -> Text(INDEX_PAGES[page], maxLines = 1) },
                subtitle = { page ->
                    Text(subtitles[page], maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                state = topBarState,
            )
        },
    ) { insets ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            // 底部安全区由 pager 统一让开，三页的列表不再各自扣一次。
            contentPadding = PaddingValues(bottom = insets.calculateBottomPadding()),
        ) { page ->
            when (page) {
                0 -> ComponentsPage(sections = sections, onOpenSection = onOpenSection)

                1 -> IconGalleryPage()

                else -> AboutPage(
                    themeMode = themeMode,
                    systemDarkTheme = systemDarkTheme,
                    onThemeModeChange = onThemeModeChange,
                    m3ColorScheme = m3ColorScheme,
                    onM3ColorSchemeChange = onM3ColorSchemeChange,
                )
            }
        }
    }
}

/**
 * 第 1 页「组件」：组件清单。
 *
 * 通栏列表：行延伸到屏幕左右，文字离屏幕的距离由 `SettingsItem` 自己的行内距决定，
 * 所以页面左右内距为 0（`catalogPageSpacing(fullBleed = true)`）。
 */
@Composable
private fun ComponentsPage(
    sections: List<CatalogSection>,
    onOpenSection: (CatalogSection) -> Unit,
) {
    val page = catalogPageSpacing(fullBleed = true)
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = page.contentPadding(),
    ) {
        item(key = "catalog-index-group") { SettingsGroupTitle("组件示例") }
        sections.forEach { section ->
            item(key = "catalog-index-${section.id}") {
                SettingsItem(
                    onClick = { onOpenSection(section) },
                    title = section.title,
                    summary = "${section.specId} · ${section.status}",
                    showChevron = true,
                )
            }
        }
    }
}

/** 第 3 页「关于」：调试环境（主题）+ 项目定位、许可与规范入口。 */
@Composable
private fun AboutPage(
    themeMode: CatalogThemeMode,
    systemDarkTheme: Boolean,
    onThemeModeChange: (CatalogThemeMode) -> Unit,
    m3ColorScheme: Boolean,
    onM3ColorSchemeChange: (Boolean) -> Unit,
) {
    val page = catalogPageSpacing()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = page.contentPadding(),
        verticalArrangement = Arrangement.spacedBy(page.blockGap),
    ) {
        item(key = "catalog-about-title") {
            SmallTitle(text = "OxygenX Catalog", specId = "CATALOG-ABOUT")
        }
        item(key = "catalog-about-env") {
            Column(verticalArrangement = Arrangement.spacedBy(page.blockGap)) {
                SmallTitle(text = "环境 / 主题", specId = "CATALOG-ENV")
                DemoCard {
                    EnvironmentControls(
                        themeMode = themeMode,
                        systemDarkTheme = systemDarkTheme,
                        onThemeModeChange = onThemeModeChange,
                        m3ColorScheme = m3ColorScheme,
                        onM3ColorSchemeChange = onM3ColorSchemeChange,
                    )
                    BasicText(
                        text = "所有令牌均为 E0 占位值，不是 OxygenOS 的复刻参数；" +
                            "打开「M3 配色」后颜色改由 Material 3 基线色板经 " +
                            "toOxygenColorScheme() 映射（28 个角色直连、9 个扩展角色按 M3 角色合成）。",
                        style = OxygenTheme.typography.caption.copy(
                            color = OxygenTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                }
            }
        }
        item(key = "catalog-about-info") {
            DemoCard {
                AboutLine(
                    title = "定位",
                    body = "第三方非官方实现：只复刻可观察的视觉规律与动效，不搬运厂商二进制与受版权" +
                        "保护的素材，与 OnePlus / OPPO / ColorOS 无隶属关系。",
                )
                AboutLine(
                    title = "许可",
                    body = "Apache-2.0；图标取自 Google Material Symbols（同为 Apache-2.0），字体、" +
                        "插画、音效一律自绘或采用宽松许可素材。",
                )
                AboutLine(
                    title = "规范",
                    body = "AGENT.md 是唯一权威入口，docs/spec/ 展开细则（生成令牌、动效、组件 API、" +
                        "catalog、工作流）。",
                )
                AboutLine(
                    title = "阶段",
                    body = "P3 组件进行中：顶栏 / 横划顶栏、底栏、按钮、芯片、开关、滑块、输入框、弹窗、" +
                        "进度、页签、设置项与图标库已落地，全部为 draft。",
                )
            }
        }
    }
}

/** 「关于」里的一条说明：标题一行、正文一段，统一走排版令牌。 */
@Composable
private fun AboutLine(title: String, body: String) {
    val colors = OxygenTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(OxygenTheme.spacing.gapXs)) {
        BasicText(
            text = title,
            style = OxygenTheme.typography.listItemTitle.copy(color = colors.onSurface),
        )
        BasicText(
            text = body,
            style = OxygenTheme.typography.bodyMedium.copy(color = colors.onSurfaceVariant),
        )
    }
}

@Composable
private fun EnvironmentControls(
    themeMode: CatalogThemeMode,
    systemDarkTheme: Boolean,
    onThemeModeChange: (CatalogThemeMode) -> Unit,
    m3ColorScheme: Boolean,
    onM3ColorSchemeChange: (Boolean) -> Unit,
) {
    CatalogThemeMode.entries.forEach { mode ->
        SettingsRow(
            title = mode.title,
            subtitle = when (mode) {
                CatalogThemeMode.System ->
                    if (systemDarkTheme) "跟随系统（当前为深色）" else "跟随系统（当前为浅色）"

                CatalogThemeMode.Light -> "固定浅色，不随系统变化"

                CatalogThemeMode.Dark -> "固定深色，不随系统变化"
            },
            onClick = { onThemeModeChange(mode) },
            role = Role.RadioButton,
            trailing = {
                // 只做展示：点击与语义都挂在整行上，控件自己不接收点击。
                RadioButton(selected = themeMode == mode, onClick = null)
            },
        )
    }
    SettingsRow(
        title = "M3 配色（桥接）",
        subtitle = if (m3ColorScheme) {
            "当前由 Material 3 基线色板映射：主色变紫，可用来核对角色有没有漏"
        } else {
            "当前用本项目令牌；打开后走 toOxygenColorScheme() 映射结果"
        },
        onClick = { onM3ColorSchemeChange(!m3ColorScheme) },
        role = Role.Switch,
        trailing = { Switch(checked = m3ColorScheme, onCheckedChange = null) },
    )
}
