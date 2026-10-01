package com.team14;

import com.team14.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Application entry point. It runs every population report in order.
 */
public final class App {

    /**
     * One report that can be printed.
     */
    @FunctionalInterface
    private interface ReportAction {

        void run() throws SQLException;
    }

    /**
     * Every report in the system, in the order they are printed.
     * Add new reports here (one line each).
     */
    private static final List<ReportAction> REPORTS = List.of(
            // CountryReport::printAllCountriesInWorld,
    );

    private App() {
        throw new UnsupportedOperationException(
                "Utility class cannot be instantiated"
        );
    }

    /**
     * Starts the application: checks the database, then prints all reports.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {

        try {
            checkDatabaseConnection();
            printAllReports();

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Fails early with a clear message if the database cannot be reached.
     */
    private static void checkDatabaseConnection() throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection()) {

            System.out.println(
                    "Population Report System started. Database connection OK: "
                            + connection.isValid(2)
            );
        }
    }

    /**
     * Prints every registered report.
     */
    private static void printAllReports() throws SQLException {

        if (REPORTS.isEmpty()) {
            System.out.println("No reports have been added yet.");
            return;
        }

        for (ReportAction report : REPORTS) {
            report.run();
        }
    }
}