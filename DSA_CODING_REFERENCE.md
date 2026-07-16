# DSA Coding Platform Reference Guide

Complete mapping of SmartCityApp DSA features to practice problems on LeetCode, HackerRank, CodeChef, Codeforces, and other platforms. Use this to learn before implementing!

---

## Quick Navigation

- **Wave 1** (Foundational) — [Sorting](#sorting-comparators) | [Stack](#stack-undo-redo) | [Queue](#queue-recently-viewed) | [HashMap](#hashmap-caching)
- **Wave 2** (Intermediate) — [Binary Search](#binary-search) | [Trie](#trie-autocomplete) | [BST](#bst-range-queries)
- **Wave 3** (Advanced) — [Heap](#heap-top-n) | [Graph BFS/DFS](#graph-proximity) | [Dijkstra](#dijkstra-routing) | [Union-Find](#union-find-zones)

---

## Wave 1: Foundational Features

### Sorting + Comparators

**SmartCityApp Feature**: Sort places by name, category, or rating

| Platform | Problem | Difficulty | Why Solve It | Time |
|----------|---------|-----------|--|---|
| **LeetCode** | [1356: Sort Integers by The Number of 1 Bits](https://leetcode.com/problems/sort-integers-by-the-number-of-1-bits/) | Easy | Custom Comparator fundamentals | 20 min |
| **LeetCode** | [912: Sort an Array](https://leetcode.com/problems/sort-an-array/) | Medium | Different sorting algorithms | 45 min |
| **LeetCode** | [2191: Sort the Jumbled Array](https://leetcode.com/problems/sort-the-jumbled-array/) | Easy | Custom sort key logic | 15 min |
| **HackerRank** | [Java Sort](https://www.hackerrank.com/challenges/java-sort/problem) | Easy | Comparable interface in Java | 20 min |
| **HackerRank** | [Comparator](https://www.hackerrank.com/challenges/java-comparator/problem) | Easy | ⭐ Direct Comparator practice | 30 min |
| **CodeChef** | [SORT](https://www.codechef.com/problems/SORT) | Easy | Sorting fundamentals | 10 min |
| **CodeChef** | [FSORT](https://www.codechef.com/problems/FSORT) | Easy | Custom sorting criteria | 15 min |
| **Codeforces** | [1000A: Codeforces Checking](https://codeforces.com/problemset/problem/1000/A) | 800 | Sorting basics | 5 min |
| **Codeforces** | [451B: Sort the Array](https://codeforces.com/problemset/problem/451/B) | 1200 | Sorting with custom logic | 20 min |

**Recommended Order**:
1. HackerRank Comparator (quick Java refresher)
2. LeetCode 1356 (core concept)
3. LeetCode 912 (algorithms deep dive)
4. Implement `PlaceComparator`

---

### Stack + Undo/Redo

**SmartCityApp Feature**: Admin place edits with undo/redo command history

| Platform | Problem | Difficulty | Why Solve It | Time |
|----------|---------|-----------|--|---|
| **LeetCode** | [155: Min Stack](https://leetcode.com/problems/min-stack/) | Medium | ⭐ Core stack operations | 30 min |
| **LeetCode** | [225: Implement Stack using Queue](https://leetcode.com/problems/implement-stack-using-queues/) | Easy | Alternative implementation | 20 min |
| **LeetCode** | [150: Evaluate Reverse Polish Notation](https://leetcode.com/problems/evaluate-reverse-polish-notation/) | Medium | Classic stack problem | 30 min |
| **HackerRank** | [Java Stack](https://www.hackerrank.com/challenges/java-stack/problem) | Easy | Stack fundamentals | 15 min |
| **HackerRank** | [Equal Stacks](https://www.hackerrank.com/challenges/equal-stacks/problem) | Easy | Multi-stack logic | 20 min |
| **CodeChef** | [STACKS](https://www.codechef.com/problems/STACKS) | Easy | Stack applications | 15 min |
| **CodeChef** | [CBALLS](https://www.codechef.com/problems/CBALLS) | Medium | Stack-based problem solving | 30 min |
| **Codeforces** | [1982B: Maximum Sum](https://codeforces.com/problemset/problem/1982/B) | 1200 | Stack optimization | 25 min |
| **Codeforces** | [863B: Kayaking](https://codeforces.com/problemset/problem/863/B) | 1200 | Stack operations | 20 min |

**Design Pattern**: [Command Pattern Tutorial](https://refactoring.guru/design-patterns/command) (20 min)

**Recommended Order**:
1. LeetCode 155 (core stack)
2. HackerRank Java Stack (syntax)
3. LeetCode 150 (application)
4. Study Command Pattern (20 min)
5. Implement `CommandHistory`

---

### Queue + Ring Buffer (Recently Viewed)

**SmartCityApp Feature**: Bounded queue tracking last 10 viewed places

| Platform | Problem | Difficulty | Why Solve It | Time |
|----------|---------|-----------|--|---|
| **LeetCode** | [346: Moving Average from Data Stream](https://leetcode.com/problems/moving-average-from-data-stream/) | Easy | ⭐ Bounded queue concept | 20 min |
| **LeetCode** | [622: Design Circular Queue](https://leetcode.com/problems/design-circular-queue/) | Medium | ⭐ Ring buffer implementation | 40 min |
| **LeetCode** | [1670: Design Front Middle Back Queue](https://leetcode.com/problems/design-front-middle-back-queue/) | Medium | Deque operations | 35 min |
| **LeetCode** | [933: Number of Recent Calls](https://leetcode.com/problems/number-of-recent-calls/) | Easy | Time-window eviction | 15 min |
| **HackerRank** | [Java Deque](https://www.hackerrank.com/challenges/java-deque/problem) | Easy | Deque API in Java | 25 min |
| **HackerRank** | [Java Queue](https://www.hackerrank.com/challenges/java-queue/problem) | Easy | Queue fundamentals | 20 min |
| **CodeChef** | [QUEUE](https://www.codechef.com/problems/QUEUE) | Easy | Queue basics | 10 min |
| **CodeChef** | [RQUE](https://www.codechef.com/problems/RQUE) | Easy | Reverse queue operations | 20 min |
| **Codeforces** | [221A: Little Elephant and Interval](https://codeforces.com/problemset/problem/221/A) | 1000 | Queue/Deque logic | 15 min |
| **Codeforces** | [901B: GCD Compression](https://codeforces.com/problemset/problem/901/B) | 1100 | Deque manipulation | 20 min |

**Visualization**: [VisuAlgo Queue](https://visualgo.net/en/list) | [Circular Buffer Wiki](https://en.wikipedia.org/wiki/Circular_buffer)

**Recommended Order**:
1. LeetCode 346 (warm-up, bounded concept)
2. LeetCode 622 (full ring buffer)
3. HackerRank Java Deque (syntax)
4. LeetCode 933 (time-based eviction)
5. Implement `RecentlyViewedManager`

---

### HashMap + Caching (Cache-Aside)

**SmartCityApp Feature**: In-memory cache layer with TTL

| Platform | Problem | Difficulty | Why Solve It | Time |
|----------|---------|-----------|--|---|
| **LeetCode** | [146: LRU Cache](https://leetcode.com/problems/lru-cache/) | Medium | ⭐⭐ MUST SOLVE FIRST | 60 min |
| **LeetCode** | [706: Design HashMap](https://leetcode.com/problems/design-hashmap/) | Easy | HashMap internals | 30 min |
| **LeetCode** | [460: LFU Cache](https://leetcode.com/problems/lfu-cache/) | Hard | Advanced eviction policy | 90 min |
| **LeetCode** | [1472: Design Browser History](https://leetcode.com/problems/design-browser-history/) | Medium | Cache + history tracking | 40 min |
| **LeetCode** | [2402: Meeting Rooms III](https://leetcode.com/problems/meeting-rooms-iii/) | Medium | HashMap for state tracking | 45 min |
| **HackerRank** | [Java HashMap](https://www.hackerrank.com/challenges/java-hashmap/problem) | Easy | Key-value fundamentals | 20 min |
| **HackerRank** | [Java HashSet](https://www.hackerrank.com/challenges/java-hashset/problem) | Easy | Hash collections | 15 min |
| **HackerRank** | [Pairs](https://www.hackerrank.com/challenges/hash-tables-ice-cream-parlor/problem) | Medium | HashMap problem-solving | 30 min |
| **CodeChef** | [HASHIT](https://www.codechef.com/problems/HASHIT) | Easy | Hash table basics | 15 min |
| **CodeChef** | [MAPHASH](https://www.codechef.com/problems/MAPHASH) | Medium | HashMap operations | 25 min |
| **Codeforces** | [1201A: Important Numbers](https://codeforces.com/problemset/problem/1201/A) | 900 | HashMap counting | 10 min |
| **Codeforces** | [1110B: Tape](https://codeforces.com/problemset/problem/1110/B) | 1000 | HashMap optimization | 20 min |

**System Design Resources**:
- [Cache-Aside Pattern](https://docs.microsoft.com/en-us/azure/architecture/patterns/cache-aside) (10 min read)
- [LRU Cache Implementation (YouTube)](https://www.youtube.com/watch?v=S6IfqDXWa10) (8 min)
- [Caching Strategies (YouTube)](https://www.youtube.com/watch?v=0sWvXAYnQ1E) (15 min)

**Recommended Order** (Critical Path):
1. **LeetCode 146 (LRU Cache)** ⭐ (60 min) — Foundation for all caching
2. LeetCode 706 (HashMap design) (30 min)
3. HackerRank HashMap (20 min)
4. LeetCode 1472 (history + cache) (40 min)
5. Implement `CachedDBConnection` with TTL

---

## Wave 2: Intermediate Features

### Binary Search

**SmartCityApp Feature**: Fast lookup in sorted cached array

| Platform | Problem | Difficulty | Why Solve It | Time |
|----------|---------|-----------|--|---|
| **LeetCode** | [704: Binary Search](https://leetcode.com/problems/binary-search/) | Easy | ⭐ Foundation | 15 min |
| **LeetCode** | [35: Search Insert Position](https://leetcode.com/problems/search-insert-position/) | Easy | Search variants | 15 min |
| **LeetCode** | [33: Search in Rotated Sorted Array](https://leetcode.com/problems/search-in-rotated-sorted-array/) | Medium | Complex binary search | 25 min |
| **LeetCode** | [74: Search a 2D Matrix](https://leetcode.com/problems/search-a-2d-matrix/) | Medium | 2D binary search | 20 min |
| **HackerRank** | [Binary Search](https://www.hackerrank.com/challenges/binary-search-1/problem) | Easy | Quick practice | 15 min |
| **CodeChef** | [Binary Search](https://www.codechef.com/problems/BSEARCH) | Easy | Fundamentals | 10 min |
| **Codeforces** | [1200D: White Lines](https://codeforces.com/problemset/problem/1200/D) | 1200 | Binary search application | 20 min |

---

### Trie + Autocomplete

**SmartCityApp Feature**: Type-ahead search for place names

| Platform | Problem | Difficulty | Why Solve It | Time |
|----------|---------|-----------|--|---|
| **LeetCode** | [208: Implement Trie](https://leetcode.com/problems/implement-trie-prefix-tree/) | Medium | ⭐ Core Trie | 40 min |
| **LeetCode** | [211: Design Add and Search Words](https://leetcode.com/problems/design-add-and-search-words-data-structure/) | Medium | Trie with wildcards | 45 min |
| **LeetCode** | [212: Word Search II](https://leetcode.com/problems/word-search-ii/) | Hard | Trie + backtracking | 60 min |
| **LeetCode** | [720: Longest Word in Dictionary](https://leetcode.com/problems/longest-word-in-dictionary/) | Medium | Trie application | 30 min |
| **HackerRank** | [Contacts](https://www.hackerrank.com/challenges/contacts/problem) | Medium | ⭐ Trie for autocomplete | 40 min |
| **CodeChef** | [TRIE](https://www.codechef.com/problems/TRIE) | Medium | Trie fundamentals | 30 min |
| **Codeforces** | [1732D: Functional Graph](https://codeforces.com/problemset/problem/1732/D) | 1600 | Trie application | 40 min |

---

### Binary Search Tree + Range Queries

**SmartCityApp Feature**: Browse places by category with rating range

| Platform | Problem | Difficulty | Why Solve It | Time |
|----------|---------|-----------|--|---|
| **LeetCode** | [95: Unique Binary Search Trees II](https://leetcode.com/problems/unique-binary-search-trees-ii/) | Medium | BST construction | 45 min |
| **LeetCode** | [98: Validate BST](https://leetcode.com/problems/validate-binary-search-tree/) | Medium | BST properties | 20 min |
| **LeetCode** | [230: Kth Smallest Element in BST](https://leetcode.com/problems/kth-smallest-element-in-a-bst/) | Medium | Range queries | 25 min |
| **LeetCode** | [236: Lowest Common Ancestor](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-search-tree/) | Medium | BST navigation | 20 min |
| **HackerRank** | [BST: Lowest Common Ancestor](https://www.hackerrank.com/challenges/binary-search-tree-lowest-common-ancestor/problem) | Easy | BST structure | 25 min |
| **CodeChef** | [BST](https://www.codechef.com/problems/BST) | Medium | Binary search tree ops | 30 min |

---

## Wave 3: Advanced/Showcase Features

### Heap + Top-N (Ratings Leaderboard)

**SmartCityApp Feature**: Top 5 highest-rated places (after Ratings roadmap item)

| Platform | Problem | Difficulty | Why Solve It | Time |
|----------|---------|-----------|--|---|
| **LeetCode** | [215: Kth Largest Element in Array](https://leetcode.com/problems/kth-largest-element-in-an-array/) | Medium | Heap fundamentals | 30 min |
| **LeetCode** | [347: Top K Frequent Elements](https://leetcode.com/problems/top-k-frequent-elements/) | Medium | ⭐ Top-K pattern | 40 min |
| **LeetCode** | [23: Merge K Sorted Lists](https://leetcode.com/problems/merge-k-sorted-lists/) | Hard | Heap application | 45 min |
| **LeetCode** | [1337: The K Weakest Rows in a Matrix](https://leetcode.com/problems/the-k-weakest-rows-in-a-matrix/) | Easy | Heap for sorting | 20 min |
| **HackerRank** | [QHEAP1](https://www.hackerrank.com/challenges/qheap1/problem) | Easy | Heap operations | 25 min |
| **CodeChef** | [HEAP](https://www.codechef.com/problems/HEAP) | Medium | Heap basics | 30 min |
| **Codeforces** | [1200E: Compress Words](https://codeforces.com/problemset/problem/1200/E) | 1300 | Heap usage | 35 min |

---

### Graph + BFS/DFS (Proximity Search)

**SmartCityApp Feature**: Places within N hops using lat/long proximity graph

| Platform | Problem | Difficulty | Why Solve It | Time |
|----------|---------|-----------|--|---|
| **LeetCode** | [104: Maximum Depth of Binary Tree](https://leetcode.com/problems/maximum-depth-of-binary-tree/) | Easy | DFS basics | 10 min |
| **LeetCode** | [200: Number of Islands](https://leetcode.com/problems/number-of-islands/) | Medium | ⭐ BFS/DFS foundation | 30 min |
| **LeetCode** | [994: Rotting Oranges](https://leetcode.com/problems/rotting-oranges/) | Medium | BFS application | 35 min |
| **LeetCode** | [127: Word Ladder](https://leetcode.com/problems/word-ladder/) | Hard | BFS shortest path | 45 min |
| **LeetCode** | [1130: Minimum Cost Tree From Leaf Values](https://leetcode.com/problems/minimum-cost-tree-from-leaf-values/) | Medium | Graph traversal | 40 min |
| **HackerRank** | [BFS: Shortest Reach](https://www.hackerrank.com/challenges/bfs-shortest-reach/problem) | Medium | ⭐ BFS on graphs | 40 min |
| **HackerRank** | [DFS: Connected Cell in a Grid](https://www.hackerrank.com/challenges/ctci-connected-cell-in-a-grid/problem) | Medium | DFS on grid | 35 min |
| **CodeChef** | [GFSORT](https://www.codechef.com/problems/GFSORT) | Medium | Graph traversal | 35 min |
| **Codeforces** | [1200C: Common Subsequence](https://codeforces.com/problemset/problem/1200/C) | 1200 | Graph-like logic | 20 min |

---

### Graph + Dijkstra (Route Planning)

**SmartCityApp Feature**: Shortest-path route between two places

| Platform | Problem | Difficulty | Why Solve It | Time |
|----------|---------|-----------|--|---|
| **LeetCode** | [743: Network Delay Time](https://leetcode.com/problems/network-delay-time/) | Medium | ⭐ Dijkstra basics | 45 min |
| **LeetCode** | [1334: Find the City with Smallest Neighbors](https://leetcode.com/problems/find-the-city-with-the-smallest-number-of-neighbors-at-a-threshold-distance/) | Medium | Dijkstra variant | 50 min |
| **LeetCode** | [787: Cheapest Flights Within K Stops](https://leetcode.com/problems/cheapest-flights-within-k-stops/) | Medium | Shortest path variant | 45 min |
| **LeetCode** | [882: Reachable Nodes In Subdivided Graph](https://leetcode.com/problems/reachable-nodes-in-subdivided-graph/) | Hard | Advanced Dijkstra | 60 min |
| **HackerRank** | [Dijkstra: Shortest Reach 2](https://www.hackerrank.com/challenges/dijkstra-shortest-reach2/problem) | Hard | ⭐ Dijkstra implementation | 60 min |
| **CodeChef** | [SHORTESTPATH](https://www.codechef.com/problems/SHORTESTPATH) | Medium | Shortest path | 40 min |
| **Codeforces** | [1200D: White Lines](https://codeforces.com/problemset/problem/1200/D) | 1200 | Graph shortest path | 35 min |

**Resources**:
- [Dijkstra Algorithm Explanation (YouTube)](https://www.youtube.com/watch?v=XB4MIexjvY0) (10 min)
- [GeeksforGeeks Dijkstra](https://www.geeksforgeeks.org/dijkstras-shortest-path-algorithm-greedy-algo-7/) (read: 15 min)

---

### Union-Find (Zone Clustering)

**SmartCityApp Feature**: Cluster places into "zones" by proximity

| Platform | Problem | Difficulty | Why Solve It | Time |
|----------|---------|-----------|--|---|
| **LeetCode** | [684: Redundant Connection](https://leetcode.com/problems/redundant-connection/) | Medium | ⭐ Union-Find intro | 40 min |
| **LeetCode** | [765: Couples Holding Hands](https://leetcode.com/problems/couples-holding-hands/) | Hard | Union-Find application | 55 min |
| **LeetCode** | [1202: Smallest String With Swaps](https://leetcode.com/problems/smallest-string-with-swaps/) | Medium | ⭐ Zone clustering | 50 min |
| **LeetCode** | [1258: Synonymous Sentences](https://leetcode.com/problems/synonymous-sentences/) | Hard | Union-Find + sorting | 60 min |
| **HackerRank** | [Superpowers](https://www.hackerrank.com/challenges/superpowers/problem) | Hard | Union-Find on large graphs | 60 min |
| **CodeChef** | [UNIONFIND](https://www.codechef.com/problems/UNIONFIND) | Medium | Union-Find ops | 40 min |
| **Codeforces** | [1200A: Hotelier](https://codeforces.com/problemset/problem/1200/A) | 1000 | Set operations (Union-Find analog) | 15 min |

**Resources**:
- [Union-Find / DSU Guide (GeeksforGeeks)](https://www.geeksforgeeks.org/union-find/) (20 min)
- [Union-Find With Path Compression (YouTube)](https://www.youtube.com/watch?v=eTaWbhmf14s) (8 min)

---

## Study Plans by Goal

### For First-Timer Contributor (2 weeks)
1. **Week 1**: Complete Wave 1 (Sorting + Stack + Queue + HashMap)
   - Day 1-2: LeetCode 1356 (sorting) + HackerRank Comparator
   - Day 3-4: LeetCode 155 (stack) + HackerRank Stack
   - Day 5-6: LeetCode 346 + 622 (queue)
   - Day 7: LeetCode 146 (caching) + implement all 4 features

2. **Week 2**: Deploy and Test
   - Write tests, integrate CLI, PR review

### For Intermediate Contributor (3-4 weeks)
1. Complete Wave 1 (1 week)
2. **Wave 2**: Binary Search (3-4 days) + Trie (4-5 days) + BST (4-5 days)
3. Deploy and test (3-5 days)

### For Advanced Contributor (6+ weeks)
1. Complete Waves 1 & 2 (3 weeks)
2. **Wave 3**: Heap (3-4 days) + Graph BFS/DFS (5-6 days) + Dijkstra (5-6 days) + Union-Find (5-6 days)
3. Deploy showcase features (4-5 days)

---

## Recommended Learning Order (By Difficulty)

**Easiest to Hardest**:
1. Sorting + Comparators (LeetCode 1356)
2. Queue (LeetCode 346, 622)
3. Stack (LeetCode 155)
4. HashMap (LeetCode 146) — **Hardest of Wave 1**
5. Binary Search (LeetCode 704)
6. Trie (LeetCode 208)
7. BST (LeetCode 98, 230)
8. Heap (LeetCode 347)
9. BFS/DFS (LeetCode 200)
10. Dijkstra (LeetCode 743)
11. Union-Find (LeetCode 684) — **Most challenging conceptually**

---

## Platform Difficulty Ratings

- **LeetCode**: Easy (< 20 min) | Medium (30-60 min) | Hard (60+ min)
- **HackerRank**: Easy (< 20 min) | Medium (20-40 min) | Hard (40+ min)
- **CodeChef**: Easy (< 15 min) | Medium (15-45 min) | Hard (45+ min)
- **Codeforces**: 800-1000 rating (easy) | 1200+ rating (medium) | 1500+ rating (hard)

---

## Tips for Success

1. **Read problem first**, don't jump to solutions
2. **Implement yourself** before checking hints
3. **After solving**, look at other solutions — learn different approaches
4. **Link concepts**: "How does this problem connect to my feature?"
5. **Time yourself**: Get comfortable solving in 30-45 min
6. **Discuss in comments**: Read solutions from top contributors

---

## Resources Summary

| Resource | Type | Time | Use For |
|----------|------|------|---------|
| LeetCode | Online Judge | 20-60 min/problem | Core algorithms, editorial discussions |
| HackerRank | Online Judge | 15-40 min/problem | Language-specific practice (Java focus) |
| CodeChef | Online Judge | 10-45 min/problem | Competitive practice, large test cases |
| Codeforces | Competitive | 15-60 min/problem | Advanced problem-solving |
| GeeksforGeeks | Tutorials | 15-30 min | Algorithm explanations, visualizations |
| VisuAlgo | Visualization | 5-10 min | See algorithms in action |
| YouTube (ByteByteGo, etc) | Video | 8-15 min | System design, Big-O analysis |

---

## Quick Links

### LeetCode Collections
- [All LeetCode Problems Used](https://leetcode.com/list/xp4k5v67/)
- [LeetCode Study Plan](https://leetcode.com/study-plan/)

### HackerRank Collections
- [Java Domain](https://www.hackerrank.com/domains/java)
- [Data Structures](https://www.hackerrank.com/domains/data-structures)

### CodeChef Practice
- [Practice](https://www.codechef.com/problems)
- [Beginner to Intermediate](https://www.codechef.com/problems/school)

### Codeforces
- [Problem Set](https://codeforces.com/problemset)
- [By Rating](https://codeforces.com/problemset?order=BY_RATING_ASC)

---

**Last Updated**: 2026-07-16  
**Maintained By**: SmartCityApp DSA Team  
**Questions?** Open an issue with label `dsa` or reach out to @Rajath2005

