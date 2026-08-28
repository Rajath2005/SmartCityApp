package com.smartcity.commands.AdminMenuCommands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.smartcity.commands.Command;
import com.smartcity.main.SmartCityApp;

public class UpdatePlaceCommand implements Command {

    private static final String UPDATE_PLACE_QUERY = "UPDATE places SET name = ?, category = ?, location = ?, description = ?, latitude = ?, longitude = ? WHERE id = ?";
    private static final String SELECT_PLACE_BY_ID_QUERY = "SELECT * FROM places WHERE id = ?";
    private final Scanner scanner;

    public UpdatePlaceCommand(Scanner scanner) {
        this.scanner = scanner;
        //this.smartCityApp = new SmartCityApp();
    }

    @Override
    public void execute() {
         System.out.println("\n--- Update Place ---");

        System.out.print("Enter place ID to update: ");
        int placeId;
        try {
            placeId = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("❌ Invalid ID. Please enter a number.");
            scanner.nextLine();
            return;
        }

        try (Connection connection = SmartCityApp.getConnectionOrPrintError()) {
            if (connection == null) {
                return;
            }

            // Fetch existing place
            String currentName, currentCategory, currentLocation, currentDescription;
            double currentLatitude, currentLongitude;
            try (PreparedStatement selectPstmt = connection.prepareStatement(SELECT_PLACE_BY_ID_QUERY)) {
                selectPstmt.setInt(1, placeId);
                try (ResultSet rs = selectPstmt.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("❌ Error: Place with ID " + placeId + " not found.");
                        return;
                    }

                    currentName = rs.getString("name");
                    currentCategory = rs.getString("category");
                    currentLocation = rs.getString("location");
                    currentDescription = rs.getString("description");
                    currentLatitude = rs.getDouble("latitude");
                    currentLongitude = rs.getDouble("longitude");
                }
            }

            System.out.println("\nCurrent details:");
            System.out.println("Name: " + currentName);
            System.out.println("Category: " + currentCategory);
            System.out.println("Location: " + currentLocation);
            System.out.println("Description: " + currentDescription);
            System.out.println("Coordinates: " + currentLatitude + ", " + currentLongitude);

            double newLatitude = currentLatitude;
            double newLongitude = currentLongitude;

            // Take new inputs
            System.out.print("\nEnter new name (or press Enter to keep current): ");
            String newName = scanner.nextLine();

            System.out.print("Enter new category (or press Enter to keep current): ");
            String newCategory = scanner.nextLine();

            System.out.print("Enter new location (or press Enter to keep current): ");
            String newLocation = scanner.nextLine();

            System.out.print("Enter new description (or press Enter to keep current): ");
            String newDescription = scanner.nextLine();

            System.out.print("Enter new latitude (or press Enter to keep current): ");
            String newLatitudeString = scanner.nextLine();

            System.out.print("Enter new longitude (or press Enter to keep current): ");
            String newLongitudeString = scanner.nextLine();

            // Use old values if input is empty
            if (newName.isEmpty()) {
                newName = currentName;
            }
            if (newCategory.isEmpty()) {
                newCategory = currentCategory;
            }
            if (newLocation.isEmpty()) {
                newLocation = currentLocation;
            }
            if (newDescription.isEmpty()) {
                newDescription = currentDescription;
            }
            if (newLatitudeString.isEmpty()) {
                newLatitude = currentLatitude;
            }
            if (newLongitudeString.isEmpty()) {
                newLongitude = currentLongitude;
            }

            // 🔥 VALIDATION
            if (newName == null || newName.trim().isEmpty()) {
                System.out.println("❌ Error: Place name cannot be empty.");
                return;
            }

            if (newLocation == null || newLocation.trim().isEmpty()) {
                System.out.println("❌ Error: Location cannot be empty.");
                return;
            }

            if (newCategory == null || newCategory.trim().isEmpty()) {
                System.out.println("❌ Error: Category cannot be empty.");
                return;
            }

            if (!newLatitudeString.trim().isEmpty()) {
                try {
                    newLatitude = Double.parseDouble(newLatitudeString);
                } catch (NumberFormatException e) {
                    System.out.println("❌ Error: Latitude is not a valid number.");
                    return;
                }
            }

            if (!newLongitudeString.trim().isEmpty()) {
                try {
                    newLongitude = Double.parseDouble(newLongitudeString);
                } catch (NumberFormatException e) {
                    System.out.println("❌ Error: Longitude is not a valid number.");
                    return;
                }
            }

            try (PreparedStatement updatePstmt = connection.prepareStatement(UPDATE_PLACE_QUERY)) {
                updatePstmt.setString(1, newName);
                updatePstmt.setString(2, newCategory);
                updatePstmt.setString(3, newLocation);
                updatePstmt.setString(4, newDescription);
                updatePstmt.setDouble(5, newLatitude);
                updatePstmt.setDouble(6, newLongitude);
                updatePstmt.setInt(7, placeId);

                int rows = updatePstmt.executeUpdate();

                if (rows > 0) {
                    System.out.println("✅ Success! Place updated successfully.");
                } else {
                    System.out.println("❌ Error: Update failed.");
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to update place.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }
}