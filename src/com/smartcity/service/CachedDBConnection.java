package com.smartcity.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.LongSupplier;

import com.smartcity.db.DBConnection;
import com.smartcity.model.Place;
import com.smartcity.model.User;

/**
 * An in-memory read cache that sits in front of {@link DBConnection} and follows the
 * cache-aside pattern: a lookup first consults a {@link HashMap}, and only on a miss
 * does it query the database and store what it found for next time.
 * <p>
 * Every cached value carries an expiry stamp, so an entry that has outlived its
 * time-to-live (TTL) is treated as absent and refreshed from the database on the next
 * lookup. Writes are never cached — after a place is added, updated, or deleted the
 * caller invalidates the affected key via {@link #invalidatePlace(int)} so the next
 * read sees the new row rather than a stale one.
 * <p>
 * Time Complexity:
 *   - Cache hit: O(1) HashMap lookup
 *   - Cache miss: O(1) plus the cost of one database query
 *   - invalidate / clear (per entry): O(1)
 * Space Complexity: O(n), where n is the number of cached entries. The cache is
 * unbounded by design; entries leave only through expiry or invalidation.
 * <p>
 * This class is deliberately <em>not</em> thread-safe: it backs a single-threaded CLI
 * and uses plain {@link HashMap}s. Sharing an instance across threads would need
 * external synchronisation or a {@code ConcurrentHashMap}.
 * <p>
 * Related Learning: LeetCode 146 "LRU Cache", LeetCode 706 "Design HashMap",
 *                   Cache-Aside Pattern
 *
 * @author SmartCityApp contributors
 * @version 1.0
 */
public class CachedDBConnection {

    /** Default time-to-live applied to cached entries: five minutes. */
    public static final long DEFAULT_TTL_MILLIS = 5 * 60 * 1000L;

    private static final String SELECT_PLACE_BY_ID_QUERY = "SELECT * FROM places WHERE id = ?";
    private static final String SELECT_USER_BY_ID_QUERY = "SELECT username, password, role FROM users WHERE id = ?";

    private final Map<Integer, CacheEntry<Place>> placeCache = new HashMap<>();
    private final Map<Integer, CacheEntry<User>> userCache = new HashMap<>();

    private final long ttlMillis;
    private final PlaceLoader placeLoader;
    private final UserLoader userLoader;
    private final LongSupplier clock;

    private long hits;
    private long misses;
    private long expirations;
    private long invalidations;

    /**
     * Loads a place from the underlying store when the cache cannot answer a lookup.
     */
    @FunctionalInterface
    public interface PlaceLoader {
        /**
         * Fetches a single place by ID.
         *
         * @param placeId the ID of the place to load
         * @return the place, or {@code null} if no such place exists or the load failed
         */
        Place load(int placeId);
    }

    /**
     * Loads a user from the underlying store when the cache cannot answer a lookup.
     */
    @FunctionalInterface
    public interface UserLoader {
        /**
         * Fetches a single user by ID.
         *
         * @param userId the ID of the user to load
         * @return the user, or {@code null} if no such user exists or the load failed
         */
        User load(int userId);
    }

    /**
     * Creates a cache in front of the application database using the default
     * five-minute TTL.
     */
    public CachedDBConnection() {
        this(DEFAULT_TTL_MILLIS);
    }

    /**
     * Creates a cache in front of the application database with a custom TTL.
     *
     * @param ttlMillis how long a cached entry stays fresh, in milliseconds
     * @throws IllegalArgumentException if {@code ttlMillis} is not positive
     */
    public CachedDBConnection(long ttlMillis) {
        this(ttlMillis,
                CachedDBConnection::loadPlaceFromDatabase,
                CachedDBConnection::loadUserFromDatabase);
    }

    /**
     * Creates a cache in front of arbitrary loaders. Useful for tests and for any
     * caller that reads places and users from somewhere other than the default
     * database queries.
     *
     * @param ttlMillis   how long a cached entry stays fresh, in milliseconds
     * @param placeLoader supplies places on a cache miss
     * @param userLoader  supplies users on a cache miss
     * @throws IllegalArgumentException if {@code ttlMillis} is not positive
     * @throws NullPointerException     if either loader is {@code null}
     */
    public CachedDBConnection(long ttlMillis, PlaceLoader placeLoader, UserLoader userLoader) {
        this(ttlMillis, placeLoader, userLoader, System::currentTimeMillis);
    }

    /**
     * Creates a cache with an injectable clock, so TTL expiry can be exercised in tests
     * without sleeping.
     *
     * @param ttlMillis   how long a cached entry stays fresh, in milliseconds
     * @param placeLoader supplies places on a cache miss
     * @param userLoader  supplies users on a cache miss
     * @param clock       source of the current time in milliseconds
     * @throws IllegalArgumentException if {@code ttlMillis} is not positive
     * @throws NullPointerException     if any loader or the clock is {@code null}
     */
    public CachedDBConnection(long ttlMillis, PlaceLoader placeLoader, UserLoader userLoader, LongSupplier clock) {
        if (ttlMillis <= 0) {
            throw new IllegalArgumentException("TTL must be positive, but was " + ttlMillis + " ms.");
        }
        if (placeLoader == null || userLoader == null) {
            throw new NullPointerException("Place and user loaders must not be null.");
        }
        if (clock == null) {
            throw new NullPointerException("Clock must not be null.");
        }
        this.ttlMillis = ttlMillis;
        this.placeLoader = placeLoader;
        this.userLoader = userLoader;
        this.clock = clock;
    }

    /**
     * Returns the place with the given ID, serving it from the cache when a fresh copy
     * is held and otherwise loading it from the database and caching the result.
     * <p>
     * A lookup that finds nothing is not cached, so a place created later is picked up
     * on the next call rather than being masked by a cached {@code null}.
     *
     * @param placeId the ID of the place to fetch
     * @return the place, or {@code null} if it does not exist or could not be loaded
     */
    public Place getPlace(int placeId) {
        CacheEntry<Place> entry = placeCache.get(placeId);

        if (entry != null) {
            if (!entry.isExpired(now())) {
                hits++;
                return entry.getValue();
            }
            // The entry outlived its TTL: drop it and fall through to the database.
            placeCache.remove(placeId);
            expirations++;
        }

        misses++;
        Place place = placeLoader.load(placeId);
        if (place != null) {
            placeCache.put(placeId, new CacheEntry<>(place, now() + ttlMillis));
        }
        return place;
    }

    /**
     * Returns the user with the given ID, serving it from the cache when a fresh copy
     * is held and otherwise loading it from the database and caching the result.
     * <p>
     * As with places, a lookup that finds nothing is not cached.
     *
     * @param userId the ID of the user to fetch
     * @return the user, or {@code null} if it does not exist or could not be loaded
     */
    public User getUser(int userId) {
        CacheEntry<User> entry = userCache.get(userId);

        if (entry != null) {
            if (!entry.isExpired(now())) {
                hits++;
                return entry.getValue();
            }
            userCache.remove(userId);
            expirations++;
        }

        misses++;
        User user = userLoader.load(userId);
        if (user != null) {
            userCache.put(userId, new CacheEntry<>(user, now() + ttlMillis));
        }
        return user;
    }

    /**
     * Reports whether a fresh copy of a place is cached, without counting the check as
     * a hit or a miss. Intended for display code that wants to say where a value came
     * from before asking for it.
     *
     * @param placeId the ID to check
     * @return true if the next {@link #getPlace(int)} would be served from the cache
     */
    public boolean isPlaceCached(int placeId) {
        CacheEntry<Place> entry = placeCache.get(placeId);
        return entry != null && !entry.isExpired(now());
    }

    /**
     * Reports whether a fresh copy of a user is cached, without counting the check as
     * a hit or a miss.
     *
     * @param userId the ID to check
     * @return true if the next {@link #getUser(int)} would be served from the cache
     */
    public boolean isUserCached(int userId) {
        CacheEntry<User> entry = userCache.get(userId);
        return entry != null && !entry.isExpired(now());
    }

    /**
     * Drops the cached copy of a place. Call this straight after a place is added,
     * updated, or deleted so the next read reflects the change instead of a stale row.
     *
     * @param placeId the ID whose cached copy should be discarded
     * @return true if an entry was actually removed
     */
    public boolean invalidatePlace(int placeId) {
        if (placeCache.remove(placeId) != null) {
            invalidations++;
            return true;
        }
        return false;
    }

    /**
     * Drops the cached copy of a user. Call this after a user's details change.
     *
     * @param userId the ID whose cached copy should be discarded
     * @return true if an entry was actually removed
     */
    public boolean invalidateUser(int userId) {
        if (userCache.remove(userId) != null) {
            invalidations++;
            return true;
        }
        return false;
    }

    /**
     * Empties both caches, counting every discarded entry as an invalidation. Hit and
     * miss totals are left untouched, so the metrics gathered so far survive a flush;
     * use {@link #resetStats()} to zero those as well.
     */
    public void clearCache() {
        invalidations += placeCache.size() + userCache.size();
        placeCache.clear();
        userCache.clear();
    }

    /**
     * Zeroes the hit, miss, expiration, and invalidation counters, leaving the cached
     * entries themselves in place. Handy for measuring one demo run in isolation.
     */
    public void resetStats() {
        hits = 0;
        misses = 0;
        expirations = 0;
        invalidations = 0;
    }

    /**
     * Takes a snapshot of the current cache metrics.
     *
     * @return an immutable {@link CacheStats} describing the cache as it stands now
     */
    public CacheStats getCacheStats() {
        return new CacheStats(hits, misses, expirations, invalidations,
                placeCache.size(), userCache.size());
    }

    /**
     * Gets the time-to-live applied to entries written by this cache.
     *
     * @return the TTL in milliseconds
     */
    public long getTtlMillis() {
        return ttlMillis;
    }

    private long now() {
        return clock.getAsLong();
    }

    /**
     * Reads one place row from the application database. Returns {@code null} — rather
     * than throwing — when the place is missing or the database is unreachable, so a
     * failed load degrades into an ordinary "not found" for the caller.
     *
     * @param placeId the ID of the place to read
     * @return the place, or {@code null} if it does not exist or the query failed
     */
    private static Place loadPlaceFromDatabase(int placeId) {
        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null) {
                return null;
            }

            try (PreparedStatement pstmt = connection.prepareStatement(SELECT_PLACE_BY_ID_QUERY)) {
                pstmt.setInt(1, placeId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    return new Place(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getString("location"),
                            rs.getString("description"),
                            rs.getDouble("latitude"),
                            rs.getDouble("longitude"));
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to load place " + placeId + " from database.");
            System.out.println("   Error message: " + e.getMessage());
            return null;
        }
    }

    /**
     * Reads one user row from the application database. Returns {@code null} when the
     * user is missing or the database is unreachable.
     *
     * @param userId the ID of the user to read
     * @return the user, or {@code null} if it does not exist or the query failed
     */
    private static User loadUserFromDatabase(int userId) {
        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null) {
                return null;
            }

            try (PreparedStatement pstmt = connection.prepareStatement(SELECT_USER_BY_ID_QUERY)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    User user = new User(rs.getString("username"), rs.getString("password"));
                    String role = rs.getString("role");
                    if (role != null && !role.isEmpty()) {
                        user.setRole(role);
                    }
                    return user;
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to load user " + userId + " from database.");
            System.out.println("   Error message: " + e.getMessage());
            return null;
        }
    }

    /**
     * A cached value paired with the instant it stops being fresh.
     *
     * @param <T> the type of the cached value
     */
    private static final class CacheEntry<T> {
        private final T value;
        private final long expiresAt;

        CacheEntry(T value, long expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
        }

        T getValue() {
            return value;
        }

        boolean isExpired(long currentTimeMillis) {
            return currentTimeMillis >= expiresAt;
        }
    }
}
