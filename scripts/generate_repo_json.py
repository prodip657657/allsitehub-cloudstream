#!/usr/bin/env python3
"""Generate a CloudStream repo manifest pointing at the builds branch."""
import json
import re
import sys
from pathlib import Path
if len(sys.argv) != 3 or not re.fullmatch(r"[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+", sys.argv[1]):
    raise SystemExit("usage: generate_repo_json.py owner/repository output.json")
owner_repo, dest = sys.argv[1:]
manifest = {
    "name": "AllSiteHub Directory (Community Beta)",
    "description": "Browse website links in 8 AllSiteHub categories; does not play videos.",
    "manifestVersion": 1,
    "pluginLists": ["https://raw.githubusercontent.com/" + owner_repo + "/builds/plugins.json"]
}
Path(dest).write_text(json.dumps(manifest, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
