package com.smartcity.commands.UserMenuComands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.smartcity.commands.Command;
import com.smartcity.main.SmartCityApp;

/* Command to search places by location */
public class SearchByLocationCommand implements Command {
    
    private static final String SEARCH_BY_LOCATION_QUERY = "SELECT * FROM places WHERE LOWER(location) LIKE LOWER(?)";
    
    private Scanner scanner = new Scanner(System.in);
    
    /**
     * Constructor to initialize the SearchByLocationCommand with a Scanner.
     *
     * @param scanner the Scanner object for user input
     */
    public SearchByLocationCommand(Scanner scanner) {
        this.scanner = scanner;
    }
    
    /**
     * Executes the command to search places by location.
     * Prompts the user for a location, queries the database, and displays the results.
     */
    @Override
    public void execute() {
        System.out.print("\nEnter location to search: ");
        String searchLocation = scanner.nextLine();

        try (Connection connection = SmartCityApp.getConnectionOrPrintError()) {
            if (connection == null) {
                return;
            }

            try (PreparedStatement pstmt = connection.prepareStatement(SEARCH_BY_LOCATION_QUERY)) {
                pstmt.setString(1, "%" + searchLocation + "%"); // Add wildcards for partial matching

                try (ResultSet resultSet = pstmt.executeQuery()) {
                    // Display search results
                    System.out.println("\n🔍 Search Results for Location: " + searchLocation);
                    System.out.println("-".repeat(50));
                    SmartCityApp.placeResultPrintout(resultSet);
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to search places by location.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }
}