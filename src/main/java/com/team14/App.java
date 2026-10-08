package com.team14;

import com.team14.config.ReportParameters;
import com.team14.database.DatabaseConnection;
import com.team14.report.Report;
import com.team14.report.country.AllCountriesInWorldReport;
import com.team14.report.country.CountriesByContinentReport;
import com.team14.report.country.CountriesByRegionReport;
import com.team14.report.country.TopCountriesByContinentReport;
import com.team14.report.country.TopCountriesByRegionReport;
import com.team14.report.country.TopCountriesWorldwideReport;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Application entry point. It runs every population report in order.
 */
public final class App {

    /** Every report in the system, in the order they are printed. */
    private static final List<Report> REPORTS = List.of(
            new AllCountriesInWorldReport(),
            new CountriesByContinentReport(ReportParameters.CONTINENT),
            new CountriesByRegionReport(ReportParameters.REGION),
            new TopCountriesWorldwideReport(ReportParameters.TOP_N),
            new TopCountriesByContinentReport(
                    ReportParameters.CONTINENT,
                    ReportParameters.TOP_N
            ),
            new TopCountriesByRegionReport(
                    ReportParameters.REGION,
                    ReportParameters.TOP_N
            )
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
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            System.exit(1);
        }

        int failures = runAllReports();

        if (failures > 0) {
            System.exit(1);
        }
    }

    private static void checkDatabaseConnection() throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection()) {
            System.out.println(
                    "Population Report System started. Database connection OK: "
                            + connection.isValid(2)
            );
        }
    }

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
