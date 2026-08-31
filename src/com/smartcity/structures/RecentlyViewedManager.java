package com.smartcity.structures;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

/**
 * Manages the recently viewed places for a user using a bounded Deque (Ring Buffer).
 * <p>
 * Time Complexity:
 *   - viewPlace(id): O(N) to remove an existing element, but since N <= 10 (bounded),
 *     it is effectively O(1). Insertion at front and removal from back are O(1).
 *   - getRecent(): O(N) to copy the list, effectively O(1) since N <= 10.
 *   - clear(): O(1)
 * Space Complexity: O(1) auxiliary space (max 10 elements).
 * <p>
 * Related Learning: LeetCode 346 "Moving Average from Data Stream",
 *                   LeetCode 622 "Design Circular Queue"
 */
public class RecentlyViewedManager {
    private final int MAX_CAPACITY = 10;
    private LinkedList<Integer> deque;

    public RecentlyViewedManager() {
        this.deque = new LinkedList<>();
    }

    /**
     * Records a viewed place ID. Moves it to the front if it already exists,
     * and removes the oldest entry if capacity is exceeded.
     *
     * @param placeId the ID of the viewed place
     */
    public void viewPlace(int placeId) {
        // If it already exists, remove it first so we can move it to the front
        if (deque.contains(placeId)) {
            // Use Integer object wrapper to remove by object rather than index
            deque.remove(Integer.valueOf(placeId));
        }

        // If at capacity, remove the oldest (last) element
        if (deque.size() >= MAX_CAPACITY) {
            deque.removeLast();
        }

        // Add to the front (most recent)
        deque.addFirst(placeId);
    }

    /**
     * Retrieves the list of recently viewed places.
     *
     * @return a list of recently viewed place IDs, from most to least recent
     */
    public List<Integer> getRecent() {
        return new ArrayList<>(deque);
    }

    /**
     * Clears all recently viewed places.
     */
    public void clear() {
        deque.clear();
    }
}
