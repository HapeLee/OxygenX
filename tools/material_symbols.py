"""Fetch pinned Google Material Symbols and regenerate the two compatible facades.

python tools/material_symbols.py --fetch --update-lock  # explicitly refresh upstream
python tools/material_symbols.py --fetch                # verify pinned upstream
python tools/material_symbols.py --check                # offline generated-file gate
"""

import argparse
from concurrent.futures import ThreadPoolExecutor
import gzip
import hashlib
import json
from pathlib import Path
import re
import urllib.request

ROOT = Path(__file__).resolve().parents[1]
MANIFEST = ROOT / "design/icons/material-symbols.json"
BASE = "https://fonts.gstatic.com/render/v1/Material+Symbols+Outlined/24dp/"
AXES = "?var=opsz,wght,FILL,GRAD@24,300,0,0"


def digest(data):
    return hashlib.sha256(data).hexdigest()


def target(icon):
    core = icon.get("core")
    module = "oxygenx-icons" if core else "oxygenx-icons-material"
    package = "io.oxygenx.icons.icon" if core else "io.oxygenx.icons.material"
    name = (core or icon["name"]) + "Glyph"
    return ROOT / module / "src/commonMain/kotlin" / package.replace(".", "/") / "generated" / (name + ".kt")


def transform(source, icon):
    symbol = icon["symbol"]
    core = icon.get("core")
    name = (core or icon["name"]) + "Glyph"
    package = "io.oxygenx.icons.icon" if core else "io.oxygenx.icons.material"
    # Replace identifiers only; preserve the official symbol name in Builder.name.
    source = source.replace("package com.example.test", "package " + package + ".generated")
    source = source.replace("public val " + symbol + ":", "internal val " + name + ":")
    source = re.sub(r"\b_" + re.escape(symbol) + r"\b", "_" + name, source)
    if icon.get("autoMirror"):
        source = source.replace("viewportHeight = 24f,", "viewportHeight = 24f,\n          autoMirror = true,")
    header = (
        "// Material Symbols (https://fonts.google.com/icons) - Apache License 2.0\n"
        "// Copyright 2026 Google LLC\n// SPDX-License-Identifier: Apache-2.0\n//\n"
        f"// Generated: {BASE}{symbol}.kt{AXES}\n"
        "// Axes: opsz=24, wght=300, FILL=0, GRAD=0 (Outlined style)\n"
        "// Do not edit by hand; run python tools/material_symbols.py --fetch.\n\n"
    )
    return header + source.rstrip() + "\n"


def facade(icons):
    lines = [
        "// Copyright 2026, OxygenX contributors",
        "// SPDX-License-Identifier: Apache-2.0",
        "package io.oxygenx.icons.material", "",
        "import androidx.compose.ui.graphics.vector.ImageVector",
        "import io.oxygenx.icons.icon.OxygenIcons",
    ]
    lines += sorted(f'import io.oxygenx.icons.material.generated.{i["name"]}Glyph' for i in icons if not i.get("core"))
    lines += ["", "/**",
        " * Google Material Symbols: Outlined, opsz=24, wght=300, FILL=0, GRAD=0; Apache-2.0.",
        " * Includes the original 100 symbols, the 14 core symbols and application additions.",
        " * Core symbols reuse OxygenIcons vectors; individual getters remain lazy and shrinkable.",
        " * Directional vectors declare autoMirror; Icon also accepts explicit mirrorInRtl safely.",
        " * Sources and SHA-256 pins: design/icons/material-symbols.json.",
        " */", "object MaterialSymbols {"]
    for i in icons:
        ref = "OxygenIcons." + i["core"] if i.get("core") else i["name"] + "Glyph"
        lines += [f'    /** `{i["symbol"]}` */', f'    val {i["name"]}: ImageVector get() = {ref}', ""]
    lines += ["    /** Enumerates all vectors once; use individual getters for normal application icons. */",
        "    val All: List<ImageVector> get() = allIcons", "",
        "    private val allIcons: List<ImageVector> by lazy {", "        listOf("]
    lines += [f'            {i["name"]},' for i in icons]
    lines += ["        )", "    }", "}", ""]
    return "\n".join(lines)


def write(path, content):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content, encoding="utf-8", newline="\n")


def fetch(icon, update):
    url = BASE + icon["symbol"] + ".kt" + AXES
    with urllib.request.urlopen(url, timeout=60) as response:
        data = response.read()
    if data[:2] == b"\x1f\x8b":
        data = gzip.decompress(data)
    source = data.decode("utf-8").replace("\r\n", "\n")
    actual = digest(source.encode())
    if not update and actual != icon.get("sourceSha256"):
        raise ValueError(f"Upstream SHA-256 changed: {icon['symbol']}; review before --update-lock")
    if f'public val {icon["symbol"]}: ImageVector' not in source or 'viewportHeight = 24f' not in source:
        raise ValueError("Unexpected Google response: " + url)
    return icon, actual, transform(source, icon)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--fetch", action="store_true")
    parser.add_argument("--update-lock", action="store_true")
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    if not args.fetch and not args.check or args.update_lock and not args.fetch:
        parser.error("choose --fetch or --check; --update-lock requires --fetch")
    manifest = json.loads(MANIFEST.read_text(encoding="utf-8"))
    icons = manifest["icons"]
    if manifest["style"] != "Outlined" or manifest["axes"] != {"opsz": 24, "wght": 300, "FILL": 0, "GRAD": 0}:
        raise ValueError("This collection requires Outlined / opsz=24 / wght=300 / FILL=0 / GRAD=0")
    assert icons == sorted(icons, key=lambda i: i["name"])
    assert len({i["symbol"] for i in icons}) == len(icons)
    assert len({i["name"] for i in icons}) == len(icons)
    facade_path = ROOT / "oxygenx-icons-material/src/commonMain/kotlin/io/oxygenx/icons/material/MaterialSymbols.kt"
    if args.fetch:
        # Complete and validate every download before changing any generated source.
        with ThreadPoolExecutor(max_workers=8) as pool:
            results = list(pool.map(lambda i: fetch(i, args.update_lock), icons))
        for icon, source_hash, content in results:
            icon["sourceSha256"] = source_hash
            icon["generatedSha256"] = digest(content.encode())
            write(target(icon), content)
        write(facade_path, facade(icons))
        write(MANIFEST, json.dumps(manifest, indent=2, ensure_ascii=False) + "\n")
    for icon in icons:
        content = target(icon).read_text(encoding="utf-8")
        if digest(content.encode()) != icon["generatedSha256"]:
            raise ValueError("Generated file differs from manifest: " + str(target(icon)))
    expected = {target(icon) for icon in icons}
    actual = {path for folder in {path.parent for path in expected} for path in folder.glob("*.kt")}
    if actual != expected:
        raise ValueError("Generated glyph files differ from the manifest: " + str(actual ^ expected))
    if facade_path.read_text(encoding="utf-8") != facade(icons):
        raise ValueError("MaterialSymbols facade needs regeneration")
    print(f"Material Symbols: {len(icons)} unique Outlined / 300 vectors; 14 core aliases; pins verified")


if __name__ == "__main__":
    main()
