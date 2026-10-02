package com.team14.database;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the DatabaseConnection utility (needs the MySQL container).
 */
class DatabaseConnectionTest {

    @Test
    @DisplayName("Verify Database Connection Status and Metadata")
    void testGetConnection() throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection()) {

            assertNotNull(connection, "Database connection should not be null.");

            // Validate connection responsiveness with a 2-second timeout
            assertTrue(connection.isValid(2), "Database connection should be active.");

            DatabaseMetaData metaData = connection.getMetaData();
            assertNotNull(metaData.getDatabaseProductName(),
                    "Database product name should be readable.");
        }
    }
}
