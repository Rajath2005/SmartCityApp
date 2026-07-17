package com.smartcity.model;

/**
 * Represents a User in the Smart City Guide application.
 * Users can either be regular users with basic access or
 * administrators with elevated privileges to manage the city.
 *
 * @author Rajath2005 (Original Creator)
 * @version 1.0
 */
public class User {

    private String username;
    private String password;
    private String role;

    /**
     * Constructs a new User object.
     * By default, newly created users are assigned the "USER" role.
     *
     * @param username The unique username of the user.
     * @param password The user's password (Note: currently stored in plain text).
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.role = "USER";
    }

    /**
     * Gets the username of the user.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username The new username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password of the user.
     * Note: In a production environment, this should return a hashed string, not plain text.
     *
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    // No public setPassword() as per issue requirements

    /**
     * Gets the role of the user (e.g., "USER", "ADMIN").
     *
     * @return The user's role.
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role of the user.
     * This is typically used when elevating a user to an administrator.
     *
     * @param role The new role (e.g., "ADMIN").
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Returns a safe string representation of the User object,
     * specifically ensuring the password is not logged or printed in plain text.
     *
     * @return A formatted string detailing the user's non-sensitive attributes.
     */
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                ", role='" + role + '\'' +
                '}';
    }
}