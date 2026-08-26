package com.smartcity.structures;

import java.util.Comparator;
import com.smartcity.model.Place;

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
        return Comparator.comparing(Place::getCategory)
            .thenComparing(Place::getName);
    }
}
