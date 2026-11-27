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
    @DisplayName("Database connection returned is not null")
    void connectionIsNotNull() throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection()) {
            assertNotNull(connection, "Database connection should not be null.");
        }
    }

    @Test
    @DisplayName("Database connection is active and valid")
    void connectionIsValid() throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Validate connection responsiveness with a 2-second timeout
            assertTrue(connection.isValid(2), "Database connection should be active.");
        }
    }

    @Test
    @DisplayName("Database product name metadata is readable")
    void databaseProductNameIsReadable() throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            assertNotNull(metaData.getDatabaseProductName(), "Database product name should be readable.");
        }
    }

    @Test
    @DisplayName("The connection is to the world database")
    void connectsToWorldDatabase() throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection()) {

            // Confirm the active catalog/database is 'world'
            assertEquals("world", connection.getCatalog(), "Expected connection catalog to be 'world'");
        }
    }

    @Test
    @DisplayName("Country table has the expected number of rows")
    void countryTableHasExpectedRowCount() throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection()) {
            assertEquals(239, countRows(connection, "country"), "Expected 239 rows in country table");
        }
    }

    @Test
    @DisplayName("City table has the expected number of rows")
    void cityTableHasExpectedRowCount() throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection()) {
            assertEquals(4079, countRows(connection, "city"), "Expected 4079 rows in city table");
        }
    }

    @Test
    @DisplayName("Each call returns a distinct Connection instance")
    void eachCallReturnsDistinctConnection() throws SQLException {

        try (Connection first = DatabaseConnection.getConnection();
             Connection second = DatabaseConnection.getConnection()) {

            // Ensure two separate Connection instances are returned
            assertNotSame(first, second, "Expected each call to return a distinct Connection instance");
        }
    }

    @Test
    @DisplayName("First returned connection is active and valid")
    void firstConnectionIsValid() throws SQLException {

        try (Connection first = DatabaseConnection.getConnection()) {
            assertTrue(first.isValid(2), "First connection should be active");
        }
    }

    @Test
    @DisplayName("Second returned connection is active and valid")
    void secondConnectionIsValid() throws SQLException {

        try (Connection second = DatabaseConnection.getConnection()) {
            assertTrue(second.isValid(2), "Second connection should be active");
        }
    }

    @Test
    @DisplayName("A connection is closed when it is closed by the caller")
    void connectionCanBeClosed() throws SQLException {

        Connection connection = DatabaseConnection.getConnection();

        connection.close();

        assertTrue(connection.isClosed(), "Connection should be closed after calling close()");
    }
}