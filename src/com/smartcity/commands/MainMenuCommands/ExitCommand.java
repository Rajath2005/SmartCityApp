package com.smartcity.commands.MainMenuCommands;

import com.smartcity.commands.Command;

/**
 * Command to exit the application.
 */
public class ExitCommand implements Command {

    /**
     * Executes the command to exit the application.
     *
     * @throws Exception if an error occurs during execution
     */
    @Override
    public void execute() throws Exception {
        System.out.println("Exiting the application. Goodbye!");
        System.exit(0);
    }

}
