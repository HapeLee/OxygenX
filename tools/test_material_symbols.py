"""Offline reproducibility and generator fixture checks."""

import json
import unittest

from material_symbols import MANIFEST, digest, facade, target, transform


class MaterialSymbolsGeneratorTest(unittest.TestCase):
    def test_google_fixture_preserves_paths_and_symbol_name(self):
        fixture = '''package com.example.test
public val arrow_back: ImageVector
  get() {
    if (_arrow_back != null) return _arrow_back!!
    _arrow_back = ImageVector.Builder(name = "arrow_back",
          viewportHeight = 24f,
        ).apply { path { moveTo(1f, 2f) } }.build()
    return _arrow_back!!
  }
private var _arrow_back: ImageVector? = null
'''
        icon = {"symbol": "arrow_back", "name": "ArrowBack", "autoMirror": True}
        result = transform(fixture, icon)
        self.assertIn('name = "arrow_back"', result)
        self.assertIn("internal val ArrowBackGlyph: ImageVector", result)
        self.assertIn("_ArrowBackGlyph!!", result)
        self.assertIn("autoMirror = true,", result)
        self.assertIn("path { moveTo(1f, 2f) }", result)
        self.assertNotIn("Rounded", result)

    def test_all_generated_files_match_pins_and_core_vectors_are_not_duplicated(self):
        icons = json.loads(MANIFEST.read_text(encoding="utf-8"))["icons"]
        self.assertEqual(128, len(icons))
        self.assertEqual(14, sum(bool(i.get("core")) for i in icons))
        for icon in icons:
            self.assertEqual(64, len(icon["sourceSha256"]))
            self.assertEqual(icon["generatedSha256"], digest(target(icon).read_text(encoding="utf-8").encode()))
        output = facade(icons)
        self.assertIn("val Settings: ImageVector get() = OxygenIcons.Settings", output)
        self.assertNotIn("import io.oxygenx.icons.material.generated.SettingsGlyph", output)


if __name__ == "__main__":
    unittest.main()
