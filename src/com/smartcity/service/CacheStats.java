package com.smartcity.service;

/**
 * An immutable snapshot of a {@link CachedDBConnection}'s counters, taken at
 * the moment {@link CachedDBConnection#getCacheStats()} is called.
 * <p>
 * Because it is a snapshot rather than a live view, two snapshots taken at
 * different times can be compared to measure a single operation, and holding
 * one never exposes the cache's internal counters to modification.
 * <p>
 * Time Complexity: O(1) for every accessor.
 * Space Complexity: O(1).
 *
 * @author SmartCityApp contributors
 * @version 1.0
 */
public final class CacheStats {

    private final long hits;
    private final long misses;
    private final long expirations;
    private final long invalidations;
    private final int placeEntries;
    private final int userEntries;

    /**
     * Constructs a snapshot of the cache counters.
     *
     * @param hits          number of lookups served from the cache
     * @param misses        number of lookups that had to fall through to the database
     * @param expirations   number of entries dropped because their TTL had elapsed
     * @param invalidations number of entries evicted explicitly after a data modification
     * @param placeEntries  number of place entries currently held (expired ones included)
     * @param userEntries   number of user entries currently held (expired ones included)
     */
    public CacheStats(long hits, long misses, long expirations, long invalidations,
                      int placeEntries, int userEntries) {
        this.hits = hits;
        this.misses = misses;
        this.expirations = expirations;
        this.invalidations = invalidations;
        this.placeEntries = placeEntries;
        this.userEntries = userEntries;
    }

    /**
     * Gets the number of lookups answered from the cache without touching the database.
     *
     * @return the hit count
     */
    public long getHits() {
        return hits;
    }

    /**
     * Gets the number of lookups that fell through to the database, whether because the
     * key was absent or because the cached entry had expired.
     *
     * @return the miss count
     */
    public long getMisses() {
        return misses;
    }

    /**
     * Gets the number of cached entries that were found expired and discarded.
     * Every expiration also counts as a miss.
     *
     * @return the expiration count
     */
    public long getExpirations() {
        return expirations;
    }

    /**
     * Gets the number of entries evicted explicitly, either by a targeted
     * invalidation after a data modification or by {@link CachedDBConnection#clearCache()}.
     *
     * @return the invalidation count
     */
    public long getInvalidations() {
        return invalidations;
    }

    /**
     * Gets the number of place entries currently held in the cache. Entries whose TTL
     * has elapsed are still counted until the next lookup evicts them.
     *
     * @return the number of cached places
     */
    public int getPlaceEntries() {
        return placeEntries;
    }

    /**
     * Gets the number of user entries currently held in the cache. Entries whose TTL
     * has elapsed are still counted until the next lookup evicts them.
     *
     * @return the number of cached users
     */
    public int getUserEntries() {
        return userEntries;
    }

    /**
     * Gets the total number of lookups performed, which is hits plus misses.
     *
     * @return the total lookup count
     */
    public long getTotalLookups() {
        return hits + misses;
    }

    /**
     * Calculates the fraction of lookups served from the cache.
     *
     * @return a value between 0.0 and 1.0, or 0.0 when no lookup has happened yet
     */
    public double getHitRatio() {
        long total = getTotalLookups();
        if (total == 0) {
            return 0.0;
        }
        return (double) hits / total;
    }

    /**
     * Returns a one-line, human-readable summary of the counters.
     *
     * @return a formatted summary such as {@code "Cache Stats: 3 hits, 1 misses (75.00% hit rate), 2 entries cached"}
     */
    @Override
    public String toString() {
        return String.format("Cache Stats: %d hits, %d misses (%.2f%% hit rate), %d entries cached",
                hits, misses, getHitRatio() * 100, placeEntries + userEntries);
    }
}
