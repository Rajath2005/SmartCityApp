package com.smartcity.model;

public class Place {
    public int id;
    public String name;
    public String category;
    public String location;
    public String description;

    // Constructor to create a new place
    public Place(int id, String name, String category, String location, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.location = location;
        this.description = description;
    }
}
