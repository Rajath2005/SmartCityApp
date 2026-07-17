package com.smartcity.utils;


import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9]{4,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    public static boolean isValidUsername(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}
