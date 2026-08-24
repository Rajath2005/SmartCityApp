package com.smartcity.commands.MainMenuCommands;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.smartcity.commands.Command;
import com.smartcity.db.DBConnection;
import com.smartcity.service.EmailService;

public class RegisterCommand implements Command {

    private static final Scanner scanner = new Scanner(System.in);

    private static final String CHECK_USERNAME_EXISTS_QUERY =
            "SELECT id FROM users WHERE username = ?";

    private static final String INSERT_USER_QUERY =
            "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)";

    @Override
    public void execute() throws Exception {
        System.out.println("\n--- Registration ---");

        System.out.print("Enter username (4-20 alphanumeric characters): ");
        String username = scanner.nextLine();

        while (!isValidUsername(username)) {
            System.out.println("Invalid username. Please try again.");
            System.out.print("Enter username (4-20 alphanumeric characters): ");
            username = scanner.nextLine();
        }

        System.out.print("Enter password (min 8 chars, 1 uppercase, 1 lowercase, 1 number, 1 special char): ");
        String password = scanner.nextLine();

        while (!isValidPassword(password)) {
            if (password.length() < 8) {
                System.out.println("Password is too short. Minimum 8 characters required.");
            } else {
                System.out.println("Invalid password. Please try again.");
            }

            System.out.print("Enter password (min 8 chars, 1 uppercase, 1 lowercase, 1 number, 1 special char): ");
            password = scanner.nextLine();
        }

        System.out.print("Enter your email: ");
        String email = scanner.nextLine();

        while (!isValidEmail(email)) {
            System.out.println("Invalid Email. Please try again.");
            email = scanner.nextLine();
        }

        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null) {
                System.out.println("❌ Could not connect to the database.");
                return;
            }

            try (PreparedStatement checkStatement =
                         connection.prepareStatement(CHECK_USERNAME_EXISTS_QUERY)) {

                checkStatement.setString(1, username);

                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println(
                                "❌ Error: Username already exists. Please choose a different username.");
                        return;
                    }
                }
            }

            try (PreparedStatement insertStatement =
                         connection.prepareStatement(INSERT_USER_QUERY)) {

                insertStatement.setString(1, username);
                insertStatement.setString(2, hashPassword(password));
                insertStatement.setString(3, email);
                insertStatement.setString(4, "USER");

                int rowsAffected = insertStatement.executeUpdate();

                if (rowsAffected > 0) {
                    EmailService.sendWelcomeEmail(email, username);
                    System.out.println(
                            "✅ Success! User '" + username + "' registered successfully.");
                } else {
                    System.out.println("❌ Error: Failed to register user. Please try again.");
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to register user.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }

    private static boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9]{4,20}$");
    }

    private static boolean isValidPassword(String password) {
        return password != null
                && password.matches(
                        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }

    private static boolean isValidEmail(String email) {
        return email != null
                && email.matches(
                        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@"
                                + "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(
                    password.getBytes(StandardCharsets.UTF_8));

            StringBuilder result = new StringBuilder();
            for (byte value : hash) {
                result.append(String.format("%02x", value));
            }

            return result.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to hash password", e);
        }
    }
}