package com.smartcity.commands.UserMenuComands;

import java.util.Scanner;

import com.smartcity.commands.Command;
import com.smartcity.commands.CommandInvoker;

/**
 * Command to search for places based on user input.
 */
public class SearchPlacesCommand implements Command {

    private final Scanner scanner;
    private final CommandInvoker searchInvoker;

    /**
     * Constructor to initialize the SearchPlacesCommand with a Scanner.
     *
     * @param scanner the Scanner object for user input
     */
    public SearchPlacesCommand(Scanner scanner) {
        this.scanner = scanner;
        this.searchInvoker = new CommandInvoker(scanner);

        searchInvoker.registerCommand(1, new SearchByCategoryCommand(scanner));
        searchInvoker.registerCommand(2, new SearchByLocationCommand(scanner));
    }

    /**
     * Executes the command to search for places.
     */
    @Override
    public void execute() {
        boolean inSearchMenu = true;

        while (inSearchMenu) {
            System.out.println("\n===== Search Places =====");
            System.out.println("1. 🏷️ Search by category");
            System.out.println("2. 📌 Search by location");
            System.out.println("3. ⬅️ Back");
            System.out.print("Enter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                if (choice == 3) {
                    inSearchMenu = false;
                } else {
                    searchInvoker.executeCommand(choice);
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid choice. Please enter a number between 1 and 3.");
            }
        }
    }
}