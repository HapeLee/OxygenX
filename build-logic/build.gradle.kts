// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0

plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.android.gradle.plugin)
    implementation(libs.spotless.plugin.gradle)
}
