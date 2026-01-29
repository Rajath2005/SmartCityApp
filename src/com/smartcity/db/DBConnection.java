package com.smartcity.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class DBConnection {
    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/smart_city_guide";
    private static final String DB_USER = "root";
    private static String DB_PASSWORD = "";

    static {
        // Load MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Error: MySQL JDBC Driver not found.");
            System.out.println("   Please add mysql-connector-java JAR to your project.");
            e.printStackTrace();
        }

        // Ask developer for database password
        Scanner scanner = new Scanner(System.in);
        System.out.print("\n🔐 Enter MySQL password for user 'root': ");
        DB_PASSWORD = scanner.nextLine();
    }

    // Public static method to get database connection
    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            return connection;
        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to connect to database.");
            System.out.println("   Error message: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
