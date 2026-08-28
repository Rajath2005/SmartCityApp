package com.smartcity.commands.AdminMenuCommands;

import com.smartcity.commands.Command;

public class LogoutCommand implements Command {
    private boolean logoutRequested;

    @Override
    public void execute() {
        System.out.println("Logging out. Goodbye!");
        logoutRequested = true;
    }

    public boolean isLogoutRequested() {
        return logoutRequested;
    }

}
