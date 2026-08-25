package com.smartcity.commands.UserMenuComands;

import com.smartcity.commands.Command;
import com.smartcity.main.SmartCityApp;

public class ViewPlacesByIdCommand implements Command {
    @Override
    public void execute() {
        SmartCityApp.viewAllPlacesSortedById();
    }
}