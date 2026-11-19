# OxygenX documentation

Unofficial / experimental. All components remain `draft`; these pages document current contracts, not a stable API guarantee. [简体中文](zh_CN/README.md).

## Start here

- [Setup and local publishing](getting-started.md)
- [Platform support](platforms.md)
- [Android performance entry points](performance.md)
- [Migration notes](migration.md)
- [Versioning and GitHub distribution](versioning.md)
- [Theme inheritance](theme.md)

## Components

| Component | Contract |
| --- | --- |
| [Text](components/text.md) | Color precedence |
| [Icon](components/icon.md) | Content color, original colors and resource overloads |
| [TextField](components/text-field.md) | Controlled input and focus motion |
| [Slider](components/slider.md) | RTL, keyboard, touch and accessibility |
| [Overscroll](components/overscroll.md) | Boundary-only rebound and normal inertia |
| [NavigationBar](components/navigation-bar.md) | List and slot APIs, selection and disabled state |
| [DropdownMenu](components/dropdown-menu.md) | Classic M3 style, bounded width, scrolling and RTL |
| [Snackbar](components/snackbar.md) | Queueing, cancellation and timeout |

This hardening pass documents the components above. Other components are demonstrated in the Android / Desktop catalog; their full documentation is pending. Contributor rules live in [spec/](spec/README.md).
