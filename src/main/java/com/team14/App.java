package com.team14;

import com.team14.database.DatabaseConnection;
import com.team14.report.Report;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Application entry point. It runs every population report in order.
 */
public final class App {

    /** Every report in the system, in the order they are printed. */
    private static final List<Report> REPORTS = List.of(

           //Add each report object here
    );

    private App() {
        throw new UnsupportedOperationException(
                "Utility class cannot be instantiated"
        );
    }

    /**
     * Starts the application: checks the database, then prints all reports.
     * A failing report is reported and skipped, so one broken report does
     * not stop the others. The exit code is 1 if any report failed.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {

        try {
            checkDatabaseConnection();
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            System.exit(1);
        }

        int failures = runAllReports();

        if (failures > 0) {
            System.exit(1);
        }
    }

    /** Fails early with a clear message if the database cannot be reached. */
    private static void checkDatabaseConnection() throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection()) {

            System.out.println(
                    "Population Report System started. Database connection OK: "
                            + connection.isValid(2)
            );
        }
    }

    /**
     * Runs every registered report.
     *
     * @return the number of reports that failed
     */
    private static int runAllReports() {

        int failures = 0;

        for (Report report : REPORTS) {
            try {
                report.run();
            } catch (SQLException e) {
                failures++;
                System.err.println(
                        "Report failed: " + report.title() + " - " + e.getMessage()
                );
            }
        }

        return failures;
    }
}
