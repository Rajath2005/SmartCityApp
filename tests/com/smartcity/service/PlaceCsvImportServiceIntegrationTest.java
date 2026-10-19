package com.smartcity.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PlaceCsvImportServiceIntegrationTest {
    private final String testDb = "smart_city_guide_test_" + UUID.randomUUID().toString().replace("-", "");
    private static final String HEADER = "id,name,category,location,description,latitude,longitude\n";
    private static final String ROW_1 = "900001,Museum,Museum,Center,History,48.72,21.26\n";
    private static final String ROW_2 = "900002,Park,Park,North,Green area,48.73,21.27\n";

    @TempDir
    Path tempDir;

    private String baseUrl;
    private String username;
    private String password;

    @BeforeAll
    void createDatabase() throws SQLException {
        // JDBC_HOST=localhost when MySQL's port is published to the host.
        // JDBC_HOST=mysql-db when running inside the Docker Compose network.
        String host = System.getenv().getOrDefault("JDBC_HOST", "localhost");
        String port = System.getenv().getOrDefault("JDBC_PORT", "3306");
        username = System.getenv().getOrDefault("DB_USER", "root");
        password = System.getenv().getOrDefault("DB_PASSWORD", "root");
        baseUrl = "jdbc:mysql://" + host + ":" + port + "/";

        // Use an isolated, dedicated schema; never touch smart_city_guide.
        try (Connection server = DriverManager.getConnection(baseUrl, username, password);
             Statement statement = server.createStatement()) {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + testDb + "`");
        }
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS places ("
                    + "id INT PRIMARY KEY, "
                    + "name VARCHAR(100) NOT NULL, "
                    + "category VARCHAR(50) NOT NULL, "
                    + "location VARCHAR(100) NOT NULL, "
                    + "description TEXT, "
                    + "latitude DOUBLE, "
                    + "longitude DOUBLE"
                    + ")");
        }
    }

    @BeforeEach
    void cleanTable() throws SQLException {
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM places");
        }
    }

    @AfterAll
    void dropDatabase() throws SQLException {
        if (baseUrl == null) return;
        try (Connection server = DriverManager.getConnection(baseUrl, username, password);
             Statement statement = server.createStatement()) {
            statement.executeUpdate("DROP DATABASE IF EXISTS `" + testDb + "`");
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(baseUrl + testDb, username, password);
    }

    private Path csv(String content) throws IOException {
        Path file = tempDir.resolve("places-" + UUID.randomUUID() + ".csv");
        return Files.writeString(file, content, StandardCharsets.UTF_8);
    }

    private int countPlaces() throws SQLException {
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM places")) {
            assertTrue(result.next());
            return result.getInt(1);
        }
    }

    @Test
    void importsSinglePlace() throws Exception {
        try (Connection connection = connect()) {
            assertEquals(1, PlaceCsvImportService.importFile(csv(HEADER + ROW_1), connection));
        }
        assertEquals(1, countPlaces());
    }

    @Test
    void importsMultiplePlacesAndReturnsCorrectCount() throws Exception {
        try (Connection connection = connect()) {
            assertEquals(2, PlaceCsvImportService.importFile(csv(HEADER + ROW_1 + ROW_2), connection));
        }
        assertEquals(2, countPlaces());
    }

    @Test
    void storesAllSevenFieldsCorrectly() throws Exception {
        try (Connection connection = connect()) {
            PlaceCsvImportService.importFile(csv(HEADER + ROW_1), connection);
        }
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, name, category, location, description, latitude, longitude FROM places WHERE id = ?")) {
            statement.setInt(1, 900001);
            try (ResultSet result = statement.executeQuery()) {
                assertTrue(result.next());
                assertEquals(900001, result.getInt("id"));
                assertEquals("Museum", result.getString("name"));
                assertEquals("Museum", result.getString("category"));
                assertEquals("Center", result.getString("location"));
                assertEquals("History", result.getString("description"));
                assertEquals(48.72, result.getDouble("latitude"), 0.000001);
                assertEquals(21.26, result.getDouble("longitude"), 0.000001);
                assertFalse(result.next());
            }
        }
    }

    @Test
    void duplicateDatabaseIdFailsWithoutAddingRows() throws Exception {
        try (Connection connection = connect()) {
            PlaceCsvImportService.importFile(csv(HEADER + ROW_1), connection);
            assertThrows(SQLException.class,
                    () -> PlaceCsvImportService.importFile(csv(HEADER + ROW_1), connection));
        }
        assertEquals(1, countPlaces());
    }

    @Test
    void rollsBackEarlierBatchRowsWhenLaterRowConflicts() throws Exception {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO places (id, name, category, location) "
                    + "VALUES (900002, 'Existing', 'Other', 'Center')");
            assertThrows(SQLException.class, () -> PlaceCsvImportService.importFile(
                    csv(HEADER + ROW_1 + ROW_2), connection));
        }
        assertEquals(1, countPlaces());
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement("SELECT name FROM places WHERE id = ?")) {
            statement.setInt(1, 900002);
            try (ResultSet result = statement.executeQuery()) {
                assertTrue(result.next());
                assertEquals("Existing", result.getString(1));
            }
        }
    }

    @Test
    void invalidCsvDoesNotInsertAnyRows() throws Exception {
        String invalid = HEADER + ROW_1 + "900003,,Park,North,Invalid,48.73,21.27\n";
        try (Connection connection = connect()) {
            assertThrows(IllegalArgumentException.class,
                    () -> PlaceCsvImportService.importFile(csv(invalid), connection));
        }
        assertEquals(0, countPlaces());
    }

    @Test
    void headerOnlyFileReturnsZeroAndLeavesDatabaseEmpty() throws Exception {
        try (Connection connection = connect()) {
            assertEquals(0, PlaceCsvImportService.importFile(csv(HEADER), connection));
        }
        assertEquals(0, countPlaces());
    }

    @Test
    void repeatedImportFailsAndKeepsOriginalRows() throws Exception {
        Path file = csv(HEADER + ROW_1 + ROW_2);
        try (Connection connection = connect()) {
            assertEquals(2, PlaceCsvImportService.importFile(file, connection));
            assertThrows(SQLException.class, () -> PlaceCsvImportService.importFile(file, connection));
        }
        assertEquals(2, countPlaces());
    }

    @Test
    void restoresAutoCommitAfterSuccessfulImport() throws Exception {
        try (Connection connection = connect()) {
            assertTrue(connection.getAutoCommit());
            PlaceCsvImportService.importFile(csv(HEADER + ROW_1), connection);
            assertTrue(connection.getAutoCommit());
        }
    }

    @Test
    void restoresAutoCommitAfterFailedImport() throws Exception {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO places (id, name, category, location) "
                    + "VALUES (900001, 'Existing', 'Other', 'Center')");
            assertThrows(SQLException.class,
                    () -> PlaceCsvImportService.importFile(csv(HEADER + ROW_1), connection));
            assertTrue(connection.getAutoCommit());
        }
        assertEquals(1, countPlaces());
    }
}
