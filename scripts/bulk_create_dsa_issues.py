#!/usr/bin/env python3
"""
GitHub Bulk Issue Creator for SmartCityApp DSA Features
Requires: PyGithub
Setup: export GITHUB_TOKEN=<your-PAT>
"""

import os
import time
import sys
try:
    from github import Github
except ImportError:
    print("ERROR: PyGithub not installed. Install with: pip install PyGithub")
    sys.exit(1)


class DSAIssueBulkCreator:
    """Create DSA feature issues in bulk with labels and descriptions"""

    def __init__(self, repo_owner: str, repo_name: str):
        """Initialize with GitHub repo credentials"""
        self.token = os.getenv("GITHUB_TOKEN")
        if not self.token:
            raise ValueError(
                "GITHUB_TOKEN environment variable not set. "
                "Export it: export GITHUB_TOKEN=<your-PAT>"
            )
        
        self.gh = Github(self.token)
        self.repo = self.gh.get_user(repo_owner).get_repo(repo_name)
        self.created_issues = []
        self.failed_issues = []

    def get_or_create_labels(self, label_specs: list) -> dict:
        """Create or fetch labels"""
        labels = {}
        existing = {l.name: l for l in self.repo.get_labels()}
        
        for spec in label_specs:
            name = spec["name"]
            if name in existing:
                labels[name] = existing[name]
                print(f"  ✓ Label '{name}' already exists")
            else:
                try:
                    label = self.repo.create_label(
                        name=name,
                        color=spec.get("color", "0366d6"),
                        description=spec.get("description", "")
                    )
                    labels[name] = label
                    print(f"  ✓ Created label '{name}'")
                except Exception as e:
                    print(f"  ✗ Failed to create label '{name}': {e}")
        
        return labels

    def create_issue(self, title: str, body: str, labels: list, 
                    assignee: str = None, milestone_title: str = None) -> dict:
        """Create a single GitHub issue"""
        try:
            label_objects = []
            for label_name in labels:
                try:
                    label = self.repo.get_label(label_name)
                    label_objects.append(label)
                except Exception as le:
                    print(f"    ⚠ Warning: Label '{label_name}' not found, skipping")
            
            issue = self.repo.create_issue(
                title=title,
                body=body,
                labels=label_objects,
            )
            
            result = {
                "success": True,
                "issue_number": issue.number,
                "url": issue.html_url,
                "error": None
            }
            self.created_issues.append(result)
            print(f"  ✓ Created issue #{issue.number}: {title}")
            return result
        
        except Exception as e:
            import traceback
            error_msg = f"{type(e).__name__}: {str(e)}"
            result = {
                "success": False,
                "issue_number": None,
                "url": None,
                "error": error_msg
            }
            self.failed_issues.append(result)
            print(f"  ✗ Failed to create '{title}'")
            print(f"      Error: {error_msg}")
            traceback.print_exc()
            return result

    def create_wave(self, wave_name: str, issues: list) -> dict:
        """Create all issues in a wave"""
        print(f"\n{'='*70}")
        print(f"Creating {wave_name}")
        print(f"{'='*70}\n")
        
        results = {
            "wave": wave_name,
            "total": len(issues),
            "created": 0,
            "failed": 0,
            "issues": []
        }
        
        for i, issue_spec in enumerate(issues, 1):
            print(f"[{i}/{len(issues)}] {issue_spec['title']}")
            result = self.create_issue(
                title=issue_spec["title"],
                body=issue_spec["body"],
                labels=issue_spec.get("labels", [])
            )
            results["issues"].append(result)
            if result["success"]:
                results["created"] += 1
            else:
                results["failed"] += 1
            
            if i < len(issues):
                time.sleep(1)
        
        return results

    def print_summary(self, all_results: list):
        """Print execution summary"""
        print(f"\n{'='*70}")
        print("SUMMARY")
        print(f"{'='*70}\n")
        
        total_created = sum(r["created"] for r in all_results)
        total_failed = sum(r["failed"] for r in all_results)
        
        for result in all_results:
            print(f"Wave: {result['wave']}")
            print(f"  Created: {result['created']}/{result['total']}")
            if result['failed'] > 0:
                print(f"  Failed: {result['failed']}/{result['total']}")
        
        print(f"\nTOTAL: {total_created} created, {total_failed} failed")
        
        if self.created_issues:
            print(f"\nCreated Issues:")
            for issue in self.created_issues:
                print(f"  #{issue['issue_number']}: {issue['url']}")


def main():
    """Main execution"""
    REPO_OWNER = "Rajath2005"
    REPO_NAME = "SmartCityApp"
    
    print(f"\n{'='*70}")
    print(f"GitHub DSA Issue Bulk Creator")
    print(f"Repository: {REPO_OWNER}/{REPO_NAME}")
    print(f"{'='*70}\n")
    
    try:
        creator = DSAIssueBulkCreator(REPO_OWNER, REPO_NAME)
    except ValueError as e:
        print(f"ERROR: {e}")
        sys.exit(1)
    
    # Step 1: Create labels
    print("Step 1: Setting up labels...")
    label_specs = [
        {"name": "dsa", "color": "1f6feb", "description": "Data Structures & Algorithms feature"},
        {"name": "dsa:array", "color": "e4ecf9", "description": "Array / ArrayList"},
        {"name": "dsa:stack", "color": "e4ecf9", "description": "Stack data structure"},
        {"name": "dsa:queue", "color": "e4ecf9", "description": "Queue / Deque data structure"},
        {"name": "dsa:hashmap", "color": "e4ecf9", "description": "HashMap / HashTable / LRU Cache"},
        {"name": "dsa:tree", "color": "e4ecf9", "description": "Binary Search Tree"},
        {"name": "dsa:trie", "color": "e4ecf9", "description": "Trie data structure"},
        {"name": "dsa:heap", "color": "e4ecf9", "description": "Heap / Priority Queue"},
        {"name": "dsa:graph", "color": "e4ecf9", "description": "Graph (BFS, DFS, Dijkstra)"},
        {"name": "dsa:union-find", "color": "e4ecf9", "description": "Union-Find / Disjoint Set"},
        {"name": "algorithm:sorting", "color": "d4c5f9", "description": "Sorting algorithms"},
        {"name": "algorithm:searching", "color": "d4c5f9", "description": "Search algorithms"},
        {"name": "leetcode-inspired", "color": "ffd97d", "description": "Problem inspired by LeetCode"},
        {"name": "Wave1", "color": "0e10c0", "description": "DSA Wave 1 (Foundational)"},
        {"name": "Wave2", "color": "0366d6", "description": "DSA Wave 2 (Intermediate)"},
        {"name": "Wave3", "color": "fbca04", "description": "DSA Wave 3 (Advanced)"},
    ]
    
    labels = creator.get_or_create_labels(label_specs)
    
    # Step 2: Create Wave 1 issues
    print("\n\nStep 2: Creating Wave 1 issues...")
    wave1_issues = load_wave1_issues()
    result = creator.create_wave("Wave 1: Foundational DSA Features", wave1_issues)
    all_results = [result]
    
    # Summary
    creator.print_summary(all_results)
    
    print("\n✅ Issue creation complete!")
    print(f"Repository: https://github.com/{REPO_OWNER}/{REPO_NAME}/issues")


def load_wave1_issues() -> list:
    """Load Wave 1 issue templates"""
    return [
        {
            "title": "DSA: Implement Comparator-based Sorting for Places",
            "body": """## Feature Description
Add the ability to sort places by different criteria (name, category, rating). This teaches sorting fundamentals and custom comparators.

## Acceptance Criteria
- [ ] Create `PlaceComparator` class with support for sorting by name, category, and rating
- [ ] Implement using `Collections.sort()` with custom comparator
- [ ] Add comprehensive JUnit tests for all sort orders
- [ ] Write CLI demonstration in `SmartCityApp.java`
- [ ] Document time complexity: O(n log n)

## Coding Problems to Solve First
1. **[LeetCode 1356: Sort Integers by The Number of 1 Bits](https://leetcode.com/problems/sort-integers-by-the-number-of-1-bits/)** ⭐ START HERE (20 min)
2. **[HackerRank: Comparator](https://www.hackerrank.com/challenges/java-comparator/problem)** ⭐ MOST RELEVANT (30 min)
3. **[LeetCode 912: Sort an Array](https://leetcode.com/problems/sort-an-array/)** (45 min)
4. **[CodeChef: SORT](https://www.codechef.com/problems/SORT)** (10 min)
5. **[CodeChef: FSORT](https://www.codechef.com/problems/FSORT)** (15 min)

## References
- See [DSA_CODING_REFERENCE.md](../DSA_CODING_REFERENCE.md#sorting--comparators) for full problem list
- Code template: [DSA_CONTRIBUTOR_GUIDE.md](../DSA_CONTRIBUTOR_GUIDE.md#template-1-simple-data-structure-comparator)
- Learning path: [DSA_MASTER_GUIDE.md](../DSA_MASTER_GUIDE.md)

## Level: 🟢 First-Timer / Easy
""",
            "labels": ["dsa", "dsa:array", "algorithm:sorting", "Wave1", "good-first-issue", "leetcode-inspired"],
        },
        {
            "title": "DSA: Implement Stack-based Undo/Redo for Admin Place Edits",
            "body": """## Feature Description
Add command history tracking and undo/redo functionality for admin place edits using Stacks with the Command pattern.

## Acceptance Criteria
- [ ] Create `Command` interface and `EditPlaceCommand` implementation
- [ ] Implement `CommandHistory` class using two stacks: `undoStack` and `redoStack`
- [ ] Add methods: `executeCommand()`, `undo()`, `redo()`, `getHistory()`
- [ ] Integrate into CLI with undo/redo prompts
- [ ] Write comprehensive JUnit tests including edge cases
- [ ] Document time complexity: O(1) for all operations

## Coding Problems to Solve First
1. **[LeetCode 155: Min Stack](https://leetcode.com/problems/min-stack/)** ⭐ START HERE (30 min)
2. **[HackerRank: Java Stack](https://www.hackerrank.com/challenges/java-stack/problem)** (15 min)
3. **[LeetCode 150: Evaluate Reverse Polish Notation](https://leetcode.com/problems/evaluate-reverse-polish-notation/)** (30 min)
4. **[CodeChef: STACKS](https://www.codechef.com/problems/STACKS)** (15 min)
5. Study: [Command Pattern](https://refactoring.guru/design-patterns/command) (15 min)

## References
- See [DSA_CODING_REFERENCE.md](../DSA_CODING_REFERENCE.md#stack--undoredo) for full problem list
- Code template: [DSA_CONTRIBUTOR_GUIDE.md](../DSA_CONTRIBUTOR_GUIDE.md#template-2-data-structure-with-state-stack--commandhistory)
- Learning path: [DSA_MASTER_GUIDE.md](../DSA_MASTER_GUIDE.md)

## Level: 🟡 Easy-Medium / Second-Timer
""",
            "labels": ["dsa", "dsa:stack", "Wave1", "leetcode-inspired"],
        },
        {
            "title": "DSA: Implement Queue-based Recently-Viewed Places Ring Buffer",
            "body": """## Feature Description
Add a "Recently Viewed Places" feature using a bounded `Deque` as a ring buffer (max 10 items). Teaches bounded queues used in production.

## Acceptance Criteria
- [ ] Create `RecentlyViewedManager` using `LinkedList<Integer>` as bounded deque (max 10)
- [ ] Implement methods: `viewPlace(id)`, `getRecent()`, `clear()`
- [ ] Integrate with `SmartCityApp`: record when user views places
- [ ] CLI display of recently viewed places with names
- [ ] Write JUnit tests: bounded overflow, empty queue, duplicates
- [ ] Document time complexity: O(1) for all operations

## Coding Problems to Solve First
1. **[LeetCode 346: Moving Average from Data Stream](https://leetcode.com/problems/moving-average-from-data-stream/)** ⭐ START HERE (20 min)
2. **[LeetCode 622: Design Circular Queue](https://leetcode.com/problems/design-circular-queue/)** ⭐ MOST RELEVANT (40 min)
3. **[HackerRank: Java Deque](https://www.hackerrank.com/challenges/java-deque/problem)** (25 min)
4. **[LeetCode 933: Number of Recent Calls](https://leetcode.com/problems/number-of-recent-calls/)** (15 min)
5. **[CodeChef: QUEUE](https://www.codechef.com/problems/QUEUE)** (10 min)

## References
- See [DSA_CODING_REFERENCE.md](../DSA_CODING_REFERENCE.md#queue--ring-buffer-recently-viewed) for full problem list
- Ring buffer pattern: https://en.wikipedia.org/wiki/Circular_buffer
- Learning path: [DSA_MASTER_GUIDE.md](../DSA_MASTER_GUIDE.md)

## Level: 🟢 First-Timer / Easy
""",
            "labels": ["dsa", "dsa:queue", "Wave1", "leetcode-inspired"],
        },
        {
            "title": "DSA: Implement HashMap-based In-Memory Cache Layer for DBConnection",
            "body": """## Feature Description
Add an in-memory cache layer in front of `DBConnection` using HashMap with TTL. Introduces the cache-aside pattern used everywhere in production.

## Acceptance Criteria
- [ ] Create `CachedDBConnection` wrapper in `com.smartcity.service`
- [ ] Implement `HashMap<Integer, Place>` and `HashMap<Integer, User>` with TTL
- [ ] Methods: `getPlace(id)`, `getUser(id)`, `clearCache()`, `getCacheStats()`
- [ ] Cache invalidation on data modifications
- [ ] CLI demo showing cache hit metrics
- [ ] Write comprehensive JUnit tests: hits, misses, TTL expiration, invalidation

## Coding Problems to Solve First (MUST SOLVE ALL)
1. **[LeetCode 146: LRU Cache](https://leetcode.com/problems/lru-cache/)** ⭐⭐ MUST SOLVE FIRST (60 min) — MOST IMPORTANT
2. **[LeetCode 706: Design HashMap](https://leetcode.com/problems/design-hashmap/)** (30 min)
3. **[HackerRank: Java HashMap](https://www.hackerrank.com/challenges/java-hashmap/problem)** (20 min)
4. **[LeetCode 1472: Design Browser History](https://leetcode.com/problems/design-browser-history/)** (40 min)
5. Study: [Cache-Aside Pattern](https://docs.microsoft.com/en-us/azure/architecture/patterns/cache-aside) (10 min)

## References
- See [DSA_CODING_REFERENCE.md](../DSA_CODING_REFERENCE.md#hashmap--caching-cache-aside) for 12+ problems and system design resources
- Code template: [DSA_CONTRIBUTOR_GUIDE.md](../DSA_CONTRIBUTOR_GUIDE.md#template-3-complex-data-structure-with-integration-cachedbconnection)
- Learning path: [DSA_MASTER_GUIDE.md](../DSA_MASTER_GUIDE.md)

## Level: 🟡 Medium (MOST IMPORTANT Wave 1 FEATURE)
""",
            "labels": ["dsa", "dsa:hashmap", "Wave1", "leetcode-inspired"],
        },
    ]


if __name__ == "__main__":
    main()
