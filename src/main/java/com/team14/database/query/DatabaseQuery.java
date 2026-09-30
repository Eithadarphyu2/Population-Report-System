package com.team14.database.query;

import com.team14.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Core query execution utility for executing safe PreparedStatements.
 */
public final class DatabaseQuery {

    private DatabaseQuery() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Functional interface to map a row of ResultSet into a Java Object.
     */
    @FunctionalInterface
    public interface RowMapper<T> {
        T mapRow(ResultSet rs) throws SQLException;
    }

    /**
     * Executes a parameterized SELECT query safely and maps results using RowMapper.
     *
     * @param <T>    Target entity type
     * @param sql    SQL query string with placeholders (?)
     * @param mapper Mapping function for ResultSet
     * @param params Parameter values to bind into SQL query
     * @return List of mapped entity objects
     * @throws SQLException if database access or query execution fails
     */
    public static <T> List<T> executeQuery(
            String sql,
            RowMapper<T> mapper,
            Object... params) throws SQLException {

        List<T> results = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapper.mapRow(rs));
                }
            }
        }

        return results;
    }
}