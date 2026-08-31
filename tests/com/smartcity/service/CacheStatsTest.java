/*
 * How to run these tests:
 *
 *   mvn test                          # run the whole test suite
 *   mvn test -Dtest=CacheStatsTest    # run only this class
 *
 * Requires JDK 21 and Maven. No database connection is needed.
 */
package com.smartcity.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link CacheStats}.
 *
 * <p>The derived figures — total lookups and hit rate — are what the CLI puts
 * in front of an admin, so they are pinned down here independently of the
 * cache that produces them.
 */
class CacheStatsTest {

    @Test
    @DisplayName("every counter is reported back exactly as it was supplied")
    void gettersShouldReturnConstructorValues() {
        CacheStats stats = new CacheStats(9, 3, 2, 1, 5, 4);

        assertEquals(9, stats.getHits());
        assertEquals(3, stats.getMisses());
        assertEquals(2, stats.getExpirations());
        assertEquals(1, stats.getInvalidations());
        assertEquals(5, stats.getPlaceEntries());
        assertEquals(4, stats.getUserEntries());
    }

    @Test
    @DisplayName("total lookups is hits plus misses")
    void totalLookupsShouldSumHitsAndMisses() {
        assertEquals(12, new CacheStats(9, 3, 0, 0, 0, 0).getTotalLookups());
    }

    @Test
    @DisplayName("the hit rate is the share of lookups served from memory")
    void hitRatioShouldBeHitsOverTotal() {
        assertEquals(0.75, new CacheStats(3, 1, 0, 0, 0, 0).getHitRatio(), 1e-9);
        assertEquals(1.0, new CacheStats(4, 0, 0, 0, 0, 0).getHitRatio(), 1e-9);
        assertEquals(0.0, new CacheStats(0, 4, 0, 0, 0, 0).getHitRatio(), 1e-9);
    }

    @Test
    @DisplayName("an idle cache reports a zero hit rate rather than dividing by zero")
    void hitRatioShouldBeZeroWhenNothingHappened() {
        assertEquals(0.0, new CacheStats(0, 0, 0, 0, 0, 0).getHitRatio(), 1e-9);
    }

    @Test
    @DisplayName("toString summarises hits, misses, hit rate, and how much is cached")
    void toStringShouldSummariseTheCounters() {
        String summary = new CacheStats(3, 1, 0, 0, 2, 1).toString();

        assertTrue(summary.contains("3 hits"), summary);
        assertTrue(summary.contains("1 misses"), summary);
        assertTrue(summary.contains("75.00%"), summary);
        assertTrue(summary.contains("3 entries cached"), summary);
    }
}
