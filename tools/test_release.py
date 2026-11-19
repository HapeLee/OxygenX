"""Distribution regressions: immutable names, strict inputs and complete Maven bundles."""

import json
from pathlib import Path
import tempfile
import unittest
from unittest.mock import patch
import zipfile

import release


class VersionTests(unittest.TestCase):
    def properties(self, text):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "version.properties"
            path.write_text(text, encoding="utf-8")
            return release.read_version(path)

    def test_initial_version(self):
        self.assertEqual(("0.0.1", False), self.properties("VERSION_MAJOR=0\nVERSION_MINOR=0\nVERSION_PATCH=1\nVERSION_SUFFIX=0\n"))

    def test_beta_and_windows_whitespace(self):
        self.assertEqual(("1.2.3", True), self.properties("# header\r\nVERSION_MAJOR = 1\r\nVERSION_MINOR=2\r\nVERSION_PATCH=3\r\nVERSION_SUFFIX=1\r\n"))

    def test_rejects_ambiguous_properties(self):
        valid = "VERSION_MAJOR=0\nVERSION_MINOR=0\nVERSION_PATCH=1\nVERSION_SUFFIX=0\n"
        for invalid in (valid + "VERSION_PATCH=2\n", valid.replace("VERSION_SUFFIX=0\n", ""), valid.replace("PATCH=1", "PATCH=01"), valid.replace("SUFFIX=0", "SUFFIX=2"), valid.replace("PATCH=1", "PATCH=-1"), valid + "TOKEN=unexpected\n"):
            with self.subTest(invalid=invalid), self.assertRaises(ValueError):
                self.properties(invalid)

    def test_release_does_not_reuse_existing_version(self):
        for beta in (False, True):
            with self.subTest(beta=beta), self.assertRaises(ValueError):
                release.resolve_version("0.0.1", beta, "release", ["v0.0.1"], "a" * 40, 7)

    def test_beta_uses_highest_matching_tag_not_tag_count(self):
        result = release.resolve_version("0.0.1", True, "release", ["v0.0.1-beta.2", "v0.0.1-beta.9", "v0.0.2-beta.99", "v0.0.1-beta.bad"], "a" * 40, 7)
        self.assertEqual("0.0.1-beta.10", result["name"])
        self.assertTrue(result["prerelease"])
        self.assertEqual(7, result["code"])

    def test_local_and_ci_names_cannot_take_release_name(self):
        self.assertEqual("0.0.1-beta.local", release.resolve_version("0.0.1", True, "local", [], "a" * 40, 7)["name"])
        self.assertEqual("0.0.1-ci." + "a" * 40, release.resolve_version("0.0.1", False, "ci", [], "a" * 40, 7)["name"])

    def test_rejects_version_injection_and_noncanonical_identifiers(self):
        for version in ("0.0.1\ncode=9", "../0.0.1", "00.0.1", "0.0.1-beta.01", "0.0.1+untracked"):
            with self.subTest(version=version), self.assertRaises(ValueError):
                release.validate_version(version)

    def test_rejects_android_version_code_outside_range(self):
        for code in (0, -1, 2_100_000_001):
            with self.subTest(code=code), self.assertRaises(ValueError):
                release.resolve_version("0.0.1", False, "release", [], "a" * 40, code)


class ApkTests(unittest.TestCase):
    fingerprint = "d" * 64
    metadata = "package: name='io.oxygenx.catalog' versionCode='2' versionName='0.0.1'"

    def verify(self, certs, metadata=None):
        with patch.object(release, "run", side_effect=[certs, metadata or self.metadata]):
            release.verify_apk(Path("catalog.apk"), Path("sdk"), "0.0.1", 2, self.fingerprint)

    def test_supports_legacy_and_build_tools_37_signer_output(self):
        for prefix in ("Signer #1", "V2 Signer:", "V3 Signer:", "V3.1 Signer:"):
            with self.subTest(prefix=prefix):
                self.verify(f"{prefix} certificate SHA-256 digest: {self.fingerprint}\r\n")

    def test_rejects_missing_wrong_or_additional_unexpected_signers(self):
        correct = f"V2 Signer: certificate SHA-256 digest: {self.fingerprint}"
        wrong = "Signer #2 certificate SHA-256 digest: " + "e" * 64
        for certs in ("", wrong, correct + "\n" + wrong, "Source Stamp Signer certificate SHA-256 digest: " + self.fingerprint):
            with self.subTest(certs=certs), self.assertRaises(ValueError):
                self.verify(certs)

    def test_rejects_wrong_package_or_version(self):
        certs = f"V2 Signer: certificate SHA-256 digest: {self.fingerprint}"
        for metadata in (self.metadata.replace("io.oxygenx.catalog", "io.oxygenx.catalog.debug"), self.metadata.replace("versionCode='2'", "versionCode='3'"), self.metadata.replace("0.0.1", "0.0.2")):
            with self.subTest(metadata=metadata), self.assertRaises(ValueError):
                self.verify(certs, metadata)


class RepositoryTests(unittest.TestCase):
    def create_repository(self, root, version="0.0.1"):
        modules = {"oxygenx-bom", *release.MODULES, *(f"{module}-{target}" for module in release.MODULES for target in ("android", "desktop", "wasm-js"))}
        for module in modules:
            folder = root / "io/oxygenx" / module / version
            folder.mkdir(parents=True)
            (folder / f"{module}-{version}.pom").write_text(f'<project xmlns="http://maven.apache.org/POM/4.0.0"><groupId>io.oxygenx</groupId><artifactId>{module}</artifactId><version>{version}</version></project>')
            (folder / f"{module}-{version}.module").write_text(json.dumps({"component": {"group": "io.oxygenx", "module": module, "version": version}}))
            extension = ".aar" if module.endswith("-android") else ".klib" if module.endswith("-wasm-js") else ".jar"
            if module != "oxygenx-bom":
                (folder / f"{module}-{version}{extension}").write_bytes(b"fixture")

    def test_bundle_contains_only_selected_version_and_no_signing_files(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory) / "repository"
            self.create_repository(root)
            self.create_repository(root, "0.0.1-local.1")
            (root / "io/oxygenx/oxygenx-ui/0.0.1/private.jks").write_bytes(b"must not ship")
            archive = Path(directory) / "maven.zip"
            release.zip_repository(root, "0.0.1", archive)
            with zipfile.ZipFile(archive) as bundle:
                self.assertTrue(bundle.namelist())
                self.assertTrue(all("/0.0.1/" in name and not name.endswith(".jks") for name in bundle.namelist()))
                self.assertIn("io/oxygenx/oxygenx-ui-android/0.0.1/oxygenx-ui-android-0.0.1.aar", bundle.namelist())

    def test_missing_platform_binary_fails_instead_of_shipping_partial_repo(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            self.create_repository(root)
            (root / "io/oxygenx/oxygenx-ui-android/0.0.1/oxygenx-ui-android-0.0.1.aar").unlink()
            with self.assertRaises(ValueError):
                release.maven_files(root, "0.0.1")

    def test_mixed_version_metadata_is_rejected(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            self.create_repository(root)
            metadata = root / "io/oxygenx/oxygenx-ui/0.0.1/oxygenx-ui-0.0.1.module"
            metadata.write_text(json.dumps({"component": {"group": "io.oxygenx", "module": "oxygenx-ui", "version": "0.0.2"}}))
            with self.assertRaises(ValueError):
                release.maven_files(root, "0.0.1")

    def test_kmp_variant_metadata_can_identify_its_common_publication(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            self.create_repository(root)
            metadata = root / "io/oxygenx/oxygenx-ui-android/0.0.1/oxygenx-ui-android-0.0.1.module"
            metadata.write_text(json.dumps({"component": {"group": "io.oxygenx", "module": "oxygenx-ui", "version": "0.0.1"}}))
            self.assertTrue(release.maven_files(root, "0.0.1"))


if __name__ == "__main__":
    unittest.main()
