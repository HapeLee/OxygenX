# Overscroll

非官方 / draft。OxygenTheme 通过 Foundation LocalOverscrollFactory 为滚动容器提供纵向回弹。

## 用法

```kotlin
OxygenTheme {
    LazyColumn {
        items(100) { index -> Text("Item $index") }
    }
}
```

## 边界契约

内容仍能消费滚动时，保持正常拖动与惯性。只有内容和嵌套滚动链未消费的纵向残量才进入过冲；横向交给容器或父级，不产生纵向弹簧。

Foundation performScroll 和 performFling 都返回已消费量。开始一次过冲需要超过半个物理像素的实际纵向残量。甩动余速本身不会起弹簧；在进行中的惯性已经产生边界位移后，同方向余速可以接入回弹。已拉出过冲后即使零速度松手也应回到零；把过冲全部收回后继续在中间甩动，不应再次产生位移。

## 大顶栏与下拉刷新

PullToRefreshBox 累积未被消费的手势下拉，松手时在惯性开始前判定是否刷新。Scaffold(topBarState = state) 会自动让顶栏优先。自定义嵌套滚动时，可以传入 `canRefresh = { topBarState.collapsedFraction <= 0f }`，先让顶栏展开。判断在实际分发滚动时读取状态。浮在内容上的顶栏可以通过 `indicatorTopPadding` 设置指示器偏移。

## 令牌与动效

最大位移取 spacing.overscrollMax，起手跟随比例取 interaction.overscrollDragScale；三次阻尼曲线渐沉。回弹由 MOT-SCROLL-001 的弹簧令牌驱动，原始触摸量与动画位移在接管时同步。此修复不调整曲线或弹簧参数。

## 回归与验收

catalog 的 Overscroll 页提供 80 项列表和顶部 / 中间 / 底部定位，显示首项与过冲状态。虚拟帧时钟覆盖完整消费、未经位移确认的余速、双向真实触边和亚像素残量、零速度释放、收回后继续甩动、横向速度；Desktop 真实 LazyColumn 逐帧对照列表中段甩动与惯性触顶，检查只有触顶才开始回弹。

测试保护 OxygenX 的行为契约，不替代 OxygenOS 逐帧拟合或全组件性能验收。原下拉刷新重载仍可用。
