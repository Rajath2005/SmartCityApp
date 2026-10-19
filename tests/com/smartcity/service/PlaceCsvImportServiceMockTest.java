package com.smartcity.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PlaceCsvImportServiceMockTest {
    private static final String HEADER =
            "id,name,category,location,description,latitude,longitude\n";
    private static final String ROW =
            "901,Museum,Museum,Center,History,48.7,21.2\n";

    @TempDir
    Path tempDir;

    private Connection connection;
    private PreparedStatement statement;

    @BeforeEach
    void setUp() throws SQLException {
        connection = mock(Connection.class);
        statement = mock(PreparedStatement.class);
        when(connection.getAutoCommit()).thenReturn(true);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
    }

    private Path csv(String body) throws IOException {
        Path path = tempDir.resolve("places.csv");
        Files.writeString(path, body, StandardCharsets.UTF_8);
        return path;
    }

    @Test
    void successfulImportUsesBatchCommitsAndRestoresAutoCommit() throws Exception {
        int count = PlaceCsvImportService.importFile(csv(HEADER + ROW), connection);

        assertEquals(1, count);
        verify(connection).setAutoCommit(false);
        verify(connection).prepareStatement(contains("INSERT INTO places"));
        verify(statement).setInt(1, 901);
        verify(statement).setString(2, "Museum");
        verify(statement).setString(3, "Museum");
        verify(statement).setString(4, "Center");
        verify(statement).setString(5, "History");
        verify(statement).setDouble(6, 48.7);
        verify(statement).setDouble(7, 21.2);
        verify(statement).addBatch();
        verify(statement).executeBatch();
        verify(connection).commit();
        verify(connection, never()).rollback();
        verify(connection).setAutoCommit(true);
        verify(statement).close();

        var order = inOrder(connection, statement);
        order.verify(connection).setAutoCommit(false);
        order.verify(connection).prepareStatement(anyString());
        order.verify(statement).executeBatch();
        order.verify(connection).commit();
        order.verify(statement).close();
        order.verify(connection).setAutoCommit(true);
    }

    @Test
    void multipleRowsAreAddedToOneBatch() throws Exception {
        String secondRow = "902,Park,Park,North,Green area,48.8,21.3\n";

        assertEquals(2, PlaceCsvImportService.importFile(csv(HEADER + ROW + secondRow), connection));
        verify(statement, times(2)).addBatch();
        verify(statement, times(1)).executeBatch();
        verify(connection).commit();
    }

    @Test
    void sqlFailureRollsBackAndRestoresAutoCommit() throws Exception {
        SQLException failure = new SQLException("Batch failed");
        when(statement.executeBatch()).thenThrow(failure);

        SQLException actual = assertThrows(SQLException.class,
                () -> PlaceCsvImportService.importFile(csv(HEADER + ROW), connection));

        assertSame(failure, actual);
        verify(connection).rollback();
        verify(connection, never()).commit();
        verify(connection).setAutoCommit(true);
        verify(statement).close();
    }

    @Test
    void rollbackFailureIsSuppressedOnOriginalException() throws Exception {
        SQLException batchFailure = new SQLException("Batch failed");
        SQLException rollbackFailure = new SQLException("Rollback failed");
        when(statement.executeBatch()).thenThrow(batchFailure);
        doThrow(rollbackFailure).when(connection).rollback();

        SQLException actual = assertThrows(SQLException.class,
                () -> PlaceCsvImportService.importFile(csv(HEADER + ROW), connection));

        assertSame(batchFailure, actual);
        assertEquals(1, actual.getSuppressed().length);
        assertSame(rollbackFailure, actual.getSuppressed()[0]);
        verify(connection).setAutoCommit(true);
    }

    @Test
    void originalAutoCommitFalseIsRestored() throws Exception {
        when(connection.getAutoCommit()).thenReturn(false);

        assertEquals(1, PlaceCsvImportService.importFile(csv(HEADER + ROW), connection));

        verify(connection, times(2)).setAutoCommit(false);
        verify(connection, never()).setAutoCommit(true);
    }

    @Test
    void invalidCsvNeverTouchesDatabase() throws Exception {
        Path file = csv(HEADER + "901,,Museum,Center,History,48.7,21.2\n");

        assertThrows(IllegalArgumentException.class,
                () -> PlaceCsvImportService.importFile(file, connection));

        verifyNoInteractions(connection, statement);
    }

    @Test
    void headerOnlyCsvNeverTouchesDatabase() throws Exception {
        assertEquals(0, PlaceCsvImportService.importFile(csv(HEADER), connection));
        verifyNoInteractions(connection, statement);
    }
}
