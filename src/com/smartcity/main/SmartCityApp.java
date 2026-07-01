package com.smartcity.main;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.smartcity.db.DBConnection;

/**
 * The main entry point for the Smart City Guide application.
 * This class handles the command-line interface (CLI) interactions,
 * user authentication (registration & login), and routing to
 * respective User or Admin menus.
 * <p>
 * It currently acts as a monolithic controller that directly manages
 * SQL queries and database connections.
 *
 * @author Rajath2005 (Original Creator)
 * @version 1.0
 */
public class SmartCityApp {

    //<editor-fold desc="SQL QUERIES">

    // SQL queries
    private static final String CHECK_QUERY = "SELECT id FROM users WHERE username = ?";
    private static final String INSERT_QUERY = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

    // SQL query to insert new place
    private static final String INSERT_PLACES = "INSERT INTO places (id, name, category, location, description) VALUES (?, ?, ?, ?, ?)";

    // SQL query with LIKE for case-insensitive search
    private static final String SELECT_LOWER_CATEGORY = "SELECT * FROM places WHERE LOWER(category) LIKE LOWER(?)";

    // SQL query with LIKE for case-insensitive search
    private static final String SELECT_LOWER_PLACES = "SELECT * FROM places WHERE LOWER(location) LIKE LOWER(?)";

    // SQL query to fetch all places
    private static final String SELECT_ALL_PLACES = "SELECT * FROM places";

    // SQL query to fetch user by username and password
    private static final String SELECT_USER_PASSWORD = "SELECT role FROM users WHERE username = ? AND password = ?";

    private static final String SELECT_UNKNOWN_ID = "SELECT * FROM places WHERE id = ?";
    private static final String UPDATE_PLACES = "UPDATE places SET name = ?, category = ?, location = ?, description = ? WHERE id = ?";

    // SQL query to delete place by ID
    private static final String DELETE_PLACE_ID = "DELETE FROM places WHERE id = ?";
    //</editor-fold>

    // Scanner object shared across methods
    private static Scanner scanner = new Scanner(System.in);


    // SQL Query Constants (Login/Register)
    private static final String CHECK_USERNAME_EXISTS_QUERY = "SELECT id FROM users WHERE username = ?";

    private static final String INSERT_USER_QUERY = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

    private static final String LOGIN_QUERY = "SELECT role FROM users WHERE username = ? AND password = ?";

    
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

    // Validates username: 4-20 characters, alphanumeric only
    private static boolean isValidUsername(String username) {
        if (username == null || username.isEmpty()) return false;
        String regex = "^[a-zA-Z0-9]{4,20}$";
        return username.matches(regex);
    }

    // Validates password: Minimum 8 chars, 1 uppercase, 1 lowercase, 1 number, 1 special char
    private static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) return false;
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(regex);
    }

    // Register new user directly to MySQL database with validation
    private static void register() {
        System.out.println("\n--- Registration ---");

        // Get and validate username
        System.out.print("Enter username (4-20 alphanumeric characters): ");
        String username = scanner.nextLine();

        //When the username the user chooses is invalid, this activates
        while (!isValidUsername(username)) {
            System.out.println("Invalid username. Please try again.");
            //It allows the user to retry again, and if they're succesful the loop stops
            System.out.print("Enter username (4-20 alphanumeric characters): ");
            username = scanner.nextLine();
        }

        // Get and validate password BEFORE hitting the database
        System.out.print("Enter password (min 8 chars, 1 uppercase, 1 lowercase, 1 number, 1 special char): ");
        String password = scanner.nextLine();

        //When the password the user chooses is invalid, this activates
        while (!isValidPassword(password)) {
            System.out.println("Invalid password. Please try again.");
            //It allows the user to retry again, and if they're succesful the loop stops
            System.out.print("Enter password (min 8 chars, 1 uppercase, 1 lowercase, 1 number, 1 special char): ");
            password = scanner.nextLine();
        }

        try {
            // Get database connection
            Connection connection = DBConnection.getConnection();

            if (connection == null) {
                System.out.println("❌ Failed to connect to database.");
                return;
            }

            // Check if username already exists
            PreparedStatement checkPstmt = connection.prepareStatement(CHECK_USERNAME_EXISTS_QUERY);
            checkPstmt.setString(1, username);
            ResultSet resultSet = checkPstmt.executeQuery();

            if (resultSet.next()) {
                System.out.println("❌ Error: Username already exists. Please choose a different username.");
                resultSet.close();
                checkPstmt.close();
                connection.close();
                return;
            }

            resultSet.close();
            checkPstmt.close();

            // Create prepared statement for insert
            PreparedStatement insertPstmt = connection.prepareStatement(INSERT_USER_QUERY);
            insertPstmt.setString(1, username);
            insertPstmt.setString(2, password);
            insertPstmt.setString(3, "USER"); // Default role for new users

            // Execute insert
            int rowsAffected = insertPstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Success! User '" + username + "' registered successfully.");
            } else {
                System.out.println("❌ Error: Failed to register user. Please try again.");
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

        try {
            // Get database connection
            Connection connection = DBConnection.getConnection();

            if (connection == null) {
                System.out.println("❌ Failed to connect to database.");
                return;
            }

            // Create prepared statement with parameter binding
            PreparedStatement pstmt = connection.prepareStatement(LOGIN_QUERY);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            // Execute query
            ResultSet resultSet = pstmt.executeQuery();

            // Check if user credentials match
            if (resultSet.next()) {
                // Get user role from database
                String role = resultSet.getString("role");

                System.out.println("✅ Success! Welcome back, " + username + "!");

                // Show appropriate menu based on user role
                if (role.equals("ADMIN")) {
                    showAdminMenu(username);
                } else {
                    showUserMenu(username);
                }
            } else {
                System.out.println("❌ Error: Username or password incorrect. Please try again.");
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


        try {
            // Get database connection
            Connection connection = DBConnection.getConnection();

            if (connection == null) {
                System.out.println("❌ Failed to connect to database.");
                return;
            }

            // Create statement and execute query
            PreparedStatement pstmt = connection.prepareStatement(SELECT_ALL_PLACES);
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


        try {
            // Get database connection
            Connection connection = DBConnection.getConnection();

            if (connection == null) {
                System.out.println("❌ Failed to connect to database.");
                return;
            }

            // Create prepared statement with parameter binding
            PreparedStatement pstmt = connection.prepareStatement(SELECT_LOWER_CATEGORY);
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


        try {
            // Get database connection
            Connection connection = DBConnection.getConnection();

            if (connection == null) {
                System.out.println("❌ Failed to connect to database.");
                return;
            }

            // Create prepared statement with parameter binding
            PreparedStatement pstmt = connection.prepareStatement(SELECT_LOWER_PLACES);
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
        int id;
        try {
            id = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("❌ Invalid ID. Please enter a number.");
            scanner.nextLine(); // Clear newline from input buffer
            return;
        }


        // Get place name
        System.out.print("Enter place name: ");
        String name = scanner.nextLine();
        //If it's not valid, then the user can try again
        while (!isValidPlaceName(name)) {
            System.out.println("❌ Error: Place name cannot be empty.");
            System.out.print("Enter place name: ");
            name = scanner.nextLine();
        }

        // Get place category
        System.out.print("Enter category (e.g., Hotel, Restaurant, Park): ");
        String category = scanner.nextLine();
        //If it's not valid, then the user can try again
        while (category == null || category.trim().isEmpty()) {
            System.out.println("❌ Error: Category cannot be empty.");
            System.out.print("Enter category (e.g., Hotel, Restaurant, Park): ");
            category = scanner.nextLine();
        }

        // Get place location
        System.out.print("Enter location: ");
        String location = scanner.nextLine();
        //If it's not valid, then the user can try again
        while (!isValidLocation(location)) {
            System.out.println("❌ Error: Location cannot be empty.");
            System.out.print("Enter location: ");
            location = scanner.nextLine();
        }

        // Get place description
        System.out.print("Enter description: ");
        String description = scanner.nextLine();


        try {
            // Get database connection
            Connection connection = DBConnection.getConnection();

            if (connection == null) {
                System.out.println("❌ Failed to connect to database.");
                return;
            }

            // Create prepared statement with parameter binding
            PreparedStatement pstmt = connection.prepareStatement(INSERT_PLACES);
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


        try {
            Connection connection = DBConnection.getConnection();

            if (connection == null) {
                System.out.println("❌ Failed to connect to database.");
                return;
            }

            // Fetch existing place
            PreparedStatement selectPstmt = connection.prepareStatement(SELECT_UNKNOWN_ID);
            selectPstmt.setInt(1, placeId);
            ResultSet rs = selectPstmt.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Error: Place with ID " + placeId + " not found.");
                return;
            }

            // Existing values
            String currentName = rs.getString("name");
            String currentCategory = rs.getString("category");
            String currentLocation = rs.getString("location");
            String currentDescription = rs.getString("description");

            System.out.println("\nCurrent details:");
            System.out.println("Name: " + currentName);
            System.out.println("Category: " + currentCategory);
            System.out.println("Location: " + currentLocation);
            System.out.println("Description: " + currentDescription);

            // Take new inputs
            System.out.print("\nEnter new name (or press Enter to keep current): ");
            String newName = scanner.nextLine();

            System.out.print("Enter new category (or press Enter to keep current): ");
            String newCategory = scanner.nextLine();

            System.out.print("Enter new location (or press Enter to keep current): ");
            String newLocation = scanner.nextLine();

            System.out.print("Enter new description (or press Enter to keep current): ");
            String newDescription = scanner.nextLine();

            // Use old values if input is empty
            if (newName.isEmpty()) newName = currentName;
            if (newCategory.isEmpty()) newCategory = currentCategory;
            if (newLocation.isEmpty()) newLocation = currentLocation;
            if (newDescription.isEmpty()) newDescription = currentDescription;

            // Single correct update query
            PreparedStatement updatePstmt = connection.prepareStatement(UPDATE_PLACES);
            updatePstmt.setString(1, newName);
            updatePstmt.setString(2, newCategory);
            updatePstmt.setString(3, newLocation);
            updatePstmt.setString(4, newDescription);
            updatePstmt.setInt(5, placeId);

            int rows = updatePstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Success! Place updated successfully.");
            } else {
                System.out.println("❌ Error: Update failed.");
            }

            // Close resources
            rs.close();
            selectPstmt.close();
            updatePstmt.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to update place.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }

    // Delete a place from the city
    private static void deletePlace() {
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


        try {
            // Get database connection
            Connection connection = DBConnection.getConnection();

            if (connection == null) {
                System.out.println("❌ Failed to connect to database.");
                return;
            }

            // Create prepared statement with parameter binding
            PreparedStatement pstmt = connection.prepareStatement(DELETE_PLACE_ID);
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

    private static boolean isValidPlaceName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    private static boolean isValidLocation(String location) {
        return location != null && !location.trim().isEmpty();
    }
}
