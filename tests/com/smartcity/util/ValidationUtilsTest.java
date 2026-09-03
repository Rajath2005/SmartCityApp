/*
 * How to run these tests:
 *
 *   mvn test                                  # run the whole test suite
 *   mvn test -Dtest=ValidationUtilsTest       # run only this class
 *
 * Requires JDK 21 and Maven. No database connection is needed.
 */
package com.smartcity.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit tests for {@link ValidationUtils}.
 *
 * <p>These pin down the registration rules so a future edit to either regex
 * cannot silently loosen or tighten what the application accepts.
 */
class ValidationUtilsTest {

    @Nested
    @DisplayName("isValidUsername")
    class IsValidUsername {

        @Test
        void validUsername_shouldReturnTrue() {
            assertTrue(ValidationUtils.isValidUsername("john123"));
        }

        @Test
        @DisplayName("letters only and digits only are both accepted")
        void alphanumericVariants_shouldReturnTrue() {
            assertTrue(ValidationUtils.isValidUsername("john"));
            assertTrue(ValidationUtils.isValidUsername("1234"));
            assertTrue(ValidationUtils.isValidUsername("JohnDoe"));
        }

        @Test
        @DisplayName("the 4 and 20 character boundaries are inclusive")
        void lengthBoundaries_shouldBeInclusive() {
            assertTrue(ValidationUtils.isValidUsername("abcd"), "4 chars should be valid");
            assertTrue(ValidationUtils.isValidUsername("a".repeat(20)), "20 chars should be valid");
        }

        @Test
        void tooShortUsername_shouldReturnFalse() {
            assertFalse(ValidationUtils.isValidUsername("ab"));
            assertFalse(ValidationUtils.isValidUsername("abc"), "3 chars is below the minimum");
        }

        @Test
        void tooLongUsername_shouldReturnFalse() {
            assertFalse(ValidationUtils.isValidUsername("a".repeat(21)));
        }

        @Test
        void usernameWithSpecialChars_shouldReturnFalse() {
            assertFalse(ValidationUtils.isValidUsername("john@123"));
        }

        @ParameterizedTest
        @DisplayName("underscores, spaces, hyphens and dots are rejected")
        @ValueSource(strings = {"john_123", "john 123", "john-123", "john.123", "john!"})
        void usernameWithDisallowedCharacters_shouldReturnFalse(String username) {
            assertFalse(ValidationUtils.isValidUsername(username));
        }

        @ParameterizedTest
        @NullSource
        @EmptySource
        void nullOrEmptyUsername_shouldReturnFalse(String username) {
            assertFalse(ValidationUtils.isValidUsername(username));
        }
    }

    @Nested
    @DisplayName("isValidPassword")
    class IsValidPassword {

        @Test
        void validPassword_shouldReturnTrue() {
            assertTrue(ValidationUtils.isValidPassword("Secure@123"));
        }

        @Test
        @DisplayName("the 8 character minimum is inclusive")
        void lengthBoundary_shouldBeInclusive() {
            assertTrue(ValidationUtils.isValidPassword("Ab1@cdef"), "8 chars should be valid");
        }

        @Test
        void passwordWithNoUppercase_shouldReturnFalse() {
            assertFalse(ValidationUtils.isValidPassword("secure@123"));
        }

        @Test
        void passwordWithNoLowercase_shouldReturnFalse() {
            assertFalse(ValidationUtils.isValidPassword("SECURE@123"));
        }

        @Test
        void passwordWithNoDigit_shouldReturnFalse() {
            assertFalse(ValidationUtils.isValidPassword("Secure@abc"));
        }

        @Test
        void passwordWithNoSpecialCharacter_shouldReturnFalse() {
            assertFalse(ValidationUtils.isValidPassword("Secure123"));
        }

        @Test
        void passwordTooShort_shouldReturnFalse() {
            assertFalse(ValidationUtils.isValidPassword("Ab@1"));
        }

        @ParameterizedTest
        @DisplayName("every accepted special character satisfies the rule")
        @ValueSource(strings = {"Secure@123", "Secure$123", "Secure!123", "Secure%123",
            "Secure*123", "Secure?123", "Secure&123"})
        void passwordWithEachAllowedSpecialCharacter_shouldReturnTrue(String password) {
            assertTrue(ValidationUtils.isValidPassword(password));
        }

        @ParameterizedTest
        @DisplayName("special characters outside the allowed set are rejected")
        @ValueSource(strings = {"Secure#123", "Secure^123", "Secure(123", "Secure 123"})
        void passwordWithDisallowedSpecialCharacter_shouldReturnFalse(String password) {
            assertFalse(ValidationUtils.isValidPassword(password));
        }

        @ParameterizedTest
        @NullSource
        @EmptySource
        void nullOrEmptyPassword_shouldReturnFalse(String password) {
            assertFalse(ValidationUtils.isValidPassword(password));
        }
    }
}
