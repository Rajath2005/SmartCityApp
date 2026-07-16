# SmartCityApp Scripts

Automation scripts for project management, testing, and contributor infrastructure.

## DSA Issue Bulk Creator

### `bulk_create_dsa_issues.py` (Recommended)

Creates GitHub issues for all DSA features in bulk using PyGithub library.

**Features**:
- ✅ Creates labels automatically (`dsa`, `dsa:array`, `dsa:stack`, etc.)
- ✅ Creates Wave 1 issues with full descriptions and acceptance criteria
- ✅ Applies labels and organizes by complexity tier
- ✅ Rate-limited (1 req/sec) to avoid GitHub API throttling
- ✅ Detailed error reporting

**Requirements**:
```bash
pip install PyGithub
export GITHUB_TOKEN="ghp_YOUR_TOKEN_HERE"
```

**Usage**:
```bash
python scripts/bulk_create_dsa_issues.py
```

**Output**: 
- 20+ labels created (DSA categories + Wave tiers)
- 4 Wave 1 issues created with full context

**Related Documentation**:
- [Quick Start Guide](../DSA_QUICKSTART.md)
- [Full DSA Roadmap](../DATA_STRUCTURES.md)

---

### `bulk_create_dsa_issues_rest.py` (Alternative)

Creates GitHub issues using direct REST API calls with `requests` library.

**Use when**: PyGithub is unavailable or you prefer direct API control.

**Features**:
- ✅ No external library dependencies (besides `requests`)
- ✅ Direct API control for advanced use cases
- ✅ Same rate limiting and error handling

**Requirements**:
```bash
pip install requests
export GITHUB_TOKEN="ghp_YOUR_TOKEN_HERE"
```

**Usage**:
```bash
python scripts/bulk_create_dsa_issues_rest.py
```

**Note**: This file provides the class; to use it, import it in your own script or extend `bulk_create_dsa_issues.py` to use `DSAIssueCreatorREST` instead of `DSAIssueBulkCreator`.

---

## Contributing to Scripts

### Adding a New Script

1. Create a new `.py` file in this directory
2. Include a header docstring explaining:
   - What the script does
   - Requirements/dependencies
   - Usage example
   - Output format
3. Add error handling and rate limiting
4. Document in this README under an appropriate section
5. Ensure Python 3.8+ compatibility

### Script Standards

- Use `argparse` for CLI arguments
- Include `--help` flag with usage info
- Log progress to stdout (print statements)
- Report errors to stderr
- Exit with non-zero code on failure
- Rate limit API calls to avoid throttling (1 req/sec typical)
- Include docstrings for all functions

---

## GitHub Token Setup

All GitHub API scripts require a Personal Access Token (PAT).

### Create a Token

1. Go to https://github.com/settings/tokens
2. Click "Generate new token (classic)"
3. Select scope: `repo` (full control of private repositories)
4. Copy the token

### Set the Token

**Windows (PowerShell)**:
```powershell
$env:GITHUB_TOKEN = "ghp_YOUR_TOKEN_HERE"
```

**Windows (Command Prompt)**:
```cmd
set GITHUB_TOKEN=ghp_YOUR_TOKEN_HERE
```

**macOS / Linux (Bash)**:
```bash
export GITHUB_TOKEN="ghp_YOUR_TOKEN_HERE"
```

**Verify**:
```bash
echo $GITHUB_TOKEN  # Should show your token
```

---

## Future Scripts (Planned)

- **`generate_issue_templates.py`** — Create issue templates for different contribution tiers
- **`bulk_assign_issues.py`** — Bulk assign issues to contributors
- **`generate_contribution_report.py`** — Generate contributor statistics and reports
- **`sync_roadmap.py`** — Sync GitHub project board with external roadmap

---

## Troubleshooting

### `ModuleNotFoundError: No module named 'github'`

Install PyGithub:
```bash
pip install PyGithub
```

### `GITHUB_TOKEN environment variable not set`

Set your token (see "GitHub Token Setup" above).

### API Rate Limit Exceeded

Scripts include rate limiting (1 req/sec by default). If you still hit limits:
- Wait 1 hour for your limit to reset
- Use a GitHub App token (higher limits)
- Reduce batch size

### Issues Not Appearing After Script Runs

1. Verify your PAT has `repo` scope
2. Test token: `curl -H "Authorization: token $GITHUB_TOKEN" https://api.github.com/user`
3. Check script output for error messages
4. Try creating one issue manually via GitHub web UI to verify repo access

---

## Related Documentation

- [DSA Quick Start Guide](../DSA_QUICKSTART.md)
- [Data Structures Roadmap](../DATA_STRUCTURES.md)
- [Contributor Guide](../DSA_CONTRIBUTOR_GUIDE.md)

---

**Last Updated**: 2026-07-16  
**Maintainer**: @Rajath2005
