// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0

plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidKotlinMultiplatformLibrary) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.binaryCompatibilityValidator) apply false
}

tasks.register("oxygenxInfo") {
    group = "oxygenx"
    description = "Print the active build configuration."
    doLast {
        println("OxygenX | Gradle ${gradle.gradleVersion} | Kotlin/CMP versions: gradle/libs.versions.toml")
        println("Android SDK: ${providers.gradleProperty("oxygenx.androidSdkDir").orElse("unset").get()}")
    }
}
