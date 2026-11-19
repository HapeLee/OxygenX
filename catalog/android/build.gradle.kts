// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
import com.android.build.api.variant.HostTestBuilder
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.baselineprofile)
    id("module.spotless")
}

// 本地文件被 Git 忽略；CI 从 Secrets 注入环境变量，口令不进入命令行或版本控制。
val localSigning =
    Properties().apply {
        val source = rootProject.file("signing.properties")
        if (source.isFile) source.inputStream().use { load(it) }
    }

fun signingValue(key: String): String? =
    providers.environmentVariable(key).orNull
        ?: localSigning.getProperty(key)
val signingKeys = listOf("RELEASE_STORE_FILE", "RELEASE_STORE_PASSWORD", "RELEASE_KEY_ALIAS", "RELEASE_KEY_PASSWORD")
val signingValues = signingKeys.associateWith(::signingValue)
val hasReleaseSigning = signingValues.values.any { it != null }
require(!hasReleaseSigning || signingValues.values.all { !it.isNullOrBlank() }) { "Release signing configuration is incomplete" }

android {
    namespace = "io.oxygenx.catalog.android"
    buildFeatures { buildConfig = true }

    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "io.oxygenx.catalog"
        minSdk = 26
        targetSdk = 36
        versionCode = rootProject.extra["oxygenxVersionCode"] as Int
        versionName = rootProject.version.toString()
    }
    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = rootProject.file(requireNotNull(signingValues["RELEASE_STORE_FILE"]))
                storePassword = signingValues["RELEASE_STORE_PASSWORD"]
                keyAlias = signingValues["RELEASE_KEY_ALIAS"]
                keyPassword = signingValues["RELEASE_KEY_PASSWORD"]
            }
        }
    }
    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            if (hasReleaseSigning) signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
        create("nonMinifiedRelease") {
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = ".benchmark"
        }
        create("benchmarkRelease") {
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = ".benchmark"
            matchingFallbacks += "release"
        }
    }
}

androidComponents {
    // AGP 9.5 的 Compose 变体要求 unit-test component；profile 插件会禁用扩展变体的该组件。
    beforeVariants(selector().all()) { builder -> builder.hostTests[HostTestBuilder.UNIT_TEST_TYPE]?.enable = true }
}

dependencies {
    implementation(project(":catalog:shared"))
    implementation(libs.androidx.activity)
    implementation(libs.androidx.profileinstaller)
    "baselineProfile"(project(":benchmark"))
}
