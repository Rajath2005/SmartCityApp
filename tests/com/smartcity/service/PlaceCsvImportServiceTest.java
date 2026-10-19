package com.smartcity.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Unit tests for CSV parsing and validation (no database required). */
class PlaceCsvImportServiceTest {

    private static final String HEADER =
            "id,name,category,location,description,latitude,longitude\n";
    private static final String VALID_ROW =
            "1,City Museum,Museum,Center,History museum,48.7,21.2\n";

    @TempDir
    Path tempDir;

    private Path csv(String content) throws IOException {
        Path file = tempDir.resolve("places.csv");
        Files.writeString(file, content, StandardCharsets.UTF_8);
        return file;
    }

    private List<PlaceCsvImportService.PlaceRow> parse(String content) throws IOException {
        return PlaceCsvImportService.readPlaces(csv(content));
    }

    private IllegalArgumentException invalidRow(String row) throws IOException {
        return assertThrows(IllegalArgumentException.class, () -> parse(HEADER + row + "\n"));
    }

    @Test
    void readsAllFieldsOfValidRow() throws IOException {
        var places = parse(HEADER + VALID_ROW);
        assertEquals(1, places.size());
        var place = places.get(0);
        assertAll(
                () -> assertEquals(1, place.id()),
                () -> assertEquals("City Museum", place.name()),
                () -> assertEquals("Museum", place.category()),
                () -> assertEquals("Center", place.location()),
                () -> assertEquals("History museum", place.description()),
                () -> assertEquals(48.7, place.latitude()),
                () -> assertEquals(21.2, place.longitude())
        );
    }

    @Test
    void readsMultipleRowsInOriginalOrder() throws IOException {
        var places = parse(HEADER + VALID_ROW
                + "2,Park,Recreation,North,Green area,48.8,21.3\n"
                + "3,Library,Education,South,Books,48.9,21.4\n");
        assertEquals(3, places.size());
        assertEquals(List.of(1, 2, 3), places.stream().map(PlaceCsvImportService.PlaceRow::id).toList());
        assertEquals(List.of("City Museum", "Park", "Library"),
                places.stream().map(PlaceCsvImportService.PlaceRow::name).toList());
    }

    @Test
    void returnsEmptyListForHeaderOnly() throws IOException {
        assertTrue(parse(HEADER).isEmpty());
    }

    @Test
    void acceptsCaseInsensitiveHeaders() throws IOException {
        var places = parse("ID,NAME,CATEGORY,LOCATION,DESCRIPTION,LATITUDE,LONGITUDE\n" + VALID_ROW);
        assertEquals(1, places.size());
    }

    @Test
    void trimsSpacesAroundHeadersAndValues() throws IOException {
        var places = parse(" id , name , category , location , description , latitude , longitude \n"
                + " 1 , City Museum , Museum , Center , History museum , 48.7 , 21.2 \n");
        assertEquals(1, places.size());
        assertEquals("City Museum", places.get(0).name());
        assertEquals("History museum", places.get(0).description());
    }

    @Test
    void parsesCommaInsideQuotedField() throws IOException {
        var places = parse(HEADER + "1,\"Museum, Gallery\",Museum,Center,Exhibition,48.7,21.2\n");
        assertEquals("Museum, Gallery", places.get(0).name());
    }

    @Test
    void acceptsEmptyDescription() throws IOException {
        var places = parse(HEADER + "1,Museum,Museum,Center,,48.7,21.2\n");
        assertEquals("", places.get(0).description());
    }

    @Test
    void acceptsBoundaryCoordinates() throws IOException {
        var places = parse(HEADER
                + "1,North,Point,World,,90,180\n"
                + "2,South,Point,World,,-90,-180\n"
                + "3,Zero,Point,World,,0,0\n");
        assertEquals(3, places.size());
        assertEquals(90.0, places.get(0).latitude());
        assertEquals(-180.0, places.get(1).longitude());
        assertEquals(0.0, places.get(2).latitude());
    }

    @Test
    void rejectsEmptyFile() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> parse(""));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "name,category,location,description,latitude,longitude",
            "id,name,category,location,description,latitude",
            "id,name,category,location,description,latitude,longitude,extra",
            "id,category,name,location,description,latitude,longitude",
            "id,name,category,location,description,latitude,wrong"
    })
    void rejectsInvalidHeaders(String header) throws IOException {
        assertThrows(IllegalArgumentException.class, () -> parse(header + "\n" + VALID_ROW));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1,Museum,Museum,Center,Description,48.7",
            "1,Museum,Museum,Center,Description,48.7,21.2,extra"
    })
    void rejectsWrongColumnCount(String row) throws IOException {
        var error = invalidRow(row);
        assertTrue(error.getMessage().contains("expected 7 columns"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1, ,Museum,Center,Description,48.7,21.2",
            "1,Museum, ,Center,Description,48.7,21.2",
            "1,Museum,Museum, ,Description,48.7,21.2"
    })
    void rejectsBlankRequiredFields(String row) throws IOException {
        var error = invalidRow(row);
        assertTrue(error.getMessage().contains("must not be empty"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "abc", "1.5", "2147483648", ""})
    void rejectsInvalidIds(String id) throws IOException {
        invalidRow(id + ",Museum,Museum,Center,Description,48.7,21.2");
    }

    @Test
    void rejectsDuplicateIdInSameFile() throws IOException {
        var error = assertThrows(IllegalArgumentException.class,
                () -> parse(HEADER + VALID_ROW + "1,Park,Recreation,North,Green,48.8,21.3\n"));
        assertTrue(error.getMessage().contains("duplicate ID 1"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"90.0001", "-90.0001", "NaN", "Infinity", "-Infinity", "abc", ""})
    void rejectsInvalidLatitude(String latitude) throws IOException {
        invalidRow("1,Museum,Museum,Center,Description," + latitude + ",21.2");
    }

    @ParameterizedTest
    @ValueSource(strings = {"180.0001", "-180.0001", "NaN", "Infinity", "-Infinity", "abc", ""})
    void rejectsInvalidLongitude(String longitude) throws IOException {
        invalidRow("1,Museum,Museum,Center,Description,48.7," + longitude);
    }

    @Test
    void reportsSecondDataRowWhenInvalid() throws IOException {
        var error = assertThrows(IllegalArgumentException.class,
                () -> parse(HEADER + VALID_ROW + "2,Park,Recreation,North,Green,not-a-number,21.3\n"));
        assertTrue(error.getMessage().contains("Row 3:"));
    }

    @Test
    void rejectsMissingFile() {
        Path missing = tempDir.resolve("missing.csv");
        assertThrows(IOException.class, () -> PlaceCsvImportService.readPlaces(missing));
    }

    @Test
    void rejectsDirectoryInsteadOfFile() {
        assertThrows(IOException.class, () -> PlaceCsvImportService.readPlaces(tempDir));
    }
}
