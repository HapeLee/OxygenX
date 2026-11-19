# Versioning and GitHub distribution

Unofficial / experimental. Initial version: 0.0.1. A 0.x release does not establish public API stability.

## Version source

| Value | Rule |
| --- | --- |
| VERSION_MAJOR / MINOR / PATCH | Manually maintained in root version.properties |
| VERSION_SUFFIX=0 | Normal version, for example 0.0.1 |
| VERSION_SUFFIX=1 | beta.local locally; beta.N increments from tags when publishing |
| Android versionCode | git rev-list --count HEAD; builds never mutate counters |
| BOM / every library / catalog | Share the root version; development checks can override oxygenxVersion |
| debug / benchmark packages | io.oxygenx.catalog.debug / .benchmark, separate from signed distribution |

Publish only from main with full Git history; do not rewrite published history. Existing final tags are rejected. Rebuilding the same commit keeps its build number. Android About shows the actual BuildConfig version. Source archives without Git fall back to local build number 1 and are not official distribution inputs.

## Local signing and validation

```powershell
.\gradlew.bat printOxygenVersion
.\gradlew.bat :catalog:android:assembleRelease publishAllPublicationsToLocalRepoRepository
$releaseVersion = python tools/release.py resolve --mode local | ConvertFrom-Json
python tools/release.py package --version $releaseVersion.name --code $releaseVersion.code --sdk D:/Android/sdk
```

Local signing.properties is Git-ignored and uses RELEASE_STORE_FILE, RELEASE_STORE_PASSWORD, RELEASE_KEY_ALIAS and RELEASE_KEY_PASSWORD; same-named environment variables take precedence. Missing configuration can produce an unsigned release build, but distribution packaging rejects unsigned APKs, unexpected certificates and mismatched versions. Never place private keys or passwords in committed files.

APK and Maven versions default to version.properties. -PoxygenxVersion and -PoxygenxVersionCode explicitly override a build; CI supplies its prepared values. Use unique suffixes for development coordinates. Breaking 0.x API changes should bump minor and include migration notes.

## GitHub Actions

Manual Auto Build defaults to publish=false and only uploads signed build artifacts. Release calls the same flow with publishing enabled. Both restrict signing distribution to the repository owner on main. Ordinary push/PR CI needs no signing secrets. Distribution runs serialize; failed builds create no tag.

Actions Secrets are ANDROID_KEYSTORE_BASE64, ANDROID_KEYSTORE_PASSWORD, ANDROID_KEY_ALIAS and ANDROID_KEY_PASSWORD. The keystore is decoded only in the runner temporary directory and removed in finally. Passwords travel through environment variables, not committed files or command-line arguments. signing-cert.sha256 holds the public certificate digest checked against the APK.

Commit and push the workflow first. Run Auto Build in Actions to verify it, then Release to create v0.0.1. VERSION_SUFFIX=1 creates a pre-release. Bump the base before publishing after an existing final version.

## Distribution assets and consumption

Assets include the signed catalog APK, R8 mapping, the complete version-specific Maven repository zip, release-info.json and SHA256SUMS.txt. The archive includes eight libraries, three platform variants and the BOM at the selected version, excluding old development versions, signing configuration and keys. CI extracts the final zip and builds an isolated BOM + UI-only consumer against it.

Download and extract the Maven zip, add the directory containing io/oxygenx as a Maven repository and consume io.oxygenx:oxygenx-bom:0.0.1 plus oxygenx-ui; add oxygenx-material3 for interoperability. This is GitHub asset distribution, not Maven Central or GitHub Packages hosting. Private repository assets require authorized access.

## Validation scope

Local validation covers versions, signing, R8, checksums, repository archives, the isolated consumer, APIs and tests. Remote workflows, Releases and asset downloads require actual runs before claiming success. A configured release system is not a published release or verified component acceptance.

Official references: [GitHub Secrets](https://docs.github.com/en/actions/how-tos/write-workflows/choose-what-workflows-do/use-secrets), [GitHub Releases](https://docs.github.com/en/rest/releases/releases).
