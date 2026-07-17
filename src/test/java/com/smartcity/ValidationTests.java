package com.smartcity;

import com.smartcity.utils.ValidationUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ValidationTests {
    //----USERNAME TESTS---
    @Test
    void emptyUsername_ShouldReturnFalse(){
        assertFalse(ValidationUtils.isValidUsername(""));
    }
    @Test
    void nullUsername_ShouldReturnFalse(){
        assertFalse(ValidationUtils.isValidUsername(null));
    }
    @Test
    void validUsername_ShouldReturnTrue() {
        assertTrue(ValidationUtils.isValidUsername("Username1234"));
    }
    @Test
    void shortUsername_ShouldReturnFalse() {
        assertFalse(ValidationUtils.isValidUsername("a24"));
    }

    @Test
    void longUsername_ShouldReturnFalse() {
        assertFalse(ValidationUtils.isValidUsername("AverylongUsername1234"));
    }

    @Test
    void usernameWithSpecialChars_ShouldReturnFalse() {
        assertFalse(ValidationUtils.isValidUsername("user-name"));
    }

    //-----PASSWORD TESTS-----
    @Test
    void validPassword_ShouldReturnTrue() {
        assertTrue(ValidationUtils.isValidPassword("SecurePass123!"));
    }

    @Test
    void shortPassword_ShouldReturnFalse() {
        assertFalse(ValidationUtils.isValidPassword("Ab1!cd2"));
    }

    @Test
    void passwordMissingNumbers_ShouldReturnFalse() {
        assertFalse(ValidationUtils.isValidPassword("NoNumbersHere!"));
    }
    @Test
    void passwordMissingLowercase_ShouldReturnFalse() {
        assertFalse(ValidationUtils.isValidPassword("SECUREPASS123!"));
    }

    @Test
    void passwordMissingUppercase_ShouldReturnFalse() {
        assertFalse(ValidationUtils.isValidPassword("securepass123!"));
    }

    @Test
    void passwordWithUnsupportedSpecialChar_ShouldReturnFalse() {
        assertFalse(ValidationUtils.isValidPassword("SecurePass123#")); // the symbol '#' is not in the allowed regex set
    }

    @Test
    void emptyPassword_ShouldReturnFalse() {
        assertFalse(ValidationUtils.isValidPassword(""));
    }

    @Test
    void nullPassword_ShouldReturnFalse() {
        assertFalse(ValidationUtils.isValidPassword(null));
    }
}
