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

    private static final List<String> HEADINGS = List.of(
            "Code", "Name", "Continent", "Region", "Population", "Capital");

    // helpers

    private static ReportOutput run(CountryReport report) throws SQLException {
        return ReportOutput.capture(report);
    }

    private static void assertAllRowsHave(ReportOutput output, String heading, String expected) {
        assertTrue(output.column(heading).stream().allMatch(expected::equals),
                "Every row should have " + heading + " = " + expected);
    }

    // requirement 1: all countries in the world

    @Test
    @DisplayName("All countries: every country is listed, largest population first")
    void allCountriesInWorld() throws SQLException {

        ReportOutput output = run(new AllCountriesInWorldReport());

        assertWellFormed(output, "All Countries in the World by Population", HEADINGS);
        assertEquals(239, output.totalRecords());
    }

    @Test
    @DisplayName("All countries: the first row shows every column correctly")
    void allCountriesFirstRow() throws SQLException {

        Map<String, String> first = run(new AllCountriesInWorldReport()).firstRow();

        assertAll(
                () -> assertEquals("CHN", first.get("Code")),
                () -> assertEquals("China", first.get("Name")),
                () -> assertEquals("Asia", first.get("Continent")),
                () -> assertEquals("Eastern Asia", first.get("Region")),
                () -> assertEquals("1,277,558,000", first.get("Population")),
                () -> assertEquals("Peking", first.get("Capital"))
        );
    }

    @Test
    @DisplayName("All countries: a country with no capital and no population shows 'No ... recorded'")
    void allCountriesShowsMissingValues() throws SQLException {

        Map<String, String> antarctica =
                run(new AllCountriesInWorldReport()).rowWhere("Code", "ATA");

        assertAll(
                () -> assertEquals("No capital recorded", antarctica.get("Capital")),
                () -> assertEquals("No population recorded", antarctica.get("Population"))
        );
    }

    // requirement 2: countries in a continent

    @Test
    @DisplayName("Countries by continent: only countries of that continent are listed")
    void countriesByContinent() throws SQLException {

        ReportOutput output = run(new CountriesByContinentReport("Europe"));

        assertWellFormed(output, "Countries in Europe by Population", HEADINGS);
        assertEquals(46, output.totalRecords());
        assertAllRowsHave(output, "Continent", "Europe");
        assertEquals("Russian Federation", output.firstRow().get("Name"));
        assertEquals("146,934,000", output.firstRow().get("Population"));
    }

    @Test
    @DisplayName("Countries by continent: spaces around the continent are ignored")
    void countriesByContinentTrimsInput() throws SQLException {

        ReportOutput output = run(new CountriesByContinentReport("  Europe "));

        assertEquals("Countries in Europe by Population", output.title());
        assertEquals(46, output.totalRecords());
    }

    @Test
    @DisplayName("Countries by continent: an unknown continent gives an empty report")
    void countriesByUnknownContinent() throws SQLException {

        ReportOutput output = run(new CountriesByContinentReport("Atlantis"));

        assertEquals(0, output.totalRecords());
        assertTrue(output.rows().isEmpty());
    }

    @Test
    @DisplayName("Countries by continent: SQL in the input is treated as plain text")
    void countriesByContinentIsNotOpenToSqlInjection() throws SQLException {

        ReportOutput output = run(new CountriesByContinentReport("Europe' OR '1'='1"));

        assertEquals(0, output.totalRecords());
    }

    // requirement 3: countries in a region

    @Test
    @DisplayName("Countries by region: only countries of that region are listed")
    void countriesByRegion() throws SQLException {

        ReportOutput output = run(new CountriesByRegionReport("Southern Europe"));

        assertWellFormed(output, "Countries in Southern Europe by Population", HEADINGS);
        assertEquals(15, output.totalRecords());
        assertAllRowsHave(output, "Region", "Southern Europe");
        assertEquals("Italy", output.firstRow().get("Name"));
    }

    @Test
    @DisplayName("Countries by region: an unknown region gives an empty report")
    void countriesByUnknownRegion() throws SQLException {

        assertEquals(0, run(new CountriesByRegionReport("Narnia")).totalRecords());
    }

    // requirement 4: top N countries in the world

    @Test
    @DisplayName("Top N countries worldwide: the N most populated countries are listed")
    void topCountriesWorldwide() throws SQLException {

        ReportOutput output = run(new TopCountriesWorldwideReport(5));

        assertWellFormed(output, "Top 5 Populated Countries Worldwide", HEADINGS);
        assertEquals(5, output.totalRecords());
        assertEquals(
                List.of("China", "India", "United States", "Indonesia", "Brazil"),
                output.column("Name"));
    }

    @Test
    @DisplayName("Top N countries worldwide: N = 1 gives only the largest country")
    void topCountriesWorldwideOne() throws SQLException {

        ReportOutput output = run(new TopCountriesWorldwideReport(1));

        assertEquals(1, output.totalRecords());
        assertEquals("China", output.firstRow().get("Name"));
    }

    @Test
    @DisplayName("Top N countries worldwide: N larger than the data gives every country")
    void topCountriesWorldwideMoreThanExist() throws SQLException {

        assertEquals(239, run(new TopCountriesWorldwideReport(500)).totalRecords());
    }

    // requirement 5: top N countries in a continent

    @Test
    @DisplayName("Top N countries in a continent: only that continent, N rows")
    void topCountriesByContinent() throws SQLException {

        ReportOutput output = run(new TopCountriesByContinentReport("Asia", 5));

        assertWellFormed(output, "Top 5 Populated Countries in Asia", HEADINGS);
        assertEquals(5, output.totalRecords());
        assertAllRowsHave(output, "Continent", "Asia");
        assertEquals(
                List.of("China", "India", "Indonesia", "Pakistan", "Bangladesh"),
                output.column("Name"));
    }

    @Test
    @DisplayName("Top N countries in a continent: N larger than the continent gives all of it")
    void topCountriesByContinentMoreThanExist() throws SQLException {

        assertEquals(46, run(new TopCountriesByContinentReport("Europe", 100)).totalRecords());
    }

    // requirement 6: top N countries in a region

    @Test
    @DisplayName("Top N countries in a region: only that region, N rows")
    void topCountriesByRegion() throws SQLException {

        ReportOutput output = run(new TopCountriesByRegionReport("Southern Europe", 3));

        assertWellFormed(output, "Top 3 Populated Countries in Southern Europe", HEADINGS);
        assertEquals(3, output.totalRecords());
        assertAllRowsHave(output, "Region", "Southern Europe");
        assertEquals(List.of("Italy", "Spain", "Yugoslavia"), output.column("Name"));
    }

    @Test
    @DisplayName("Top N countries in a region: an unknown region gives an empty report")
    void topCountriesByUnknownRegion() throws SQLException {

        assertEquals(0, run(new TopCountriesByRegionReport("Narnia", 3)).totalRecords());
    }
}
