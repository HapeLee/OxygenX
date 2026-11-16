// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0

import io.oxygenx.build.generateTokens
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

val tokenSources = fileTree(rootProject.file("design/tokens")) {
    include("**/*.yaml")
}
val generatedDir = layout.projectDirectory.dir("src/commonMain/kotlin/io/oxygenx/theme/generated")

tasks.register("generateTokens") {
    group = "oxygenx"
    description = "Generate Kotlin token constants from design/tokens YAML sources."
    inputs.files(tokenSources)
    outputs.dir(generatedDir)
    doLast {
        generateTokens(tokenSources.files, generatedDir.asFile, rootProject.rootDir)
    }
}

tasks.withType<KotlinCompilationTask<*>>().configureEach {
    dependsOn("generateTokens")
}

// 发布用的源码包（androidSourcesJar / desktopSourcesJar / …）也会读 generated 目录，
// 它们不是 KotlinCompilationTask，必须单独声明依赖，否则 Gradle 9 判为隐式依赖并直接失败。
tasks.configureEach {
    if (name.endsWith("SourcesJar", ignoreCase = true)) {
        dependsOn("generateTokens")
    }
}
