package com.team14.report.country;

import com.team14.report.ReportOutput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static com.team14.report.ReportAssertions.assertWellFormed;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for the country reports (requirements 1 to 6).
 * <p>
 * Each test runs a report against the real MySQL "world" database (needs the
 * MySQL container) and checks what is printed. The expected numbers come from
 * database/world.sql: 239 countries, 46 in Europe, 51 in Asia and 15 in
 * Southern Europe.
 */
class CountryReportIntegrationTest {

    // Expected table column order shared across all country report outputs
    private static final List<String> HEADINGS = List.of(
            "Code", "Name", "Continent", "Region", "Population", "Capital");

    // Helper methods

    // Executes a CountryReport and captures its formatted console/report output
    private static ReportOutput run(CountryReport report) throws SQLException {
        return ReportOutput.capture(report);
    }

    // Verifies that every row in the given column matches the expected value
    private static void assertAllRowsHave(ReportOutput output, String heading, String expected) {
        assertTrue(output.column(heading).stream().allMatch(expected::equals),
                "Every row should have " + heading + " = " + expected);
    }

    // Requirement 1: All countries in the world

    @Test
    @DisplayName("All countries: every country is listed, largest population first")
    void allCountriesInWorld() throws SQLException {
        // Run report for all countries globally
        ReportOutput output = run(new AllCountriesInWorldReport());

        // Verify report title, column headings, and total country count
        assertWellFormed(output, "All Countries in the World by Population", HEADINGS);
        assertEquals(239, output.totalRecords(),
                "Expected all 239 countries in the world database");
    }

    @Test
    @DisplayName("All countries: the first row shows every column correctly")
    void allCountriesFirstRow() throws SQLException {
        // Fetch the first row (highest population country in the dataset)
        Map<String, String> first = run(new AllCountriesInWorldReport()).firstRow();

        // Check every column value for China in the "world" dataset
        assertAll(
                () -> assertEquals("CHN", first.get("Code"),
                        "Expected first country code to be CHN"),
                () -> assertEquals("China", first.get("Name"),
                        "Expected first country name to be China"),
                () -> assertEquals("Asia", first.get("Continent"),
                        "Expected first country continent to be Asia"),
                () -> assertEquals("Eastern Asia", first.get("Region"),
                        "Expected first country region to be Eastern Asia"),
                () -> assertEquals("1,277,558,000", first.get("Population"),
                        "Expected formatted population for China"),
                () -> assertEquals("Peking", first.get("Capital"),
                        "Expected capital of China from world database to be Peking")
        );
    }

    @Test
    @DisplayName("All countries: a country with no capital and no population shows 'No ... recorded'")
    void allCountriesShowsMissingValues() throws SQLException {
        // Look up Antarctica (ATA), which has missing capital and population in world.sql
        Map<String, String> antarctica =
                run(new AllCountriesInWorldReport()).rowWhere("Code", "ATA");

        // Ensure fallback labels are displayed for missing values
        assertAll(
                () -> assertEquals("No capital recorded", antarctica.get("Capital"),
                        "Expected missing capital for ATA to show 'No capital recorded'"),
                () -> assertEquals("No population recorded", antarctica.get("Population"),
                        "Expected missing population for ATA to show 'No population recorded'")
        );
    }

    // Requirement 2: Countries in a continent

    @Test
    @DisplayName("Countries by continent: only countries of that continent are listed")
    void countriesByContinent() throws SQLException {
        // Filter countries by Europe
        ReportOutput output = run(new CountriesByContinentReport("Europe"));

        // Verify title, headings, count, continent filter, and top country details
        assertWellFormed(output, "Countries in Europe by Population", HEADINGS);
        assertEquals(46, output.totalRecords(),
                "Expected 46 countries in Europe");
        assertAllRowsHave(output, "Continent", "Europe");
        assertEquals("Russian Federation", output.firstRow().get("Name"),
                "Expected most populated country in Europe to be Russian Federation");
        assertEquals("146,934,000", output.firstRow().get("Population"),
                "Expected formatted population for Russian Federation");
    }

    @Test
    @DisplayName("Countries by continent: spaces around the continent are ignored")
    void countriesByContinentTrimsInput() throws SQLException {
        // Pass leading and trailing spaces around "Europe"
        ReportOutput output = run(new CountriesByContinentReport("  Europe "));

        // Confirm input trimming still produces the expected Europe report
        assertEquals("Countries in Europe by Population", output.title(),
                "Expected trimmed continent input to produce standard Europe report title");
        assertEquals(46, output.totalRecords(),
                "Expected trimmed continent input to return all 46 European countries");
    }

    @Test
    @DisplayName("Countries by continent: an unknown continent gives an empty report")
    void countriesByUnknownContinent() throws SQLException {
        // Query a non-existent continent
        ReportOutput output = run(new CountriesByContinentReport("Atlantis"));

        // Expect zero records and no rows
        assertEquals(0, output.totalRecords(),
                "Expected 0 records for unknown continent Atlantis");
        assertTrue(output.rows().isEmpty(),
                "Expected rows list to be empty for unknown continent Atlantis");
    }

    @Test
    @DisplayName("Countries by continent: SQL in the input is treated as plain text")
    void countriesByContinentIsNotOpenToSqlInjection() throws SQLException {
        // Pass a SQL injection string to verify parameterized/safe handling
        ReportOutput output = run(new CountriesByContinentReport("Europe' OR '1'='1"));

        // Should match no continent and return 0 records
        assertEquals(0, output.totalRecords(),
                "Expected SQL injection payload to be treated literally and return 0 rows");
    }

    // Requirement 3: Countries in a region

    @Test
    @DisplayName("Countries by region: only countries of that region are listed")
    void countriesByRegion() throws SQLException {
        // Filter countries by Southern Europe
        ReportOutput output = run(new CountriesByRegionReport("Southern Europe"));

        // Verify structure, record count, region consistency, and top country
        assertWellFormed(output, "Countries in Southern Europe by Population", HEADINGS);
        assertEquals(15, output.totalRecords(),
                "Expected 15 countries in Southern Europe");
        assertAllRowsHave(output, "Region", "Southern Europe");
        assertEquals("Italy", output.firstRow().get("Name"),
                "Expected most populated country in Southern Europe to be Italy");
    }

    @Test
    @DisplayName("Countries by region: an unknown region gives an empty report")
    void countriesByUnknownRegion() throws SQLException {
        // Query a non-existent region and expect 0 records
        assertEquals(0, run(new CountriesByRegionReport("Narnia")).totalRecords(),
                "Expected 0 records for unknown region Narnia");
    }

    // Requirement 4: Top N countries in the world

    @Test
    @DisplayName("Top N countries worldwide: the N most populated countries are listed")
    void topCountriesWorldwide() throws SQLException {
        // Request top 5 most populated countries globally
        ReportOutput output = run(new TopCountriesWorldwideReport(5));

        // Verify title, headings, count of 5, and exact descending order of names
        assertWellFormed(output, "Top 5 Populated Countries Worldwide", HEADINGS);
        assertEquals(5, output.totalRecords(),
                "Expected 5 records for top 5 countries worldwide");
        assertEquals(
                List.of("China", "India", "United States", "Indonesia", "Brazil"),
                output.column("Name"),
                "Expected top 5 most populated countries worldwide in descending order");
    }

    @Test
    @DisplayName("Top N countries worldwide: N = 1 gives only the largest country")
    void topCountriesWorldwideOne() throws SQLException {
        // Request only the single largest country worldwide
        ReportOutput output = run(new TopCountriesWorldwideReport(1));

        // Verify only 1 row is returned and it is China
        assertEquals(1, output.totalRecords(),
                "Expected 1 record when N = 1");
        assertEquals("China", output.firstRow().get("Name"),
                "Expected China to be the single most populated country worldwide");
    }

    @Test
    @DisplayName("Top N countries worldwide: N larger than the data gives every country")
    void topCountriesWorldwideMoreThanExist() throws SQLException {
        // Request 500 when only 239 countries exist; expect all 239
        assertEquals(239, run(new TopCountriesWorldwideReport(500)).totalRecords(),
                "Expected all 239 countries when N exceeds total country count");
    }

    // Requirement 5: Top N countries in a continent

    @Test
    @DisplayName("Top N countries in a continent: only that continent, N rows")
    void topCountriesByContinent() throws SQLException {
        // Request top 5 most populated countries in Asia
        ReportOutput output = run(new TopCountriesByContinentReport("Asia", 5));

        // Verify title, headings, row count, continent value, and ranking order
        assertWellFormed(output, "Top 5 Populated Countries in Asia", HEADINGS);
        assertEquals(5, output.totalRecords(),
                "Expected 5 records for top 5 countries in Asia");
        assertAllRowsHave(output, "Continent", "Asia");
        assertEquals(
                List.of("China", "India", "Indonesia", "Pakistan", "Bangladesh"),
                output.column("Name"),
                "Expected top 5 most populated countries in Asia in descending order");
    }

    @Test
    @DisplayName("Top N countries in a continent: N larger than the continent gives all of it")
    void topCountriesByContinentMoreThanExist() throws SQLException {
        // Request 100 countries in Europe (which has 46); expect all 46
        assertEquals(46, run(new TopCountriesByContinentReport("Europe", 100)).totalRecords(),
                "Expected all 46 European countries when N exceeds continent size");
    }

    // Requirement 6: Top N countries in a region

    @Test
    @DisplayName("Top N countries in a region: only that region, N rows")
    void topCountriesByRegion() throws SQLException {
        // Request top 3 most populated countries in Southern Europe
        ReportOutput output = run(new TopCountriesByRegionReport("Southern Europe", 3));

        // Verify title, headings, row count, region value, and top 3 country names
        assertWellFormed(output, "Top 3 Populated Countries in Southern Europe", HEADINGS);
        assertEquals(3, output.totalRecords(),
                "Expected 3 records for top 3 countries in Southern Europe");
        assertAllRowsHave(output, "Region", "Southern Europe");
        assertEquals(List.of("Italy", "Spain", "Yugoslavia"), output.column("Name"),
                "Expected top 3 most populated countries in Southern Europe in descending order");
    }

    @Test
    @DisplayName("Top N countries in a region: an unknown region gives an empty report")
    void topCountriesByUnknownRegion() throws SQLException {
        // Request top 3 from an unknown region; expect 0 records
        assertEquals(0, run(new TopCountriesByRegionReport("Narnia", 3)).totalRecords(),
                "Expected 0 records for unknown region Narnia");
    }
}