package com.team14.report.country;

import com.team14.input.UserInput;
import com.team14.report.FakeResultSet;
import com.team14.report.ReportDefinition;
import com.team14.report.ReportDisplay.Alignment;
import com.team14.report.ReportDisplay.Column;
import com.team14.report.SqlReport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the country reports (requirements 1 to 6).
 * <p>
 * They check what each report asks for (title, SQL and parameters), that bad
 * values are rejected, and how the shared CountryReport reads a database row.
 * They need no database. The reports are run against MySQL in
 * CountryReportIntegrationTest.
 */
class CountryReportTest {

    // Default SQL ordering clause for country population reports
    private static final String BY_POPULATION = "ORDER BY c.Population DESC";

    // Save original System.out so it can be restored after each test
    private final PrintStream originalOut = System.out;

    // Buffer to capture console output written to System.out
    private final ByteArrayOutputStream captured = new ByteArrayOutputStream();

    /**
     * Redirects System.out to a UTF-8 PrintStream with auto-flush before each test.
     */
    @BeforeEach
    void captureOutput() {
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
    }

    /**
     * Restores the original System.out after each test.
     */
    @AfterEach
    void restoreOutput() {
        System.setOut(originalOut);
    }

    // ---------------------------------------------------------------- helpers

    /**
     * Creates a UserInput backed by a Scanner reading the given simulated lines
     * instead of real keyboard input.
     */
    private static UserInput typing(String... lines) {
        return new UserInput(new Scanner(String.join("\n", lines) + "\n"));
    }

    /**
     * Checks the title, the SQL clauses and the parameters of a report, and
     * that every ? in the SQL has a parameter.
     */
    private static void assertDefinition(SqlReport report,
                                         String title,
                                         String clauses,
                                         Object... parameters) {
        // Verify report title, SQL clauses, bound parameters, and placeholder count
        assertEquals(title, report.title(),
                "Unexpected report title");
        assertEquals(clauses, ReportDefinition.clauses(report),
                "Unexpected SQL clauses");
        assertArrayEquals(parameters, ReportDefinition.parameters(report),
                "Unexpected SQL query parameters");
        assertEquals(ReportDefinition.placeholders(report), parameters.length,
                "Every ? in the SQL needs one parameter");
    }

    // ------------------------------------------- requirement 1: world

    @Test
    @DisplayName("All countries in the world: sorted by population, no parameters")
    void allCountriesInWorld() {
        // Verify default world country report has no filter/limit parameters
        assertDefinition(
                new AllCountriesInWorldReport(),
                "All Countries in the World by Population",
                BY_POPULATION
        );
    }

    // ------------------------------------------- requirement 2: continent

    @Test
    @DisplayName("Countries in a continent: title, SQL and parameter")
    void countriesByContinent() {
        // Verify continent filter clause and single continent parameter
        assertDefinition(
                new CountriesByContinentReport("Europe"),
                "Countries in Europe by Population",
                "WHERE c.Continent = ? " + BY_POPULATION,
                "Europe"
        );
    }

    @Test
    @DisplayName("Countries in a continent: spaces around the continent are removed")
    void countriesByContinentTrims() {
        // Leading and trailing whitespace around continent names should be trimmed
        assertDefinition(
                new CountriesByContinentReport("  Asia "),
                "Countries in Asia by Population",
                "WHERE c.Continent = ? " + BY_POPULATION,
                "Asia"
        );
    }

    @Test
    @DisplayName("Countries in a continent: a null or blank continent is rejected")
    void countriesByContinentRejectsBlank() {
        // Reject null and whitespace-only continent inputs
        assertThrows(IllegalArgumentException.class,
                () -> new CountriesByContinentReport((String) null));
        assertThrows(IllegalArgumentException.class,
                () -> new CountriesByContinentReport("   "));
    }

    @Test
    @DisplayName("Countries in a continent: the continent can be read from user input")
    void countriesByContinentFromInput() {
        // Simulate typing a continent name via UserInput
        assertArrayEquals(
                new Object[]{"Africa"},
                ReportDefinition.parameters(new CountriesByContinentReport(typing("Africa"))),
                "Expected continent parameter read from UserInput to be Africa"
        );
    }

    @Test
    @DisplayName("Countries in a continent: null user input is rejected")
    void countriesByContinentRejectsNullInput() {
        // Null UserInput should throw NullPointerException
        assertThrows(NullPointerException.class,
                () -> new CountriesByContinentReport((UserInput) null));
    }

    // ------------------------------------------- requirement 3: region

    @Test
    @DisplayName("Countries in a region: title, SQL and parameter")
    void countriesByRegion() {
        // Verify region filter clause and single region parameter
        assertDefinition(
                new CountriesByRegionReport("Southern Europe"),
                "Countries in Southern Europe by Population",
                "WHERE c.Region = ? " + BY_POPULATION,
                "Southern Europe"
        );
    }

    @Test
    @DisplayName("Countries in a region: spaces around the region are removed")
    void countriesByRegionTrims() {
        // Ensure padded region name is trimmed to "Caribbean"
        assertArrayEquals(
                new Object[]{"Caribbean"},
                ReportDefinition.parameters(new CountriesByRegionReport("  Caribbean ")),
                "Expected trimmed region parameter to be Caribbean"
        );
    }

    @Test
    @DisplayName("Countries in a region: a null or blank region is rejected")
    void countriesByRegionRejectsBlank() {
        // Reject null and empty region names
        assertThrows(IllegalArgumentException.class,
                () -> new CountriesByRegionReport((String) null));
        assertThrows(IllegalArgumentException.class,
                () -> new CountriesByRegionReport(""));
    }

    @Test
    @DisplayName("Countries in a region: the region can be read from user input")
    void countriesByRegionFromInput() {
        // Simulate reading a region from UserInput
        assertArrayEquals(
                new Object[]{"Polynesia"},
                ReportDefinition.parameters(new CountriesByRegionReport(typing("Polynesia"))),
                "Expected region parameter read from UserInput to be Polynesia"
        );
    }

    @Test
    @DisplayName("Countries in a region: null user input is rejected")
    void countriesByRegionRejectsNullInput() {
        // Null UserInput should throw NullPointerException
        assertThrows(NullPointerException.class,
                () -> new CountriesByRegionReport((UserInput) null));
    }

    // ------------------------------------------- requirement 4: top N world

    @Test
    @DisplayName("Top N countries worldwide: title, SQL and parameter")
    void topCountriesWorldwide() {
        // Verify LIMIT clause and top-N integer parameter
        assertDefinition(
                new TopCountriesWorldwideReport(5),
                "Top 5 Populated Countries Worldwide",
                BY_POPULATION + " LIMIT ?",
                5
        );
    }

    @Test
    @DisplayName("Top N countries worldwide: N must be greater than 0")
    void topCountriesWorldwideRejectsBadN() {
        // N must be positive; zero or negative values should be rejected
        assertThrows(IllegalArgumentException.class,
                () -> new TopCountriesWorldwideReport(0));
        assertThrows(IllegalArgumentException.class,
                () -> new TopCountriesWorldwideReport(-4));
    }

    @Test
    @DisplayName("Top N countries worldwide: N can be read from user input")
    void topCountriesWorldwideFromInput() {
        // Simulate typing limit N via UserInput
        assertArrayEquals(
                new Object[]{8},
                ReportDefinition.parameters(new TopCountriesWorldwideReport(typing("8"))),
                "Expected limit parameter read from UserInput to be 8"
        );
    }

    @Test
    @DisplayName("Top N countries worldwide: null user input is rejected")
    void topCountriesWorldwideRejectsNullInput() {
        // Null UserInput should throw NullPointerException
        assertThrows(NullPointerException.class,
                () -> new TopCountriesWorldwideReport((UserInput) null));
    }

    // ------------------------------------------- requirement 5: top N continent

    @Test
    @DisplayName("Top N countries in a continent: title, SQL and parameters")
    void topCountriesByContinent() {
        // Verify continent filter, population sort, LIMIT clause, and both parameters
        assertDefinition(
                new TopCountriesByContinentReport("Asia", 5),
                "Top 5 Populated Countries in Asia",
                "WHERE c.Continent = ? " + BY_POPULATION + " LIMIT ?",
                "Asia", 5
        );
    }

    @Test
    @DisplayName("Top N countries in a continent: spaces are removed from the continent")
    void topCountriesByContinentTrims() {
        // Verify continent input is trimmed while preserving limit N
        assertArrayEquals(
                new Object[]{"Europe", 3},
                ReportDefinition.parameters(new TopCountriesByContinentReport(" Europe ", 3)),
                "Expected trimmed continent 'Europe' and limit 3"
        );
    }

    @Test
    @DisplayName("Top N countries in a continent: a blank continent or a bad N is rejected")
    void topCountriesByContinentRejectsBadValues() {
        // Reject blank continent or non-positive limit N
        assertThrows(IllegalArgumentException.class,
                () -> new TopCountriesByContinentReport("  ", 5));
        assertThrows(IllegalArgumentException.class,
                () -> new TopCountriesByContinentReport("Asia", 0));
    }

    @Test
    @DisplayName("Top N countries in a continent: the continent and N are read from user input")
    void topCountriesByContinentFromInput() {
        // Simulate reading continent and N on separate input lines
        assertArrayEquals(
                new Object[]{"Oceania", 2},
                ReportDefinition.parameters(
                        new TopCountriesByContinentReport(typing("Oceania", "2"))),
                "Expected continent 'Oceania' and limit 2 from UserInput"
        );
    }

    @Test
    @DisplayName("Top N countries in a continent: null user input is rejected")
    void topCountriesByContinentRejectsNullInput() {
        // Null UserInput should throw NullPointerException
        assertThrows(NullPointerException.class,
                () -> new TopCountriesByContinentReport((UserInput) null));
    }

    // ------------------------------------------- requirement 6: top N region

    @Test
    @DisplayName("Top N countries in a region: title, SQL and parameters")
    void topCountriesByRegion() {
        // Verify region filter, population sort, LIMIT clause, and both parameters
        assertDefinition(
                new TopCountriesByRegionReport("Southern Europe", 3),
                "Top 3 Populated Countries in Southern Europe",
                "WHERE c.Region = ? " + BY_POPULATION + " LIMIT ?",
                "Southern Europe", 3
        );
    }

    @Test
    @DisplayName("Top N countries in a region: spaces are removed from the region")
    void topCountriesByRegionTrims() {
        // Verify region input is trimmed while preserving limit N
        assertArrayEquals(
                new Object[]{"Caribbean", 4},
                ReportDefinition.parameters(new TopCountriesByRegionReport(" Caribbean ", 4)),
                "Expected trimmed region 'Caribbean' and limit 4"
        );
    }

    @Test
    @DisplayName("Top N countries in a region: a blank region or a bad N is rejected")
    void topCountriesByRegionRejectsBadValues() {
        // Reject empty region or negative limit N
        assertThrows(IllegalArgumentException.class,
                () -> new TopCountriesByRegionReport("", 5));
        assertThrows(IllegalArgumentException.class,
                () -> new TopCountriesByRegionReport("Caribbean", -1));
    }

    @Test
    @DisplayName("Top N countries in a region: the region and N are read from user input")
    void topCountriesByRegionFromInput() {
        // Simulate reading region and N from UserInput
        assertArrayEquals(
                new Object[]{"Melanesia", 6},
                ReportDefinition.parameters(
                        new TopCountriesByRegionReport(typing("Melanesia", "6"))),
                "Expected region 'Melanesia' and limit 6 from UserInput"
        );
    }

    @Test
    @DisplayName("Top N countries in a region: null user input is rejected")
    void topCountriesByRegionRejectsNullInput() {
        // Null UserInput should throw NullPointerException
        assertThrows(NullPointerException.class,
                () -> new TopCountriesByRegionReport((UserInput) null));
    }

    // ------------------------------------------- shared CountryReport code

    @Test
    @DisplayName("Country reports show the columns the coursework requires")
    void countryColumns() {
        // Retrieve column definitions and check headings, alignment, and zero-as-missing flag
        List<Column> columns = ReportDefinition.columns(new AllCountriesInWorldReport());

        assertEquals(
                List.of("Code", "Name", "Continent", "Region", "Population", "Capital"),
                columns.stream().map(Column::heading).toList(),
                "Country report headings do not match coursework specification"
        );
        assertEquals(Alignment.RIGHT, columns.get(4).alignment(),
                "Population column (index 4) should be right-aligned");
        assertTrue(columns.get(4).zeroIsMissing(),
                "Population column (index 4) should treat zero as missing");
    }

    @Test
    @DisplayName("Country reports select from country and join the capital city")
    void countrySelect() {
        // Verify SELECT clause, capital city LEFT JOIN, and FROM country table
        String select = ReportDefinition.select(new AllCountriesInWorldReport());

        assertTrue(select.startsWith("SELECT c.Code, c.Name, c.Continent, c.Region,"),
                "Expected SELECT clause to start with Code, Name, Continent, and Region");
        assertTrue(select.contains("c.Population, ci.Name AS Capital"),
                "Expected SELECT clause to include Population and Capital alias");
        assertTrue(select.contains("FROM country c"),
                "Expected SELECT clause to query FROM country c");
        assertTrue(select.contains("LEFT JOIN city ci ON c.Capital = ci.ID"),
                "Expected SELECT clause to left-join city table on capital ID");
    }

    @Test
    @DisplayName("A country row is read into the six report values")
    void readsCountryRow() throws SQLException {
        // Build a fake ResultSet row representing China
        ResultSet row = FakeResultSet.row(
                "Code", "CHN", "Name", "China", "Continent", "Asia",
                "Region", "Eastern Asia", "Population", 1277558000L,
                "Capital", "Peking");

        // Verify all six values are extracted in order
        assertEquals(
                List.of("CHN", "China", "Asia", "Eastern Asia", 1277558000L, "Peking"),
                ReportDefinition.readRow(new AllCountriesInWorldReport(), row),
                "Expected row values for China to match"
        );
    }

    @Test
    @DisplayName("A country with no capital is read with an empty capital")
    void readsCountryWithoutCapital() throws SQLException {
        // Antarctica has population 0 and no capital city (null)
        ResultSet row = FakeResultSet.row(
                "Code", "ATA", "Name", "Antarctica", "Continent", "Antarctica",
                "Region", "Antarctica", "Population", 0L, "Capital", null);

        // Verify null capital is preserved when reading the row
        assertEquals(
                Arrays.asList("ATA", "Antarctica", "Antarctica", "Antarctica", 0L, null),
                ReportDefinition.readRow(new AllCountriesInWorldReport(), row),
                "Expected Antarctica row to have population 0 and null capital"
        );
    }
}