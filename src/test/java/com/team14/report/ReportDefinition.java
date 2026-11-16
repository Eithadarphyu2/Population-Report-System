package com.team14.report;

import com.team14.report.ReportDisplay.Column;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Test helper: lets unit tests in any package read what a report asks for
 * (its SQL, parameters and columns) without running a query.
 * <p>
 * SqlReport keeps these methods protected. This helper sits in the same
 * package as SqlReport, so it can call them for every report.
 */
public final class ReportDefinition {

    private ReportDefinition() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /** The shared SELECT part of the report, on one line. */
    public static String select(SqlReport report) {
        return oneLine(report.select());
    }

    /** The report's WHERE / ORDER BY / LIMIT part, on one line. */
    public static String clauses(SqlReport report) {
        return oneLine(report.clauses());
    }

    /** The values for the ? placeholders, in order. */
    public static Object[] parameters(SqlReport report) {
        return report.parameters();
    }

    /** The report's column definitions. */
    public static List<Column> columns(SqlReport report) {
        return report.columns();
    }

    /** Reads one database row the way the report does. */
    public static List<?> readRow(SqlReport report, ResultSet row) throws SQLException {
        return report.readRow(row);
    }

    /** The number of ? placeholders in the report's SQL clauses. */
    public static int placeholders(SqlReport report) {
        return (int) report.clauses().chars().filter(c -> c == '?').count();
    }

    private static String oneLine(String sql) {
        return sql.replaceAll("\\s+", " ").trim();
    }
}