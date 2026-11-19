// Copyright 2026, OxygenX contributors
// SPDX-License-Identifier: Apache-2.0

package io.oxygenx.build

import java.io.File

private val idPattern = Regex("[a-z][a-z0-9]*(\\.[a-z0-9]+)+")
private val hexColorPattern = Regex("0x[0-9A-Fa-f]{8}")
private val numberPattern = Regex("-?[0-9]+(\\.[0-9]+)?")

private val requiredFields = listOf("id", "kind", "baseline", "source", "evidence", "status")
private val evidenceLevels = setOf("E0", "E1", "E2", "E3")
private val statuses = setOf("draft", "in-review", "verified")
/** 只允许通用字族，避免把厂商专有字体当成令牌。 */
private val fontFamilies = setOf("sans-serif", "serif", "monospace")
private val kinds = setOf(
    "color",
    "dimension",
    "typography",
    "duration",
    "easing",
    "spring",
    "stagger",
    "motionSpec",
)

private data class Token(
    val id: String,
    val fields: Map<String, String>,
    val origin: String,
)

/**
 * 把 design/tokens 下的 YAML 编译成 GeneratedTokens.kt。
 * 失败即报错：不生成半成品，也不静默放过无凭据的 verified 令牌。
 */
fun generateTokens(sources: Collection<File>, outputDir: File, rootDir: File) {
    val tokens = sources.sortedBy { it.name }.flatMap { parseTokens(it) }
    require(tokens.isNotEmpty()) { "design/tokens/*.yaml 中没有解析到任何令牌" }

    val seen = mutableSetOf<String>()
    tokens.forEach { token ->
        require(seen.add(token.id)) { "重复的令牌 ID `${token.id}`：${token.origin}" }
        validate(token, rootDir)
    }

    val target = outputDir.resolve("GeneratedTokens.kt")
    target.parentFile.mkdirs()
    target.writeText(render(tokens))
    println("[OxygenX] ${tokens.size} 个令牌 -> ${target.path}")
}

private fun parseTokens(file: File): List<Token> {
    val result = mutableListOf<Token>()
    var current: LinkedHashMap<String, String>? = null
    var startLine = 0

    file.readLines().forEachIndexed { index, raw ->
        val trimmed = raw.trim()
        if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed == "tokens:") return@forEachIndexed

        val isNewEntry = trimmed.startsWith("- ")
        val content = if (isNewEntry) trimmed.removePrefix("- ") else trimmed
        val separator = content.indexOf(": ")
        require(separator > 0) {
            "${file.name}:${index + 1} 无法解析「$raw」；令牌文件只允许扁平 `key: value`"
        }
        val key = content.substring(0, separator).trim()
        val value = content.substring(separator + 2).trim()
        require(key.isNotEmpty() && value.isNotEmpty()) {
            "${file.name}:${index + 1} 键值不能为空"
        }

        if (isNewEntry) {
            current?.let { result += Token(it["id"].orEmpty(), HashMap(it), "${file.name}:$startLine") }
            current = linkedMapOf(key to value)
            startLine = index + 1
        } else {
            val entry = current ?: error("${file.name}:${index + 1} 字段出现在首个 `-` 条目之前")
            require(key !in entry) { "${file.name}:${index + 1} 字段 `$key` 重复" }
            entry[key] = value
        }
    }
    current?.let { result += Token(it["id"].orEmpty(), HashMap(it), "${file.name}:$startLine") }
    return result
}

private fun validate(token: Token, rootDir: File) {
    val f = token.fields
    fun field(name: String): String = f[name]
        ?: error("令牌 ${token.id}（${token.origin}）缺少字段 `$name`")

    requiredFields.forEach {
        require(it in f) { "令牌 ${token.id}（${token.origin}）缺少必填字段 `$it`" }
    }
    require(idPattern.matches(token.id)) { "非法令牌 ID `${token.id}`：${token.origin}" }
    require(field("kind") in kinds) { "未知 kind `${field("kind")}`：${token.origin}" }
    require(field("evidence") in evidenceLevels) { "非法证据等级：${token.origin}" }
    require(field("status") in statuses) { "非法状态：${token.origin}" }

    if (field("status") == "verified") {
        require(field("evidence") in setOf("E2", "E3")) {
            "verified 令牌 ${token.id} 需要 E2/E3 证据，当前是 ${field("evidence")}"
        }
        val source = field("source")
        require(!source.contains("<") && rootDir.resolve(source).exists()) {
            "verified 令牌 ${token.id} 需要指向存在的 reference：$source"
        }
        require(!field("baseline").contains("<")) {
            "verified 令牌 ${token.id} 的 baseline 不能含占位符"
        }
    }

    when (field("kind")) {
        "color" -> listOf("light", "dark").forEach {
            require(hexColorPattern.matches(field(it))) {
                "令牌 ${token.id} 的 $it 必须是 0xAARRGGBB"
            }
        }
        "dimension" -> require(numberPattern.matches(field("value"))) { "dimension 需要数值 value：${token.origin}" }
        "typography" -> {
            listOf("size", "lineHeight", "weight", "letterSpacing").forEach {
                require(numberPattern.matches(field(it))) { "typography 需要数值 $it：${token.origin}" }
            }
            require(field("family") in fontFamilies) {
                "typography 的 family 只允许通用字族（${fontFamilies.joinToString()}）：${token.origin}"
            }
        }
        "duration" -> require(numberPattern.matches(field("millis"))) { "duration 需要数值 millis：${token.origin}" }
        "stagger" -> require(numberPattern.matches(field("delayMillis"))) { "stagger 需要数值 delayMillis：${token.origin}" }
        "spring" -> listOf("stiffness", "dampingRatio", "visibilityThreshold").forEach {
            require(numberPattern.matches(field(it))) { "spring 需要数值 $it：${token.origin}" }
        }
        "easing" -> require(field("curve").isNotBlank()) { "easing 需要 curve：${token.origin}" }
        "motionSpec" -> listOf("enterSpec", "exitSpec").forEach {
            require(field(it).isNotBlank()) { "motionSpec 需要 $it：${token.origin}" }
        }
    }
}

private fun render(tokens: List<Token>): String = buildString {
    appendLine("// Copyright 2026, OxygenX contributors")
    appendLine("// SPDX-License-Identifier: Apache-2.0")
    appendLine("//")
    appendLine("// 本文件由 ./gradlew :oxygenx-theme:generateTokens 生成，请勿手工修改。")
    appendLine("// 真源：design/tokens 下的 YAML；取值来源与证据等级见各令牌注释。")
    appendLine()
    appendLine("@file:Suppress(\"unused\", \"RedundantVisibilityModifier\")")
    appendLine()
    appendLine("package io.oxygenx.theme.generated")
    appendLine()
    appendLine("public object GeneratedTokens {")

    tokens.forEach { token ->
        val f = token.fields
        val name = token.id.uppercase().replace('.', '_').replace('-', '_')
        appendLine("    // ${token.id} | ${f["evidence"]} | ${f["status"]}")
        appendLine("    // baseline: ${f["baseline"]}")
        when (f.getValue("kind")) {
            "color" -> {
                appendLine("    public const val ${name}_LIGHT: Long = ${f.getValue("light")}L")
                appendLine("    public const val ${name}_DARK: Long = ${f.getValue("dark")}L")
            }
            "dimension" -> appendLine("    public const val $name: Float = ${asFloat(f.getValue("value"))}")
            "typography" -> {
                appendLine("    public const val ${name}_SIZE: Float = ${asFloat(f.getValue("size"))}")
                appendLine("    public const val ${name}_LINE_HEIGHT: Float = ${asFloat(f.getValue("lineHeight"))}")
                appendLine("    public const val ${name}_WEIGHT: Int = ${f.getValue("weight").toInt()}")
                appendLine("    public const val ${name}_LETTER_SPACING: Float = ${asFloat(f.getValue("letterSpacing"))}")
                appendLine("    public const val ${name}_FAMILY: String = \"${f.getValue("family")}\"")
            }
            "duration" -> appendLine("    public const val ${name}_MILLIS: Long = ${asLong(f.getValue("millis"))}")
            "stagger" -> appendLine("    public const val ${name}_DELAY_MILLIS: Long = ${asLong(f.getValue("delayMillis"))}")
            "spring" -> {
                appendLine("    public const val ${name}_STIFFNESS: Float = ${asFloat(f.getValue("stiffness"))}")
                appendLine("    public const val ${name}_DAMPING_RATIO: Float = ${asFloat(f.getValue("dampingRatio"))}")
                appendLine("    public const val ${name}_VISIBILITY_THRESHOLD: Float = ${asFloat(f.getValue("visibilityThreshold"))}")
            }
            "easing" -> appendLine("    public const val ${name}_CURVE: String = \"${f.getValue("curve")}\"")
            "motionSpec" -> {
                appendLine("    public const val ${name}_ENTER: String = \"${f.getValue("enterSpec")}\"")
                appendLine("    public const val ${name}_EXIT: String = \"${f.getValue("exitSpec")}\"")
            }
        }
        appendLine()
    }
    appendLine("}")
}

private fun asLong(raw: String): String = "${raw.toLong()}L"

private fun asFloat(raw: String): String {
    val value = raw.toFloat()
    return if (value == value.toLong().toFloat()) "${value.toLong()}f" else "${value}f"
}
