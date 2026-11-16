
package com.team14.report.city;

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
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("City Report Tests")
class CityReportTest {

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

        assertEquals(title, report.title());
        assertEquals(clauses, ReportDefinition.clauses(report));
        assertArrayEquals(parameters, ReportDefinition.parameters(report));

        assertEquals(
                ReportDefinition.placeholders(report),
                parameters.length,
                "Every ? in the SQL needs one parameter"
        );
    }

    // ------------------------------------------- all cities worldwide

    @Test
    @DisplayName("All cities worldwide: sorted by population, no parameters")
    void allCitiesInWorld() {
        assertDefinition(
                new AllCitiesInWorldReport(),
                "All Cities in the World by Population",
                BY_POPULATION
        );
    }

    // ------------------------------------------- cities by continent

    @Test
    @DisplayName("Cities in a continent: title, SQL and parameter")
    void citiesByContinent() {
        assertDefinition(
                new CitiesByContinentReport("Europe"),
                "Cities in Europe by Population",
                "WHERE c.Continent = ? " + BY_POPULATION,
                "Europe"
        );
    }

    @Test
    @DisplayName("Cities in a continent: spaces around the continent are removed")
    void citiesByContinentTrims() {
        assertDefinition(
                new CitiesByContinentReport("  Asia "),
                "Cities in Asia by Population",
                "WHERE c.Continent = ? " + BY_POPULATION,
                "Asia"
        );
    }

    @Test
    @DisplayName("Cities in a continent: a null or blank continent is rejected")
    void citiesByContinentRejectsBlank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CitiesByContinentReport((String) null)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new CitiesByContinentReport("   ")
        );
    }

    @Test
    @DisplayName("Cities in a continent: continent can be read from user input")
    void citiesByContinentFromInput() {
        assertArrayEquals(
                new Object[]{"Africa"},
                ReportDefinition.parameters(
                        new CitiesByContinentReport(typing("Africa"))
                )
        );
    }

    @Test
    @DisplayName("Cities in a continent: null user input is rejected")
    void citiesByContinentRejectsNullInput() {
        assertThrows(
                NullPointerException.class,
                () -> new CitiesByContinentReport((UserInput) null)
        );
    }

    // ------------------------------------------- cities by country

    @Test
    @DisplayName("Cities in a country: title, SQL and parameters")
    void citiesByCountry() {
        assertDefinition(
                new CitiesByCountryReport("United Kingdom"),
                "Cities in United Kingdom by Population",
                "WHERE c.Name = ? OR c.Code = ? " + BY_POPULATION,
                "United Kingdom",
                "United Kingdom"
        );
    }

    @Test
    @DisplayName("Cities in a country: spaces around the country are removed")
    void citiesByCountryTrims() {
        assertArrayEquals(
                new Object[]{"United Kingdom", "United Kingdom"},
                ReportDefinition.parameters(
                        new CitiesByCountryReport(" United Kingdom ")
                )
        );
    }

    @Test
    @DisplayName("Cities in a country: a null or blank country is rejected")
    void citiesByCountryRejectsBlank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CitiesByCountryReport((String) null)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new CitiesByCountryReport("   ")
        );
    }

    @Test
    @DisplayName("Cities in a country: country can be read from user input")
    void citiesByCountryFromInput() {
        assertArrayEquals(
                new Object[]{"Japan", "Japan"},
                ReportDefinition.parameters(
                        new CitiesByCountryReport(typing("Japan"))
                )
        );
    }

    @Test
    @DisplayName("Cities in a country: null user input is rejected")
    void citiesByCountryRejectsNullInput() {
        assertThrows(
                NullPointerException.class,
                () -> new CitiesByCountryReport((UserInput) null)
        );
    }

    // ------------------------------------------- cities by district

    @Test
    @DisplayName("Cities in a district: title, SQL and parameter")
    void citiesByDistrict() {
        assertDefinition(
                new CitiesByDistrictReport("England"),
                "Cities in England District by Population",
                "WHERE ci.District = ? " + BY_POPULATION,
                "England"
        );
    }

    @Test
    @DisplayName("Cities in a district: spaces around the district are removed")
    void citiesByDistrictTrims() {
        assertArrayEquals(
                new Object[]{"England"},
                ReportDefinition.parameters(
                        new CitiesByDistrictReport(" England ")
                )
        );
    }

    @Test
    @DisplayName("Cities in a district: a null or blank district is rejected")
    void citiesByDistrictRejectsBlank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CitiesByDistrictReport((String) null)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new CitiesByDistrictReport("   ")
        );
    }

    @Test
    @DisplayName("Cities in a district: district can be read from user input")
    void citiesByDistrictFromInput() {
        assertArrayEquals(
                new Object[]{"Scotland"},
                ReportDefinition.parameters(
                        new CitiesByDistrictReport(typing("Scotland"))
                )
        );
    }

    @Test
    @DisplayName("Cities in a district: null user input is rejected")
    void citiesByDistrictRejectsNullInput() {
        assertThrows(
                NullPointerException.class,
                () -> new CitiesByDistrictReport((UserInput) null)
        );
    }

    // ------------------------------------------- cities by region

    @Test
    @DisplayName("Cities in a region: title, SQL and parameter")
    void citiesByRegion() {
        assertDefinition(
                new CitiesByRegionReport("Southern Europe"),
                "Cities in Southern Europe by Population",
                "WHERE c.Region = ? " + BY_POPULATION,
                "Southern Europe"
        );
    }

    @Test
    @DisplayName("Cities in a region: spaces around the region are removed")
    void citiesByRegionTrims() {
        assertArrayEquals(
                new Object[]{"Caribbean"},
                ReportDefinition.parameters(
                        new CitiesByRegionReport(" Caribbean ")
                )
        );
    }

    @Test
    @DisplayName("Cities in a region: a null or blank region is rejected")
    void citiesByRegionRejectsBlank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CitiesByRegionReport((String) null)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new CitiesByRegionReport("   ")
        );
    }

    @Test
    @DisplayName("Cities in a region: region can be read from user input")
    void citiesByRegionFromInput() {
        assertArrayEquals(
                new Object[]{"Polynesia"},
                ReportDefinition.parameters(
                        new CitiesByRegionReport(typing("Polynesia"))
                )
        );
    }

    @Test
    @DisplayName("Cities in a region: null user input is rejected")
    void citiesByRegionRejectsNullInput() {
        assertThrows(
                NullPointerException.class,
                () -> new CitiesByRegionReport((UserInput) null)
        );
    }

    // ------------------------------------------- top N cities worldwide

    @Test
    @DisplayName("Top N cities worldwide: title, SQL and parameter")
    void topCitiesWorldwide() {
        assertDefinition(
                new TopCitiesByWorldwideReport(5),
                "Top 5 Populated Cities Worldwide",
                BY_POPULATION + " LIMIT ?",
                5
        );
    }

    @Test
    @DisplayName("Top N cities worldwide: N must be greater than 0")
    void topCitiesWorldwideRejectsBadN() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new TopCitiesByWorldwideReport(0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new TopCitiesByWorldwideReport(-1)
        );
    }

    @Test
    @DisplayName("Top N cities worldwide: N can be read from user input")
    void topCitiesWorldwideFromInput() {
        assertArrayEquals(
                new Object[]{8},
                ReportDefinition.parameters(
                        new TopCitiesByWorldwideReport(typing("8"))
                )
        );
    }

    @Test
    @DisplayName("Top N cities worldwide: null user input is rejected")
    void topCitiesWorldwideRejectsNullInput() {
        assertThrows(
                NullPointerException.class,
                () -> new TopCitiesByWorldwideReport((UserInput) null)
        );
    }

    // ------------------------------------------- top N cities by continent

    @Test
    @DisplayName("Top N cities in a continent: title, SQL and parameters")
    void topCitiesByContinent() {
        assertDefinition(
                new TopCitiesByContinentReport("Asia", 5),
                "Top 5 Populated Cities in Asia",
                "WHERE c.Continent = ? " + BY_POPULATION + " LIMIT ?",
                "Asia",
                5
        );
    }

    @Test
    @DisplayName("Top N cities in a continent: spaces are removed")
    void topCitiesByContinentTrims() {
        assertArrayEquals(
                new Object[]{"Europe", 3},
                ReportDefinition.parameters(
                        new TopCitiesByContinentReport(" Europe ", 3)
                )
        );
    }

    @Test
    @DisplayName("Top N cities in a continent: blank continent or bad N is rejected")
    void topCitiesByContinentRejectsBadValues() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new TopCitiesByContinentReport("  ", 5)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new TopCitiesByContinentReport("Asia", 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new TopCitiesByContinentReport("Asia", -1)
        );
    }

    @Test
    @DisplayName("Top N cities in a continent: values are read from user input")
    void topCitiesByContinentFromInput() {
        assertArrayEquals(
                new Object[]{"Oceania", 2},
                ReportDefinition.parameters(
                        new TopCitiesByContinentReport(
                                typing("Oceania", "2")
                        )
                )
        );
    }

    @Test
    @DisplayName("Top N cities in a continent: null user input is rejected")
    void topCitiesByContinentRejectsNullInput() {
        assertThrows(
                NullPointerException.class,
                () -> new TopCitiesByContinentReport((UserInput) null)
        );
    }

    // ------------------------------------------- top N cities by country

    @Test
    @DisplayName("Top N cities in a country: title, SQL and parameters")
    void topCitiesByCountry() {
        assertDefinition(
                new TopCitiesByCountryReport("United Kingdom", 5),
                "Top 5 Populated Cities in United Kingdom",
                "WHERE c.Name = ? " + BY_POPULATION + " LIMIT ?",
                "United Kingdom",
                5
        );
    }

    @Test
    @DisplayName("Top N cities in a country: spaces are removed")
    void topCitiesByCountryTrims() {
        assertArrayEquals(
                new Object[]{"Japan", 3},
                ReportDefinition.parameters(
                        new TopCitiesByCountryReport(" Japan ", 3)
                )
        );
    }

    @Test
    @DisplayName("Top N cities in a country: blank country or bad N is rejected")
    void topCitiesByCountryRejectsBadValues() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new TopCitiesByCountryReport("  ", 5)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new TopCitiesByCountryReport("Japan", 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new TopCitiesByCountryReport("Japan", -1)
        );
    }

    @Test
    @DisplayName("Top N cities in a country: values are read from user input")
    void topCitiesByCountryFromInput() {
        assertArrayEquals(
                new Object[]{"Japan", 4},
                ReportDefinition.parameters(
                        new TopCitiesByCountryReport(
                                typing("Japan", "4")
                        )
                )
        );
    }

    @Test
    @DisplayName("Top N cities in a country: null user input is rejected")
    void topCitiesByCountryRejectsNullInput() {
        assertThrows(
                NullPointerException.class,
                () -> new TopCitiesByCountryReport((UserInput) null)
        );
    }

    // ------------------------------------------- top N cities by district

    @Test
    @DisplayName("Top N cities in a district: title, SQL and parameters")
    void topCitiesByDistrict() {
        assertDefinition(
                new TopCitiesByDistrictReport("England", 5),
                "Top 5 Populated Cities in England",
                "WHERE ci.District = ? " + BY_POPULATION + " LIMIT ?",
                "England",
                5
        );
    }

    @Test
    @DisplayName("Top N cities in a district: spaces are removed")
    void topCitiesByDistrictTrims() {
        assertArrayEquals(
                new Object[]{"Scotland", 3},
                ReportDefinition.parameters(
                        new TopCitiesByDistrictReport(" Scotland ", 3)
                )
        );
    }

    @Test
    @DisplayName("Top N cities in a district: blank district or bad N is rejected")
    void topCitiesByDistrictRejectsBadValues() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new TopCitiesByDistrictReport("  ", 5)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new TopCitiesByDistrictReport("England", 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new TopCitiesByDistrictReport("England", -1)
        );
    }

    @Test
    @DisplayName("Top N cities in a district: values are read from user input")
    void topCitiesByDistrictFromInput() {
        assertArrayEquals(
                new Object[]{"Scotland", 2},
                ReportDefinition.parameters(
                        new TopCitiesByDistrictReport(
                                typing("Scotland", "2")
                        )
                )
        );
    }

    @Test
    @DisplayName("Top N cities in a district: null user input is rejected")
    void topCitiesByDistrictRejectsNullInput() {
        assertThrows(
                NullPointerException.class,
                () -> new TopCitiesByDistrictReport((UserInput) null)
        );
    }

    // ------------------------------------------- top N cities by region

    @Test
    @DisplayName("Top N cities in a region: title, SQL and parameters")
    void topCitiesByRegion() {
        assertDefinition(
                new TopCitiesByRegionReport("Southern Europe", 5),
                "Top 5 Populated Cities in Southern Europe",
                "WHERE c.Region = ? " + BY_POPULATION + " LIMIT ?",
                "Southern Europe",
                5
        );
    }

    @Test
    @DisplayName("Top N cities in a region: spaces are removed")
    void topCitiesByRegionTrims() {
        assertArrayEquals(
                new Object[]{"Caribbean", 4},
                ReportDefinition.parameters(
                        new TopCitiesByRegionReport(" Caribbean ", 4)
                )
        );
    }

    @Test
    @DisplayName("Top N cities in a region: blank region or bad N is rejected")
    void topCitiesByRegionRejectsBadValues() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new TopCitiesByRegionReport("  ", 5)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new TopCitiesByRegionReport("Caribbean", 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new TopCitiesByRegionReport("Caribbean", -1)
        );
    }

    @Test
    @DisplayName("Top N cities in a region: values are read from user input")
    void topCitiesByRegionFromInput() {
        assertArrayEquals(
                new Object[]{"Melanesia", 6},
                ReportDefinition.parameters(
                        new TopCitiesByRegionReport(
                                typing("Melanesia", "6")
                        )
                )
        );
    }

    @Test
    @DisplayName("Top N cities in a region: null user input is rejected")
    void topCitiesByRegionRejectsNullInput() {
        assertThrows(
                NullPointerException.class,
                () -> new TopCitiesByRegionReport((UserInput) null)
        );
    }

    // ------------------------------------------- shared CityReport code

    @Test
    @DisplayName("City reports show the required report columns")
    void cityColumns() {

        List<Column> columns =
                ReportDefinition.columns(new AllCitiesInWorldReport());

        assertEquals(
                List.of("Name", "Country", "District", "Population"),
                columns.stream().map(Column::heading).toList()
        );

        assertEquals(Alignment.RIGHT, columns.get(3).alignment());
        assertTrue(columns.get(3).zeroIsMissing());
    }

    @Test
    @DisplayName("City reports select from city and join country")
    void citySelect() {

        String select =
                ReportDefinition.select(new AllCitiesInWorldReport());

        assertTrue(
                select.startsWith(
                        "SELECT ci.Name, c.Name AS Country,"
                )
        );

        assertTrue(select.contains("ci.District, ci.Population"));
        assertTrue(select.contains("FROM city ci"));
        assertTrue(
                select.contains(
                        "JOIN country c ON ci.CountryCode = c.Code"
                )
        );
    }

    @Test
    @DisplayName("A city row is read into the four report values")
    void readsCityRow() throws SQLException {

        ResultSet row = FakeResultSet.row(
                "Name", "Edinburgh",
                "Country", "United Kingdom",
                "District", "Scotland",
                "Population", 450180L
        );

        assertEquals(
                List.of(
                        "Edinburgh",
                        "United Kingdom",
                        "Scotland",
                        450180L
                ),
                ReportDefinition.readRow(
                        new AllCitiesInWorldReport(),
                        row
                )
        );
    }

    @Test
    @DisplayName("A city with a null district is read correctly")
    void readsCityWithNullDistrict() throws SQLException {

        ResultSet row = FakeResultSet.row(
                "Name", "Test City",
                "Country", "Test Country",
                "District", null,
                "Population", 1000L
        );

        assertEquals(
                Arrays.asList(
                        "Test City",
                        "Test Country",
                        null,
                        1000L
                ),
                ReportDefinition.readRow(
                        new AllCitiesInWorldReport(),
                        row
                )
        );
    }
}
