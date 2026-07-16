# DSA Package Structure & Contributor Guide

This guide explains the recommended package organization for DSA features in SmartCityApp and provides starter templates for contributors.

## Package Organization

All DSA features should live in two main packages:

### 1. `com.smartcity.structures.*` — Core Data Structures

Pure implementations of data structures and algorithms, independent of the database layer.

```
com/smartcity/structures/
├── Comparators.java                    # All Place comparators (sorting)
├── RecentlyViewedManager.java          # Deque-based recently viewed
│
├── command/                            # Stack-based undo/redo
│   ├── Command.java                    # Interface
│   ├── EditPlaceCommand.java           # Concrete implementations
│   ├── AddPlaceCommand.java
│   ├── DeletePlaceCommand.java
│   └── CommandHistory.java             # Undo/redo orchestrator
│
├── trie/
│   ├── TrieNode.java
│   └── PlaceNameTrie.java
│
├── tree/
│   ├── TreeNode.java
│   └── PlaceRangeTree.java
│
├── heap/
│   └── PlaceHeap.java
│
├── graph/
│   ├── PlaceGraph.java
│   ├── GraphSearch.java                # BFS/DFS
│   └── PlaceGraphDijkstra.java
│
├── unionfind/
│   └── PlaceZoneUnionFind.java
│
└── hash/
    └── BloomFilterUsername.java        # (future)
```

### 2. `com.smartcity.service.*` — Service Layer (DB Integration)

Wrappers and services that integrate DSA structures with the database and application logic.

```
com/smartcity/service/
├── CachedDBConnection.java             # Cache-aside pattern
└── CacheStats.java                     # Cache metrics (optional)
```

### 3. Test Organization

Mirror the package structure in `src/test/java`:

```
src/test/java/com/smartcity/structures/
├── Comparators.Test.java
├── RecentlyViewedManagerTest.java
├── command/
│   ├── CommandTest.java
│   └── CommandHistoryTest.java
├── trie/
│   └── PlaceNameTrieTest.java
├── tree/
│   └── PlaceRangeTreeTest.java
├── graph/
│   └── PlaceGraphTest.java
└── unionfind/
    └── PlaceZoneUnionFindTest.java
```

---

## Starter Templates

### Template 1: Simple Data Structure (Comparator)

Use this template for straightforward structures like comparators, basic arrays, simple classes.

**File**: `com/smartcity/structures/Comparators.java`

```java
package com.smartcity.structures;

import com.smartcity.model.Place;
import java.util.Comparator;

/**
 * Comparators for Place objects, supporting multiple sort orders.
 * 
 * Time Complexity:
 *   - Creating comparator: O(1)
 *   - Using with Collections.sort(list, comparator): O(n log n)
 * Space Complexity: O(1) for comparator instance
 * 
 * Related Learning: LeetCode 1356 "Sort Integers by The Number of 1 Bits"
 */
public class Comparators {
    
    /**
     * Returns a comparator that sorts places alphabetically by name.
     * @return Comparator<Place> comparing by name
     */
    public static Comparator<Place> byName() {
        return Comparator.comparing(Place::getName);
    }
    
    /**
     * Returns a comparator that sorts places by category (alphabetical),
     * then by name as a tie-breaker.
     * @return Comparator<Place> comparing by category, then name
     */
    public static Comparator<Place> byCategory() {
        return Comparator
            .comparing(Place::getCategory)
            .thenComparing(Place::getName);
    }
    
    /**
     * Returns a comparator that sorts places by rating (descending),
     * then by name as a tie-breaker.
     * @return Comparator<Place> comparing by rating (desc), then name
     */
    public static Comparator<Place> byRating() {
        return Comparator
            .comparing(Place::getRating, Comparator.reverseOrder())
            .thenComparing(Place::getName);
    }
}
```

**Test File**: `src/test/java/com/smartcity/structures/ComparatorsTest.java`

```java
package com.smartcity.structures;

import com.smartcity.model.Place;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ComparatorsTest {
    
    @Test
    public void testSortByName() {
        List<Place> places = new ArrayList<>();
        places.add(new Place(1, "Zoo", "Recreation", 4.5, 10.5, 20.5));
        places.add(new Place(2, "Museum", "Culture", 4.8, 10.6, 20.6));
        places.add(new Place(3, "Arena", "Recreation", 4.2, 10.7, 20.7));
        
        Collections.sort(places, Comparators.byName());
        
        assertEquals("Arena", places.get(0).getName());
        assertEquals("Museum", places.get(1).getName());
        assertEquals("Zoo", places.get(2).getName());
    }
    
    @Test
    public void testSortByRating() {
        List<Place> places = new ArrayList<>();
        places.add(new Place(1, "Zoo", "Recreation", 3.5, 10.5, 20.5));
        places.add(new Place(2, "Museum", "Culture", 4.8, 10.6, 20.6));
        places.add(new Place(3, "Arena", "Recreation", 4.2, 10.7, 20.7));
        
        Collections.sort(places, Comparators.byRating());
        
        assertEquals(4.8, places.get(0).getRating());
        assertEquals(4.2, places.get(1).getRating());
        assertEquals(3.5, places.get(2).getRating());
    }
}
```

---

### Template 2: Data Structure with State (Stack / CommandHistory)

Use this for stateful structures that maintain internal collections.

**File**: `com/smartcity/structures/command/CommandHistory.java`

```java
package com.smartcity.structures.command;

import java.util.Stack;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages command history for undo/redo functionality.
 * Uses two stacks: one for undo, one for redo.
 * 
 * Time Complexity:
 *   - execute(): O(1) push operation
 *   - undo(): O(1) pop and push operations
 *   - redo(): O(1) pop and push operations
 * Space Complexity: O(n) where n = number of commands in history
 * 
 * Related Learning: LeetCode 155 "Min Stack", Command Pattern
 */
public class CommandHistory {
    
    private Stack<Command> undoStack;
    private Stack<Command> redoStack;
    
    /**
     * Initialize an empty command history.
     */
    public CommandHistory() {
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }
    
    /**
     * Execute a command and add it to the undo stack.
     * Clear the redo stack (since we're making a new action).
     * 
     * @param cmd Command to execute
     */
    public void executeCommand(Command cmd) {
        cmd.execute();
        undoStack.push(cmd);
        redoStack.clear(); // Redo stack is invalid after new action
    }
    
    /**
     * Undo the last command by popping from undo stack,
     * reversing its effect, and pushing to redo stack.
     * 
     * @return true if undo was performed, false if undo stack is empty
     */
    public boolean undo() {
        if (undoStack.isEmpty()) {
            return false;
        }
        Command cmd = undoStack.pop();
        cmd.undo();
        redoStack.push(cmd);
        return true;
    }
    
    /**
     * Redo the last undone command by popping from redo stack,
     * executing it, and pushing to undo stack.
     * 
     * @return true if redo was performed, false if redo stack is empty
     */
    public boolean redo() {
        if (redoStack.isEmpty()) {
            return false;
        }
        Command cmd = redoStack.pop();
        cmd.execute();
        undoStack.push(cmd);
        return true;
    }
    
    /**
     * Get the history of all executed commands (undo stack only, in order).
     * 
     * @return List of commands in order of execution
     */
    public List<Command> getHistory() {
        return new ArrayList<>(undoStack);
    }
    
    /**
     * Clear both undo and redo stacks.
     */
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
    
    /**
     * Check if undo is possible.
     * @return true if undo stack is not empty
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }
    
    /**
     * Check if redo is possible.
     * @return true if redo stack is not empty
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
}
```

**Interface File**: `com/smartcity/structures/command/Command.java`

```java
package com.smartcity.structures.command;

/**
 * Command interface for undo/redo operations.
 * Implement execute() and undo() for each command type.
 */
public interface Command {
    /**
     * Execute the command.
     */
    void execute();
    
    /**
     * Undo the command (reverse its effect).
     */
    void undo();
}
```

**Test File**: `src/test/java/com/smartcity/structures/command/CommandHistoryTest.java`

```java
package com.smartcity.structures.command;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CommandHistoryTest {
    
    private CommandHistory history;
    
    @org.junit.jupiter.api.BeforeEach
    public void setUp() {
        history = new CommandHistory();
    }
    
    @Test
    public void testExecuteAndUndo() {
        // Create a mock command
        MockCommand cmd = new MockCommand();
        
        history.executeCommand(cmd);
        assertTrue(cmd.isExecuted());
        assertFalse(cmd.isUndone());
        
        history.undo();
        assertTrue(cmd.isUndone());
    }
    
    @Test
    public void testUndoRedo() {
        MockCommand cmd = new MockCommand();
        history.executeCommand(cmd);
        history.undo();
        history.redo();
        
        assertTrue(cmd.isExecuted());
        assertFalse(cmd.isUndone());
    }
    
    @Test
    public void testUndoOnEmptyStack() {
        assertFalse(history.undo());
    }
    
    @Test
    public void testRedoClearedAfterNewCommand() {
        MockCommand cmd1 = new MockCommand();
        MockCommand cmd2 = new MockCommand();
        
        history.executeCommand(cmd1);
        history.undo();
        history.executeCommand(cmd2); // Should clear redo stack
        
        assertFalse(history.redo());
    }
    
    // Mock command for testing
    private static class MockCommand implements Command {
        private boolean executed = false;
        private boolean undone = false;
        
        @Override
        public void execute() {
            executed = true;
            undone = false;
        }
        
        @Override
        public void undo() {
            undone = true;
        }
        
        public boolean isExecuted() { return executed; }
        public boolean isUndone() { return undone; }
    }
}
```

---

### Template 3: Complex Data Structure with Integration (CachedDBConnection)

Use this for structures that integrate with the database layer.

**File**: `com/smartcity/service/CachedDBConnection.java`

```java
package com.smartcity.service;

import com.smartcity.db.DBConnection;
import com.smartcity.model.Place;
import com.smartcity.model.User;
import java.util.HashMap;
import java.util.Map;

/**
 * Cached wrapper around DBConnection using cache-aside pattern.
 * 
 * Time Complexity:
 *   - Cache hit: O(1) HashMap lookup
 *   - Cache miss: O(1) + DB query time
 *   - put/invalidate: O(1)
 * Space Complexity: O(n) where n = number of cached entries
 * 
 * Related Learning: LeetCode 146 "LRU Cache", Cache-Aside Pattern
 */
public class CachedDBConnection {
    
    private DBConnection dbConnection;
    private Map<Integer, CacheEntry<Place>> placeCache;
    private Map<Integer, CacheEntry<User>> userCache;
    private long ttlMillis; // Time-to-live in milliseconds
    private CacheStats stats;
    
    /**
     * Initialize cached connection with default 5-minute TTL.
     * @param dbConnection underlying DB connection to wrap
     */
    public CachedDBConnection(DBConnection dbConnection) {
        this(dbConnection, 5 * 60 * 1000); // 5 minutes
    }
    
    /**
     * Initialize cached connection with custom TTL.
     * @param dbConnection underlying DB connection
     * @param ttlMillis time-to-live for cache entries in milliseconds
     */
    public CachedDBConnection(DBConnection dbConnection, long ttlMillis) {
        this.dbConnection = dbConnection;
        this.ttlMillis = ttlMillis;
        this.placeCache = new HashMap<>();
        this.userCache = new HashMap<>();
        this.stats = new CacheStats();
    }
    
    /**
     * Get a place, checking cache first (cache-aside pattern).
     * 
     * @param placeId ID of the place
     * @return Place object, or null if not found
     */
    public Place getPlace(int placeId) {
        CacheEntry<Place> entry = placeCache.get(placeId);
        
        // Check if cached entry exists and is not expired
        if (entry != null && !entry.isExpired()) {
            stats.recordHit();
            return entry.getValue();
        }
        
        // Cache miss or expired — query DB
        stats.recordMiss();
        Place place = dbConnection.getPlaceById(placeId);
        
        if (place != null) {
            placeCache.put(placeId, new CacheEntry<>(place, System.currentTimeMillis() + ttlMillis));
        }
        
        return place;
    }
    
    /**
     * Get a user, checking cache first.
     * 
     * @param userId ID of the user
     * @return User object, or null if not found
     */
    public User getUser(int userId) {
        CacheEntry<User> entry = userCache.get(userId);
        
        if (entry != null && !entry.isExpired()) {
            stats.recordHit();
            return entry.getValue();
        }
        
        stats.recordMiss();
        User user = dbConnection.getUserById(userId);
        
        if (user != null) {
            userCache.put(userId, new CacheEntry<>(user, System.currentTimeMillis() + ttlMillis));
        }
        
        return user;
    }
    
    /**
     * Add a place and invalidate the cache.
     */
    public void addPlace(Place place) {
        dbConnection.addPlace(place);
        placeCache.clear();
    }
    
    /**
     * Edit a place and invalidate the cache.
     */
    public void editPlace(int placeId, Place updatedPlace) {
        dbConnection.editPlace(placeId, updatedPlace);
        placeCache.remove(placeId);
    }
    
    /**
     * Delete a place and invalidate the cache.
     */
    public void deletePlace(int placeId) {
        dbConnection.deletePlace(placeId);
        placeCache.remove(placeId);
    }
    
    /**
     * Clear all caches.
     */
    public void clearCache() {
        placeCache.clear();
        userCache.clear();
    }
    
    /**
     * Get cache statistics.
     * @return CacheStats with hit/miss counts
     */
    public CacheStats getCacheStats() {
        return stats;
    }
    
    /**
     * Internal class to wrap cached values with expiration time.
     */
    private static class CacheEntry<T> {
        private T value;
        private long expiresAt;
        
        CacheEntry(T value, long expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
        }
        
        T getValue() {
            return value;
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }
    }
}
```

**Stats File**: `com/smartcity/service/CacheStats.java`

```java
package com.smartcity.service;

/**
 * Simple cache statistics tracking hits and misses.
 */
public class CacheStats {
    private long hits = 0;
    private long misses = 0;
    
    public void recordHit() {
        hits++;
    }
    
    public void recordMiss() {
        misses++;
    }
    
    public long getHits() { return hits; }
    public long getMisses() { return misses; }
    public long getTotal() { return hits + misses; }
    
    public double getHitRatio() {
        if (getTotal() == 0) return 0.0;
        return (double) hits / getTotal();
    }
    
    @Override
    public String toString() {
        return String.format("Cache Stats: %d hits, %d misses (ratio: %.2f%%)",
            hits, misses, getHitRatio() * 100);
    }
}
```

---

## Step-by-Step: Implementing a DSA Feature

### 1. **Pick an Issue**

Choose an issue from the `dsa` label. Start with Wave 1 if you're new to the codebase.

### 2. **Create the Package Structure**

If not already created, create the package directory:

```bash
mkdir -p src/main/java/com/smartcity/structures/
mkdir -p src/test/java/com/smartcity/structures/
```

### 3. **Implement the Data Structure**

Write the main class following the template above. Include:
- **Javadoc with complexity analysis**
- **Clear method names and types**
- **Edge case handling** (empty, null, etc.)
- **Inline comments** for non-obvious logic

### 4. **Write Comprehensive Tests**

Create a test class mirroring the structure:

```java
@Test
public void testFeature() {
    // Setup
    MyStructure struct = new MyStructure();
    
    // Action
    struct.doSomething();
    
    // Assert
    assertEquals(expected, actual);
}
```

Include:
- Normal cases
- Edge cases (empty, single element, boundary values)
- Error cases (invalid input, state violations)
- Performance assertions if relevant (e.g., "10k ops complete in <1s")

### 5. **Integrate with SmartCityApp**

Add CLI menu item in `SmartCityApp.java` to demo the feature:

```java
case 8:
    System.out.println("\n=== DSA Demo: Sorting Places ===");
    List<Place> places = db.getAllPlaces();
    List<Place> sorted = new ArrayList<>(places);
    Collections.sort(sorted, Comparators.byRating());
    System.out.println("Top 5 rated places:");
    sorted.stream().limit(5).forEach(p -> 
        System.out.printf("  %s - %.1f★\n", p.getName(), p.getRating())
    );
    break;
```

### 6. **Run Tests**

```bash
mvn clean test
```

Verify all tests pass.

### 7. **Document Complexity in PR**

When opening a PR, include a Big-O summary:

```
## Complexity Analysis

- **Time**: O(n log n) for sorting, O(1) for cache lookup
- **Space**: O(n) for sorted copy
- **Justification**: Uses Timsort (Collections.sort) which is O(n log n) avg/worst
```

### 8. **Link This File**

Reference `DATA_STRUCTURES.md` in your PR description.

---

## Testing Checklist

Before submitting a PR, ensure:

- [ ] All unit tests pass (`mvn clean test`)
- [ ] Code compiles without warnings
- [ ] Edge cases tested (empty, null, boundaries)
- [ ] Big-O analysis documented
- [ ] Javadoc comments on all public methods
- [ ] CLI demo works in `SmartCityApp.java`
- [ ] No hardcoded values (use constants)
- [ ] Thread safety considered (if applicable)
- [ ] PR description includes Big-O summary

---

## Code Style & Standards

- **Naming**: Classes use PascalCase (`PlaceCache`), methods use camelCase (`getPlace()`)
- **Javadoc**: Required on all public classes and methods
- **Complexity**: Document with Big-O notation in class-level Javadoc
- **Tests**: JUnit 5 format, test names describe what's being tested
- **Packages**: Group by structure type (`com.smartcity.structures.tree`, `.heap`, etc.)

---

## Questions?

- Check `DATA_STRUCTURES.md` for the full roadmap and context
- Review existing implementations for patterns
- Open a discussion in the repository
- Reach out to @Rajath2005

Happy coding! 🚀
