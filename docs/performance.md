# Android performance entry points

Unofficial / draft. benchmark is an Android test module for the catalog and is not published with the UI library. Startup measurements and component frame/performance acceptance are separate.

## Run

```powershell
# Build the test APK without connecting a device
.\gradlew.bat :benchmark:assemble
# Select one unlocked physical Android 13+ device
$env:ANDROID_SERIAL = '<device-serial>'
.\gradlew.bat :catalog:android:generateBaselineProfile
.\gradlew.bat :benchmark:runStartupBenchmark
```

## Measurement contract

BaselineProfileGenerator collects catalog startup and waits for its real index node. Generated rules are written below catalog/android/src in generated/baselineProfiles; profiles must not be fabricated manually. Coverage is limited to the captured startup path and is not a library profile.

StartupBenchmarks uses the same benchmarkRelease APK: non-debuggable, R8 enabled, shell profiling enabled and signed locally with the debug key. It runs 10 cold starts each with CompilationMode.None and Partial(BaselineProfileMode.Require), reporting initial display and full display after ReportDrawn. Require fails if the packaged profile is missing. Device-state errors are not automatically suppressed.

Only the Android entry maps the index test tag to a resource id. UIAutomator timeouts fail; a fixed sleep does not establish successful startup. ProfileInstaller is a catalog dependency and does not enter the core library.

Some vendor ART configurations delay the first profile save. If the real index loads but capture is empty, append `-Pandroid.testInstrumentationRunnerArguments.profileSaveDelayMs=10000` to the generation command. This waits for profile capture only and never enters startup measurement. Allowed range: 0–60000 ms, default 0. Stop inspection-tool services that hold UIAutomation before running instrumentation.

## Results and limits

Detailed JSON, Perfetto traces and instrumentation reports live below benchmark/build/outputs and benchmark/build/reports. Repackage benchmarkRelease after generating the profile, then compare compilation modes of the same APK. Do not subtract debug startup from release startup.

Use an unlocked physical device. Profile generation requires Android 13+ or a supported rooted device. Temperature, system load, power, refresh rate and OS version affect results and should be recorded. CI builds the test APK without claiming performance acceptance on a shared runner.

This entry does not measure scrolling frame distributions, component recomposition counts, OxygenOS motion fitting or every library code path; runMotionBenchmark remains unimplemented.

## Sources

Follows official [Baseline Profile generation](https://developer.android.com/topic/performance/baselineprofiles/create-baselineprofile) and [Macrobenchmark configuration](https://developer.android.com/topic/performance/benchmarking/macrobenchmark-overview). Versions are pinned in gradle/libs.versions.toml.

2026-11-17 local capture: Xiaomi 2211133C / Android 17 API 37 produced catalog startup profiles with profileSaveDelayMs=10000 after the default capture returned empty. This device exposes an 8000 ms first-save delay. Generated rules are retained under catalog/android/src/release/generated/baselineProfiles. Cold-start measurements are recorded separately in [the work log](spec/text-icon-performance-plan.md).

The same-day cold-start comparison passed: same R8 APK, 10 runs per mode, median 401.3 ms → 333.8 ms (about -16.8% for this sample). Raw data is in benchmark/results/2026-11-17-xiaomi-startup.json. Some synthetic startup methods did not match. See the work log for coverage, power/temperature/OS conditions and limits; this does not establish performance for other devices or every component.
