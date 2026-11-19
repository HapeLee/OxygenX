// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
plugins {
    id("module.kmp-library")
    id("module.android-library")
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    id("module.spotless")
    id("module.publishing")
    alias(libs.plugins.binaryCompatibilityValidator)
}

tasks.withType<Test>().configureEach {
    val goldenDirectory = rootProject.file("catalog/golden")
    inputs.dir(goldenDirectory)
    systemProperty("oxygenx.goldenDir", goldenDirectory.absolutePath)
    systemProperty("oxygenx.updateGoldens", providers.gradleProperty("oxygenxUpdateGoldens").getOrElse("false"))
    systemProperty(
        "oxygenx.actualGoldenDir",
        layout.buildDirectory
            .dir("golden-actual")
            .get()
            .asFile.absolutePath,
    )
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":oxygenx-theme"))
            api(project(":oxygenx-motion"))
            api(project(":oxygenx-effects"))
            api(project(":oxygenx-icons"))
            api(project(":oxygenx-core"))
            api(libs.compose.runtime)
            api(libs.compose.ui)
            api(libs.compose.foundation)
            implementation(libs.compose.animation)
            // 仅取 Material 的水波纹 indication（触摸态），不用其组件外观。
            implementation(libs.compose.material.ripple)
            implementation(libs.kotlinx.collections.immutable)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }
        desktopTest.dependencies {
            implementation(project(":oxygenx-icons-material"))
            implementation(libs.compose.ui.test)
            implementation(compose.desktop.currentOs)
        }
        androidMain.dependencies {
            // 预测式返回（Android 14+）：activity-compose 的 PredictiveBackHandler。
            implementation(libs.androidx.activity)
        }
    }
}
