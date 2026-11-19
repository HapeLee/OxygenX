"""Check paired user documentation, Kotlin examples, headings and local links."""

from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
DOCS = ROOT / "docs"


def user_pages(directory: Path) -> set[Path]:
    return {
        path.relative_to(directory)
        for path in directory.rglob("*.md")
        if path.relative_to(directory).parts[0] not in {"spec", "zh_CN", "node_modules", ".vitepress"}
        and path.name != "ui-library-hardening.md"
    }


def check() -> None:
    english = user_pages(DOCS)
    chinese = user_pages(DOCS / "zh_CN")
    errors = []
    if english != chinese:
        errors.append(f"Unpaired pages: {sorted(map(str, english ^ chinese))}")
    for relative in sorted(english & chinese):
        en = (DOCS / relative).read_text(encoding="utf-8")
        zh = (DOCS / "zh_CN" / relative).read_text(encoding="utf-8")
        if re.findall(r"^#{1,6} ", en, re.M) != re.findall(r"^#{1,6} ", zh, re.M):
            errors.append(f"Heading structure differs: {relative}")
        if re.findall(r"```kotlin\s*\n(.*?)```", en, re.S) != re.findall(r"```kotlin\s*\n(.*?)```", zh, re.S):
            errors.append(f"Kotlin examples differ: {relative}")
        if sum(line.startswith("|") for line in en.splitlines()) != sum(line.startswith("|") for line in zh.splitlines()):
            errors.append(f"Parameter table row count differs: {relative}")
    pages = [ROOT / "README.md", *(DOCS / page for page in english), *(DOCS / "zh_CN" / page for page in chinese)]
    for path in pages:
        content = path.read_text(encoding="utf-8")
        for target in re.findall(r"\[[^\]]*\]\(([^)]+)\)", content):
            if target.startswith(("https://", "http://", "#", "mailto:")):
                continue
            local = target.split("#", 1)[0]
            if not (path.parent / local).exists():
                errors.append(f"Broken link in {path.relative_to(ROOT)}: {target}")
    if errors:
        raise SystemExit("\n".join(errors))
    print(f"Documentation OK: {len(english)} bilingual page pairs and local links")


if __name__ == "__main__":
    check()
