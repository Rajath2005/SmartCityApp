
package com.smartcity.commands;

/**
 * The Command interface defines the contract for all command objects
 * in the Smart City Guide application.
 *
 * This interface is part of the Command Design Pattern implementation,
 * which allows menu choices to be encapsulated as objects that can be
 * executed at runtime. This provides several benefits:
 *
 * 1. Decoupling: The main menu logic is decoupled from individual
 *    command implementations.
 * 2. Extensibility: New commands can be added without modifying
 *    existing switch-case statements.
 * 3. Maintainability: Each command's logic is isolated in its own class,
 *    making the code easier to understand and maintain.
 * 4. Reusability: Commands can be reused, queued, or logged if needed.
 *
 * @author SmartCityApp Team
 * @version 1.0
 */
public interface Command {
    /**
     * Executes the command logic.
     *
     * This method is called when the user selects a menu option that
     * corresponds to this command. Implementations should contain the
     * actual logic for the command (e.g., user registration, login,
     * resource management, etc.).
     *
     * @throws Exception if an error occurs during command execution
     */
    void execute() throws Exception;
}