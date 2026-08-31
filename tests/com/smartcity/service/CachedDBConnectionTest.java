/*
 * How to run these tests:
 *
 *   mvn test                                   # run the whole test suite
 *   mvn test -Dtest=CachedDBConnectionTest     # run only this class
 *
 * Requires JDK 21 and Maven. No database connection is needed: every test
 * drives the cache through in-memory loaders and a fake clock, so TTL expiry
 * is exercised without a single Thread.sleep.
 */
package com.smartcity.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.function.LongSupplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.smartcity.model.Place;
import com.smartcity.model.User;

/**
 * Unit tests for {@link CachedDBConnection}.
 *
 * <p>These pin down the cache-aside contract: what is served from memory, what
 * falls through to the loader, when an entry stops being fresh, and what the
 * reported metrics mean. A future change that quietly stops invalidating on a
 * write, or that starts caching "not found", will fail here.
 */
class CachedDBConnectionTest {

    private static final long TTL_MILLIS = 1_000L;

    private CountingPlaceLoader placeLoader;
    private CountingUserLoader userLoader;
    private MutableClock clock;
    private CachedDBConnection cache;

    @BeforeEach
    void setUp() {
        placeLoader = new CountingPlaceLoader();
        userLoader = new CountingUserLoader();
        clock = new MutableClock(0L);
        cache = new CachedDBConnection(TTL_MILLIS, placeLoader, userLoader, clock);

        placeLoader.store(place(1, "City Museum"));
        placeLoader.store(place(2, "Riverside Park"));
        userLoader.store(7, user("alice"));
        userLoader.store(8, user("bob"));
    }

    @Nested
    @DisplayName("cache misses")
    class Misses {

        @Test
        @DisplayName("the first lookup of a place goes to the loader and is recorded as a miss")
        void firstPlaceLookup_shouldMissAndLoad() {
            Place loaded = cache.getPlace(1);

            assertNotNull(loaded);
            assertEquals("City Museum", loaded.getName());
            assertEquals(1, placeLoader.callCount(), "the loader should have been consulted once");

            CacheStats stats = cache.getCacheStats();
            assertEquals(0, stats.getHits());
            assertEquals(1, stats.getMisses());
            assertEquals(1, stats.getPlaceEntries(), "the loaded place should now be cached");
        }

        @Test
        @DisplayName("the first lookup of a user goes to the loader and is recorded as a miss")
        void firstUserLookup_shouldMissAndLoad() {
            User loaded = cache.getUser(7);

            assertNotNull(loaded);
            assertEquals("alice", loaded.getUsername());
            assertEquals(1, userLoader.callCount());

            CacheStats stats = cache.getCacheStats();
            assertEquals(0, stats.getHits());
            assertEquals(1, stats.getMisses());
            assertEquals(1, stats.getUserEntries());
        }

        @Test
        @DisplayName("a place that does not exist returns null and is never cached")
        void unknownPlace_shouldNotBeCached() {
            assertNull(cache.getPlace(404));
            assertNull(cache.getPlace(404));

            assertEquals(2, placeLoader.callCount(), "a missing row must be retried, not remembered");

            CacheStats stats = cache.getCacheStats();
            assertEquals(0, stats.getHits());
            assertEquals(2, stats.getMisses());
            assertEquals(0, stats.getPlaceEntries());
        }

        @Test
        @DisplayName("a row created after a failed lookup is picked up on the next read")
        void placeAddedAfterMiss_shouldBeVisible() {
            assertNull(cache.getPlace(3));

            placeLoader.store(place(3, "Harbour Lights"));

            Place loaded = cache.getPlace(3);
            assertNotNull(loaded, "a cached null would have hidden the new row");
            assertEquals("Harbour Lights", loaded.getName());
        }

        @Test
        @DisplayName("a user that does not exist returns null and is never cached")
        void unknownUser_shouldNotBeCached() {
            assertNull(cache.getUser(404));
            assertNull(cache.getUser(404));

            assertEquals(2, userLoader.callCount());
            assertEquals(0, cache.getCacheStats().getUserEntries());
        }
    }

    @Nested
    @DisplayName("cache hits")
    class Hits {

        @Test
        @DisplayName("a repeated place lookup is served from memory without touching the loader")
        void repeatedPlaceLookup_shouldHit() {
            Place first = cache.getPlace(1);
            Place second = cache.getPlace(1);

            assertSame(first, second, "a hit should return the very object that was cached");
            assertEquals(1, placeLoader.callCount(), "the loader must not be consulted a second time");

            CacheStats stats = cache.getCacheStats();
            assertEquals(1, stats.getHits());
            assertEquals(1, stats.getMisses());
        }

        @Test
        @DisplayName("a repeated user lookup is served from memory without touching the loader")
        void repeatedUserLookup_shouldHit() {
            User first = cache.getUser(7);
            User second = cache.getUser(7);

            assertSame(first, second);
            assertEquals(1, userLoader.callCount());
            assertEquals(1, cache.getCacheStats().getHits());
        }

        @Test
        @DisplayName("places and users with the same ID are cached independently")
        void placeAndUserKeys_shouldNotCollide() {
            placeLoader.store(place(7, "Seven Sisters"));

            Place cachedPlace = cache.getPlace(7);
            User cachedUser = cache.getUser(7);

            assertEquals("Seven Sisters", cachedPlace.getName());
            assertEquals("alice", cachedUser.getUsername());

            CacheStats stats = cache.getCacheStats();
            assertEquals(1, stats.getPlaceEntries());
            assertEquals(1, stats.getUserEntries());
        }

        @Test
        @DisplayName("the hit rate rises as the same place is read again and again")
        void hitRatio_shouldReflectRepeatedReads() {
            cache.getPlace(1);
            cache.getPlace(1);
            cache.getPlace(1);
            cache.getPlace(1);

            CacheStats stats = cache.getCacheStats();
            assertEquals(3, stats.getHits());
            assertEquals(1, stats.getMisses());
            assertEquals(4, stats.getTotalLookups());
            assertEquals(0.75, stats.getHitRatio(), 1e-9);
        }
    }

    @Nested
    @DisplayName("TTL expiration")
    class Expiration {

        @Test
        @DisplayName("an entry stays fresh for the whole TTL window")
        void justBeforeExpiry_shouldStillHit() {
            cache.getPlace(1);
            clock.advance(TTL_MILLIS - 1);

            cache.getPlace(1);

            assertEquals(1, placeLoader.callCount(), "the entry should not have expired yet");
            assertEquals(1, cache.getCacheStats().getHits());
        }

        @Test
        @DisplayName("once the TTL elapses the entry is discarded and reloaded")
        void afterExpiry_shouldMissAndReload() {
            cache.getPlace(1);
            clock.advance(TTL_MILLIS);

            cache.getPlace(1);

            assertEquals(2, placeLoader.callCount(), "an expired entry must be reloaded");

            CacheStats stats = cache.getCacheStats();
            assertEquals(0, stats.getHits());
            assertEquals(2, stats.getMisses());
            assertEquals(1, stats.getExpirations());
        }

        @Test
        @DisplayName("an expired entry serves the current data, not the stale copy")
        void afterExpiry_shouldServeFreshData() {
            assertEquals("City Museum", cache.getPlace(1).getName());

            placeLoader.store(place(1, "City Museum (renovated)"));
            clock.advance(TTL_MILLIS + 1);

            assertEquals("City Museum (renovated)", cache.getPlace(1).getName());
        }

        @Test
        @DisplayName("the reloaded entry gets a fresh TTL window of its own")
        void reloadedEntry_shouldGetANewWindow() {
            cache.getPlace(1);
            clock.advance(TTL_MILLIS);
            cache.getPlace(1);

            clock.advance(TTL_MILLIS - 1);
            cache.getPlace(1);

            assertEquals(2, placeLoader.callCount(), "the refreshed entry should still be fresh");
            assertEquals(1, cache.getCacheStats().getHits());
        }

        @Test
        @DisplayName("user entries expire on the same terms as place entries")
        void users_shouldExpireToo() {
            cache.getUser(7);
            clock.advance(TTL_MILLIS + 1);
            cache.getUser(7);

            assertEquals(2, userLoader.callCount());
            assertEquals(1, cache.getCacheStats().getExpirations());
        }

        @Test
        @DisplayName("isPlaceCached reports an expired entry as not cached")
        void isPlaceCached_shouldRespectTtl() {
            cache.getPlace(1);
            assertTrue(cache.isPlaceCached(1));

            clock.advance(TTL_MILLIS);
            assertFalse(cache.isPlaceCached(1));
        }
    }

    @Nested
    @DisplayName("invalidation")
    class Invalidation {

        @Test
        @DisplayName("invalidating a place forces the next read back to the loader")
        void invalidatePlace_shouldEvictTheEntry() {
            cache.getPlace(1);
            assertTrue(cache.invalidatePlace(1), "the entry was cached, so it should have been removed");

            cache.getPlace(1);

            assertEquals(2, placeLoader.callCount());
            assertEquals(0, cache.getCacheStats().getHits());
            assertEquals(1, cache.getCacheStats().getInvalidations());
        }

        @Test
        @DisplayName("an update followed by invalidation is visible immediately, without waiting for the TTL")
        void invalidateAfterUpdate_shouldServeFreshData() {
            assertEquals("City Museum", cache.getPlace(1).getName());

            // Simulates the admin flow: the row is updated, then its key is evicted.
            placeLoader.store(place(1, "City Museum (east wing)"));
            cache.invalidatePlace(1);

            assertEquals("City Museum (east wing)", cache.getPlace(1).getName());
        }

        @Test
        @DisplayName("a deleted place stops being served once its key is invalidated")
        void invalidateAfterDelete_shouldStopServingTheRow() {
            assertNotNull(cache.getPlace(1));

            placeLoader.remove(1);
            cache.invalidatePlace(1);

            assertNull(cache.getPlace(1), "a deleted row must not survive in the cache");
        }

        @Test
        @DisplayName("invalidating an uncached key changes nothing and is not counted")
        void invalidateUncachedKey_shouldBeANoOp() {
            assertFalse(cache.invalidatePlace(99));
            assertFalse(cache.invalidateUser(99));

            assertEquals(0, cache.getCacheStats().getInvalidations());
        }

        @Test
        @DisplayName("invalidating one place leaves the others cached")
        void invalidatePlace_shouldNotTouchOtherEntries() {
            cache.getPlace(1);
            cache.getPlace(2);

            cache.invalidatePlace(1);

            assertFalse(cache.isPlaceCached(1));
            assertTrue(cache.isPlaceCached(2));
        }

        @Test
        @DisplayName("invalidating a user forces the next read back to the loader")
        void invalidateUser_shouldEvictTheEntry() {
            cache.getUser(7);
            assertTrue(cache.invalidateUser(7));

            cache.getUser(7);

            assertEquals(2, userLoader.callCount());
            assertEquals(1, cache.getCacheStats().getInvalidations());
        }
    }

    @Nested
    @DisplayName("clearCache and resetStats")
    class Clearing {

        @Test
        @DisplayName("clearCache empties both maps and counts every entry it dropped")
        void clearCache_shouldEmptyBothMaps() {
            cache.getPlace(1);
            cache.getPlace(2);
            cache.getUser(7);

            cache.clearCache();

            CacheStats stats = cache.getCacheStats();
            assertEquals(0, stats.getPlaceEntries());
            assertEquals(0, stats.getUserEntries());
            assertEquals(3, stats.getInvalidations());
            assertFalse(cache.isPlaceCached(1));
            assertFalse(cache.isUserCached(7));
        }

        @Test
        @DisplayName("clearCache keeps the hit and miss history")
        void clearCache_shouldPreserveCounters() {
            cache.getPlace(1);
            cache.getPlace(1);

            cache.clearCache();

            CacheStats stats = cache.getCacheStats();
            assertEquals(1, stats.getHits());
            assertEquals(1, stats.getMisses());
        }

        @Test
        @DisplayName("reads after a clear go back to the loader")
        void clearCache_shouldForceReload() {
            cache.getPlace(1);
            cache.clearCache();
            cache.getPlace(1);

            assertEquals(2, placeLoader.callCount());
        }

        @Test
        @DisplayName("clearing an empty cache counts nothing")
        void clearEmptyCache_shouldCountNothing() {
            cache.clearCache();

            assertEquals(0, cache.getCacheStats().getInvalidations());
        }

        @Test
        @DisplayName("resetStats zeroes the counters but keeps the cached entries")
        void resetStats_shouldKeepEntries() {
            cache.getPlace(1);
            cache.getPlace(1);

            cache.resetStats();

            CacheStats stats = cache.getCacheStats();
            assertEquals(0, stats.getHits());
            assertEquals(0, stats.getMisses());
            assertEquals(1, stats.getPlaceEntries(), "the entry itself should survive a stats reset");

            cache.getPlace(1);
            assertEquals(1, cache.getCacheStats().getHits());
            assertEquals(1, placeLoader.callCount());
        }
    }

    @Nested
    @DisplayName("metrics reporting")
    class Metrics {

        @Test
        @DisplayName("isPlaceCached and isUserCached do not count as lookups")
        void cacheProbes_shouldNotRecordStats() {
            cache.isPlaceCached(1);
            cache.isUserCached(7);

            CacheStats stats = cache.getCacheStats();
            assertEquals(0, stats.getTotalLookups(), "probing must not distort the hit rate");
        }

        @Test
        @DisplayName("a stats snapshot is frozen at the moment it was taken")
        void statsSnapshot_shouldNotChangeAfterwards() {
            cache.getPlace(1);
            CacheStats snapshot = cache.getCacheStats();

            cache.getPlace(1);
            cache.getPlace(2);

            assertEquals(0, snapshot.getHits(), "the older snapshot should not have moved");
            assertEquals(1, snapshot.getMisses());
            assertEquals(1, snapshot.getPlaceEntries());

            assertEquals(1, cache.getCacheStats().getHits(), "the live cache should have moved on");
        }

        @Test
        @DisplayName("the hit rate is zero before any lookup happens")
        void hitRatio_shouldBeZeroWhenIdle() {
            CacheStats stats = cache.getCacheStats();

            assertEquals(0, stats.getTotalLookups());
            assertEquals(0.0, stats.getHitRatio(), 1e-9);
        }
    }

    @Nested
    @DisplayName("construction")
    class Construction {

        @Test
        @DisplayName("the default constructor uses a five-minute TTL")
        void defaultConstructor_shouldUseDefaultTtl() {
            assertEquals(CachedDBConnection.DEFAULT_TTL_MILLIS, new CachedDBConnection().getTtlMillis());
        }

        @Test
        @DisplayName("a zero or negative TTL is rejected")
        void nonPositiveTtl_shouldThrow() {
            assertThrows(IllegalArgumentException.class,
                    () -> new CachedDBConnection(0, placeLoader, userLoader));
            assertThrows(IllegalArgumentException.class,
                    () -> new CachedDBConnection(-1, placeLoader, userLoader));
        }

        @Test
        @DisplayName("null loaders and a null clock are rejected")
        void nullCollaborators_shouldThrow() {
            assertThrows(NullPointerException.class,
                    () -> new CachedDBConnection(TTL_MILLIS, null, userLoader));
            assertThrows(NullPointerException.class,
                    () -> new CachedDBConnection(TTL_MILLIS, placeLoader, null));
            assertThrows(NullPointerException.class,
                    () -> new CachedDBConnection(TTL_MILLIS, placeLoader, userLoader, null));
        }
    }

    private static Place place(int id, String name) {
        return new Place(id, name, "Museum", "Downtown", "A description.", 12.9, 77.6);
    }

    private static User user(String username) {
        return new User(username, "hashed-password");
    }

    /**
     * A clock the tests move by hand, so TTL expiry can be tested exactly and
     * instantly instead of by sleeping.
     */
    private static final class MutableClock implements LongSupplier {
        private long currentTimeMillis;

        MutableClock(long startMillis) {
            this.currentTimeMillis = startMillis;
        }

        void advance(long millis) {
            currentTimeMillis += millis;
        }

        @Override
        public long getAsLong() {
            return currentTimeMillis;
        }
    }

    /**
     * A stand-in for the database that serves places from a map and counts how
     * often it was asked — which is how the tests tell a hit from a miss.
     */
    private static final class CountingPlaceLoader implements CachedDBConnection.PlaceLoader {
        private final Map<Integer, Place> rows = new HashMap<>();
        private int calls;

        void store(Place place) {
            rows.put(place.getId(), place);
        }

        void remove(int placeId) {
            rows.remove(placeId);
        }

        int callCount() {
            return calls;
        }

        @Override
        public Place load(int placeId) {
            calls++;
            return rows.get(placeId);
        }
    }

    /**
     * The user-side equivalent of {@link CountingPlaceLoader}.
     */
    private static final class CountingUserLoader implements CachedDBConnection.UserLoader {
        private final Map<Integer, User> rows = new HashMap<>();
        private int calls;

        void store(int userId, User user) {
            rows.put(userId, user);
        }

        int callCount() {
            return calls;
        }

        @Override
        public User load(int userId) {
            calls++;
            return rows.get(userId);
        }
    }
}
