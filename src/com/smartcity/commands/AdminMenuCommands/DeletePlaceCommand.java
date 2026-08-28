package com.smartcity.commands.AdminMenuCommands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.smartcity.commands.Command;
import com.smartcity.main.SmartCityApp;

public class DeletePlaceCommand implements Command {

    private static final String DELETE_PLACE_QUERY = "DELETE FROM places WHERE id = ?";
    private final Scanner scanner;

    public DeletePlaceCommand(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("\n--- Delete Place ---");

        // Ask admin for place ID to delete
        System.out.print("Enter place ID to delete: ");
        int placeId;
        try {
            placeId = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("❌ Invalid ID. Please enter a number.");
            scanner.nextLine(); // Clear newline from input buffer
            return;
        }

        try (Connection connection = SmartCityApp.getConnectionOrPrintError()) {
            if (connection == null) {
                return;
            }

            try (PreparedStatement pstmt = connection.prepareStatement(DELETE_PLACE_QUERY)) {
                pstmt.setInt(1, placeId);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("✅ Success! Place with ID " + placeId + " has been deleted.");
                } else {
                    System.out.println("❌ Error: Place with ID " + placeId + " not found.");
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to delete place from database.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }
}