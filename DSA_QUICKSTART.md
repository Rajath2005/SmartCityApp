# DSA Bulk Issue Creator — Quick Start Guide

This guide walks you through creating all Wave 1 DSA feature issues in your SmartCityApp repository using the automated Python script.

## Prerequisites

1. **Python 3.8+** installed on your system
2. **PyGithub library**: 
   ```bash
   pip install PyGithub
   ```
3. **GitHub Personal Access Token (PAT)** with `repo` scope:
   - Go to https://github.com/settings/tokens
   - Click "Generate new token (classic)"
   - Select scope: `repo` (full control of private repositories)
   - Copy the token and save it securely

## Setup

### 1. Set Your GitHub Token

**Windows (PowerShell)**:
```powershell
$env:GITHUB_TOKEN = "ghp_YOUR_TOKEN_HERE"
```

**macOS / Linux (Bash)**:
```bash
export GITHUB_TOKEN="ghp_YOUR_TOKEN_HERE"
```

### 2. Run the Script

Navigate to the SmartCityApp root directory and run:

```bash
python scripts/bulk_create_dsa_issues.py
```

### What the Script Does

1. ✅ **Creates labels** for DSA categories (`dsa`, `dsa:array`, `dsa:stack`, etc.)
2. ✅ **Creates labels** for complexity tiers (`Wave1`, `Wave2`, `Wave3`)
3. ✅ **Creates all Wave 1 issues** with full descriptions, acceptance criteria, and learning resources
4. ✅ **Applies labels** to each issue for easy filtering
5. ✅ **Rate limits** requests to avoid GitHub API throttling

## Expected Output

```
======================================================================
GitHub DSA Issue Bulk Creator
Repository: Rajath2005/SmartCityApp
======================================================================

Step 1: Setting up labels...
  ✓ Label 'dsa' already exists
  ✓ Created label 'dsa:array'
  ✓ Created label 'dsa:stack'
  ✓ Created label 'dsa:queue'
  ✓ Created label 'dsa:hashmap'
  ...

Step 2: Loading Wave 1 issues from JSON...

======================================================================
Creating Wave 1: Foundational DSA Features
======================================================================

[1/4] Creating: DSA: Implement Comparator-based Sorting for Places
  ✓ Created issue #45: DSA: Implement Comparator-based Sorting for Places

[2/4] Creating: DSA: Implement Stack-based Undo/Redo for Admin Place Edits
  ✓ Created issue #46: DSA: Implement Stack-based Undo/Redo for Admin Place Edits

[3/4] Creating: DSA: Implement Queue-based Recently-Viewed Places Ring Buffer
  ✓ Created issue #47: DSA: Implement Queue-based Recently-Viewed Places Ring Buffer

[4/4] Creating: DSA: Implement HashMap-based In-Memory Cache Layer for DBConnection
  ✓ Created issue #48: DSA: Implement HashMap-based In-Memory Cache Layer for DBConnection

======================================================================
SUMMARY
======================================================================

Wave: Wave 1: Foundational DSA Features
  Created: 4/4

TOTAL: 4 created, 0 failed

Created Issues:
  #45: https://github.com/Rajath2005/SmartCityApp/issues/45
  #46: https://github.com/Rajath2005/SmartCityApp/issues/46
  #47: https://github.com/Rajath2005/SmartCityApp/issues/47
  #48: https://github.com/Rajath2005/SmartCityApp/issues/48

✅ Issue creation complete!
Repository URL: https://github.com/Rajath2005/SmartCityApp/issues
```

## Next Steps After Running the Script

### 1. Verify Issues Were Created

Go to your repository's Issues tab: https://github.com/Rajath2005/SmartCityApp/issues

You should see the 4 new Wave 1 issues with `dsa`, `Wave1`, and appropriate sub-labels (`dsa:array`, `dsa:stack`, etc.).

### 2. Create Milestones (Optional but Recommended)

In your repo settings, create milestones for each wave:
- **DSA Wave 1** — 4 issues, due date: 2 weeks
- **DSA Wave 2** — 3 issues, due date: 4 weeks
- **DSA Wave 3** — 4 issues, due date: 6-8 weeks

Assign milestone to issues for better tracking.

### 3. Create a Project Board (Optional but Recommended)

1. Go to **Projects** tab in your repo
2. Create a new project: **DSA Roadmap**
3. Add columns: **Wave 1 (To Do)** → **In Progress** → **Review** → **Done**
4. Drag issues into columns as contributors start work

### 4. Update Your README.md

Add a section linking to the DSA roadmap:

```markdown
## Data Structures & Algorithms Roadmap

We're building production features *and* teaching DSA fundamentals. Every data structure is tied to a real feature.

- 📋 **Full roadmap**: See [DATA_STRUCTURES.md](DATA_STRUCTURES.md)
- 🔍 **Contributing**: Check out `dsa` labeled issues — great for learning!
  - [Wave 1 (Foundational)](https://github.com/Rajath2005/SmartCityApp/labels/Wave1)
  - [Wave 2 (Intermediate)](https://github.com/Rajath2005/SmartCityApp/labels/Wave2)
  - [Wave 3 (Showcase)](https://github.com/Rajath2005/SmartCityApp/labels/Wave3)
```

## Extending the Script for Wave 2 & 3

The script is modular. To add Wave 2 issues:

1. Add a new function `load_wave2_issues()` similar to `load_wave1_issues()`
2. Call `creator.create_wave("Wave 2: ...", wave2_issues)` in `main()`
3. Re-run the script

Template for a new wave:

```python
def load_wave2_issues() -> list[dict]:
    """Load Wave 2 issue templates"""
    return [
        {
            "title": "DSA: Implement Binary Search on Sorted Place Cache",
            "body": """## Feature Description
...
""",
            "labels": ["dsa", "dsa:search", "algorithm:searching", "Wave2"],
            "milestone": None,
        },
        # ... more issues
    ]
```

## Troubleshooting

### `GITHUB_TOKEN environment variable not set`

**Solution**: Set your token as shown in the Setup section above.

```powershell
# PowerShell
$env:GITHUB_TOKEN = "ghp_..."
```

### `PyGithub not installed`

**Solution**:
```bash
pip install PyGithub
```

### API Rate Limit Exceeded

The script rate-limits itself to 1 request/second. If you still hit limits:
- Wait 1 hour for your rate limit to reset
- Or use a GitHub App token (higher limits)

### Issues not appearing

1. Check your PAT scope: it must include `repo`
2. Verify the token works: `curl -H "Authorization: token $GITHUB_TOKEN" https://api.github.com/user`
3. Check the error output in the script for details

## What's in Each Issue?

Each DSA issue includes:

- **Feature Description** — what the user sees (not "implement X")
- **Why This Data Structure** — one-line connection to the problem
- **Acceptance Criteria** — concrete, testable requirements (8–12 items)
- **Complexity Analysis** — expected Big-O to include in PR
- **Related Learning** — LeetCode-style reference problem + concept name
- **Level** — First-Timer / Easy / Medium / etc.
- **Resources** — links to Java docs, patterns, tutorials

## Tips for Contributors

1. **Read the full issue body** — it's not just a title!
2. **Check the references** — the LeetCode problem teaches the same core logic
3. **Implement tests first** (TDD) — acceptance criteria make it obvious what tests to write
4. **Include Big-O in your PR description** — this is part of the learning
5. **Link this file** — mention `DATA_STRUCTURES.md` in your PR for context

## Manual Issue Creation (Alternative)

If the script fails or you prefer manual creation:

1. Go to https://github.com/Rajath2005/SmartCityApp/issues/new
2. Use the issue body template from `DATA_STRUCTURES.md`
3. Add labels: `dsa`, `Wave1`, and the specific structure label (e.g., `dsa:array`)
4. Click **Create**

---

**Questions?** Open a discussion in the repository or reach out to @Rajath2005.

Happy coding! 🚀
