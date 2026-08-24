
package com.smartcity.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * CommandInvoker manages the execution of Command objects in the Smart City Guide
 * application. This class implements the Invoker part of the Command Design Pattern.
 *
 * Responsibilities:
 * 1. Maintains a registry (Map) of command choices to Command implementations
 * 2. Allows registration of new commands at runtime
 * 3. Executes commands based on user menu choice
 * 4. Handles invalid/unmapped command choices gracefully
 *
 * This design provides several benefits:
 * - Eliminates massive switch-case statements from the main controller
 * - Makes it easy to add new commands without modifying existing code
 * - Centralizes command routing logic in one place
 * - Allows commands to be registered dynamically
 *
 * @author SmartCityApp Team
 * @version 1.0
 */
public class CommandInvoker {

    /**
     * Map that stores the relationship between menu choice (Integer) and
     * the corresponding Command implementation to execute.
     */
    private final Map<Integer, Command> commandMap;

    /**
     * Scanner instance shared across all commands that require user input.
     */
    private final Scanner scanner;

    /**
     * Constructs a CommandInvoker with an empty command map.
     *
     * @param scanner the Scanner instance to be passed to commands that need user input
     */
    public CommandInvoker(Scanner scanner) {
        this.commandMap = new HashMap<>();
        this.scanner = scanner;
    }

    /**
     * Registers a command for a specific menu choice.
     *
     * This method associates a menu choice number with a Command implementation.
     * If a command is already registered for this choice, it will be overwritten.
     *
     * Example usage:
     * <pre>
     *     invoker.registerCommand(1, new RegisterCommand(scanner));
     *     invoker.registerCommand(2, new LoginCommand(scanner));
     *     invoker.registerCommand(3, new ExitCommand());
     * </pre>
     *
     * @param choice the menu choice number (e.g., 1, 2, 3)
     * @param command the Command object to execute for this choice
     */
    public void registerCommand(int choice, Command command) {
        commandMap.put(choice, command);
    }

    /**
     * Executes the command associated with the given menu choice.
     *
     * This method looks up the command in the map and executes it. If no command
     * is found for the given choice, a user-friendly error message is displayed.
     *
     * @param choice the menu choice number entered by the user
     * @return true if the command was found and executed successfully, false otherwise
     */
    public boolean executeCommand(int choice) {
        Command command = commandMap.get(choice);

        if (command == null) {
            System.out.println("❌ Invalid choice. Please try again.");
            return false;
        }

        try {
            command.execute();
            return true;
        } catch (Exception e) {
            System.out.println("❌ Error executing command: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns the Scanner instance used by this invoker.
     *
     * This is useful for menu display and other CLI operations that need
     * to interact with the user input stream.
     *
     * @return the Scanner instance
     */
    public Scanner getScanner() {
        return scanner;
    }

    /**
     * Checks whether a command is registered for the given menu choice.
     *
     * This method can be used to validate user input before attempting execution.
     *
     * @param choice the menu choice to check
     * @return true if a command is registered for this choice, false otherwise
     */
    public boolean hasCommand(int choice) {
        return commandMap.containsKey(choice);
    }

    /**
     * Clears all registered commands.
     *
     * This method is useful for resetting the invoker state or preparing
     * for a different menu context.
     */
    public void clearCommands() {
        commandMap.clear();
    }
}