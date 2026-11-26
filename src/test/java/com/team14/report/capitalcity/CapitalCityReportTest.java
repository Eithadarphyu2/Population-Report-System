package com.team14.report.capitalcity;

import com.team14.input.UserInput;
import com.team14.report.FakeResultSet;
import com.team14.report.ReportDefinition;
import com.team14.report.ReportDisplay.Alignment;
import com.team14.report.ReportDisplay.Column;
import com.team14.report.SqlReport;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Capital City Report Tests")
class CapitalCityReportTest {

    private static final String BY_POPULATION =
            "ORDER BY ci.Population DESC";

    // ---------------------------------------------------------------- helpers

    /** A UserInput that reads the given lines instead of the keyboard. */
    private static UserInput typing(String... lines) {
        return new UserInput(
                new Scanner(String.join("\n", lines) + "\n")
        );
    }

    /**
     * Checks the title, SQL clauses and parameters of a report,
     * and verifies that every SQL placeholder has a parameter.
     */
    private static void assertDefinition(
            SqlReport report,
            String title,
            String clauses,
            Object... parameters) {

        assertEquals(
                title,
                report.title(),
                "Report title should match expected title"
        );
        assertEquals(
                clauses,
                ReportDefinition.clauses(report),
                "SQL clauses should match expected clauses"
        );
        assertArrayEquals(
                parameters,
                ReportDefinition.parameters(report),
                "SQL parameters should match expected parameters"
        );

        assertEquals(
                ReportDefinition.placeholders(report),
                parameters.length,
                "Every ? in the SQL needs one parameter"
        );
    }

    // ------------------------------------------- all capital cities worldwide

    @Test
    @DisplayName("All capital cities worldwide: sorted by population, no parameters")
    void allCapitalCitiesInWorld() {
        assertDefinition(
                new AllCapitalCitiesInWorldReport(),
                "All Capital Cities in the World by Population",
                BY_POPULATION
        );
    }

    // ------------------------------------------- capital cities by continent

    @Test
    @DisplayName("Capital cities in a continent: title, SQL and parameter")
    void capitalCitiesByContinent() {
        assertDefinition(
                new CapitalCitiesByContinentReport("Europe"),
                "Capital Cities in Europe by Population",
                "WHERE c.Continent = ? " + BY_POPULATION,
                "Europe"
        );
    }

    @Test
    @DisplayName("Capital cities in a continent: spaces around the continent are removed")
    void capitalCitiesByContinentTrims() {
        assertDefinition(
                new CapitalCitiesByContinentReport("  Asia "),
                "Capital Cities in Asia by Population",
                "WHERE c.Continent = ? " + BY_POPULATION,
                "Asia"
        );
    }

    @Test
    @DisplayName("Capital cities in a continent: a null or blank continent is rejected")
    void capitalCitiesByContinentRejectsBlank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CapitalCitiesByContinentReport((String) null),
                "Null continent should be rejected"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new CapitalCitiesByContinentReport("   "),
                "Blank continent should be rejected"
        );
    }

    @Test
    @DisplayName("Capital cities in a continent: continent can be read from user input")
    void capitalCitiesByContinentFromInput() {
        assertArrayEquals(
                new Object[]{"Africa"},
                ReportDefinition.parameters(
                        new CapitalCitiesByContinentReport(typing("Africa"))
                ),
                "Continent parameter should be read from user input"
        );
    }

    @Test
    @DisplayName("Capital cities in a continent: null user input is rejected")
    void capitalCitiesByContinentRejectsNullInput() {
        assertThrows(
                NullPointerException.class,
                () -> new CapitalCitiesByContinentReport((UserInput) null),
                "Null user input should be rejected"
        );
    }

    // ------------------------------------------- capital cities by region

    @Test
    @DisplayName("Capital cities in a region: title, SQL and parameter")
    void capitalCitiesByRegion() {
        assertDefinition(
                new CapitalCitiesByRegionReport("Southern Europe"),
                "Capital Cities in Southern Europe by Population",
                "WHERE c.Region = ? " + BY_POPULATION,
                "Southern Europe"
        );
    }

    @Test
    @DisplayName("Capital cities in a region: spaces around the region are removed")
    void capitalCitiesByRegionTrims() {
        assertDefinition(
                new CapitalCitiesByRegionReport(" Caribbean "),
                "Capital Cities in Caribbean by Population",
                "WHERE c.Region = ? " + BY_POPULATION,
                "Caribbean"
        );
    }

    @Test
    @DisplayName("Capital cities in a region: a null or blank region is rejected")
    void capitalCitiesByRegionRejectsBlank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CapitalCitiesByRegionReport((String) null),
                "Null region should be rejected"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new CapitalCitiesByRegionReport("   "),
                "Blank region should be rejected"
        );
    }

    @Test
    @DisplayName("Capital cities in a region: region can be read from user input")
    void capitalCitiesByRegionFromInput() {
        assertArrayEquals(
                new Object[]{"Polynesia"},
                ReportDefinition.parameters(
                        new CapitalCitiesByRegionReport(typing("Polynesia"))
                ),
                "Region parameter should be read from user input"
        );
    }

    @Test
    @DisplayName("Capital cities in a region: null user input is rejected")
    void capitalCitiesByRegionRejectsNullInput() {
        assertThrows(
                NullPointerException.class,
                () -> new CapitalCitiesByRegionReport((UserInput) null),
                "Null user input should be rejected"
        );
    }

    // ------------------------------------------- top N capital cities worldwide

    @Test
    @DisplayName("Top N capital cities worldwide: title, SQL and parameter")
    void topCapitalCitiesWorldwide() {
        assertDefinition(
                new TopCapitalCitiesWorldwideReport(5),
                "Top 5 Populated Capital Cities Worldwide",
                BY_POPULATION + " LIMIT ?",
                5
        );
    }

    @Test
    @DisplayName("Top N capital cities worldwide: N must be greater than 0")
    void topCapitalCitiesWorldwideRejectsBadN() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new TopCapitalCitiesWorldwideReport(0),
                "Zero limit should be rejected"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new TopCapitalCitiesWorldwideReport(-1),
                "Negative limit should be rejected"
        );
    }

    @Test
    @DisplayName("Top N capital cities worldwide: N can be read from user input")
    void topCapitalCitiesWorldwideFromInput() {
        assertArrayEquals(
                new Object[]{8},
                ReportDefinition.parameters(
                        new TopCapitalCitiesWorldwideReport(typing("8"))
                ),
                "Top N limit should be read from user input"
        );
    }

    @Test
    @DisplayName("Top N capital cities worldwide: null user input is rejected")
    void topCapitalCitiesWorldwideRejectsNullInput() {
        assertThrows(
                NullPointerException.class,
                () -> new TopCapitalCitiesWorldwideReport((UserInput) null),
                "Null user input should be rejected"
        );
    }

    // ------------------------------------------- top N capital cities by continent

    @Test
    @DisplayName("Top N capital cities in a continent: title, SQL and parameters")
    void topCapitalCitiesByContinent() {
        assertDefinition(
                new TopCapitalCitiesByContinentReport("Asia", 5),
                "Top 5 Populated Capital Cities in Asia",
                "WHERE c.Continent = ? " + BY_POPULATION + " LIMIT ?",
                "Asia",
                5
        );
    }

    @Test
    @DisplayName("Top N capital cities in a continent: spaces are removed")
    void topCapitalCitiesByContinentTrims() {
        assertArrayEquals(
                new Object[]{"Europe", 3},
                ReportDefinition.parameters(
                        new TopCapitalCitiesByContinentReport(" Europe ", 3)
                ),
                "Surrounding spaces should be trimmed from the continent name"
        );
    }

    @Test
    @DisplayName("Top N capital cities in a continent: blank continent or bad N is rejected")
    void topCapitalCitiesByContinentRejectsBadValues() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new TopCapitalCitiesByContinentReport("  ", 5),
                "Blank continent should be rejected"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new TopCapitalCitiesByContinentReport("Asia", 0),
                "Zero limit should be rejected"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new TopCapitalCitiesByContinentReport("Asia", -1),
                "Negative limit should be rejected"
        );
    }

    @Test
    @DisplayName("Top N capital cities in a continent: values are read from user input")
    void topCapitalCitiesByContinentFromInput() {
        assertArrayEquals(
                new Object[]{"Oceania", 2},
                ReportDefinition.parameters(
                        new TopCapitalCitiesByContinentReport(
                                typing("Oceania", "2")
                        )
                ),
                "Continent and top N parameters should be read from user input"
        );
    }

    @Test
    @DisplayName("Top N capital cities in a continent: null user input is rejected")
    void topCapitalCitiesByContinentRejectsNullInput() {
        assertThrows(
                NullPointerException.class,
                () -> new TopCapitalCitiesByContinentReport((UserInput) null),
                "Null user input should be rejected"
        );
    }

    // ------------------------------------------- top N capital cities by region

    @Test
    @DisplayName("Top N capital cities in a region: title, SQL and parameters")
    void topCapitalCitiesByRegion() {
        assertDefinition(
                new TopCapitalCitiesByRegionReport("Southern Europe", 5),
                "Top 5 Populated Capital Cities in Southern Europe",
                "WHERE c.Region = ? " + BY_POPULATION + " LIMIT ?",
                "Southern Europe",
                5
        );
    }

    @Test
    @DisplayName("Top N capital cities in a region: spaces are removed")
    void topCapitalCitiesByRegionTrims() {
        assertArrayEquals(
                new Object[]{"Caribbean", 4},
                ReportDefinition.parameters(
                        new TopCapitalCitiesByRegionReport(" Caribbean ", 4)
                ),
                "Surrounding spaces should be trimmed from the region name"
        );
    }

    @Test
    @DisplayName("Top N capital cities in a region: blank region or bad N is rejected")
    void topCapitalCitiesByRegionRejectsBadValues() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new TopCapitalCitiesByRegionReport("  ", 5),
                "Blank region should be rejected"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new TopCapitalCitiesByRegionReport("Caribbean", 0),
                "Zero limit should be rejected"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new TopCapitalCitiesByRegionReport("Caribbean", -1),
                "Negative limit should be rejected"
        );
    }

    @Test
    @DisplayName("Top N capital cities in a region: values are read from user input")
    void topCapitalCitiesByRegionFromInput() {
        assertArrayEquals(
                new Object[]{"Melanesia", 6},
                ReportDefinition.parameters(
                        new TopCapitalCitiesByRegionReport(
                                typing("Melanesia", "6")
                        )
                ),
                "Region and top N parameters should be read from user input"
        );
    }

    @Test
    @DisplayName("Top N capital cities in a region: null user input is rejected")
    void topCapitalCitiesByRegionRejectsNullInput() {
        assertThrows(
                NullPointerException.class,
                () -> new TopCapitalCitiesByRegionReport((UserInput) null),
                "Null user input should be rejected"
        );
    }

    // ------------------------------------------- shared CapitalCityReport code

    @Test
    @DisplayName("Capital city reports show the required report columns")
    void capitalCityColumns() {

        List<Column> columns =
                ReportDefinition.columns(
                        new AllCapitalCitiesInWorldReport()
                );

        assertEquals(
                List.of("Name", "Country", "Population"),
                columns.stream().map(Column::heading).toList(),
                "Capital city report should include Name, Country, and Population columns"
        );

        assertEquals(
                Alignment.RIGHT,
                columns.get(2).alignment(),
                "Population column should be right-aligned"
        );
        assertTrue(
                columns.get(2).zeroIsMissing(),
                "Population column should treat zero as missing"
        );
    }

    @Test
    @DisplayName("Capital city reports select from country and join city")
    void capitalCitySelect() {

        String select =
                ReportDefinition.select(
                        new AllCapitalCitiesInWorldReport()
                );

        assertTrue(
                select.startsWith(
                        "SELECT ci.Name, c.Name AS Country, ci.Population"
                ),
                "SQL query should select capital city name, country name, and population"
        );

        assertTrue(
                select.contains("FROM country c"),
                "SQL query should select from the country table"
        );
        assertTrue(
                select.contains(
                        "JOIN city ci ON c.Capital = ci.ID"
                ),
                "SQL query should join the city table on country capital ID"
        );
    }

    @Test
    @DisplayName("A capital city row is read into the three report values")
    void readsCapitalCityRow() throws SQLException {

        ResultSet row = FakeResultSet.row(
                "Name", "London",
                "Country", "United Kingdom",
                "Population", 8982000L
        );

        assertEquals(
                List.of(
                        "London",
                        "United Kingdom",
                        8982000L
                ),
                ReportDefinition.readRow(
                        new AllCapitalCitiesInWorldReport(),
                        row
                ),
                "Row values should match city name, country name, and population"
        );
    }
}