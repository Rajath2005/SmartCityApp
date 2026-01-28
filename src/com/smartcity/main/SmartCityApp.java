package com.smartcity.main;

import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;
import com.smartcity.model.User;
import com.smartcity.model.Place;

public class SmartCityApp {
    // In-memory user store: key = username, value = User object
    private static HashMap<String, User> users = new HashMap<>();

    // In-memory place store: list of city places
    private static ArrayList<Place> places = new ArrayList<>();

    // Scanner object shared across methods
    private static Scanner scanner = new Scanner(System.in);

    static {
        // Initialize places with sample data
        places.add(new Place(1, "Grand Hotel", "Hotel", "Downtown", "5-star luxury hotel in the heart of the city"));
        places.add(new Place(2, "Pizza Palace", "Restaurant", "Main Street", "Italian restaurant with authentic cuisine"));
        places.add(new Place(3, "City Museum", "Tourist Spot", "Cultural District", "Historical museum showcasing city heritage"));
        places.add(new Place(4, "Central Park", "Park", "North Avenue", "Large green space for recreation and relaxation"));
        places.add(new Place(5, "Tech Hub Cafe", "Cafe", "Business Zone", "Modern cafe popular with tech professionals"));
    }

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

    // Implement registration with in-memory storage
    private static void register() {
        System.out.println("\n--- Registration ---");

        // Ask user for username
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        // Check if username already exists
        if (users.containsKey(username)) {
            System.out.println("Error: Username already exists. Please choose a different username.");
            return;
        }

        // Ask user for password
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        // Validate password is not empty
        if (password.isEmpty()) {
            System.out.println("Error: Password cannot be empty.");
            return;
        }

        // Create new user and store in HashMap
        User newUser = new User(username, password);
        users.put(username, newUser);

        System.out.println("Success! User '" + username + "' registered successfully.");
    }

    // Implement login with username and password validation
    private static void login() {
        System.out.println("\n--- Login ---");

        // Ask user for username
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        // Check if username exists in HashMap
        if (!users.containsKey(username)) {
            System.out.println("Error: Username not found. Please register first.");
            return;
        }

        // Ask user for password
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        // Get the user from HashMap and verify password
        User user = users.get(username);
        if (user.password.equals(password)) {
            System.out.println("Success! Welcome back, " + username + "!");

            // Check user role and show appropriate menu
            if (user.role.equals("ADMIN")) {
                showAdminMenu(username);
            } else {
                showUserMenu(username);
            }
        } else {
            System.out.println("Error: Incorrect password. Please try again.");
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
                    System.out.println("Managing city resources...");
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
            System.out.println("2. View nearby services");
            System.out.println("3. Check navigation");
            System.out.println("4. Logout");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear newline from input buffer

            switch (choice) {
                case 1:
                    System.out.println("Loading city attractions...");
                    break;
                case 2:
                    System.out.println("Finding nearby services...");
                    break;
                case 3:
                    System.out.println("Opening navigation...");
                    break;
                case 4:
                    System.out.println("Logging out. Goodbye!");
                    inUserMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
