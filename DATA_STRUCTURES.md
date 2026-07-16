# Data Structures & Algorithms in SmartCityApp

This document tracks all DSA features implemented in SmartCityApp, linking each structure to its real-world feature, use case, and performance characteristics. This roadmap bridges the gap between LeetCode-style practice problems and production code.

**Goal**: Turn DSA learning into portfolio-worthy features that are tested, documented, and integrated into a real application.

---

## Quick Reference Table

| Data Structure | Feature | Package | Status | Issues | Complexity | Tier |
|---|---|---|---|---|---|---|
| **Array + Comparator** | Sort places by name/category/rating | `com.smartcity.structures` | 🔄 Wave 1 | #DSA-001 | O(n log n) | First-Timer |
| **Stack** | Undo/Redo for admin edits | `com.smartcity.structures.command` | 🔄 Wave 1 | #DSA-002 | O(1) ops | Easy-Medium |
| **Queue / Deque** | Recently-viewed places (ring buffer) | `com.smartcity.structures` | 🔄 Wave 1 | #DSA-003 | O(1) ops | First-Timer |
| **HashMap + TTL** | In-memory cache layer | `com.smartcity.service` | 🔄 Wave 1 | #DSA-004 | O(1) avg | Easy-Medium |
| **Binary Search** | Fast lookup in sorted cache | `com.smartcity.structures` | 🔄 Wave 2 | #DSA-005 | O(log n) | Medium |
| **Trie** | Autocomplete place name search | `com.smartcity.structures.trie` | ⏳ Wave 2 | #DSA-006 | O(m) where m=prefix len | Medium |
| **Binary Search Tree** | Range queries on place ratings | `com.smartcity.structures.tree` | ⏳ Wave 2 | #DSA-007 | O(log n) avg | Medium |
| **Priority Queue / Heap** | Top-N rated places | `com.smartcity.structures.heap` | ⏳ Wave 3 | #DSA-008 | O(log n) insert/pop | Intermediate |
| **Graph + BFS/DFS** | Places within N-hops proximity | `com.smartcity.structures.graph` | ⏳ Wave 3 | #DSA-009 | O(V+E) | Showcase |
| **Graph + Dijkstra** | Shortest-path route planner | `com.smartcity.structures.graph` | ⏳ Wave 3 | #DSA-010 | O((V+E) log V) | Showcase |
| **Union-Find** | Cluster places into zones | `com.smartcity.structures.unionfind` | ⏳ Wave 3 | #DSA-011 | O(α(n)) amortized | Advanced |
| **Bloom Filter** | Fast "does username exist" check | `com.smartcity.structures.hash` | 🔮 Future | — | O(k) where k=hash funcs | Expert |

**Status Legend:**
- 🔄 **In Development** (Wave 1-2)
- ⏳ **Planned** (Wave 3)
- 🔮 **Future / Research Phase**
- ✅ **Implemented & Tested**

---

## Wave 1: Foundational DSA Features (Low Risk, High Learning)

These features don't touch schema, integrate easily with existing code, and are safe for new contributors.

### 1.1 Array + Comparator: Sort Places by Name/Category/Rating

**Feature**: Extend place browsing to allow client-side sorting without relying on SQL `ORDER BY`.

**Why This Data Structure**:
- Teaching sorting fundamentals without adding infrastructure
- Safe first-timer issue — no DB schema changes needed
- Natural progression: arrays → comparators → stream API

**Acceptance Criteria**:
- [ ] `PlaceComparator` class supporting name, category, rating sort orders
- [ ] In-memory sorting via `Collections.sort()`
- [ ] CLI menu: "Sort places by: [1] Name [2] Category [3] Rating"
- [ ] JUnit tests: all sort orders, edge cases (null/empty, ties)
- [ ] Big-O documented in PR: Time O(n log n), Space O(n)

**Reference Problem**: LeetCode 1356 "Sort Integers by The Number of 1 Bits"

**Package Structure**:
```
com/smartcity/structures/
├── Comparators.java (all place comparators)
└── PlaceComparator.java (legacy; migrate to Comparators)
```

**Complexity**:
- Time: **O(n log n)** for sorting (Collections.sort uses Timsort)
- Space: **O(n)** for sorted copy
- Comparator calls: **O(n log n)** calls to `compare()`

**Issue**: #DSA-001 | **Labels**: `dsa`, `dsa:array`, `algorithm:sorting`, `Wave1`, `good-first-issue`

---

### 1.2 Stack: Undo/Redo for Admin Place Edits

**Feature**: Add command history tracking so admins can undo/redo edits to place records.

**Why This Data Structure**:
- Stacks are tailor-made for undo/redo (two stacks: undo and redo)
- Self-contained feature; no dependencies on other Wave 1 features
- Immediately testable with mock commands

**Acceptance Criteria**:
- [ ] `Command` interface and concrete implementations (`EditPlaceCommand`, `DeletePlaceCommand`, etc.)
- [ ] `CommandHistory` class with `undoStack` and `redoStack`
- [ ] Methods: `execute()`, `undo()`, `redo()`, `getHistory()`
- [ ] CLI demo: edit place → undo → redo chain
- [ ] JUnit tests: undo/redo sequences, edge cases (empty stacks), command logging
- [ ] Big-O documented: Time O(1) for all ops, Space O(n) for history

**Reference Problem**: LeetCode 155 "Min Stack" (foundation), Interpreter/Command pattern

**Package Structure**:
```
com/smartcity/structures/command/
├── Command.java (interface)
├── EditPlaceCommand.java
├── DeletePlaceCommand.java
├── AddPlaceCommand.java
└── CommandHistory.java (manages undo/redo stacks)
```

**Complexity**:
- Time: **O(1)** for all operations (push, pop)
- Space: **O(n)** where n = number of commands in history (bounded by session lifetime)

**Issue**: #DSA-002 | **Labels**: `dsa`, `dsa:stack`, `Wave1`

---

### 1.3 Queue / Deque: Recently-Viewed Places Ring Buffer

**Feature**: Track last 10 places a user viewed, persisting for the session.

**Why This Data Structure**:
- Deques are perfect for bounded queues (add to back, evict from front)
- Teaches queue semantics with immediate feedback
- Natural stepping stone to caching concepts

**Acceptance Criteria**:
- [ ] `RecentlyViewedManager` class using `LinkedList<Integer>` as bounded deque (max 10)
- [ ] Methods: `viewPlace(id)`, `getRecent()`, `clear()`
- [ ] Integrate with place view CLI: when user views a place, record it
- [ ] CLI feature: "Show recently viewed places" with place names
- [ ] JUnit tests: bounded overflow, duplicates (move to top), empty queue
- [ ] Big-O documented: Time O(1) for ops, Space O(k) where k=10

**Reference Problem**: LeetCode 346 "Moving Average from Data Stream"

**Package Structure**:
```
com/smartcity/structures/
└── RecentlyViewedManager.java (session-scoped singleton)
```

**Complexity**:
- Time: **O(1)** for add, remove, peek (Deque backed by LinkedList)
- Space: **O(k)** where k = max capacity (10 in this case)

**Issue**: #DSA-003 | **Labels**: `dsa`, `dsa:queue`, `Wave1`

---

### 1.4 HashMap + TTL: In-Memory Cache Layer

**Feature**: Cache place and user lookups to reduce repeated DB hits (cache-aside pattern).

**Why This Data Structure**:
- HashMap is the canonical choice for caching (O(1) lookup)
- Introduces cache-aside pattern, TTL, and metrics
- Direct performance win for the app

**Acceptance Criteria**:
- [ ] `CachedDBConnection` wrapper around `DBConnection`
- [ ] `HashMap<Integer, Place>` and `HashMap<Integer, User>` caches with TTL (default 5 min)
- [ ] Methods: `getPlace(id)` (cache-aside), `getUser(id)`, `clearCache()`, `getCacheStats()`
- [ ] Cache invalidation on: `addPlace()`, `editPlace()`, `deletePlace()`
- [ ] CLI demo: call same place 5 times, show cache hit metrics
- [ ] JUnit tests: hits, misses, TTL expiration, invalidation, concurrent access patterns
- [ ] Big-O documented: Time O(1) cache hit, Space O(n)

**Reference Problem**: LeetCode 146 "LRU Cache" (foundation for cache eviction)

**Package Structure**:
```
com/smartcity/service/
├── CachedDBConnection.java (wraps DBConnection)
└── CacheStats.java (hit/miss counters, optional metrics bean)
```

**Complexity**:
- Time: **O(1)** average for cache hit/miss lookup (HashMap)
- Space: **O(n)** where n = number of cached entries (bounded by TTL sweep)
- DB operations: **O(1)** (no change from original)

**Issue**: #DSA-004 | **Labels**: `dsa`, `dsa:hashmap`, `Wave1`

---

## Wave 2: Intermediate DSA Features (Builds on Wave 1)

These features depend on Wave 1's cache existing and teach more advanced patterns.

### 2.1 Binary Search: Fast Lookup in Sorted Cache

**Feature**: Use binary search to find places by ID in a sorted cached array, once the cache is populated.

**Why This Data Structure**:
- Pairs naturally with sorting from Wave 1
- O(log n) is visibly faster than O(n) for large datasets
- Bridges into algorithmic thinking

**Acceptance Criteria**:
- [ ] Implement binary search on sorted place cache
- [ ] CLI: "Quick lookup by ID" (uses binary search if cache is warm)
- [ ] JUnit tests: found, not found, boundary cases
- [ ] Big-O analysis: Time O(log n), Space O(1)

**Reference Problem**: LeetCode 704 "Binary Search", 35 "Search Insert Position"

**Issue**: #DSA-005 | **Labels**: `dsa`, `algorithm:searching`, `Wave2`

---

### 2.2 Trie: Autocomplete Place Name Search

**Feature**: Type-ahead search for place names as the user types.

**Why This Data Structure**:
- Trie is the canonical solution for prefix-based search
- Highly visual in CLI output
- Foundation for more complex string problems

**Acceptance Criteria**:
- [ ] `PlaceNameTrie` class with insert and prefix search
- [ ] CLI: search box that updates as user types (simulated)
- [ ] JUnit tests: insert, search, prefix matching, duplicate handling
- [ ] Big-O analysis: Insert O(m), Search O(m) where m = word length
- [ ] Autocomplete demo with top-5 results

**Reference Problem**: LeetCode 208 "Implement Trie (Prefix Tree)", 211 "Design Add and Search Words Data Structure"

**Issue**: #DSA-006 | **Labels**: `dsa`, `dsa:trie`, `Wave2`

---

### 2.3 Binary Search Tree: Category-Based Range Queries

**Feature**: Browse places sorted by category with range queries (e.g., "places in category 3 with rating 3–5").

**Why This Data Structure**:
- BSTs teach the foundation for balanced trees and ordered data structures
- Range queries are a natural fit

**Acceptance Criteria**:
- [ ] `PlaceRangeTree` class supporting insert, range queries
- [ ] CLI: range query menu
- [ ] JUnit tests: range queries, rotations (if self-balancing), edge cases
- [ ] Big-O analysis: avg O(log n), worst O(n)

**Reference Problem**: LeetCode 95 "Unique Binary Search Trees II", 98 "Validate BST"

**Issue**: #DSA-007 | **Labels**: `dsa`, `dsa:tree`, `Wave2`

---

## Wave 3: Advanced / Showcase Features (Real Portfolio Pieces)

These are visually impressive and tie into the roadmap; gate them appropriately.

### 3.1 Priority Queue / Heap: Top-N Rated Places

**Feature**: Show the top 5 highest-rated places globally (requires Ratings & Reviews roadmap item first).

**Why This Data Structure**:
- Heaps are essential for top-K problems
- Showcase feature: visually impressive leaderboard

**Acceptance Criteria**:
- [ ] Implement max-heap using `PriorityQueue<Place>`
- [ ] CLI: "Top 5 Rated Places"
- [ ] **Gate**: Only enabled after Ratings feature lands
- [ ] JUnit tests: heap operations, ties, dynamic updates
- [ ] Big-O analysis: O(log n) insert/pop

**Reference Problem**: LeetCode 215 "Kth Largest Element", 347 "Top K Frequent Elements"

**Issue**: #DSA-008 (blocked by Ratings & Reviews) | **Labels**: `dsa`, `dsa:heap`, `Wave3`

---

### 3.2 Graph + BFS/DFS: Places Within N Hops of Current Location

**Feature**: Build a proximity graph using lat/long distance and show places within N "hops" (neighboring places).

**Why This Data Structure**:
- Graph search is fundamental; BFS/DFS are the building blocks
- Uses existing lat/long data
- Showcase-tier visual demo

**Acceptance Criteria**:
- [ ] Build place proximity graph (edge = distance < threshold)
- [ ] BFS for N-hop neighbors
- [ ] CLI demo: "Places within 2 hops of [place]"
- [ ] JUnit tests: graph construction, BFS on various topologies
- [ ] Big-O analysis: O(V + E) for BFS

**Reference Problem**: LeetCode 104 "Maximum Depth of Binary Tree" (BFS foundation), 994 "Rotting Oranges"

**Issue**: #DSA-009 | **Labels**: `dsa`, `dsa:graph`, `algorithm:searching`, `Wave3`

---

### 3.3 Graph + Dijkstra: Shortest-Path Route Planner

**Feature**: Plan a route between two places using Dijkstra's algorithm with distance as edge weights.

**Why This Data Structure**:
- Dijkstra is a classic showcase algorithm
- Real-world use case (route planning)
- Portfolio-grade feature

**Acceptance Criteria**:
- [ ] Implement Dijkstra using lat/long distances
- [ ] CLI: "Plan route from Place A to Place B"
- [ ] Visual ANSI output showing path
- [ ] JUnit tests: shortest paths, no path scenarios
- [ ] Big-O analysis: O((V+E) log V) with binary heap

**Reference Problem**: LeetCode 743 "Network Delay Time", 1334 "Find the City With the Smallest Number of Neighbors at a Threshold Distance"

**Issue**: #DSA-010 | **Labels**: `dsa`, `dsa:graph`, `algorithm:searching`, `Wave3`

---

### 3.4 Union-Find: Cluster Places into Zones

**Feature**: Use Union-Find to automatically cluster places into "zones" based on proximity.

**Why This Data Structure**:
- Union-Find is an advanced, elegant algorithm
- Standout contributor feature
- Leads into component analysis and clustering

**Acceptance Criteria**:
- [ ] Implement Union-Find with path compression and union by rank
- [ ] Cluster places into zones
- [ ] CLI: "Show zones and member places"
- [ ] JUnit tests: union, find, connected components, cycle detection
- [ ] Big-O analysis: O(α(n)) amortized where α is inverse Ackermann

**Reference Problem**: LeetCode 684 "Redundant Connection", 765 "Couples Holding Hands"

**Issue**: #DSA-011 | **Labels**: `dsa`, `dsa:union-find`, `Wave3`

---

## Future: Stretch/Expert Features

### Bloom Filter: Fast "Does User Exist?" Pre-check

**Feature**: Use a Bloom filter to do a fast, probabilistic "does username exist" check before hitting the DB (clearly labeled as a learning exercise, not production security).

**Why This Data Structure**:
- Advanced probabilistic data structure
- Expert-level contributor feature
- Foundation for understanding trade-offs in system design

**Package**: `com.smartcity.structures.hash`

**Issue**: TBD | **Labels**: `dsa`, `dsa:bloom-filter`, `expert`, `future`

---

## Testing & Code Quality Standards

All DSA features **must** include:

1. **JUnit Tests** (leverage existing `mvn test` setup)
   - Unit tests for all public methods
   - Edge cases (empty, null, boundary values)
   - Performance assertions where appropriate (e.g., "10k insertions complete in <1s")
   - Mock tests for DB-dependent features (use Mockito)

2. **Big-O Analysis**
   - Every PR must include a brief Big-O summary in the description
   - Format: "Time: O(n log n), Space: O(n)"
   - Justify design decisions based on complexity

3. **CLI Integration**
   - Demo each feature in `SmartCityApp.java` CLI
   - Add a menu item or flag to trigger the feature
   - Show before/after or sample output

4. **Documentation**
   - Inline code comments for non-obvious algorithms
   - Link PR to this `DATA_STRUCTURES.md` with brief description

5. **Code Organization**
   - All DSA code lives in `com.smartcity.structures.*` or `com.smartcity.service.*`
   - No DSA code in the model/main app logic (separation of concerns)

---

## Package Structure (Target)

```
com/smartcity/
├── db/
│   └── DBConnection.java (unchanged)
├── main/
│   └── SmartCityApp.java (CLI with DSA menu items)
├── model/
│   ├── Place.java
│   └── User.java
├── service/
│   └── CachedDBConnection.java (cache layer)
└── structures/
    ├── Comparators.java (place sorting)
    ├── RecentlyViewedManager.java (queue)
    ├── command/
    │   ├── Command.java (interface)
    │   ├── EditPlaceCommand.java
    │   └── CommandHistory.java (stacks)
    ├── trie/
    │   └── PlaceNameTrie.java
    ├── tree/
    │   └── PlaceRangeTree.java
    ├── heap/
    │   └── PlaceHeap.java
    ├── graph/
    │   ├── PlaceGraph.java
    │   ├── PlaceGraphDijkstra.java
    │   └── GraphSearch.java (BFS/DFS)
    ├── unionfind/
    │   └── PlaceZoneUnionFind.java
    └── hash/
        └── BloomFilterUsername.java (future)
```

---

## Roadmap Alignment

| DSA Feature | Related Roadmap Item | Blocks / Blocked By | Status |
|---|---|---|---|
| Sorting | JUnit Test Suite | No dependencies | 🔄 Wave 1 |
| Stack (Undo) | DAO Refactor | No dependencies | 🔄 Wave 1 |
| Queue (Recent) | Session management | No dependencies | 🔄 Wave 1 |
| Cache | Performance optimization | No dependencies | 🔄 Wave 1 |
| Ratings Leaderboard | Ratings & Reviews | **Blocks** Heap feature | ⏳ Wave 3 |
| Graph proximity | N/A | No dependencies | ⏳ Wave 3 |
| Dijkstra route | N/A | No dependencies | ⏳ Wave 3 |
| Union-Find zones | N/A | No dependencies | ⏳ Wave 3 |

---

## How to Contribute

1. **Find an issue**: Pick from Wave 1, Wave 2, or Wave 3 in the `dsa` label
2. **Review acceptance criteria**: Check the issue body and this file
3. **Implement & test**: Write code + JUnit tests
4. **Document complexity**: Add Big-O analysis to your PR description
5. **CLI demo**: Show your feature working in `SmartCityApp.java`
6. **Link this file**: Update `DATA_STRUCTURES.md` status when done

---

## References & Learning Resources

### Primary Resources
- **[DSA Coding Reference Guide](DSA_CODING_REFERENCE.md)** ⭐ **START HERE**
  - Complete mapping of every feature to LeetCode, HackerRank, CodeChef, Codeforces problems
  - Study plans by contributor level
  - Quick links to all platforms
  - Recommended learning order

### Documentation
- [DSA Quick Start Guide](DSA_QUICKSTART.md) — Run the issue creator script
- [DSA Contributor Guide](DSA_CONTRIBUTOR_GUIDE.md) — Implementation handbook with code templates
- [Scripts README](scripts/README.md) — Automation tools for issue creation

### External References
- **Textbook**: _Introduction to Algorithms_ (CLRS) — comprehensive reference
- **LeetCode**: https://leetcode.com/ — 40+ problems mapped to this roadmap
- **HackerRank**: https://www.hackerrank.com/ — Java-focused practice
- **CodeChef**: https://www.codechef.com/ — Competitive programming
- **Codeforces**: https://codeforces.com/ — Advanced problem-solving
- **GeeksforGeeks**: https://www.geeksforgeeks.org/ — Algorithm tutorials
- **Visualizations**: https://visualgo.net/ — DSA visualization tools
- **Big-O Cheat Sheet**: https://www.bigocheatsheet.com/
- **Design Patterns**: https://refactoring.guru/design-patterns — for architectural patterns used (e.g., Command)

---

**Last Updated**: 2026-07-16  
**Maintainer**: @Rajath2005  
**Issues Label**: `dsa` (all data structure features)

---

## Quick Links to Coding Problems

### Wave 1 (Start Here!)
- [Sorting + Comparators](DSA_CODING_REFERENCE.md#sorting--comparators)
- [Stack + Undo/Redo](DSA_CODING_REFERENCE.md#stack--undoredo)
- [Queue + Ring Buffer](DSA_CODING_REFERENCE.md#queue--ring-buffer-recently-viewed)
- [HashMap + Caching](DSA_CODING_REFERENCE.md#hashmap--caching-cache-aside)

### Wave 2 (Intermediate)
- [Binary Search](DSA_CODING_REFERENCE.md#binary-search)
- [Trie + Autocomplete](DSA_CODING_REFERENCE.md#trie--autocomplete)
- [BST + Range Queries](DSA_CODING_REFERENCE.md#binary-search-tree--range-queries)

### Wave 3 (Advanced)
- [Heap + Top-N](DSA_CODING_REFERENCE.md#heap--top-n-ratings-leaderboard)
- [Graph + BFS/DFS](DSA_CODING_REFERENCE.md#graph--bfsdfs-proximity-search)
- [Dijkstra + Route Planning](DSA_CODING_REFERENCE.md#graph--dijkstra-route-planning)
- [Union-Find + Zone Clustering](DSA_CODING_REFERENCE.md#union-find-zone-clustering)
