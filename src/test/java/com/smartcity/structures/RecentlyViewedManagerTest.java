package com.smartcity.structures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecentlyViewedManagerTest {

    private RecentlyViewedManager manager;

    @BeforeEach
    public void setUp() {
        manager = new RecentlyViewedManager();
    }

    @Test
    public void testEmptyQueue() {
        assertTrue(manager.getRecent().isEmpty(), "Queue should be empty initially");
    }

    @Test
    public void testViewPlaceAndGetRecent() {
        manager.viewPlace(1);
        manager.viewPlace(2);
        manager.viewPlace(3);

        List<Integer> recent = manager.getRecent();
        assertEquals(3, recent.size());
        assertEquals(3, recent.get(0), "Most recent should be 3");
        assertEquals(2, recent.get(1));
        assertEquals(1, recent.get(2), "Oldest should be 1");
    }

    @Test
    public void testBoundedOverflow() {
        // Add 12 items (capacity is 10)
        for (int i = 1; i <= 12; i++) {
            manager.viewPlace(i);
        }

        List<Integer> recent = manager.getRecent();
        assertEquals(10, recent.size(), "Queue size should not exceed 10");

        // The most recent should be 12, down to 3
        assertEquals(12, recent.get(0));
        assertEquals(3, recent.get(9));
        assertFalse(recent.contains(1), "Oldest elements should be removed");
        assertFalse(recent.contains(2));
    }

    @Test
    public void testDuplicates() {
        manager.viewPlace(1);
        manager.viewPlace(2);
        manager.viewPlace(3);
        
        // View 2 again
        manager.viewPlace(2);

        List<Integer> recent = manager.getRecent();
        assertEquals(3, recent.size(), "Duplicates should not increase size");
        assertEquals(2, recent.get(0), "2 should be moved to the front");
        assertEquals(3, recent.get(1));
        assertEquals(1, recent.get(2));
    }

    @Test
    public void testClear() {
        manager.viewPlace(1);
        manager.viewPlace(2);
        manager.clear();

        assertTrue(manager.getRecent().isEmpty(), "Queue should be empty after clear");
    }
}
