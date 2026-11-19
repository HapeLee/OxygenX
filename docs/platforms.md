# Platform support

Unofficial / experimental. Compilation, behavior and device fidelity are separate promises.

## Support matrix

| Platform | Current scope | Remaining validation |
| --- | --- | --- |
| Android, minSdk 26 | Main platform, APK catalog; ModalBottomSheet predictive back | Full TalkBack matrix, frame calibration and high-refresh benchmarks |
| Desktop JVM | Preview and offscreen UI tests on Windows / JDK 21 | Other desktop systems and device-specific input |
| WasmJs | Library compilation and common logic browser tests | Web application entry, browser accessibility and UI parity |
| iOS / macOS Native | Not configured | No support commitment |

## Regression coverage

`./gradlew check` includes ABI and existing tests. Desktop UI tests cover theme inheritance, text colors, Slider input, representative Button / Checkbox / TextField behavior and animation interruption. Light/dark × LTR/RTL × font scale 1/2 is representative coverage, not full coverage of every component.

Slider PNG goldens are fixed-density, text-free OxygenX regression baselines, not OxygenOS references. See [golden instructions](../catalog/golden/README.md).

## CI and releases

A Windows CI workflow checks formatting, compilation, tests/ABI, bilingual documentation and local publication. Remote status requires an actual GitHub Actions run. Signed GitHub asset distribution is configured; Maven Central/GitHub Packages hosting and a stable API compatibility policy remain unconfigured.

A manual GitHub Release workflow now builds signed APK and Maven repository assets; see [versioning](versioning.md) for activation and validation. Maven Central, GitHub Packages, Native targets and full component acceptance remain unconfigured.
