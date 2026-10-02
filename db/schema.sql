-- Smart City Guide database schema and sample data.
-- Run with: mysql -u root -p < db/schema.sql
-- Safe to run more than once: tables and rows are only created if missing.

CREATE DATABASE IF NOT EXISTS smart_city_guide;
USE smart_city_guide;

-- users: everyone who can log in. role is 'USER' or 'ADMIN'.
-- password holds a SHA-256 hex hash (plaintext rows are hashed on app startup).
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(20) DEFAULT 'USER'
);

-- places: city attractions shown to users and managed by admins.
-- id is entered by the admin when adding a place, so it is not auto-incremented.
CREATE TABLE IF NOT EXISTS places (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    location VARCHAR(100) NOT NULL,
    description TEXT,
    latitude DOUBLE,
    longitude DOUBLE
);

-- Sample admin account (change the password in production!)
INSERT IGNORE INTO users (username, password, email, role)
VALUES ('admin', 'Admin@123', 'admin@example.com', 'ADMIN');

-- Sample places so a fresh setup has something to browse
INSERT IGNORE INTO places (id, name, category, location, description, latitude, longitude) VALUES
    (1, 'Central Park', 'Park', 'City Centre', 'Large green park with a lake and walking trails.', 12.9716, 77.5946),
    (2, 'Grand Hotel Downtown', 'Hotel', 'Main Street', 'Historic hotel close to the shopping district.', 12.9750, 77.6050),
    (3, 'City Museum', 'Museum', 'Old Town', 'Exhibits on the history and culture of the city.', 12.9650, 77.5870),
    (4, 'Spice Garden', 'Restaurant', 'Market Road', 'Popular spot for local cuisine.', 12.9800, 77.6000);
