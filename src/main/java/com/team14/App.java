package com.team14;

import com.team14.config.ReportParameters;
import com.team14.database.DatabaseConnection;
import com.team14.report.Report;
import com.team14.report.capitalcity.AllCapitalCitiesInWorldReport;
import com.team14.report.city.AllCitiesInWorldReport;
import com.team14.report.country.AllCountriesInWorldReport;
import com.team14.report.country.CountriesByContinentReport;
import com.team14.report.country.CountriesByRegionReport;
import com.team14.report.country.TopCountriesWorldwideReport;
import com.team14.report.country.TopCountriesByContinentReport;
import com.team14.report.country.TopCountriesByRegionReport;
import com.team14.report.city.CitiesByContinentReport;
import com.team14.report.city.CitiesByRegionReport;
import com.team14.report.city.TopCitiesByWorldwideReport;
import com.team14.report.city.TopCitiesByContinentReport;
import com.team14.report.city.TopCitiesByDistrictReport;
import com.team14.report.city.TopCitiesByCountryReport;
import com.team14.report.city.TopCitiesByRegionReport;
import com.team14.report.city.CitiesByCountryReport;
import com.team14.report.city.CitiesByDistrictReport;
import com.team14.report.capitalcity.TopCapitalCitiesByContinentReport;
import com.team14.report.capitalcity.TopCapitalCitiesByRegionReport;
import com.team14.report.capitalcity.TopCapitalCitiesWorldwideReport;
import com.team14.report.capitalcity.CapitalCitiesByContinentReport;
import com.team14.report.capitalcity.CapitalCitiesByRegionReport;
import com.team14.report.population.PopulationDistributionByContinentReport;
import com.team14.report.population.PopulationDistributionByRegionReport;
import com.team14.report.population.PopulationDistributionByCountryReport;
import com.team14.report.population.WorldPopulationReport;
import com.team14.report.population.ContinentPopulationReport;
import com.team14.report.population.RegionPopulationReport;
import com.team14.report.population.CountryPopulationReport;
import com.team14.report.population.DistrictPopulationReport;
import com.team14.report.population.CityPopulationReport;
import com.team14.report.language.LanguagePopulationReport;

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
            ),
            new AllCitiesInWorldReport(),
            new CitiesByContinentReport(ReportParameters.CONTINENT),
            new CitiesByRegionReport(ReportParameters.REGION),
            new CitiesByCountryReport(ReportParameters.COUNTRY),
            new CitiesByDistrictReport(ReportParameters.DISTRICT),
            new TopCitiesByWorldwideReport(ReportParameters.TOP_N),
            new TopCitiesByContinentReport(
                    ReportParameters.CONTINENT,
                    ReportParameters.TOP_N
            ),
            new TopCitiesByRegionReport(
                    ReportParameters.REGION,
                    ReportParameters.TOP_N
            ),
            new TopCitiesByCountryReport(
                    ReportParameters.COUNTRY,
                    ReportParameters.TOP_N
            ),
            new TopCitiesByDistrictReport(
                    ReportParameters.DISTRICT,
                    ReportParameters.TOP_N
            ),
            new AllCapitalCitiesInWorldReport(),
            new CapitalCitiesByContinentReport(ReportParameters.CONTINENT),
            new CapitalCitiesByRegionReport(ReportParameters.REGION),
            new TopCapitalCitiesWorldwideReport(ReportParameters.TOP_N),
            new TopCapitalCitiesByContinentReport(
                    ReportParameters.CONTINENT,
                    ReportParameters.TOP_N
            ),
            new TopCapitalCitiesByRegionReport(
                    ReportParameters.REGION,
                    ReportParameters.TOP_N
            ),
            new PopulationDistributionByContinentReport(),
            new PopulationDistributionByRegionReport(),
            new PopulationDistributionByCountryReport(),
            new WorldPopulationReport(),
            new ContinentPopulationReport(ReportParameters.CONTINENT),
            new RegionPopulationReport(ReportParameters.REGION),
            new CountryPopulationReport(ReportParameters.COUNTRY),
            new DistrictPopulationReport(ReportParameters.DISTRICT),
            new CityPopulationReport(ReportParameters.CITY),
            new LanguagePopulationReport()
    );

    private App() {
        throw new UnsupportedOperationException(
                "Utility class cannot be instantiated"
        );
    }

    /**
     * Starts the application: checks the database, then prints all reports.
     * The program ends with exit code 1 if the database or a report fails.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {

        int exitCode = run();

        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    /**
     * Runs the application without ending the program, so it can be tested.
     *
     * @return 0 if the database and every report worked, otherwise 1
     */
    static int run() {

        try {
            checkDatabaseConnection();
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return 1;
        }

        return runReports(REPORTS) > 0 ? 1 : 0;
    }

    /**
     * Gives the tests the reports the application runs.
     *
     * @return every registered report, in the order they are printed
     */
    static List<Report> reports() {
        return REPORTS;
    }

    private static void checkDatabaseConnection() throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection()) {
            System.out.println(
                    "Population Report System started. Database connection OK: "
                            + connection.isValid(2)
            );
        }
    }

    /**
     * Runs the given reports one after the other. A report that fails is
     * reported and skipped, so one broken report does not stop the others.
     *
     * @param reports the reports to run
     * @return the number of reports that failed
     */
    static int runReports(List<Report> reports) {

        int failures = 0;

        for (Report report : reports) {
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