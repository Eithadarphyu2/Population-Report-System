package com.team14.report;

import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Test helper: a database row made in memory, so a report's row reading can
 * be tested without a database.
 * <p>
 * Reading a column that was not given throws an SQLException, as a real
 * database does, so a wrong column name in a report is caught by the test.
 */
public final class FakeResultSet {

    private FakeResultSet() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Creates one row.
     *
     * @param labelsAndValues pairs of column label and value, for example
     *                        {@code row("Code", "CHN", "Population", 1000L)}.
     *                        A value may be null.
     * @return a ResultSet that returns those values
     */
    public static ResultSet row(Object... labelsAndValues) {

        if (labelsAndValues.length % 2 != 0) {
            throw new IllegalArgumentException("Give each column label a value.");
        }

        Map<String, Object> values = new LinkedHashMap<>();

        for (int i = 0; i < labelsAndValues.length; i += 2) {
            values.put((String) labelsAndValues[i], labelsAndValues[i + 1]);
        }

        return (ResultSet) Proxy.newProxyInstance(
                ResultSet.class.getClassLoader(),
                new Class<?>[]{ResultSet.class},
                (proxy, method, args) -> {

                    if (args == null || args.length != 1 || !(args[0] instanceof String label)) {
                        throw new UnsupportedOperationException(
                                "FakeResultSet does not support " + method.getName());
                    }

                    if (!values.containsKey(label)) {
                        throw new SQLException("Column '" + label + "' not found.");
                    }

                    Object value = values.get(label);

                    return switch (method.getName()) {
                        case "getString" -> value == null ? null : value.toString();
                        case "getLong" -> value == null ? 0L : ((Number) value).longValue();
                        case "getInt" -> value == null ? 0 : ((Number) value).intValue();
                        case "getDouble" -> value == null ? 0.0 : ((Number) value).doubleValue();
                        default -> throw new UnsupportedOperationException(
                                "FakeResultSet does not support " + method.getName());
                    };
                }
        );
    }
}