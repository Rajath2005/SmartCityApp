package com.smartcity.commands.UserMenuComands;

import com.smartcity.commands.Command;
import com.smartcity.main.SmartCityApp;

public class SearchByLocationCommand implements Command {
    @Override
    public void execute() {
        SmartCityApp.searchByLocation();
    }
}