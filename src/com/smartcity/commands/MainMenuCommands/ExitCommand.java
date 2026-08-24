package com.smartcity.commands.MainMenuCommands;

import com.smartcity.commands.Command;

public class ExitCommand implements com.smartcity.commands.Command {
    @Override
    public void execute() throws Exception {
        System.out.println("Exiting the application. Goodbye!");
        System.exit(0);
    }

}
