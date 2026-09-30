package com.team14.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class responsible for managing database connections using environment defaults.
 */
public final class DatabaseConnection {

    // Private constructor to prevent instantiation of this utility class
    private DatabaseConnection() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // Read environment variables with safe fallback defaults for local and container environments
    private static final String HOST = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
    private static final String PORT = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "3306";
    private static final String DATABASE = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "world";
    private static final String USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "appuser";
    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "apppassword";

    // Build JDBC Connection URL with MySQL 8 compatibility parameters
    private static final String URL = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
            HOST, PORT, DATABASE
    );

    /**
     * Establishes and returns an active connection to the MySQL database.
     *
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}