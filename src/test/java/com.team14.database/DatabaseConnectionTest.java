package com.team14.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for verifying the DatabaseConnection utility.
 */
class DatabaseConnectionTest {

    @Test
    @DisplayName("Verify Database Connection Status and Metadata")
    void testGetConnection() {
        assertDoesNotThrow(() -> {
            // Attempt to establish database connection within try-with-resources
            try (Connection connection = DatabaseConnection.getConnection()) {
                // Verify connection instance is not null
                assertNotNull(connection, "Database connection should not be null.");

                // Validate connection responsiveness with a 2-second timeout
                assertTrue(connection.isValid(2), "Database connection should be active.");

                // Retrieve and assert database metadata readability
                DatabaseMetaData metaData = connection.getMetaData();
                assertNotNull(metaData.getDatabaseProductName(), "Database product name should be readable.");
            }
        });
    }
}