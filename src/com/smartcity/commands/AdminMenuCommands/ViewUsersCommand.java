package com.smartcity.commands.AdminMenuCommands;

import com.smartcity.commands.Command;

public class ViewUsersCommand implements Command {

    @Override
    public void execute() {
        System.out.println("Viewing all registered users...");
    }
}