package com.smartcity.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link User} model class.
 *
 * <p>The most important guarantee exercised here is that the password never
 * escapes through {@code toString()}, since user objects are routinely printed
 * to logs and to the console.
 */
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("alice", "secret");
    }

    @Nested
    @DisplayName("Constructor and accessors")
    class Accessors {

        @Test
        void constructor_shouldStoreUsername() {
            assertEquals("alice", user.getUsername());
        }

        @Test
        void constructor_shouldStorePassword() {
            assertEquals("secret", user.getPassword());
        }

        @Test
        void defaultRole_shouldBeUser() {
            assertEquals("USER", user.getRole());
        }

        @Test
        void setUsername_shouldUpdateUsername() {
            user.setUsername("bob");
            assertEquals("bob", user.getUsername());
        }

        @Test
        void setRole_shouldPromoteToAdmin() {
            user.setRole("ADMIN");
            assertEquals("ADMIN", user.getRole());
        }

        @Test
        @DisplayName("each new user starts with the USER role independently")
        void newUsers_shouldNotShareRoleState() {
            user.setRole("ADMIN");
            User other = new User("carol", "pass");

            assertEquals("USER", other.getRole());
            assertEquals("ADMIN", user.getRole());
        }
    }

    @Nested
    @DisplayName("Password protection")
    class PasswordProtection {

        @Test
        void toString_shouldNotExposePassword() {
            assertFalse(user.toString().contains("secret"));
        }

        @Test
        void toString_shouldMaskPasswordWithPlaceholder() {
            assertTrue(user.toString().contains("[PROTECTED]"),
                    "expected a masked password in: " + user.toString());
        }

        @Test
        @DisplayName("password stays masked no matter what value it holds")
        void toString_shouldNotExposeUnusualPasswords() {
            User odd = new User("dave", "p@ssw0rd!#$");
            assertFalse(odd.toString().contains("p@ssw0rd!#$"));
        }

        @Test
        @DisplayName("no public setPassword() is exposed on the model")
        void user_shouldNotExposePublicPasswordSetter() {
            assertThrows(NoSuchMethodException.class,
                    () -> User.class.getMethod("setPassword", String.class));
        }
    }

    @Nested
    @DisplayName("toString()")
    class ToString {

        @Test
        void toString_shouldContainUsername() {
            assertTrue(user.toString().contains("alice"));
        }

        @Test
        void toString_shouldContainRole() {
            user.setRole("ADMIN");
            assertTrue(user.toString().contains("ADMIN"));
        }

        @Test
        void toString_shouldNotContainNull() {
            assertFalse(user.toString().contains("null"));
        }
    }
}
