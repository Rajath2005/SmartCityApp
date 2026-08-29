package com.smartcity.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Place} model class.
 *
 * <p>Covers both constructors, every getter and setter, and the contract that
 * {@code toString()} renders all fields without leaking literal {@code "null"}.
 */
class PlaceTest {

    /** Tolerance for comparing latitude/longitude values. */
    private static final double DELTA = 1e-9;

    private Place place;

    @BeforeEach
    void setUp() {
        place = new Place(1, "Central Park", "Park", "Downtown", "A beautiful green space");
    }

    @Nested
    @DisplayName("Constructors")
    class Constructors {

        @Test
        @DisplayName("legacy constructor populates every descriptive field")
        void legacyConstructor_shouldPopulateDescriptiveFields() {
            assertEquals(1, place.getId());
            assertEquals("Central Park", place.getName());
            assertEquals("Park", place.getCategory());
            assertEquals("Downtown", place.getLocation());
            assertEquals("A beautiful green space", place.getDescription());
        }

        @Test
        @DisplayName("legacy constructor defaults coordinates to the -1 sentinel")
        void legacyConstructor_shouldDefaultCoordinatesToMinusOne() {
            assertEquals(-1.0, place.getLatitude(), DELTA);
            assertEquals(-1.0, place.getLongitude(), DELTA);
        }

        @Test
        @DisplayName("full constructor stores the supplied coordinates")
        void fullConstructor_shouldStoreCoordinates() {
            Place located = new Place(2, "City Museum", "Museum", "Old Town",
                    "Local history exhibits", 12.9716, 77.5946);

            assertEquals(2, located.getId());
            assertEquals("City Museum", located.getName());
            assertEquals(12.9716, located.getLatitude(), DELTA);
            assertEquals(77.5946, located.getLongitude(), DELTA);
        }
    }

    @Nested
    @DisplayName("Getters and setters")
    class Accessors {

        @Test
        void getId_shouldReturn1() {
            assertEquals(1, place.getId());
        }

        @Test
        void getName_shouldReturnCentralPark() {
            assertEquals("Central Park", place.getName());
        }

        @Test
        void setId_shouldUpdateId() {
            place.setId(42);
            assertEquals(42, place.getId());
        }

        @Test
        void setName_shouldUpdateName() {
            place.setName("City Garden");
            assertEquals("City Garden", place.getName());
        }

        @Test
        void setCategory_shouldUpdateCategory() {
            place.setCategory("Garden");
            assertEquals("Garden", place.getCategory());
        }

        @Test
        void setLocation_shouldUpdateLocation() {
            place.setLocation("Uptown");
            assertEquals("Uptown", place.getLocation());
        }

        @Test
        void setDescription_shouldUpdateDescription() {
            place.setDescription("Renovated in 2024");
            assertEquals("Renovated in 2024", place.getDescription());
        }

        @Test
        void setLatitude_shouldUpdateLatitude() {
            place.setLatitude(51.5074);
            assertEquals(51.5074, place.getLatitude(), DELTA);
        }

        @Test
        void setLongitude_shouldUpdateLongitude() {
            place.setLongitude(-0.1278);
            assertEquals(-0.1278, place.getLongitude(), DELTA);
        }
    }

    @Nested
    @DisplayName("toString()")
    class ToString {

        @Test
        void toString_shouldContainName() {
            assertTrue(place.toString().contains("Central Park"));
        }

        @Test
        void toString_shouldContainEveryDescriptiveField() {
            String rendered = place.toString();

            assertTrue(rendered.contains("id=1"), "id missing from: " + rendered);
            assertTrue(rendered.contains("Park"), "category missing from: " + rendered);
            assertTrue(rendered.contains("Downtown"), "location missing from: " + rendered);
            assertTrue(rendered.contains("A beautiful green space"),
                    "description missing from: " + rendered);
        }

        @Test
        void toString_shouldContainCoordinates() {
            place.setLatitude(12.9716);
            place.setLongitude(77.5946);
            String rendered = place.toString();

            assertTrue(rendered.contains("latitude=12.9716"), "latitude missing from: " + rendered);
            assertTrue(rendered.contains("longitude=77.5946"), "longitude missing from: " + rendered);
        }

        @Test
        void toString_shouldNotContainNull() {
            assertFalse(place.toString().contains("null"));
        }

        @Test
        void toString_shouldReflectUpdatedName() {
            place.setName("City Garden");
            String rendered = place.toString();

            assertTrue(rendered.contains("City Garden"));
            assertFalse(rendered.contains("Central Park"));
        }
    }
}
