package com.smartcity.commands.UserMenuComands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.smartcity.commands.Command;
import com.smartcity.main.SmartCityApp;

public class SearchByCategoryCommand implements Command {

    private static final String SEARCH_BY_CATEGORY_QUERY = "SELECT * FROM places WHERE category LIKE ?";
    private Scanner scanner = new Scanner(System.in);

    public SearchByCategoryCommand(Scanner scanner) {
        this.scanner = scanner;
    }
    
    @Override
    public void execute() {
        System.out.print("\nEnter category to search: ");
        String searchCategory = scanner.nextLine();

        try (Connection connection = SmartCityApp.getConnectionOrPrintError()) {
            if (connection == null) {
                return;
            }

            try (PreparedStatement pstmt = connection.prepareStatement(SEARCH_BY_CATEGORY_QUERY)) {
                pstmt.setString(1, "%" + searchCategory + "%"); // Add wildcards for partial matching

                try (ResultSet resultSet = pstmt.executeQuery()) {
                    // Display search results
                    System.out.println("\n🔍 Search Results for Category: " + searchCategory);
                    System.out.println("-".repeat(50));
                    SmartCityApp.placeResultPrintout(resultSet);
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to search places by category.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }
}