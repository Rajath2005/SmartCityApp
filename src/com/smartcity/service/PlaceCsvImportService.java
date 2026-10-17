package com.smartcity.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlaceCsvImportService {
    private static final String INSERT_SQL = "INSERT INTO places "
        + "(id, name, category, location, description, latitude, longitude) "
        + "VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String[] HEADERS = {
        "id", "name", "category", "location", "description", "latitude", "longitude"
    };

    private PlaceCsvImportService() {
    }

    /** Reads, validates and inserts a CSV file atomically; returns inserted row count. */
    public static int importFile(Path file, Connection connection) throws IOException, SQLException {
        List<PlaceRow> places = readPlaces(file);
        if (places.isEmpty()) {
            return 0;
        }

        boolean originalAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
                for (PlaceRow place : places) {
                    statement.setInt(1, place.id());
                    statement.setString(2, place.name());
                    statement.setString(3, place.category());
                    statement.setString(4, place.location());
                    statement.setString(5, place.description());
                    statement.setDouble(6, place.latitude());
                    statement.setDouble(7, place.longitude());
                    statement.addBatch();
                }
                statement.executeBatch();
                connection.commit();
                return places.size();
            }
        } catch (SQLException | RuntimeException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackError) {
                e.addSuppressed(rollbackError);
            }
            throw e;
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    /** Parses a CSV file and validates all records before database insertion. */
    static List<PlaceRow> readPlaces(Path file) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setIgnoreSurroundingSpaces(true)
            .build();
        try (CSVParser parser = CSVParser.parse(file, StandardCharsets.UTF_8, format)) {
            List<String> actualHeaders = parser.getHeaderNames();
            if (actualHeaders.size() != HEADERS.length) {
                throw new IllegalArgumentException("Invalid CSV header. Expected: " + String.join(",", HEADERS));
            }
            for (int i = 0; i < HEADERS.length; i++) {
                if (!HEADERS[i].equalsIgnoreCase(actualHeaders.get(i).trim())) {
                    throw new IllegalArgumentException("Invalid CSV header. Expected: " + String.join(",", HEADERS));
                }
            }

            List<PlaceRow> places = new ArrayList<>();
            Set<Integer> ids = new HashSet<>();
            for (CSVRecord record : parser) {
                long rowNumber = record.getRecordNumber() + 1;
                if (record.size() != HEADERS.length) {
                    throw new IllegalArgumentException("Row " + rowNumber + ": expected 7 columns");
                }
                try {
                    int id = Integer.parseInt(record.get(0).trim());
                    String name = required(record.get(1), "name");
                    String category = required(record.get(2), "category");
                    String location = required(record.get(3), "location");
                    String description = record.get(4).trim();
                    double latitude = Double.parseDouble(record.get(5).trim());
                    double longitude = Double.parseDouble(record.get(6).trim());
                    if (id <= 0 || !Double.isFinite(latitude) || !Double.isFinite(longitude)
                        || latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
                        throw new IllegalArgumentException("invalid ID or coordinates");
                    }
                    if (!ids.add(id)) {
                        throw new IllegalArgumentException("duplicate ID " + id);
                    }
                    places.add(new PlaceRow(id, name, category, location, description, latitude, longitude));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Row " + rowNumber + ": invalid number", e);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Row " + rowNumber + ": " + e.getMessage(), e);
                }
            }
            return places;
        }
    }

    private static String required(String value, String field) {
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be empty");
        }
        return trimmed;
    }

    record PlaceRow(int id, String name, String category, String location,
                    String description, double latitude, double longitude) {
    }
}

