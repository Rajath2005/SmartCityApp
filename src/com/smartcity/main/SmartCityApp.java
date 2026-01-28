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

    // Display all places in the city
    private static void viewAllPlaces() {
        // Check if places list is empty
        if (places.isEmpty()) {
            System.out.println("\n❌ No places available at the moment.");
            return;
        }

        // Display header
        System.out.println("\n🏙️  ===== ALL CITY ATTRACTIONS =====");
        System.out.println("-".repeat(50));

        // Loop through and display each place
        for (int i = 0; i < places.size(); i++) {
            Place place = places.get(i);
            System.out.println("\n📍 Place " + (i + 1) + ":");
            System.out.println("   Name: " + place.name);
            System.out.println("   Category: " + place.category);
            System.out.println("   Location: " + place.location);
            System.out.println("   Description: " + place.description);
        }

        System.out.println("\n" + "-".repeat(50));
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

    // Search places by category (case-insensitive)
    private static void searchByCategory() {
        System.out.print("\nEnter category to search: ");
        String searchCategory = scanner.nextLine().toLowerCase(); // Convert to lowercase for comparison

        // List to track if any results found
        boolean found = false;

        System.out.println("\n🔍 Search Results for Category: " + searchCategory);
        System.out.println("-".repeat(50));

        // Loop through all places and find matches
        for (Place place : places) {
            // Case-insensitive comparison
            if (place.category.toLowerCase().contains(searchCategory)) {
                System.out.println("\n📍 " + place.name);
                System.out.println("   Category: " + place.category);
                System.out.println("   Location: " + place.location);
                System.out.println("   Description: " + place.description);
                found = true;
            }
        }

        // Handle no results found
        if (!found) {
            System.out.println("❌ No places found in category: " + searchCategory);
        }

        System.out.println("-".repeat(50));
    }

    // Search places by location (case-insensitive)
    private static void searchByLocation() {
        System.out.print("\nEnter location to search: ");
        String searchLocation = scanner.nextLine().toLowerCase(); // Convert to lowercase for comparison

        // List to track if any results found
        boolean found = false;

        System.out.println("\n🔍 Search Results for Location: " + searchLocation);
        System.out.println("-".repeat(50));

        // Loop through all places and find matches
        for (Place place : places) {
            // Case-insensitive comparison
            if (place.location.toLowerCase().contains(searchLocation)) {
                System.out.println("\n📍 " + place.name);
                System.out.println("   Category: " + place.category);
                System.out.println("   Location: " + place.location);
                System.out.println("   Description: " + place.description);
                found = true;
            }
        }

        // Handle no results found
        if (!found) {
            System.out.println("❌ No places found in location: " + searchLocation);
        }

        System.out.println("-".repeat(50));
    }
}
