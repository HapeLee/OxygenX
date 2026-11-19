// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        exclusiveContent {
            forRepository {
                maven { url = uri(providers.gradleProperty("oxygenxRepository").getOrElse("../../build/maven-repo")) }
            }
            filter { includeGroup("io.oxygenx") }
        }
    }
    versionCatalogs {
        create("libs") { from(files("../../gradle/libs.versions.toml")) }
    }
}

rootProject.name = "oxygenx-consumer-smoke"
