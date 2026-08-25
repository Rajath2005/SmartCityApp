package com.smartcity.commands.AdminMenuCommands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.smartcity.commands.Command;
import com.smartcity.db.DBConnection;
import com.smartcity.main.SmartCityApp;

public class AddPlaceCommand implements Command {

    private static final String INSERT_PLACE_QUERY =
            "INSERT INTO places "
                    + "(id, name, category, location, description, latitude, longitude) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private final Scanner scanner;

    public AddPlaceCommand(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("\n--- Add New Place ---");

        System.out.print("Enter place ID: ");
        int id;

        try {
            id = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("❌ Invalid ID. Please enter a number.");
            scanner.nextLine();
            return;
        }

        System.out.print("Enter place name: ");
        String name = scanner.nextLine();

        while (!SmartCityApp.isValidPlaceName(name)) {
            System.out.println("❌ Error: Place name cannot be empty.");
            System.out.print("Enter place name: ");
            name = scanner.nextLine();
        }

        System.out.print("Enter category (e.g., Hotel, Restaurant, Park): ");
        String category = scanner.nextLine();

        while (!SmartCityApp.isValidCategory(category)) {
            System.out.println("❌ Error: Category cannot be empty.");
            System.out.print("Enter category (e.g., Hotel, Restaurant, Park): ");
            category = scanner.nextLine();
        }

        System.out.print("Enter location: ");
        String location = scanner.nextLine();

        while (!SmartCityApp.isValidLocation(location)) {
            System.out.println("❌ Error: Location cannot be empty.");
            System.out.print("Enter location: ");
            location = scanner.nextLine();
        }

        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        System.out.print("Enter place latitude: ");
        double latitude;

        try {
            latitude = scanner.nextDouble();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("❌ Invalid latitude. Please enter a valid number.");
            scanner.nextLine();
            return;
        }

        System.out.print("Enter place longitude: ");
        double longitude;

        try {
            longitude = scanner.nextDouble();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("❌ Invalid longitude. Please enter a valid number.");
            scanner.nextLine();
            return;
        }

        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null) {
                SmartCityApp.getConnectionOrPrintError();
                return;
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(INSERT_PLACE_QUERY)) {

                statement.setInt(1, id);
                statement.setString(2, name);
                statement.setString(3, category);
                statement.setString(4, location);
                statement.setString(5, description);
                statement.setDouble(6, latitude);
                statement.setDouble(7, longitude);

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println(
                            "✅ Success! Place '" + name + "' has been added to the city.");
                } else {
                    System.out.println("❌ Error: Failed to add place. Please try again.");
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to add new place to database.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }

    
}