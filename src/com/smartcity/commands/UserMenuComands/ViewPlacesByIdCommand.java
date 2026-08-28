package com.smartcity.commands.UserMenuComands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.smartcity.commands.Command;
import com.smartcity.main.SmartCityApp;

public class ViewPlacesByIdCommand implements Command {
    @Override
    public void execute() {
        //SmartCityApp.viewAllPlacesSortedById();
        String query = "SELECT * FROM places ORDER BY id ASC";

        try (Connection connection = SmartCityApp.getConnectionOrPrintError()) {
            if (connection == null) {
                return;
            }

            try (PreparedStatement pstmt = connection.prepareStatement(query);
                 ResultSet resultSet = pstmt.executeQuery()) {

                System.out.println("\n🏙️  ===== ALL CITY ATTRACTIONS (BY ID) =====");
                System.out.println("-".repeat(50));
                SmartCityApp.placeResultPrintout(resultSet);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to fetch places from database.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }
}