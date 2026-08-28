package com.smartcity.commands.AdminMenuCommands;

import com.smartcity.commands.Command;

public class ViewSystemLogsCommand implements Command {

    @Override
    public void execute() {
        System.out.println("Displaying system logs...");
    }
}