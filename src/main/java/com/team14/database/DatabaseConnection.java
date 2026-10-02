package com.team14.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class responsible for creating MySQL database connections.
 */
public final class DatabaseConnection {

    private static final String HOST =
            getEnvironmentVariable("DB_HOST", "localhost");

    private static final String PORT =
            getEnvironmentVariable("DB_PORT", "3306");

    private static final String DATABASE =
            getEnvironmentVariable("DB_NAME", "world");

    private static final String USER =
            getEnvironmentVariable("DB_USER", "appuser");

    private static final String PASSWORD =
            getEnvironmentVariable("DB_PASSWORD", "apppassword");

    private static final String URL = String.format(
            "jdbc:mysql://%s:%s/%s"
                    + "?useSSL=false"
                    + "&allowPublicKeyRetrieval=true"
                    + "&serverTimezone=UTC",
            HOST,
            PORT,
            DATABASE
    );

    private DatabaseConnection() {
        throw new UnsupportedOperationException(
                "Utility class cannot be instantiated"
        );
    }

    /**
     * Gets an environment variable or returns a default value.
     */
    private static String getEnvironmentVariable(
            String name,
            String defaultValue
    ) {
        String value = System.getenv(name);

        return value != null && !value.isBlank()
                ? value
                : defaultValue;
    }

    /**
     * Creates and returns a connection to the MySQL database.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }
}