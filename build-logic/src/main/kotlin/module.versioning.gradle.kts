// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0
import java.util.Properties

// 仅根工程应用：APK、BOM 和所有发布模块读取同一份解析结果。
val versionSource = providers.fileContents(layout.projectDirectory.file("version.properties")).asText.get()
val versionProperties = Properties().apply { load(versionSource.reader()) }
val segments = listOf("VERSION_MAJOR", "VERSION_MINOR", "VERSION_PATCH", "VERSION_SUFFIX").map { key ->
    require(versionSource.lineSequence().count { Regex("\\s*$key\\s*=.*").matches(it) } == 1) { "$key must appear exactly once" }
    val raw = versionProperties.getProperty(key, "").trim()
    require(Regex("0|[1-9][0-9]*").matches(raw)) { "$key must be a canonical non-negative integer" }
    requireNotNull(raw.toIntOrNull()) { "$key is too large" }
}
require(segments[3] in 0..1) { "VERSION_SUFFIX must be 0 (release) or 1 (beta)" }
val baseVersion = segments.take(3).joinToString(".")
val resolvedVersion = providers.gradleProperty("oxygenxVersion")
    .getOrElse(if (segments[3] == 1) "$baseVersion-beta.local" else baseVersion)
require(Regex("(0|[1-9][0-9]*)\\.(0|[1-9][0-9]*)\\.(0|[1-9][0-9]*)(-[0-9A-Za-z-]+(\\.[0-9A-Za-z-]+)*)?").matches(resolvedVersion)) {
    "oxygenxVersion must be a semantic version without build metadata"
}
resolvedVersion.substringAfter('-', "").split('.').filter { it.isNotEmpty() }.forEach {
    require(!it.all(Char::isDigit) || it == "0" || !it.startsWith('0')) { "Numeric prerelease identifiers cannot have leading zeros" }
}

fun gitValue(vararg arguments: String): String? = runCatching {
    val output = providers.exec {
        workingDir(rootDir)
        commandLine("git", *arguments)
        isIgnoreExitValue = true
    }
    if (output.result.get().exitValue == 0) output.standardOutput.asText.get().trim() else null
}.getOrNull()

val explicitCode = providers.gradleProperty("oxygenxVersionCode").orNull
require(explicitCode != null || gitValue("rev-parse", "--is-shallow-repository") != "true") {
    "Shallow Git history cannot determine versionCode; fetch full history or supply oxygenxVersionCode"
}
val versionCode = if (explicitCode != null) {
    requireNotNull(explicitCode.toIntOrNull()) { "oxygenxVersionCode must be an integer" }
} else {
    gitValue("rev-list", "--count", "HEAD")?.toIntOrNull() ?: 1
}
require(versionCode in 1..2_100_000_000) { "oxygenxVersionCode is outside the Android range" }

version = resolvedVersion
extra["oxygenxVersionCode"] = versionCode
extra["oxygenxBaseVersion"] = baseVersion

tasks.register("printOxygenVersion") {
    group = "oxygenx"
    description = "Print the shared library/catalog version and Android build number."
    doLast { println("OXYGENX_VERSION=$resolvedVersion\nOXYGENX_VERSION_CODE=$versionCode") }
}
