-- Database creation
CREATE DATABASE IF NOT EXISTS smart_city_guide;
USE smart_city_guide;

-- Table for Users
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(20) DEFAULT 'USER'
);

-- Table for Places
CREATE TABLE IF NOT EXISTS places (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    location VARCHAR(100) NOT NULL,
    description TEXT
);
-- Add Coordinate support
ALTER TABLE places
    ADD COLUMN latitude DOUBLE,
    ADD COLUMN longitude DOUBLE;

-- Insert sample admin account (Change password in production!)
INSERT IGNORE INTO users (username, password, email, role)
VALUES ('admin', 'Admin@123', 'admin@example.com', 'ADMIN');
