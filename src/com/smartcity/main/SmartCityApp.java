package com.smartcity.main;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.smartcity.model.User;
import com.smartcity.model.Place;
import com.smartcity.db.DBConnection;

public class SmartCityApp {
    // Scanner object shared across methods
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Smart City Guide Started Successfully");

        boolean isRunning = true;

        // Loop to repeatedly show menu until user exits
        while (isRunning) {
            displayMenu();

            // Get user's choice
            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear the newline character from input buffer

            // Handle user choice
            switch (choice) {
                case 1:
                    register();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    System.out.println("Exiting Smart City Guide. Goodbye!");
                    isRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }

    // Display menu options to user
    private static void displayMenu() {
        System.out.println("\n===== Smart City Guide Menu =====");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
    }

    // Register new user directly to MySQL database
    private static void register() {
        System.out.println("\n--- Registration ---");

        // Get username from user input
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        // Validate username is not empty
        if (username.isEmpty()) {
            System.out.println("Error: Username cannot be empty.");
            return;
        }

        // SQL query to check if username already exists
        String checkQuery = "SELECT id FROM users WHERE username = ?";

        try {
            // Get database connection
            Connection connection = DBConnection.getConnection();

            if (connection == null) {
                System.out.println("❌ Failed to connect to database.");
                return;
            }

            // Check if username already exists
            PreparedStatement checkPstmt = connection.prepareStatement(checkQuery);
            checkPstmt.setString(1, username);
            ResultSet resultSet = checkPstmt.executeQuery();

            if (resultSet.next()) {
                System.out.println("Error: Username already exists. Please choose a different username.");
                resultSet.close();
                checkPstmt.close();
                connection.close();
                return;
            }

            resultSet.close();
            checkPstmt.close();

            // Get password from user input
            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            // Validate password is not empty
            if (password.isEmpty()) {
                System.out.println("Error: Password cannot be empty.");
                connection.close();
                return;
            }

            // SQL query to insert new user with default "USER" role
            String insertQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

            // Create prepared statement for insert
            PreparedStatement insertPstmt = connection.prepareStatement(insertQuery);
            insertPstmt.setString(1, username);
            insertPstmt.setString(2, password);
            insertPstmt.setString(3, "USER"); // Default role for new users

            // Execute insert
            int rowsAffected = insertPstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Success! User '" + username + "' registered successfully.");
            } else {
                System.out.println("Error: Failed to register user. Please try again.");
            }

            // Close resources
            insertPstmt.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to register user.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }

    // Login user by validating credentials from MySQL database
    private static void login() {
        System.out.println("\n--- Login ---");

        // Get username from user input
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        // Get password from user input
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        // SQL query to fetch user by username and password
        String query = "SELECT role FROM users WHERE username = ? AND password = ?";

        try {
            // Get database connection
            Connection connection = DBConnection.getConnection();

            if (connection == null) {
                System.out.println("❌ Failed to connect to database.");
                return;
            }

            // Create prepared statement with parameter binding
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            // Execute query
            ResultSet resultSet = pstmt.executeQuery();

            // Check if user credentials match
            if (resultSet.next()) {
                // Get user role from database
                String role = resultSet.getString("role");

                System.out.println("Success! Welcome back, " + username + "!");

                // Show appropriate menu based on user role
                if (role.equals("ADMIN")) {
                    showAdminMenu(username);
                } else {
                    showUserMenu(username);
                }
            } else {
                System.out.println("Error: Username or password incorrect. Please try again.");
            }

            // Close resources
            resultSet.close();
            pstmt.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to login user.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }

    // Display admin menu with admin-specific options
    private static void showAdminMenu(String username) {
        boolean inAdminMenu = true;

        while (inAdminMenu) {
            System.out.println("\n===== Admin Menu (User: " + username + ") =====");
            System.out.println("1. View all users");
            System.out.println("2. Manage city resources");
            System.out.println("3. View system logs");
            System.out.println("4. Logout");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear newline from input buffer

            switch (choice) {
                case 1:
                    System.out.println("Viewing all registered users...");
                    break;
                case 2:
                    // Manage city resources
                    manageCityResources();
                    break;
                case 3:
                    System.out.println("Displaying system logs...");
                    break;
                case 4:
                    System.out.println("Logging out from admin account. Goodbye!");
                    inAdminMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Display user menu with regular user options
    private static void showUserMenu(String username) {
        boolean inUserMenu = true;

        while (inUserMenu) {
            System.out.println("\n===== User Menu (User: " + username + ") =====");
            System.out.println("1. Explore city attractions");
            System.out.println("2. Search places");
            System.out.println("3. View nearby services");
            System.out.println("4. Check navigation");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear newline from input buffer

            switch (choice) {
                case 1:
                    // Display all city attractions
                    viewAllPlaces();
                    break;
                case 2:
                    // Search for places
                    searchPlacesMenu();
                    break;
                case 3:
                    System.out.println("Finding nearby services...");
                    break;
                case 4:
                    System.out.println("Opening navigation...");
                    break;
                case 5:
                    System.out.println("Logging out. Goodbye!");
                    inUserMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Display all places in the city from MySQL database
    private static void viewAllPlaces() {
        // SQL query to fetch all places
        String query = "SELECT * FROM places";

        try {
            // Get database connection
            Connection connection = DBConnection.getConnection();

            if (connection == null) {
                System.out.println("❌ Failed to connect to database.");
                return;
            }

            // Create statement and execute query
            PreparedStatement pstmt = connection.prepareStatement(query);
            ResultSet resultSet = pstmt.executeQuery();

            // Display header
            System.out.println("\n🏙️  ===== ALL CITY ATTRACTIONS =====");
            System.out.println("-".repeat(50));

            boolean hasResults = false;

            // Loop through ResultSet and display each place
            while (resultSet.next()) {
                hasResults = true;
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String category = resultSet.getString("category");
                String location = resultSet.getString("location");
                String description = resultSet.getString("description");

                System.out.println("\n📍 Place ID: " + id);
                System.out.println("   Name: " + name);
                System.out.println("   Category: " + category);
                System.out.println("   Location: " + location);
                System.out.println("   Description: " + description);
            }

            // Handle case when no places found
            if (!hasResults) {
                System.out.println("❌ No places available at the moment.");
            }

            System.out.println("\n" + "-".repeat(50));

            // Close resources
            resultSet.close();
            pstmt.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to fetch places from database.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }

    // Display search menu with search options
    private static void searchPlacesMenu() {
        boolean inSearchMenu = true;

        while (inSearchMenu) {
            System.out.println("\n===== Search Places =====");
            System.out.println("1. Search by category");
            System.out.println("2. Search by location");
            System.out.println("3. Back");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear newline from input buffer

            switch (choice) {
                case 1:
                    // Search places by category
                    searchByCategory();
                    break;
                case 2:
                    // Search places by location
                    searchByLocation();
                    break;
                case 3:
                    // Return to user menu
                    inSearchMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Search places by category from MySQL database
    private static void searchByCategory() {
        System.out.print("\nEnter category to search: ");
        String searchCategory = scanner.nextLine();

        // SQL query with LIKE for case-insensitive search
        String query = "SELECT * FROM places WHERE LOWER(category) LIKE LOWER(?)";

        try {
            // Get database connection
            Connection connection = DBConnection.getConnection();

            if (connection == null) {
                System.out.println("❌ Failed to connect to database.");
                return;
            }

            // Create prepared statement with parameter binding
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, "%" + searchCategory + "%"); // Add wildcards for partial matching
            ResultSet resultSet = pstmt.executeQuery();

            // Display search results
            System.out.println("\n🔍 Search Results for Category: " + searchCategory);
            System.out.println("-".repeat(50));

            boolean found = false;

            // Loop through ResultSet and display matching places
            while (resultSet.next()) {
                found = true;
                String name = resultSet.getString("name");
                String category = resultSet.getString("category");
                String location = resultSet.getString("location");
                String description = resultSet.getString("description");

                System.out.println("\n📍 " + name);
                System.out.println("   Category: " + category);
                System.out.println("   Location: " + location);
                System.out.println("   Description: " + description);
            }

            // Handle no results found
            if (!found) {
                System.out.println("❌ No places found in category: " + searchCategory);
            }

            System.out.println("-".repeat(50));

            // Close resources
            resultSet.close();
            pstmt.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to search places by category.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }

    // Search places by location from MySQL database
    private static void searchByLocation() {
        System.out.print("\nEnter location to search: ");
        String searchLocation = scanner.nextLine();

        // SQL query with LIKE for case-insensitive search
        String query = "SELECT * FROM places WHERE LOWER(location) LIKE LOWER(?)";

        try {
            // Get database connection
            Connection connection = DBConnection.getConnection();

            if (connection == null) {
                System.out.println("❌ Failed to connect to database.");
                return;
            }

            // Create prepared statement with parameter binding
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, "%" + searchLocation + "%"); // Add wildcards for partial matching
            ResultSet resultSet = pstmt.executeQuery();

            // Display search results
            System.out.println("\n🔍 Search Results for Location: " + searchLocation);
            System.out.println("-".repeat(50));

            boolean found = false;

            // Loop through ResultSet and display matching places
            while (resultSet.next()) {
                found = true;
                String name = resultSet.getString("name");
                String category = resultSet.getString("category");
                String location = resultSet.getString("location");
                String description = resultSet.getString("description");

                System.out.println("\n📍 " + name);
                System.out.println("   Category: " + category);
                System.out.println("   Location: " + location);
                System.out.println("   Description: " + description);
            }

            // Handle no results found
            if (!found) {
                System.out.println("❌ No places found in location: " + searchLocation);
            }

            System.out.println("-".repeat(50));

            // Close resources
            resultSet.close();
            pstmt.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to search places by location.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }

    // Manage city resources - admin submenu
    private static void manageCityResources() {
        boolean inResourceMenu = true;

        while (inResourceMenu) {
            System.out.println("\n===== Manage City Resources =====");
            System.out.println("1. Add new place");
            System.out.println("2. Update place");
            System.out.println("3. Delete place");
            System.out.println("4. Back");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear newline from input buffer

            switch (choice) {
                case 1:
                    // Add a new place to the system
                    addNewPlace();
                    break;
                case 2:
                    // Update an existing place
                    updatePlace();
                    break;
                case 3:
                    // Delete a place from the system
                    deletePlace();
                    break;
                case 4:
                    // Return to admin menu
                    inResourceMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Add a new place to the city
    private static void addNewPlace() {
        System.out.println("\n--- Add New Place ---");

        // Get place ID
        System.out.print("Enter place ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Clear newline from input buffer

        // Get place name
        System.out.print("Enter place name: ");
        String name = scanner.nextLine();

        // Get place category
        System.out.print("Enter category (e.g., Hotel, Restaurant, Park): ");
        String category = scanner.nextLine();

        // Get place location
        System.out.print("Enter location: ");
        String location = scanner.nextLine();

        // Get place description
        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        // SQL query to insert new place
        String query = "INSERT INTO places (id, name, category, location, description) VALUES (?, ?, ?, ?, ?)";

        try {
            // Get database connection
            Connection connection = DBConnection.getConnection();

            if (connection == null) {
                System.out.println("❌ Failed to connect to database.");
                return;
            }

            // Create prepared statement with parameter binding
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, category);
            pstmt.setString(4, location);
            pstmt.setString(5, description);

            // Execute insert
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Success! Place '" + name + "' has been added to the city.");
            } else {
                System.out.println("❌ Error: Failed to add place. Please try again.");
            }

            // Close resources
            pstmt.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to add new place to database.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }

    // Update an existing place in the city
    private static void updatePlace() {
        System.out.println("\n--- Update Place ---");

        // Ask admin for place ID to update
        System.out.print("Enter place ID to update: ");
        int placeId = scanner.nextInt();
        scanner.nextLine(); // Clear newline from input buffer

        // SQL query to fetch place by ID
        String selectQuery = "SELECT * FROM places WHERE id = ?";
        // SQL query to update place details
        String updateQuery = "UPDATE places SET name = ?, category = ?, location = ?, description = ? WHERE id = ?";

        try {
            // Get database connection
            Connection connection = DBConnection.getConnection();

            if (connection == null) {
                System.out.println("❌ Failed to connect to database.");
                return;
            }

            // Check if place exists
            PreparedStatement selectPstmt = connection.prepareStatement(selectQuery);
            selectPstmt.setInt(1, placeId);
            ResultSet resultSet = selectPstmt.executeQuery();

            // If place not found, show error and return
            if (!resultSet.next()) {
                System.out.println("❌ Error: Place with ID " + placeId + " not found.");
                return;
            }

            // Display current place details
            System.out.println("\nCurrent details:");
            System.out.println("Name: " + resultSet.getString("name"));
            System.out.println("Category: " + resultSet.getString("category"));
            System.out.println("Location: " + resultSet.getString("location"));
            System.out.println("Description: " + resultSet.getString("description"));

            // Get new place name
            System.out.print("\nEnter new name (or press Enter to keep current): ");
            String newName = scanner.nextLine();
            if (!newName.isEmpty()) {
                // Update name in database
                PreparedStatement updatePstmt = connection.prepareStatement(updateQuery);
                updatePstmt.setString(1, newName);
                updatePstmt.setInt(5, placeId);
                updatePstmt.executeUpdate();
            }

            // Get new category
            System.out.print("Enter new category (or press Enter to keep current): ");
            String newCategory = scanner.nextLine();
            if (!newCategory.isEmpty()) {
                // Update category in database
                PreparedStatement updatePstmt = connection.prepareStatement(updateQuery);
                updatePstmt.setString(2, newCategory);
                updatePstmt.setInt(5, placeId);
                updatePstmt.executeUpdate();
            }

            // Get new location
            System.out.print("Enter new location (or press Enter to keep current): ");
            String newLocation = scanner.nextLine();
            if (!newLocation.isEmpty()) {
                // Update location in database
                PreparedStatement updatePstmt = connection.prepareStatement(updateQuery);
                updatePstmt.setString(3, newLocation);
                updatePstmt.setInt(5, placeId);
                updatePstmt.executeUpdate();
            }

            // Get new description
            System.out.print("Enter new description (or press Enter to keep current): ");
            String newDescription = scanner.nextLine();
            if (!newDescription.isEmpty()) {
                // Update description in database
                PreparedStatement updatePstmt = connection.prepareStatement(updateQuery);
                updatePstmt.setString(4, newDescription);
                updatePstmt.setInt(5, placeId);
                updatePstmt.executeUpdate();
            }

            System.out.println("✅ Success! Place with ID " + placeId + " has been updated.");

            // Close resources
            resultSet.close();
            selectPstmt.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to update place in database.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }

    // Delete a place from the city
    private static void deletePlace() {
        System.out.println("\n--- Delete Place ---");

        // Ask admin for place ID to delete
        System.out.print("Enter place ID to delete: ");
        int placeId = scanner.nextInt();
        scanner.nextLine(); // Clear newline from input buffer

        // SQL query to delete place by ID
        String query = "DELETE FROM places WHERE id = ?";

        try {
            // Get database connection
            Connection connection = DBConnection.getConnection();

            if (connection == null) {
                System.out.println("❌ Failed to connect to database.");
                return;
            }

            // Create prepared statement with parameter binding
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, placeId);

            // Execute delete
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Success! Place with ID " + placeId + " has been deleted.");
            } else {
                System.out.println("❌ Error: Place with ID " + placeId + " not found.");
            }

            // Close resources
            pstmt.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to delete place from database.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }
}
