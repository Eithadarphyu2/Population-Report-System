package com.team14.database;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for the DatabaseConnection utility.
 * They need the MySQL container with the world database.
 */
class DatabaseConnectionIntegrationTest {

    /**
     * Counts the rows of a table using an open connection.
     */
    private static int countRows(Connection connection, String table) throws SQLException {

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + table)) {

            resultSet.next();

            return resultSet.getInt(1);
        }
    }

    @Test
    @DisplayName("Verify Database Connection Status and Metadata")
    void testGetConnection() throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection()) {

            assertNotNull(connection, "Database connection should not be null.");

            // Validate connection responsiveness with a 2-second timeout
            assertTrue(connection.isValid(2), "Database connection should be active.");

            DatabaseMetaData metaData = connection.getMetaData();
            assertNotNull(metaData.getDatabaseProductName(), "Database product name should be readable.");
        }
    }

    @Test
    @DisplayName("The connection is to the world database")
    void connectsToWorldDatabase() throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection()) {

            assertEquals("world", connection.getCatalog());
        }
    }

    @Test
    @DisplayName("The country and city data can be read through the connection")
    void worldDataIsReachable() throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection()) {

            assertEquals(239, countRows(connection, "country"));
            assertEquals(4079, countRows(connection, "city"));
        }
    }

    @Test
    @DisplayName("Each call returns a new, independent connection")
    void eachCallReturnsNewConnection() throws SQLException {

        try (Connection first = DatabaseConnection.getConnection();
             Connection second = DatabaseConnection.getConnection()) {

            assertNotSame(first, second);
            assertTrue(first.isValid(2));
            assertTrue(second.isValid(2));
        }
    }

    @Test
    @DisplayName("A connection is closed when it is closed by the caller")
    void connectionCanBeClosed() throws SQLException {

        Connection connection = DatabaseConnection.getConnection();

        connection.close();

        assertTrue(connection.isClosed());
    }
}