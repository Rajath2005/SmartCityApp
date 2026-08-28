package com.smartcity.main;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.smartcity.db.DBConnection;
import com.smartcity.service.EmailService;
import com.smartcity.util.ValidationUtils;

/**
 * The main entry point for the Smart City Guide application.
 * This class handles the command-line interface (CLI) interactions,
 * user authentication (registration & login), and routing to
 * the respective User or Admin menus.
 * <p>
 * It currently acts as a monolithic controller that directly manages
 * SQL queries and database connections.
 *
 * @author Rajath2005 (Original Creator)
 * @version 1.0
 */
public class SmartCityApp {
    // Scanner object shared across methods
    private final static Scanner scanner = new Scanner(System.in);

    // SQL Query Constants
    private static final String CHECK_USERNAME_EXISTS_QUERY = "SELECT id FROM users WHERE username = ?";
    private static final String INSERT_USER_QUERY = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)";
    private static final String LOGIN_QUERY = "SELECT role FROM users WHERE username = ? AND password = ?";
    private static final String SEARCH_BY_CATEGORY_QUERY = "SELECT * FROM places WHERE LOWER(category) LIKE LOWER(?)";
    private static final String SEARCH_BY_LOCATION_QUERY = "SELECT * FROM places WHERE LOWER(location) LIKE LOWER(?)";
    private static final String INSERT_PLACE_QUERY = "INSERT INTO places (id, name, category, location, description, latitude, longitude) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_PLACE_BY_ID_QUERY = "SELECT * FROM places WHERE id = ?";
    private static final String UPDATE_PLACE_QUERY = "UPDATE places SET name = ?, category = ?, location = ?, description = ?, latitude = ?, longitude = ? WHERE id = ?";
    private static final String DELETE_PLACE_QUERY = "DELETE FROM places WHERE id = ?";
    private static final String SELECT_ALL_CREDENTIALS_QUERY = "SELECT id, password FROM users";

    private static final String UPDATE_PASSWORD_QUERY = "UPDATE users SET password = ? WHERE id = ?";

    private static final String SHA256_HEX_PATTERN = "^[a-f0-9]{64}$";

    /**
     * Application entry point. Starts the Smart City Guide CLI, runs a
     * one-time migration of any legacy plaintext passwords, and then
     * repeatedly displays the main menu until the user chooses to exit.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Smart City Guide Started Successfully");

        migrateExistingPlaintextPasswords();

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
                    EmailService.shutdownExecutor();
                    isRunning = false;
                    break;
                default:
                    System.out.println("❌ Invalid choice '" + choice + "'. Please enter a number between 1 and 3.");
            }
        }

        scanner.close();
    }

    /**
     * Gets a database connection and prints a helpful error message if the
     * connection attempt fails.
     *
     * @return a valid {@link Connection}, or {@code null} if the connection failed
     */
    private static Connection getConnectionOrPrintError() {
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            System.out.println("❌ Could not connect to the database.");
            System.out.println("   Please check:");
            System.out.println("   1. Is MySQL running on your machine?");
            System.out.println("   2. Did you run db_setup.sql to create the database?");
            System.out.println("   3. Is your password correct?");
        }
        return conn;
    }

    /**
     * Clears the screen and prints the main menu options (Register, Login, Exit)
     * to standard output.
     */
    private static void displayMenu() {
        clearScreen();
        System.out.println("\n===== Smart City Guide Menu =====");
        System.out.println("1. 📝 Register");
        System.out.println("2. 🔑 Login");
        System.out.println("3. 🚪 Exit");
        System.out.print("Enter your choice: ");
    }

    //Method to validate the email
    private static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(regex);
    }

    /**
     * Hashes a plaintext password using SHA-256 and encodes the result as a
     * lowercase hexadecimal string.
     *
     * @param password the plaintext password to hash
     * @return the SHA-256 hash of the password, represented as a 64-character
     *         lowercase hexadecimal string
     */
    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    /**
     * One-time startup migration that scans all stored user passwords and
     * re-hashes any that are not already in SHA-256 hex format (i.e. legacy
     * plaintext passwords), replacing them in the database with their
     * SHA-256 hash.
     */
    private static void migrateExistingPlaintextPasswords() {
        Connection connection = getConnectionOrPrintError();

        if (connection == null) {
            return;
        }

        try (connection;
             PreparedStatement selectPstmt = connection.prepareStatement(SELECT_ALL_CREDENTIALS_QUERY);
             PreparedStatement updatePstmt = connection.prepareStatement(UPDATE_PASSWORD_QUERY);
             ResultSet resultSet = selectPstmt.executeQuery()) {

            int migratedCount = 0;

            while (resultSet.next()) {
                String storedPassword = resultSet.getString("password");

                if (!storedPassword.matches(SHA256_HEX_PATTERN)) {
                    updatePstmt.setString(1, hashPassword(storedPassword));
                    updatePstmt.setInt(2, resultSet.getInt("id"));
                    updatePstmt.executeUpdate();
                    migratedCount++;
                }
            }

            if (migratedCount > 0) {
                System.out.println("Migrated " + migratedCount + " plaintext password(s) to SHA-256.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to migrate plaintext passwords.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }

    /**
     * Registers a new user by validating the provided credentials,
     * ensuring the username is unique, and storing the user in the database.
     */
    private static void register() {
        System.out.println("\n--- Registration ---");

        // Get and validate username
        System.out.print("Enter username (4-20 alphanumeric characters): ");
        String username = scanner.nextLine();

        // When the username the user chooses is invalid, this activates
        while (!ValidationUtils.isValidUsername(username)) {
            System.out.println("Invalid username. Please try again.");
            // It allows the user to retry again, and if they're successful the loop stops
            System.out.print("Enter username (4-20 alphanumeric characters): ");
            username = scanner.nextLine();
        }

        // Get and validate password BEFORE hitting the database
        System.out.print("Enter password (min 8 chars, 1 uppercase, 1 lowercase, 1 number, 1 special char): ");
        String password = scanner.nextLine();

        // When the password the user chooses is invalid, this activates
        while (password.length() < 8 || !ValidationUtils.isValidPassword(password)) {
            if (password.length() < 8) {
                System.out.println("Password is too short. Minimum 8 characters required.");
            } else {
                System.out.println("Invalid password. Please try again.");
            }
            // It allows the user to retry again, and if they're succesful the loop stops
            System.out.print("Enter password (min 8 chars, 1 uppercase, 1 lowercase, 1 number, 1 special char): ");
            password = scanner.nextLine();
        }
        System.out.println("Enter your email:");
        String email = scanner.nextLine();
        while (!isValidEmail(email)) {
            System.out.println("Invalid Email. Please try again.");
            email = scanner.nextLine();
        }

        try (Connection connection = getConnectionOrPrintError()) {
            if (connection == null) {
                return;
            }

            // Check if the username already exists
            try (PreparedStatement checkPstmt = connection.prepareStatement(CHECK_USERNAME_EXISTS_QUERY)) {
                checkPstmt.setString(1, username);
                try (ResultSet resultSet = checkPstmt.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("❌ Error: Username already exists. Please choose a different username.");
                        return;
                    }
                }
            }

            // Insert new user
            try (PreparedStatement insertPstmt = connection.prepareStatement(INSERT_USER_QUERY)) {
                insertPstmt.setString(1, username);
                insertPstmt.setString(2, hashPassword(password));
                insertPstmt.setString(3, email);
                insertPstmt.setString(4, "USER"); // Default role for new users



                int rowsAffected = insertPstmt.executeUpdate();

                if (rowsAffected > 0) {
                    EmailService.sendWelcomeEmail(email, username);
                    System.out.println("✅ Success! User '" + username + "' registered successfully.");
                } else {
                    System.out.println("❌ Error: Failed to register user. Please try again.");
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to register user.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }

    /**
     * Prompts the user for a username and password, validates the credentials
     * against the database, and routes the user to the Admin or User menu
     * based on their stored role.
     */
    private static void login() {
        System.out.println("\n--- Login ---");

        // Get username from user input
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        // Get password from user input
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try (Connection connection = getConnectionOrPrintError()) {
            if (connection == null) {
                return;
            }

            // Create prepared statement with parameter binding
            try (PreparedStatement pstmt = connection.prepareStatement(LOGIN_QUERY)) {
                pstmt.setString(1, username);
                pstmt.setString(2, hashPassword(password));

                try (ResultSet resultSet = pstmt.executeQuery()) {
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
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to login user.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }

    /**
     * Displays the admin menu loop, allowing the logged-in admin to view
     * users, manage city resources, view system logs, or log out.
     *
     * @param username the username of the currently logged-in admin, used
     *                  for display purposes
     */
    private static void showAdminMenu(String username) {
        boolean inAdminMenu = true;

        while (inAdminMenu) {
            clearScreen();
            System.out.println("\n===== Admin Menu (User: " + username + ") =====");
            System.out.println("1. 👥 View all users");
            System.out.println("2. 🏗️ Manage city resources");
            System.out.println("3. 📋 View system logs");
            System.out.println("4. 🚪 Logout");
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
                    System.out.println("❌ Invalid choice '" + choice + "'. Please enter a number between 1 and 4.");
            }
        }
    }

    /**
     * Displays the regular user menu loop, allowing the logged-in user to
     * explore attractions, search places, view nearby services, check
     * navigation, or log out.
     *
     * @param username the username of the currently logged-in user, used
     *                  for display purposes
     */
    private static void showUserMenu(String username) {
        boolean inUserMenu = true;

        while (inUserMenu) {
            clearScreen();
            System.out.println("\n===== User Menu (User: " + username + ") =====");
            System.out.println("1. 🏙 Explore city attractions");
            System.out.println("2. 🔍 Search places");
            System.out.println("3. 📍 View nearby services");
            System.out.println("4. 🧭 Check navigation");
            System.out.println("5. 🚪 Logout");

            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear newline from input buffer

            switch (choice) {
                case 1:
                    // Display all city attractions
                    viewAllPlaces();
                    break;
                // Display all city attractions sorted by ID
                case 2:
                    viewAllPlacesSortedById();
                    break;
                case 3:
                    // Search for places
                    searchPlacesMenu();
                    break;
                case 4:
                    System.out.println("Finding nearby services...");
                    break;
                case 5:
                    System.out.println("Opening navigation...");
                    break;
                case 6:
                    System.out.println("Logging out. Goodbye!");
                    inUserMenu = false;
                    break;
                default:
                    System.out.println("❌ Invalid choice '" + choice + "'. Please enter a number between 1 and 6.");
            }
        }
    }

    /**
     * Iterates over a {@link ResultSet} of places and prints the details of
     * each one to standard output. Prints a "no places available" message
     * if the result set is empty.
     *
     * @param resultSet the result set containing place rows to print
     */
    private static void placeResultPrintout(ResultSet resultSet) {
        boolean hasResults = false;
        try {
            // Loop through ResultSet and display each place
            while (resultSet.next()) {
                hasResults = true;
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String category = resultSet.getString("category");
                String location = resultSet.getString("location");
                String description = resultSet.getString("description");
                double latitude = resultSet.getDouble("latitude");
                double longitude = resultSet.getDouble("longitude");

                System.out.println("\n📍 Place ID: " + id);
                System.out.println("   Name: " + name);
                System.out.println("   Category: " + category);
                System.out.println("   Location: " + location);
                System.out.println("   Description: " + description);
                System.out.println("   Coordinates " + latitude + ", " + longitude);
            }

            // Handle case when no places found
            if (!hasResults) {
                System.out.println("❌ No places available at the moment.");
            }

            System.out.println("\n" + "-".repeat(50));
        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to fetch places from database.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }

    /**
     * Queries the database for all places, ordered alphabetically by name,
     * and prints them to standard output.
     */
    private static void viewAllPlaces() {
        String query = "SELECT * FROM places ORDER BY name ASC";

        try (Connection connection = getConnectionOrPrintError()) {
            if (connection == null) {
                return;
            }

            try (PreparedStatement pstmt = connection.prepareStatement(query);
                 ResultSet resultSet = pstmt.executeQuery()) {

                // Display header
                System.out.println("\n🏙️  ===== ALL CITY ATTRACTIONS =====");
                System.out.println("-".repeat(50));
                placeResultPrintout(resultSet);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to fetch places from database.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }

    /**
     * Queries the database for all places, ordered by their ID in ascending
     * order, and prints them to standard output.
     */
    private static void viewAllPlacesSortedById() {
        String query = "SELECT * FROM places ORDER BY id ASC";

        try (Connection connection = getConnectionOrPrintError()) {
            if (connection == null) {
                return;
            }

            try (PreparedStatement pstmt = connection.prepareStatement(query);
                 ResultSet resultSet = pstmt.executeQuery()) {

                System.out.println("\n🏙️  ===== ALL CITY ATTRACTIONS (BY ID) =====");
                System.out.println("-".repeat(50));
                placeResultPrintout(resultSet);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to fetch places from database.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }

    /**
     * Displays the search submenu loop, allowing the user to search places
     * by category, search by location, or go back to the previous menu.
     */
    private static void searchPlacesMenu() {
        boolean inSearchMenu = true;

        while (inSearchMenu) {
            System.out.println("\n===== Search Places =====");
            System.out.println("1. 🏷️ Search by category");
            System.out.println("2. 📌 Search by location");
            System.out.println("3. ⬅️ Back");
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
                    System.out.println("❌ Invalid choice '" + choice + "'. Please enter a number between 1 and 3.");
            }
        }
    }

    /**
     * Prompts the user for a category keyword and queries the database for
     * all places whose category contains that keyword (case-insensitive),
     * printing the matching results.
     */
    private static void searchByCategory() {
        System.out.print("\nEnter category to search: ");
        String searchCategory = scanner.nextLine();

        try (Connection connection = getConnectionOrPrintError()) {
            if (connection == null) {
                return;
            }

            try (PreparedStatement pstmt = connection.prepareStatement(SEARCH_BY_CATEGORY_QUERY)) {
                pstmt.setString(1, "%" + searchCategory + "%"); // Add wildcards for partial matching

                try (ResultSet resultSet = pstmt.executeQuery()) {
                    // Display search results
                    System.out.println("\n🔍 Search Results for Category: " + searchCategory);
                    System.out.println("-".repeat(50));
                    placeResultPrintout(resultSet);
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to search places by category.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }

    /**
     * Prompts the user for a location keyword and queries the database for
     * all places whose location contains that keyword (case-insensitive),
     * printing the matching results.
     */
    private static void searchByLocation() {
        System.out.print("\nEnter location to search: ");
        String searchLocation = scanner.nextLine();

        try (Connection connection = getConnectionOrPrintError()) {
            if (connection == null) {
                return;
            }

            try (PreparedStatement pstmt = connection.prepareStatement(SEARCH_BY_LOCATION_QUERY)) {
                pstmt.setString(1, "%" + searchLocation + "%"); // Add wildcards for partial matching

                try (ResultSet resultSet = pstmt.executeQuery()) {
                    // Display search results
                    System.out.println("\n🔍 Search Results for Location: " + searchLocation);
                    System.out.println("-".repeat(50));
                    placeResultPrintout(resultSet);
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to search places by location.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }

    /**
     * Displays the admin's city-resource management submenu loop, allowing
     * the admin to add, update, or delete a place, or go back to the
     * previous menu.
     */
    private static void manageCityResources() {
        boolean inResourceMenu = true;

        while (inResourceMenu) {
            System.out.println("\n===== Manage City Resources =====");
            System.out.println("1. ➕ Add new place");
            System.out.println("2. ✏️ Update place");
            System.out.println("3. 🗑️ Delete place");
            System.out.println("4. ⬅️ Back");
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
                    System.out.println("❌ Invalid choice '" + choice + "'. Please enter a number between 1 and 4.");
            }
        }
    }

    /**
     * Prompts the admin for a new place's ID, name, category, location,
     * description, latitude, and longitude, validates the input, and
     * inserts the new place into the database.
     */
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

        // If it's not valid, then the user can try again
        while (!isValidPlaceName(name)) {
            System.out.println("❌ Error: Place name cannot be empty.");
            System.out.print("Enter place name: ");
            name = scanner.nextLine();
        }

        // Get place category
        System.out.print("Enter category (e.g., Hotel, Restaurant, Park): ");
        String category = scanner.nextLine();

        // If it's not valid, then the user can try again
        while (category == null || category.trim().isEmpty()) {
            System.out.println("❌ Error: Category cannot be empty.");
            System.out.print("Enter category (e.g., Hotel, Restaurant, Park): ");
            category = scanner.nextLine();
        }

        // Get place location
        System.out.print("Enter location: ");
        String location = scanner.nextLine();

        // If it's not valid, then the user can try again
        while (!isValidLocation(location)) {
            System.out.println("❌ Error: Location cannot be empty.");
            System.out.print("Enter location: ");
            location = scanner.nextLine();
        }

        // Get place description
        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        // Get place latitude
        System.out.print("Enter place latitude: ");
        double latitude;
        try {
            latitude = scanner.nextDouble();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("❌ Invalid latitude. Please enter a valid number.");
            scanner.nextLine(); // Clear newline from input buffer
            return;
        }

        // Get place longitude
        System.out.print("Enter place longitude: ");
        double longitude;
        try {
            longitude = scanner.nextDouble();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("❌ Invalid longitude. Please enter a valid number.");
            scanner.nextLine(); // Clear newline from input buffer
            return;
        }

        try (Connection connection = getConnectionOrPrintError()) {
            if (connection == null) {
                return;
            }

            try (PreparedStatement pstmt = connection.prepareStatement(INSERT_PLACE_QUERY)) {
                pstmt.setInt(1, id);
                pstmt.setString(2, name);
                pstmt.setString(3, category);
                pstmt.setString(4, location);
                pstmt.setString(5, description);
                pstmt.setDouble(6, latitude);
                pstmt.setDouble(7, longitude);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("✅ Success! Place '" + name + "' has been added to the city.");
                } else {
                    System.out.println("❌ Error: Failed to add place. Please try again.");
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to add new place to database.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }

    /**
     * Prompts the admin for a place ID, fetches the existing place details,
     * allows the admin to optionally overwrite any field (pressing Enter
     * keeps the current value), validates the new values, and updates the
     * place in the database.
     */
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

        try (Connection connection = getConnectionOrPrintError()) {
            if (connection == null) {
                return;
            }

            // Fetch existing place
            String currentName, currentCategory, currentLocation, currentDescription;
            double currentLatitude, currentLongitude;
            try (PreparedStatement selectPstmt = connection.prepareStatement(SELECT_PLACE_BY_ID_QUERY)) {
                selectPstmt.setInt(1, placeId);
                try (ResultSet rs = selectPstmt.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("❌ Error: Place with ID " + placeId + " not found.");
                        return;
                    }

                    currentName = rs.getString("name");
                    currentCategory = rs.getString("category");
                    currentLocation = rs.getString("location");
                    currentDescription = rs.getString("description");
                    currentLatitude = rs.getDouble("latitude");
                    currentLongitude = rs.getDouble("longitude");
                }
            }

            System.out.println("\nCurrent details:");
            System.out.println("Name: " + currentName);
            System.out.println("Category: " + currentCategory);
            System.out.println("Location: " + currentLocation);
            System.out.println("Description: " + currentDescription);
            System.out.println("Coordinates: " + currentLatitude + ", " + currentLongitude);

            double newLatitude = currentLatitude;
            double newLongitude = currentLongitude;

            // Take new inputs
            System.out.print("\nEnter new name (or press Enter to keep current): ");
            String newName = scanner.nextLine();

            System.out.print("Enter new category (or press Enter to keep current): ");
            String newCategory = scanner.nextLine();

            System.out.print("Enter new location (or press Enter to keep current): ");
            String newLocation = scanner.nextLine();

            System.out.print("Enter new description (or press Enter to keep current): ");
            String newDescription = scanner.nextLine();

            System.out.print("Enter new latitude (or press Enter to keep current): ");
            String newLatitudeString = scanner.nextLine();

            System.out.print("Enter new longitude (or press Enter to keep current): ");
            String newLongitudeString = scanner.nextLine();

            // Use old values if input is empty
            if (newName.isEmpty()) {
                newName = currentName;
            }
            if (newCategory.isEmpty()) {
                newCategory = currentCategory;
            }
            if (newLocation.isEmpty()) {
                newLocation = currentLocation;
            }
            if (newDescription.isEmpty()) {
                newDescription = currentDescription;
            }
            if (newLatitudeString.isEmpty()) {
                newLatitude = currentLatitude;
            }
            if (newLongitudeString.isEmpty()) {
                newLongitude = currentLongitude;
            }

            // 🔥 VALIDATION
            if (newName == null || newName.trim().isEmpty()) {
                System.out.println("❌ Error: Place name cannot be empty.");
                return;
            }

            if (newLocation == null || newLocation.trim().isEmpty()) {
                System.out.println("❌ Error: Location cannot be empty.");
                return;
            }

            if (newCategory == null || newCategory.trim().isEmpty()) {
                System.out.println("❌ Error: Category cannot be empty.");
                return;
            }

            if (!newLatitudeString.trim().isEmpty()) {
                try {
                    newLatitude = Double.parseDouble(newLatitudeString);
                } catch (NumberFormatException e) {
                    System.out.println("❌ Error: Latitude is not a valid number.");
                    return;
                }
            }

            if (!newLongitudeString.trim().isEmpty()) {
                try {
                    newLongitude = Double.parseDouble(newLongitudeString);
                } catch (NumberFormatException e) {
                    System.out.println("❌ Error: Longitude is not a valid number.");
                    return;
                }
            }

            try (PreparedStatement updatePstmt = connection.prepareStatement(UPDATE_PLACE_QUERY)) {
                updatePstmt.setString(1, newName);
                updatePstmt.setString(2, newCategory);
                updatePstmt.setString(3, newLocation);
                updatePstmt.setString(4, newDescription);
                updatePstmt.setDouble(5, newLatitude);
                updatePstmt.setDouble(6, newLongitude);
                updatePstmt.setInt(7, placeId);

                int rows = updatePstmt.executeUpdate();

                if (rows > 0) {
                    System.out.println("✅ Success! Place updated successfully.");
                } else {
                    System.out.println("❌ Error: Update failed.");
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error: Failed to update place.");
            System.out.println("   Error message: " + e.getMessage());
        }
    }

    /**
     * Prompts the admin for a place ID and deletes the corresponding place
     * from the database, if it exists.
     */
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

        try (Connection connection = getConnectionOrPrintError()) {
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

    /**
     * Validates that a place name is not null or blank.
     *
     * @param name the place name to validate
     * @return true if the name is non-null and contains non-whitespace
     *         characters, false otherwise
     */
    private static boolean isValidPlaceName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    /**
     * Validates that a location string is not null or blank.
     *
     * @param location the locations string to validate
     * @return true if the location is non-null and contains non-whitespace
     *         characters, false otherwise
     */
    private static boolean isValidLocation(String location) {
        return location != null && !location.trim().isEmpty();
    }

    /**
     * Clears console logs: wipes previous menu/output from the terminal
     * before redrawing, keeping the CLI screen clean between menu displays.
     * <p>
     * Note: relies on ANSI escape codes, works in real terminals
     * (Linux/macOS, Windows Terminal/PowerShell with VT processing), but has
     * no effect in IDE run consoles (IntelliJ/Eclipse) since those don't
     * interpret ANSI.
     */
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}