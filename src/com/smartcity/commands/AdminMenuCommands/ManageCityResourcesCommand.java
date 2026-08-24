package com.smartcity.commands.AdminMenuCommands;

import java.util.Scanner;

import com.smartcity.commands.Command;
import com.smartcity.commands.CommandInvoker;

public class ManageCityResourcesCommand implements Command {

    private final Scanner scanner;
    private final CommandInvoker resourceMenuInvoker;

    public ManageCityResourcesCommand(Scanner scanner) {
        this.scanner = scanner;
        this.resourceMenuInvoker = new CommandInvoker(scanner);

        resourceMenuInvoker.registerCommand(1, new AddPlaceCommand());
        resourceMenuInvoker.registerCommand(2, new UpdatePlaceCommand());
        resourceMenuInvoker.registerCommand(3, new DeletePlaceCommand());
        //resourceMenuInvoker.registerCommand(4, new BackCommand());
    }

    @Override
    public void execute() {
        boolean inResourceMenu = true;

        while (inResourceMenu) {
            System.out.println("\n===== Manage City Resources =====");
            System.out.println("1. ➕ Add new place");
            System.out.println("2. ✏️ Update place");
            System.out.println("3. 🗑️ Delete place");
            System.out.println("4. ⬅️ Back");
            System.out.print("Enter your choice: ");

            int choice;

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid choice. Please enter a number between 1 and 4.");
                continue;
            }

            if (choice == 4) {
                inResourceMenu = false;
            } else {
                resourceMenuInvoker.executeCommand(choice);
            }
        }
    }
}