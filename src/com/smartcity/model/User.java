package com.smartcity.model;

public class User {
    public String username;
    public String password;
    public String role;

    // Constructor to create a new user
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.role = "USER"; // Default role
    }
}
