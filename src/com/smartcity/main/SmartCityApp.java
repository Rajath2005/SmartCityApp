package com.smartcity.main;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.smartcity.commands.CommandInvoker;
import com.smartcity.commands.AdminMenuCommands.LogoutCommand;
import com.smartcity.commands.AdminMenuCommands.ManageCityResourcesCommand;
import com.smartcity.commands.AdminMenuCommands.ViewSystemLogsCommand;
import com.smartcity.commands.AdminMenuCommands.ViewUsersCommand;
import com.smartcity.commands.MainMenuCommands.ExitCommand;
import com.smartcity.commands.MainMenuCommands.LoginCommand;
import com.smartcity.commands.MainMenuCommands.RegisterCommand;
import com.smartcity.commands.UserMenuComands.ExploreCityAttractionsCommand;
import com.smartcity.commands.UserMenuComands.NavigationCommand;
import com.smartcity.commands.UserMenuComands.NearbyServicesCommand;
import com.smartcity.commands.UserMenuComands.SearchPlacesCommand;
import com.smartcity.db.DBConnection;

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

    private static CommandInvoker mainMenuInvoker = new CommandInvoker(scanner);
    private static CommandInvoker userMenuInvoker = new CommandInvoker(scanner);
    private static CommandInvoker adminMenuInvoker = new CommandInvoker(scanner);

    private static final String SELECT_ALL_CREDENTIALS_QUERY = "SELECT id, password FROM users";

    private static final String UPDATE_PASSWORD_QUERY = "UPDATE users SET password = ? WHERE id = ?";

    private static final String SHA256_HEX_PATTERN = "^[a-f0-9]{64}$";

    private static final String INSERT_PLACE_QUERY = "INSERT INTO places (id, name, category, location, description, latitude, longitude) VALUES (?, ?, ?, ?, ?, ?, ?)";

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
        registerCommands();

        boolean isRunning = true;

        // Loop to repeatedly show menu until user exits
        while (isRunning) {
            displayMenu();
            // Dispatch the selected action through the command invoker.
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice < 1 || choice > 3) {
                    System.out.println("❌ Invalid choice '" + choice
                            + "'. Please enter a number between 1 and 3.");
                    continue;
                }

                mainMenuInvoker.executeCommand(choice);
                isRunning = choice != 3;
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid choice. Please enter a number between 1 and 3.");
            }
        }
    }

    private static void registerCommands() {
        mainMenuInvoker.registerCommand(1, new RegisterCommand());
        mainMenuInvoker.registerCommand(2, new LoginCommand());
        mainMenuInvoker.registerCommand(3, new ExitCommand());

        adminMenuInvoker.registerCommand(1, new ViewUsersCommand());
        adminMenuInvoker.registerCommand(2, new ManageCityResourcesCommand(scanner));
        adminMenuInvoker.registerCommand(3, new ViewSystemLogsCommand());

        userMenuInvoker.registerCommand(1, new ExploreCityAttractionsCommand());
        userMenuInvoker.registerCommand(2, new SearchPlacesCommand(scanner));
        userMenuInvoker.registerCommand(3, new NearbyServicesCommand());
        userMenuInvoker.registerCommand(4, new NavigationCommand());
        userMenuInvoker.registerCommand(5, new LogoutCommand());
    }

    /**
     * Gets a database connection and prints a helpful error message if the
     * connection attempt fails.
     *
     * @return a valid {@link Connection}, or {@code null} if the connection failed
     */
    public static Connection getConnectionOrPrintError() {
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

    /**
     * Validates that a username meets the required format.
     *
     * @param username the username string to validate
     * @return true if valid (4-20 alphanumeric characters), false otherwise
     */
    private static boolean isValidUsername(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        String regex = "^[a-zA-Z0-9]{4,20}$";
        return username.matches(regex);
    }

    /**
     * Validates that a password meets the required strength rules.
     *
     * @param password the password string to validate
     * @return true if valid (minimum 8 characters containing at least one
     *         uppercase letter, one lowercase letter, one digit, and one
     *         special character), false otherwise
     */
    private static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(regex);
    }

    // Method to validate the email
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
     * Displays the admin menu loop, allowing the logged-in admin to view
     * users, manage city resources, view system logs, or log out.
     *
     * @param username the username of the currently logged-in admin, used
     *                 for display purposes
     */
    public static void showAdminMenu(String username) {
        LogoutCommand logoutCommand = new LogoutCommand();
        adminMenuInvoker.registerCommand(4, logoutCommand);

        boolean inAdminMenu = true;

        while (inAdminMenu) {
            clearScreen();

            System.out.println("\n===== Admin Menu (User: " + username + ") =====");
            System.out.println("1. 👥 View all users");
            System.out.println("2. 🏗️ Manage city resources");
            System.out.println("3. 📋 View system logs");
            System.out.println("4. 🚪 Logout");
            System.out.print("Enter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                adminMenuInvoker.executeCommand(choice);

                if (logoutCommand.isLogoutRequested()) {
                    inAdminMenu = false;
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid choice. Please enter a number between 1 and 4.");
            }
        }
    }

    /**
     * Displays the regular user menu loop, allowing the logged-in user to
     * explore attractions, search places, view nearby services, check
     * navigation, or log out.
     *
     * @param username the username of the currently logged-in user, used
     *                 for display purposes
     */
    public static void showUserMenu(String username) {
        com.smartcity.commands.UserMenuComands.LogoutCommand logoutCommand = new com.smartcity.commands.UserMenuComands.LogoutCommand();

        userMenuInvoker.registerCommand(5, logoutCommand);

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

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                userMenuInvoker.executeCommand(choice);

                if (logoutCommand.isLogoutRequested()) {
                    inUserMenu = false;
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid choice. Please enter a number between 1 and 5.");
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
    public static void placeResultPrintout(ResultSet resultSet) {
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

    // /**
    //  * Prompts the user for a category keyword and queries the database for
    //  * all places whose category contains that keyword (case-insensitive),
    //  * printing the matching results.
    //  */
    // private static void searchByCategory() {
    //     System.out.print("\nEnter category to search: ");
    //     String searchCategory = scanner.nextLine();

    //     try (Connection connection = getConnectionOrPrintError()) {
    //         if (connection == null) {
    //             return;
    //         }

    //         try (PreparedStatement pstmt = connection.prepareStatement(SEARCH_BY_CATEGORY_QUERY)) {
    //             pstmt.setString(1, "%" + searchCategory + "%"); // Add wildcards for partial matching

    //             try (ResultSet resultSet = pstmt.executeQuery()) {
    //                 // Display search results
    //                 System.out.println("\n🔍 Search Results for Category: " + searchCategory);
    //                 System.out.println("-".repeat(50));
    //                 placeResultPrintout(resultSet);
    //             }
    //         }

    //     } catch (SQLException e) {
    //         System.out.println("❌ Error: Failed to search places by category.");
    //         System.out.println("   Error message: " + e.getMessage());
    //     }
    // }

    
    // /**
    //  * Displays the admin's city-resource management submenu loop, allowing
    //  * the admin to add, update, or delete a place, or go back to the
    //  * previous menu.
    //  */
    // public static void manageCityResources() {
    //     boolean inResourceMenu = true;

    //     while (inResourceMenu) {
    //         System.out.println("\n===== Manage City Resources =====");
    //         System.out.println("1. ➕ Add new place");
    //         System.out.println("2. ✏️ Update place");
    //         System.out.println("3. 🗑️ Delete place");
    //         System.out.println("4. ⬅️ Back");
    //         System.out.print("Enter your choice: ");

    //         int choice = scanner.nextInt();
    //         scanner.nextLine(); // Clear newline from input buffer

    //         switch (choice) {
    //             case 1:
    //                 // Add a new place to the system
    //                 addNewPlace();
    //                 break;
    //             case 2:
    //                 // Update an existing place
    //                 updatePlace();
    //                 break;
    //             case 3:
    //                 // Delete a place from the system
    //                 deletePlace();
    //                 break;
    //             case 4:
    //                 // Return to admin menu
    //                 inResourceMenu = false;
    //                 break;
    //             default:
    //                 System.out.println("❌ Invalid choice '" + choice + "'. Please enter a number between 1 and 4.");
    //         }
    //     }
    // }

    


    // /**
    //  * Prompts the admin for a place ID, fetches the existing place details,
    //  * allows the admin to optionally overwrite any field (pressing Enter
    //  * keeps the current value), validates the new values, and updates the
    //  * place in the database.
    //  */
    // private static void updatePlace() {
    //     System.out.println("\n--- Update Place ---");

    //     System.out.print("Enter place ID to update: ");
    //     int placeId;
    //     try {
    //         placeId = scanner.nextInt();
    //         scanner.nextLine();
    //     } catch (InputMismatchException e) {
    //         System.out.println("❌ Invalid ID. Please enter a number.");
    //         scanner.nextLine();
    //         return;
    //     }

    //     try (Connection connection = getConnectionOrPrintError()) {
    //         if (connection == null) {
    //             return;
    //         }

    //         // Fetch existing place
    //         String currentName, currentCategory, currentLocation, currentDescription;
    //         double currentLatitude, currentLongitude;
    //         try (PreparedStatement selectPstmt = connection.prepareStatement(SELECT_PLACE_BY_ID_QUERY)) {
    //             selectPstmt.setInt(1, placeId);
    //             try (ResultSet rs = selectPstmt.executeQuery()) {
    //                 if (!rs.next()) {
    //                     System.out.println("❌ Error: Place with ID " + placeId + " not found.");
    //                     return;
    //                 }

    //                 currentName = rs.getString("name");
    //                 currentCategory = rs.getString("category");
    //                 currentLocation = rs.getString("location");
    //                 currentDescription = rs.getString("description");
    //                 currentLatitude = rs.getDouble("latitude");
    //                 currentLongitude = rs.getDouble("longitude");
    //             }
    //         }

    //         System.out.println("\nCurrent details:");
    //         System.out.println("Name: " + currentName);
    //         System.out.println("Category: " + currentCategory);
    //         System.out.println("Location: " + currentLocation);
    //         System.out.println("Description: " + currentDescription);
    //         System.out.println("Coordinates: " + currentLatitude + ", " + currentLongitude);

    //         double newLatitude = currentLatitude;
    //         double newLongitude = currentLongitude;

    //         // Take new inputs
    //         System.out.print("\nEnter new name (or press Enter to keep current): ");
    //         String newName = scanner.nextLine();

    //         System.out.print("Enter new category (or press Enter to keep current): ");
    //         String newCategory = scanner.nextLine();

    //         System.out.print("Enter new location (or press Enter to keep current): ");
    //         String newLocation = scanner.nextLine();

    //         System.out.print("Enter new description (or press Enter to keep current): ");
    //         String newDescription = scanner.nextLine();

    //         System.out.print("Enter new latitude (or press Enter to keep current): ");
    //         String newLatitudeString = scanner.nextLine();

    //         System.out.print("Enter new longitude (or press Enter to keep current): ");
    //         String newLongitudeString = scanner.nextLine();

    //         // Use old values if input is empty
    //         if (newName.isEmpty()) {
    //             newName = currentName;
    //         }
    //         if (newCategory.isEmpty()) {
    //             newCategory = currentCategory;
    //         }
    //         if (newLocation.isEmpty()) {
    //             newLocation = currentLocation;
    //         }
    //         if (newDescription.isEmpty()) {
    //             newDescription = currentDescription;
    //         }
    //         if (newLatitudeString.isEmpty()) {
    //             newLatitude = currentLatitude;
    //         }
    //         if (newLongitudeString.isEmpty()) {
    //             newLongitude = currentLongitude;
    //         }

    //         // 🔥 VALIDATION
    //         if (newName == null || newName.trim().isEmpty()) {
    //             System.out.println("❌ Error: Place name cannot be empty.");
    //             return;
    //         }

    //         if (newLocation == null || newLocation.trim().isEmpty()) {
    //             System.out.println("❌ Error: Location cannot be empty.");
    //             return;
    //         }

    //         if (newCategory == null || newCategory.trim().isEmpty()) {
    //             System.out.println("❌ Error: Category cannot be empty.");
    //             return;
    //         }

    //         if (!newLatitudeString.trim().isEmpty()) {
    //             try {
    //                 newLatitude = Double.parseDouble(newLatitudeString);
    //             } catch (NumberFormatException e) {
    //                 System.out.println("❌ Error: Latitude is not a valid number.");
    //                 return;
    //             }
    //         }

    //         if (!newLongitudeString.trim().isEmpty()) {
    //             try {
    //                 newLongitude = Double.parseDouble(newLongitudeString);
    //             } catch (NumberFormatException e) {
    //                 System.out.println("❌ Error: Longitude is not a valid number.");
    //                 return;
    //             }
    //         }

    //         try (PreparedStatement updatePstmt = connection.prepareStatement(UPDATE_PLACE_QUERY)) {
    //             updatePstmt.setString(1, newName);
    //             updatePstmt.setString(2, newCategory);
    //             updatePstmt.setString(3, newLocation);
    //             updatePstmt.setString(4, newDescription);
    //             updatePstmt.setDouble(5, newLatitude);
    //             updatePstmt.setDouble(6, newLongitude);
    //             updatePstmt.setInt(7, placeId);

    //             int rows = updatePstmt.executeUpdate();

    //             if (rows > 0) {
    //                 System.out.println("✅ Success! Place updated successfully.");
    //             } else {
    //                 System.out.println("❌ Error: Update failed.");
    //             }
    //         }

    //     } catch (SQLException e) {
    //         System.out.println("❌ Error: Failed to update place.");
    //         System.out.println("   Error message: " + e.getMessage());
    //     }
    // }


    // /**
    //  * Prompts the admin for a place ID and deletes the corresponding place
    //  * from the database, if it exists.
    //  */
    // private static void deletePlace() {
    //     System.out.println("\n--- Delete Place ---");

    //     // Ask admin for place ID to delete
    //     System.out.print("Enter place ID to delete: ");
    //     int placeId;
    //     try {
    //         placeId = scanner.nextInt();
    //         scanner.nextLine();
    //     } catch (InputMismatchException e) {
    //         System.out.println("❌ Invalid ID. Please enter a number.");
    //         scanner.nextLine(); // Clear newline from input buffer
    //         return;
    //     }

    //     try (Connection connection = SmartCityApp.getConnectionOrPrintError()) {
    //         if (connection == null) {
    //             return;
    //         }

    //         try (PreparedStatement pstmt = connection.prepareStatement(DELETE_PLACE_QUERY)) {
    //             pstmt.setInt(1, placeId);

    //             int rowsAffected = pstmt.executeUpdate();

    //             if (rowsAffected > 0) {
    //                 System.out.println("✅ Success! Place with ID " + placeId + " has been deleted.");
    //             } else {
    //                 System.out.println("❌ Error: Place with ID " + placeId + " not found.");
    //             }
    //         }

    //     } catch (SQLException e) {
    //         System.out.println("❌ Error: Failed to delete place from database.");
    //         System.out.println("   Error message: " + e.getMessage());
    //     }
    // }

    /**
     * Validates that a place name is not null or blank.
     *
     * @param name the place name to validate
     * @return true if the name is non-null and contains non-whitespace
     *         characters, false otherwise
     */
    public static boolean isValidPlaceName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    /**
     * Validates that a location string is not null or blank.
     *
     * @param location the locations string to validate
     * @return true if the location is non-null and contains non-whitespace
     *         characters, false otherwise
     */
    public static boolean isValidLocation(String location) {
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

    public static boolean isValidCategory(String category) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isValidCategory'");
    }
}