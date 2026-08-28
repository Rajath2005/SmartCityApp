package com.smartcity.commands.UserMenuComands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.smartcity.commands.Command;
import com.smartcity.main.SmartCityApp;

/**
 * Displays all city attractions ordered alphabetically by name.
 */
public class ExploreCityAttractionsCommand implements Command {
    /**
     * Retrieves and prints all attractions from the places database table.
     *
     * <p>
     * If a database connection cannot be established, the command returns
     * without displaying any results. SQL errors are reported to standard
     * output.
     * </p>
     */
    @Override
    public void execute() {
        String query = "SELECT * FROM places ORDER BY name ASC";

        try (Connection connection = SmartCityApp.getConnectionOrPrintError()) {
            if (connection == null) {
                return;
            }

            try (PreparedStatement pstmt = connection.prepareStatement(query);
                    ResultSet resultSet = pstmt.executeQuery()) {

                // Display header
                System.out.println("\n🏙️  ===== ALL CITY ATTRACTIONS =====");
                System.out.println("-".repeat(50));
                SmartCityApp.placeResultPrintout(resultSet);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to fetch places from database.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }
}