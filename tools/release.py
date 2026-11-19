"""Resolve shared versions and verify/package OxygenX distribution artifacts."""

import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import shutil
import subprocess
import xml.etree.ElementTree as ET
import zipfile

ROOT = Path(__file__).resolve().parents[1]
MODULES = (
    "oxygenx-core", "oxygenx-theme", "oxygenx-motion", "oxygenx-effects",
    "oxygenx-icons", "oxygenx-icons-material", "oxygenx-ui", "oxygenx-material3",
)
SEMVER = re.compile(r"(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)(?:-([0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*))?")


def validate_version(value: str) -> str:
    match = SEMVER.fullmatch(value)
    if not match or any(part.isdecimal() and len(part) > 1 and part.startswith("0") for part in (match[4] or "").split(".")):
        raise ValueError("Invalid semantic version")
    return value


def read_version(path: Path) -> tuple[str, bool]:
    values = {}
    keys = {"VERSION_MAJOR", "VERSION_MINOR", "VERSION_PATCH", "VERSION_SUFFIX"}
    for line in path.read_text(encoding="utf-8").splitlines():
        line = line.strip()
        if not line or line.startswith(("#", "!")):
            continue
        key, separator, value = line.partition("=")
        key, value = key.strip(), value.strip()
        if not separator or key not in keys or key in values or not re.fullmatch(r"0|[1-9][0-9]*", value):
            raise ValueError("Version file has missing, duplicate or invalid properties")
        if int(value) > 2_147_483_647:
            raise ValueError("Version property exceeds the Gradle integer range")
        values[key] = int(value)
    if values.keys() != keys or values["VERSION_SUFFIX"] not in (0, 1):
        raise ValueError("Version file must define all four properties; suffix must be 0 or 1")
    return ".".join(str(values[f"VERSION_{part}"]) for part in ("MAJOR", "MINOR", "PATCH")), values["VERSION_SUFFIX"] == 1


def run(*command: str, cwd: Path = ROOT) -> str:
    return subprocess.run(command, cwd=cwd, check=True, capture_output=True, text=True, encoding="utf-8", errors="replace").stdout.strip()


def resolve_version(base: str, beta: bool, mode: str, tags: list[str], sha: str, code: int) -> dict:
    if not 1 <= code <= 2_100_000_000:
        raise ValueError("Invalid Android versionCode")
    if mode == "release":
        if f"v{base}" in tags:
            raise ValueError(f"v{base} already exists; bump version.properties")
        numbers = [int(match[1]) for tag in tags if (match := re.fullmatch(re.escape(f"v{base}-beta.") + r"([1-9][0-9]*)", tag))]
        name = f"{base}-beta.{max(numbers, default=0) + 1}" if beta else base
    elif mode == "ci":
        name = f"{base}-ci.{sha}"
    else:
        name = f"{base}-beta.local" if beta else base
    validate_version(name)
    return {"name": name, "code": code, "tag": f"v{name}", "prerelease": beta if mode == "release" else "-" in name, "commit": sha}


def maven_files(repository: Path, version: str) -> list[Path]:
    group = repository / "io/oxygenx"
    expected = {"oxygenx-bom", *MODULES, *(f"{module}-{target}" for module in MODULES for target in ("android", "desktop", "wasm-js"))}
    files = []
    namespace = {"m": "http://maven.apache.org/POM/4.0.0"}
    for artifact in sorted(expected):
        directory = group / artifact / version
        pom = directory / f"{artifact}-{version}.pom"
        if not pom.is_file():
            raise ValueError(f"Missing publication: {artifact}:{version}")
        metadata = ET.parse(pom).getroot()
        if metadata.findtext("m:version", namespaces=namespace) != version or metadata.findtext("m:groupId", namespaces=namespace) != "io.oxygenx" or metadata.findtext("m:artifactId", namespaces=namespace) != artifact:
            raise ValueError(f"Publication identity mismatch: {artifact}")
        if artifact != "oxygenx-bom":
            extension = ".aar" if artifact.endswith("-android") else ".klib" if artifact.endswith("-wasm-js") else ".jar"
            if not (directory / f"{artifact}-{version}{extension}").is_file():
                raise ValueError(f"Missing binary publication: {artifact}")
            component = json.loads((directory / f"{artifact}-{version}.module").read_text(encoding="utf-8"))["component"]
            # KMP target metadata identifies the common/root publication via component.url.
            root_module = next((module for module in MODULES if artifact == module or artifact in {f"{module}-{target}" for target in ("android", "desktop", "wasm-js")}), artifact)
            if component["group"] != "io.oxygenx" or component["module"] not in {artifact, root_module} or component["version"] != version:
                raise ValueError(f"Gradle metadata identity mismatch: {artifact}")
        for dependency in metadata.findall(".//m:dependency", namespace):
            if dependency.findtext("m:groupId", namespaces=namespace) == "io.oxygenx" and dependency.findtext("m:version", namespaces=namespace) != version:
                raise ValueError(f"Mixed OxygenX dependency versions in {artifact}")
        for file in sorted(directory.iterdir()):
            if file.is_file() and file.suffix in {".pom", ".module", ".jar", ".aar", ".klib", ".md5", ".sha1", ".sha256", ".sha512"}:
                files.append(file)
    return files


def zip_repository(repository: Path, version: str, destination: Path) -> None:
    files = maven_files(repository, version)
    with zipfile.ZipFile(destination, "w", compression=zipfile.ZIP_DEFLATED) as archive:
        for file in files:
            archive.write(file, file.relative_to(repository).as_posix())


def verify_apk(apk: Path, sdk: Path, version: str, code: int, fingerprint: str) -> None:
    tools = sdk / "build-tools/37.0.0"
    signer = tools / ("apksigner.bat" if os.name == "nt" else "apksigner")
    certs = run(str(signer), "verify", "--print-certs", str(apk))
    digests = re.findall(r"(?m)^(?:Signer #\d+|V[1-4](?:\.\d+)? Signer(?: #\d+)?):? certificate SHA-256 digest:[ \t]*([0-9a-fA-F:]+)[ \t\r]*$", certs)
    if not digests or any(digest.replace(":", "").lower() != fingerprint for digest in digests):
        raise ValueError("APK signer does not match signing-cert.sha256")
    badging = run(str(tools / ("aapt2.exe" if os.name == "nt" else "aapt2")), "dump", "badging", str(apk)).splitlines()[0]
    fields = dict(re.findall(r"(\w+)='([^']*)'", badging))
    if fields.get("name") != "io.oxygenx.catalog" or fields.get("versionName") != version or fields.get("versionCode") != str(code):
        raise ValueError("APK package/version metadata does not match the release")


def package(version: str, code: int, sdk: Path, require_clean: bool) -> Path:
    validate_version(version)
    if not 1 <= code <= 2_100_000_000:
        raise ValueError("Invalid Android versionCode")
    dirty = bool(run("git", "status", "--porcelain"))
    if require_clean and dirty:
        raise ValueError("Distribution requires a clean checkout")
    apks = list((ROOT / "catalog/android/build/outputs/apk/release").glob("*.apk"))
    if len(apks) != 1 or "unsigned" in apks[0].name:
        raise ValueError("Expected exactly one signed release APK")
    fingerprint = (ROOT / "signing-cert.sha256").read_text().strip().lower()
    if not re.fullmatch(r"[0-9a-f]{64}", fingerprint):
        raise ValueError("Invalid public signing certificate digest")
    verify_apk(apks[0], sdk, version, code, fingerprint)
    mapping = ROOT / "catalog/android/build/outputs/mapping/release/mapping.txt"
    if not mapping.is_file():
        raise ValueError("Missing release R8 mapping")
    destination = ROOT / "build/distribution" / version
    destination.mkdir(parents=True, exist_ok=True)
    apk_name = f"OxygenX-Catalog-{version}-{code}.apk"
    mapping_name = f"OxygenX-Catalog-{version}-{code}-mapping.txt"
    archive_name = f"OxygenX-{version}-maven.zip"
    zip_repository(ROOT / "build/maven-repo", version, destination / archive_name)
    shutil.copyfile(apks[0], destination / apk_name)
    shutil.copyfile(mapping, destination / mapping_name)
    info = {"version": version, "versionCode": code, "commit": run("git", "rev-parse", "HEAD"), "dirty": dirty,
            "signingCertificateSha256": fingerprint, "unofficial": True, "apiStability": "experimental"}
    (destination / "release-info.json").write_text(json.dumps(info, indent=2) + "\n", encoding="utf-8")
    names = [apk_name, mapping_name, archive_name, "release-info.json"]
    checksums = [f"{hashlib.sha256((destination / name).read_bytes()).hexdigest()}  {name}" for name in names]
    (destination / "SHA256SUMS.txt").write_text("\n".join(checksums) + "\n", encoding="utf-8")
    if {file.name for file in destination.iterdir()} != {*names, "SHA256SUMS.txt"}:
        raise ValueError("Unexpected files in distribution directory")
    return destination


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    commands = parser.add_subparsers(dest="command", required=True)
    resolve = commands.add_parser("resolve")
    resolve.add_argument("--mode", choices=("local", "ci", "release"), default="local")
    resolve.add_argument("--github-output", type=Path)
    bundle = commands.add_parser("package")
    bundle.add_argument("--version", required=True)
    bundle.add_argument("--code", required=True, type=int)
    bundle.add_argument("--sdk", required=True, type=Path)
    bundle.add_argument("--require-clean", action="store_true")
    args = parser.parse_args()
    if args.command == "resolve":
        base, beta = read_version(ROOT / "version.properties")
        if run("git", "rev-parse", "--is-shallow-repository") != "false":
            raise ValueError("Full Git history is required for distribution")
        values = resolve_version(base, beta, args.mode, run("git", "tag", "--list").splitlines(), run("git", "rev-parse", "HEAD"), int(run("git", "rev-list", "--count", "HEAD")))
        print(json.dumps(values, indent=2))
        if args.github_output:
            with args.github_output.open("a", encoding="utf-8") as output:
                for key, value in values.items():
                    output.write(f"{key}={str(value).lower() if isinstance(value, bool) else value}\n")
    else:
        print(package(args.version, args.code, args.sdk, args.require_clean))


if __name__ == "__main__":
    main()
