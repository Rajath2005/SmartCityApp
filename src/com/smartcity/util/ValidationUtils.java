package com.smartcity.util;

/**
 * Input validation helpers shared across the Smart City Guide application.
 *
 * <p>The rules live here rather than in the CLI controller so they can be unit
 * tested directly, without starting the application or widening the API of
 * {@code SmartCityApp}.
 */
public final class ValidationUtils {

    /** Usernames are 4-20 characters, letters and digits only. */
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9]{4,20}$";

    /**
     * Passwords are at least 8 characters and must contain a lowercase letter,
     * an uppercase letter, a digit, and one of the special characters
     * {@code @ $ ! % * ? &}.
     */
    private static final String PASSWORD_PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    /** Utility class; not meant to be instantiated. */
    private ValidationUtils() {
    }

    /**
     * Validates that a username meets the required format.
     *
     * @param username the username string to validate
     * @return true if valid (4-20 alphanumeric characters), false otherwise
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        return username.matches(USERNAME_PATTERN);
    }

    /**
     * Validates that a password meets the required strength rules.
     *
     * @param password the password string to validate
     * @return true if valid (minimum 8 characters containing at least one
     *         uppercase letter, one lowercase letter, one digit, and one
     *         special character), false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return password.matches(PASSWORD_PATTERN);
    }
}
