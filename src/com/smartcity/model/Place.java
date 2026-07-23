package com.smartcity.model;

/**
 * Represents a Place or attraction in the Smart City Guide application.
 * This model stores all details related to a specific location, such as
 * its category, name, and description.
 *
 * @author Rajath2005 (Original Creator)
 * @version 1.0
 */
public class Place {
    private int id;
    private String name;
    private String category;
    private String location;
    private String description;
    private double latitude;
    private double longitude;

    /**
     * Constructs a new Place object with legacy parameters. Latitude and longitude are initialized,
     * but not set correctly.
     *
     * @param id          The unique identifier for the place.
     * @param name        The name of the place.
     * @param category    The category this place belongs to (e.g., Hotel, Park).
     * @param location    The general location or address of the place.
     * @param description A brief description of the place.
     */
    public Place(int id, String name, String category, String location, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.location = location;
        this.description = description;
        latitude = -1;
        longitude = -1;
    }


    /**
     * Constructs a new Place object with all necessary details.
     * @param id          The unique identifier for the place.
     * @param name        The name of the place.
     * @param category    The category this place belongs to (e.g., Hotel, Park).
     * @param location    The general location or address of the place.
     * @param description A brief description of the place.
     * @param latitude    Latitude coordinates of the place.
     * @param longitude   Longitude coordinates of the place.
     */
    public Place(int id, String name, String category, String location, String description,
                 double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.location = location;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Gets the unique identifier of the place.
     *
     * @return The place ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the place.
     *
     * @param id The new place ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the name of the place.
     *
     * @return The name of the place.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the place.
     *
     * @param name The new name of the place.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the category of the place.
     *
     * @return The category (e.g., Hotel, Park).
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category of the place.
     *
     * @param category The new category.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the location of the place.
     *
     * @return The location.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location of the place.
     *
     * @param location The new location.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets the description of the place.
     *
     * @return The description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the place.
     *
     * @param description The new description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns a string representation of the Place object.
     *
     * @return A formatted string detailing the place's attributes.
     */
    @Override
    public String toString() {
        return "Place{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    /**
     * Gets the latitude coordinates of the place.
     *
     * @return The latitude coordinates.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude coordinates of the place.
     *
     * @param latitude The new latitude coordinates.
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the longitude coordinates of the place.
     *
     * @return The longitude coordinates.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude coordinates of the place.
     *
     * @param longitude The new longitude coordinates.
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}