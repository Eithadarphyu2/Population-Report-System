package com.team14.database.query;

import com.team14.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for executing parameterized database queries.
 */
public final class DatabaseQuery {

    private DatabaseQuery() {
        throw new UnsupportedOperationException(
                "Utility class cannot be instantiated"
        );
    }

    /**
     * Maps one ResultSet row to a Java object.
     */
    @FunctionalInterface
    public interface RowMapper<T> {

        T mapRow(ResultSet resultSet) throws SQLException;
    }

    /**
     * Executes a parameterized SELECT query and maps each result row.
     */
    public static <T> List<T> executeQuery(
            String sql,
            RowMapper<T> mapper,
            Object... params
    ) throws SQLException {

        List<T> results = new ArrayList<>();

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 1, params[i]);
                }
            }

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    results.add(mapper.mapRow(resultSet));
                }
            }
        }

        return results;
    }
}