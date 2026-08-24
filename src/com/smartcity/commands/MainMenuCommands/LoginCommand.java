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
import com.smartcity.main.SmartCityApp;

public class LoginCommand implements Command {

    private static final Scanner scanner = new Scanner(System.in);

    private static final String LOGIN_QUERY =
            "SELECT role FROM users WHERE username = ? AND password = ?";

    @Override
    public void execute() throws Exception {
        System.out.println("\n--- Login ---");

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null) {
                System.out.println("❌ Could not connect to the database.");
                return;
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(LOGIN_QUERY)) {

                statement.setString(1, username);
                statement.setString(2, hashPassword(password));

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String role = resultSet.getString("role");

                        System.out.println(
                                "✅ Success! Welcome back, " + username + "!");

                        if ("ADMIN".equals(role)) {
                            SmartCityApp.showAdminMenu(username);
                        } else {
                            SmartCityApp.showUserMenu(username);
                        }
                    } else {
                        System.out.println(
                                "❌ Error: Username or password incorrect. "
                                        + "Please try again.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to login user.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                    password.getBytes(StandardCharsets.UTF_8));

            StringBuilder result = new StringBuilder();
            for (byte value : hash) {
                result.append(String.format("%02x", value));
            }
            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }
}