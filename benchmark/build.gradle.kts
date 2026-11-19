// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
plugins {
    alias(libs.plugins.androidTest)
    alias(libs.plugins.baselineprofile)
    id("module.spotless")
}

android {
    namespace = "io.oxygenx.benchmark"
    compileSdk { version = release(37) }
    defaultConfig {
        minSdk = 28
        targetSdk = 36
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    targetProjectPath = ":catalog:android"
}

baselineProfile { useConnectedDevices = true }

dependencies {
    implementation(libs.androidx.test.junit)
    implementation(libs.androidx.test.runner)
    implementation(libs.androidx.uiautomator)
    implementation(libs.androidx.benchmark.macro)
}

androidComponents {
    onVariants { variant ->
        val loader = variant.artifacts.getBuiltArtifactsLoader()
        variant.instrumentationRunnerArguments.put(
            "targetAppId",
            variant.testedApks.map { loader.load(it)?.applicationId ?: error("Missing target app id") },
        )
    }
}

tasks.register("runStartupBenchmark") {
    group = "verification"
    description = "Measure cold startup on a connected physical device using a release-like catalog."
    dependsOn("connectedBenchmarkReleaseAndroidTest")
}
