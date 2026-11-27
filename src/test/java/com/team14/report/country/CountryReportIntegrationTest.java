package com.team14.report.country;

import com.team14.report.ReportOutput;
import org.junit.jupiter.api.BeforeAll;
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

    // Cached outputs for repeated report queries against the MySQL "world" database
    private static ReportOutput allCountriesOutput;
    private static ReportOutput europeCountriesOutput;
    private static ReportOutput trimmedEuropeCountriesOutput;
    private static ReportOutput unknownContinentOutput;
    private static ReportOutput southernEuropeCountriesOutput;
    private static ReportOutput topFiveWorldOutput;
    private static ReportOutput topOneWorldOutput;
    private static ReportOutput topFiveAsiaOutput;
    private static ReportOutput topThreeSouthernEuropeOutput;

    // Executes shared report queries once before running integration tests
    @BeforeAll
    static void setUpReportOutputs() throws SQLException {
        allCountriesOutput = ReportOutput.capture(new AllCountriesInWorldReport());
        europeCountriesOutput = ReportOutput.capture(new CountriesByContinentReport("Europe"));
        trimmedEuropeCountriesOutput = ReportOutput.capture(new CountriesByContinentReport("  Europe "));
        unknownContinentOutput = ReportOutput.capture(new CountriesByContinentReport("Atlantis"));
        southernEuropeCountriesOutput = ReportOutput.capture(new CountriesByRegionReport("Southern Europe"));
        topFiveWorldOutput = ReportOutput.capture(new TopCountriesWorldwideReport(5));
        topOneWorldOutput = ReportOutput.capture(new TopCountriesWorldwideReport(1));
        topFiveAsiaOutput = ReportOutput.capture(new TopCountriesByContinentReport("Asia", 5));
        topThreeSouthernEuropeOutput = ReportOutput.capture(new TopCountriesByRegionReport("Southern Europe", 3));
    }

    // ---------------------------------------------------------------- helpers

    // Checks whether every entry in the specified column matches the expected value
    private static boolean allRowsHave(ReportOutput output, String heading, String expected) {
        return output.column(heading).stream().allMatch(expected::equals);
    }

    // ------------------------------------------- Requirement 1: All countries in the world

    @Test
    @DisplayName("All countries: report header and column headings are well-formed")
    void allCountriesWellFormed() {
        // Verify report title and required six column headings
        assertWellFormed(allCountriesOutput, "All Countries in the World by Population", HEADINGS);
    }

    @Test
    @DisplayName("All countries: every country in the world dataset is listed")
    void allCountriesInWorld() {
        // Verify total country count matches world.sql (239 countries)
        assertEquals(239, allCountriesOutput.totalRecords(),
                "Expected all 239 countries in the world database");
    }

    @Test
    @DisplayName("All countries: the first row shows every column correctly")
    void allCountriesFirstRow() {
        // Fetch the first row (highest population country in the dataset)
        Map<String, String> first = allCountriesOutput.firstRow();

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
    void allCountriesShowsMissingValues() {
        // Look up Antarctica (ATA), which has missing capital and population in world.sql
        Map<String, String> antarctica = allCountriesOutput.rowWhere("Code", "ATA");

        // Ensure fallback labels are displayed for missing values
        assertAll(
                () -> assertEquals("No capital recorded", antarctica.get("Capital"),
                        "Expected missing capital for ATA to show 'No capital recorded'"),
                () -> assertEquals("No population recorded", antarctica.get("Population"),
                        "Expected missing population for ATA to show 'No population recorded'")
        );
    }

    // ------------------------------------------- Requirement 2: Countries in a continent

    @Test
    @DisplayName("Countries by continent: report structure is well-formed for Europe")
    void countriesByContinentWellFormed() {
        // Verify title and headings for the Europe country report
        assertWellFormed(europeCountriesOutput, "Countries in Europe by Population", HEADINGS);
    }

    @Test
    @DisplayName("Countries by continent: returns all 46 countries in Europe")
    void countriesByContinentCount() {
        // Verify record count matches world.sql for Europe
        assertEquals(46, europeCountriesOutput.totalRecords(),
                "Expected 46 countries in Europe");
    }

    @Test
    @DisplayName("Countries by continent: every row belongs to Europe")
    void countriesByContinentAllMatchEurope() {
        // Confirm continent filter applied across all returned rows
        assertTrue(allRowsHave(europeCountriesOutput, "Continent", "Europe"),
                "Every row should have Continent = Europe");
    }

    @Test
    @DisplayName("Countries by continent: most populated country in Europe is Russian Federation")
    void countriesByContinentTopCountryName() {
        // Confirm descending population sort places Russian Federation first
        assertEquals("Russian Federation", europeCountriesOutput.firstRow().get("Name"),
                "Expected most populated country in Europe to be Russian Federation");
    }

    @Test
    @DisplayName("Countries by continent: formatted population of Russian Federation matches")
    void countriesByContinentTopCountryPopulation() {
        // Confirm formatted population string for the top European country
        assertEquals("146,934,000", europeCountriesOutput.firstRow().get("Population"),
                "Expected formatted population for Russian Federation");
    }

    @Test
    @DisplayName("Countries by continent: leading and trailing spaces keep expected title")
    void countriesByContinentTrimsInputTitle() {
        // Confirm input trimming still produces the standard Europe report title
        assertEquals("Countries in Europe by Population", trimmedEuropeCountriesOutput.title(),
                "Expected trimmed continent input to produce standard Europe report title");
    }

    @Test
    @DisplayName("Countries by continent: leading and trailing spaces return all 46 countries")
    void countriesByContinentTrimsInputCount() {
        // Confirm input trimming still returns all 46 European countries
        assertEquals(46, trimmedEuropeCountriesOutput.totalRecords(),
                "Expected trimmed continent input to return all 46 European countries");
    }

    @Test
    @DisplayName("Countries by continent: an unknown continent gives zero total records")
    void countriesByUnknownContinentRecordCount() {
        // Expect zero records for a non-existent continent
        assertEquals(0, unknownContinentOutput.totalRecords(),
                "Expected 0 records for unknown continent Atlantis");
    }

    @Test
    @DisplayName("Countries by continent: an unknown continent gives an empty row list")
    void countriesByUnknownContinentRowsEmpty() {
        // Expect rows list to be empty for a non-existent continent
        assertTrue(unknownContinentOutput.rows().isEmpty(),
                "Expected rows list to be empty for unknown continent Atlantis");
    }

    @Test
    @DisplayName("Countries by continent: SQL in the input is treated as plain text")
    void countriesByContinentIsNotOpenToSqlInjection() throws SQLException {
        // Pass a SQL injection string to verify parameterized/safe handling
        ReportOutput output = ReportOutput.capture(
                new CountriesByContinentReport("Europe' OR '1'='1"));

        // Should match no continent and return 0 records
        assertEquals(0, output.totalRecords(),
                "Expected SQL injection payload to be treated literally and return 0 rows");
    }

    // ------------------------------------------- Requirement 3: Countries in a region

    @Test
    @DisplayName("Countries by region: report structure is well-formed for Southern Europe")
    void countriesByRegionWellFormed() {
        // Verify title and column headings for Southern Europe
        assertWellFormed(southernEuropeCountriesOutput,
                "Countries in Southern Europe by Population", HEADINGS);
    }

    @Test
    @DisplayName("Countries by region: returns all 15 countries in Southern Europe")
    void countriesByRegionCount() {
        // Verify record count matches world.sql for Southern Europe
        assertEquals(15, southernEuropeCountriesOutput.totalRecords(),
                "Expected 15 countries in Southern Europe");
    }

    @Test
    @DisplayName("Countries by region: every row belongs to Southern Europe")
    void countriesByRegionAllMatchRegion() {
        // Confirm region filter applied across all returned rows
        assertTrue(allRowsHave(southernEuropeCountriesOutput, "Region", "Southern Europe"),
                "Every row should have Region = Southern Europe");
    }

    @Test
    @DisplayName("Countries by region: most populated country in Southern Europe is Italy")
    void countriesByRegionTopCountry() {
        // Confirm descending population sort places Italy first
        assertEquals("Italy", southernEuropeCountriesOutput.firstRow().get("Name"),
                "Expected most populated country in Southern Europe to be Italy");
    }

    @Test
    @DisplayName("Countries by region: an unknown region gives an empty report")
    void countriesByUnknownRegion() throws SQLException {
        // Query a non-existent region and expect 0 records
        assertEquals(0, ReportOutput.capture(new CountriesByRegionReport("Narnia")).totalRecords(),
                "Expected 0 records for unknown region Narnia");
    }

    // ------------------------------------------- Requirement 4: Top N countries in the world

    @Test
    @DisplayName("Top N countries worldwide: report structure is well-formed for top 5")
    void topCountriesWorldwideWellFormed() {
        // Verify title and column headings for top 5 countries worldwide
        assertWellFormed(topFiveWorldOutput, "Top 5 Populated Countries Worldwide", HEADINGS);
    }

    @Test
    @DisplayName("Top N countries worldwide: returns exactly 5 records for N = 5")
    void topCountriesWorldwideCount() {
        // Verify row count matches requested limit N
        assertEquals(5, topFiveWorldOutput.totalRecords(),
                "Expected 5 records for top 5 countries worldwide");
    }

    @Test
    @DisplayName("Top N countries worldwide: lists top 5 countries in descending population order")
    void topCountriesWorldwideOrder() {
        // Verify exact descending population order for top 5 countries globally
        assertEquals(
                List.of("China", "India", "United States", "Indonesia", "Brazil"),
                topFiveWorldOutput.column("Name"),
                "Expected top 5 most populated countries worldwide in descending order");
    }

    @Test
    @DisplayName("Top N countries worldwide: N = 1 returns a single record")
    void topCountriesWorldwideOneCount() {
        // Verify only 1 row is returned when limit is 1
        assertEquals(1, topOneWorldOutput.totalRecords(),
                "Expected 1 record when N = 1");
    }

    @Test
    @DisplayName("Top N countries worldwide: N = 1 returns China as the largest country")
    void topCountriesWorldwideOneName() {
        // Verify China is the single most populated country worldwide
        assertEquals("China", topOneWorldOutput.firstRow().get("Name"),
                "Expected China to be the single most populated country worldwide");
    }

    @Test
    @DisplayName("Top N countries worldwide: N larger than the data gives every country")
    void topCountriesWorldwideMoreThanExist() throws SQLException {
        // Request 500 when only 239 countries exist; expect all 239
        assertEquals(239,
                ReportOutput.capture(new TopCountriesWorldwideReport(500)).totalRecords(),
                "Expected all 239 countries when N exceeds total country count");
    }

    // ------------------------------------------- Requirement 5: Top N countries in a continent

    @Test
    @DisplayName("Top N countries in a continent: report structure is well-formed for top 5 in Asia")
    void topCountriesByContinentWellFormed() {
        // Verify title and column headings for top 5 countries in Asia
        assertWellFormed(topFiveAsiaOutput, "Top 5 Populated Countries in Asia", HEADINGS);
    }

    @Test
    @DisplayName("Top N countries in a continent: returns 5 rows for top 5 in Asia")
    void topCountriesByContinentCount() {
        // Verify record count matches requested limit N
        assertEquals(5, topFiveAsiaOutput.totalRecords(),
                "Expected 5 records for top 5 countries in Asia");
    }

    @Test
    @DisplayName("Top N countries in a continent: every row belongs to Asia")
    void topCountriesByContinentAllMatchContinent() {
        // Confirm continent filter applied across all returned rows
        assertTrue(allRowsHave(topFiveAsiaOutput, "Continent", "Asia"),
                "Every row should have Continent = Asia");
    }

    @Test
    @DisplayName("Top N countries in a continent: lists top 5 Asian countries in descending order")
    void topCountriesByContinentOrder() {
        // Verify exact descending population order for top 5 countries in Asia
        assertEquals(
                List.of("China", "India", "Indonesia", "Pakistan", "Bangladesh"),
                topFiveAsiaOutput.column("Name"),
                "Expected top 5 most populated countries in Asia in descending order");
    }

    @Test
    @DisplayName("Top N countries in a continent: N larger than the continent gives all of it")
    void topCountriesByContinentMoreThanExist() throws SQLException {
        // Request 100 countries in Europe (which has 46); expect all 46
        assertEquals(46,
                ReportOutput.capture(new TopCountriesByContinentReport("Europe", 100)).totalRecords(),
                "Expected all 46 European countries when N exceeds continent size");
    }

    // ------------------------------------------- Requirement 6: Top N countries in a region

    @Test
    @DisplayName("Top N countries in a region: report structure is well-formed for top 3 in Southern Europe")
    void topCountriesByRegionWellFormed() {
        // Verify title and column headings for top 3 countries in Southern Europe
        assertWellFormed(topThreeSouthernEuropeOutput,
                "Top 3 Populated Countries in Southern Europe", HEADINGS);
    }

    @Test
    @DisplayName("Top N countries in a region: returns 3 rows for top 3 in Southern Europe")
    void topCountriesByRegionCount() {
        // Verify record count matches requested limit N
        assertEquals(3, topThreeSouthernEuropeOutput.totalRecords(),
                "Expected 3 records for top 3 countries in Southern Europe");
    }

    @Test
    @DisplayName("Top N countries in a region: every row belongs to Southern Europe")
    void topCountriesByRegionAllMatchRegion() {
        // Confirm region filter applied across all returned rows
        assertTrue(allRowsHave(topThreeSouthernEuropeOutput, "Region", "Southern Europe"),
                "Every row should have Region = Southern Europe");
    }

    @Test
    @DisplayName("Top N countries in a region: lists top 3 Southern European countries in descending order")
    void topCountriesByRegionOrder() {
        // Verify exact descending population order for top 3 countries in Southern Europe
        assertEquals(List.of("Italy", "Spain", "Yugoslavia"),
                topThreeSouthernEuropeOutput.column("Name"),
                "Expected top 3 most populated countries in Southern Europe in descending order");
    }

    @Test
    @DisplayName("Top N countries in a region: an unknown region gives an empty report")
    void topCountriesByUnknownRegion() throws SQLException {
        // Request top 3 from an unknown region; expect 0 records
        assertEquals(0,
                ReportOutput.capture(new TopCountriesByRegionReport("Narnia", 3)).totalRecords(),
                "Expected 0 records for unknown region Narnia");
    }
}