package com.smartcity.db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handles the database connection for the Smart City Guide application.
 * This class uses the Singleton pattern approach for providing a connection
 * to the MySQL database. Connection settings are read from environment
 * variables so the app can run unattended (e.g. inside Docker).
 *
 * @author Rajath2005 (Original Creator)
 * @version 1.0
 */
public class DBConnection {
    // Database credentials, overridable via environment variables
    private static final String DB_HOST = getEnv("DB_HOST", "localhost");
    private static final String DB_PORT = getEnv("DB_PORT", "3306");
    private static final String DB_NAME = getEnv("DB_NAME", "smart_city_guide");
    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
    private static final String DB_USER = getEnv("DB_USER", "root");
    private static final String DB_PASSWORD = getEnv("DB_PASSWORD", "");

    private static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.isEmpty()) ? defaultValue : value;
    }

    /**
     * Static initialization block.
     * This block is executed once when the class is first loaded into memory.
     * It loads the MySQL JDBC driver.
     */
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
    }

    /**
     * Establishes and returns a connection to the MySQL database.
     * If the connection fails, it catches the SQLException and returns null.
     * 
     * Note: Callers are responsible for closing this connection (preferably
     * using try-with-resources) to prevent resource leaks.
     *
     * @return A valid {@link Connection} object, or null if connection fails.
     */
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
