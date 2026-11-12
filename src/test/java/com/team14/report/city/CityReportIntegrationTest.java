package com.team14.report.city;

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
 * Integration tests for the city reports (requirements 7 to 16).
 * <p>
 * Each test runs a report against the real MySQL "world" database (needs the
 * MySQL container) and checks what is printed. The expected numbers come from
 * database/world.sql: 4,079 cities, 841 in Europe, 156 in Southern Europe,
 * 81 in the United Kingdom and 71 in the district England.
 */
class CityReportIntegrationTest {

    private static final List<String> HEADINGS =
            List.of("Name", "Country", "District", "Population");

    // helpers

    private static ReportOutput run(CityReport report) throws SQLException {
        return ReportOutput.capture(report);
    }

    private static void assertAllRowsHave(ReportOutput output, String heading, String expected) {
        assertTrue(output.column(heading).stream().allMatch(expected::equals),
                "Every row should have " + heading + " = " + expected);
    }

    // all cities in the world

    @Test
    @DisplayName("All cities: every city is listed, largest population first")
    void allCitiesInWorld() throws SQLException {

        ReportOutput output = run(new AllCitiesInWorldReport());

        assertWellFormed(output, "All Cities in the World by Population", HEADINGS);
        assertEquals(4079, output.totalRecords());
    }

    @Test
    @DisplayName("All cities: the first row shows every column correctly")
    void allCitiesFirstRow() throws SQLException {

        Map<String, String> first = run(new AllCitiesInWorldReport()).firstRow();

        assertAll(
                () -> assertEquals("Mumbai (Bombay)", first.get("Name")),
                () -> assertEquals("India", first.get("Country")),
                () -> assertEquals("Maharashtra", first.get("District")),
                () -> assertEquals("10,500,000", first.get("Population"))
        );
    }

    @Test
    @DisplayName("All cities: a city with no district shows 'No district recorded'")
    void allCitiesShowsMissingDistrict() throws SQLException {

        Map<String, String> city =
                run(new AllCitiesInWorldReport()).rowWhere("Name", "Ciudad Losada");

        assertEquals("Venezuela", city.get("Country"));
        assertEquals("No district recorded", city.get("District"));
    }

    // cities in a continent

    @Test
    @DisplayName("Cities by continent: every city of that continent is listed")
    void citiesByContinent() throws SQLException {

        ReportOutput output = run(new CitiesByContinentReport("Europe"));

        assertWellFormed(output, "Cities in Europe by Population", HEADINGS);
        assertEquals(841, output.totalRecords());
        assertEquals("Moscow", output.firstRow().get("Name"));
        assertEquals("8,389,200", output.firstRow().get("Population"));
    }

    @Test
    @DisplayName("Cities by continent: an unknown continent gives an empty report")
    void citiesByUnknownContinent() throws SQLException {

        assertEquals(0, run(new CitiesByContinentReport("Atlantis")).totalRecords());
    }

    @Test
    @DisplayName("Cities by continent: SQL in the input is treated as plain text")
    void citiesByContinentIsNotOpenToSqlInjection() throws SQLException {

        assertEquals(0, run(new CitiesByContinentReport("Europe' OR '1'='1")).totalRecords());
    }

    // cities in a region

    @Test
    @DisplayName("Cities by region: every city of that region is listed")
    void citiesByRegion() throws SQLException {

        ReportOutput output = run(new CitiesByRegionReport("Southern Europe"));

        assertWellFormed(output, "Cities in Southern Europe by Population", HEADINGS);
        assertEquals(156, output.totalRecords());
        assertEquals("Madrid", output.firstRow().get("Name"));
        assertEquals("2,879,052", output.firstRow().get("Population"));
    }

    // cities in a country

    @Test
    @DisplayName("Cities by country: only cities of that country are listed")
    void citiesByCountry() throws SQLException {

        ReportOutput output = run(new CitiesByCountryReport("United Kingdom"));

        assertWellFormed(output, "Cities in United Kingdom by Population", HEADINGS);
        assertEquals(81, output.totalRecords());
        assertAllRowsHave(output, "Country", "United Kingdom");
        assertEquals("London", output.firstRow().get("Name"));
    }

    @Test
    @DisplayName("Cities by country: the country code works as well as the name")
    void citiesByCountryCode() throws SQLException {

        ReportOutput byCode = run(new CitiesByCountryReport("GBR"));
        ReportOutput byName = run(new CitiesByCountryReport("United Kingdom"));

        assertEquals(byName.totalRecords(), byCode.totalRecords());
        assertEquals(byName.populations(), byCode.populations());
    }

    @Test
    @DisplayName("Cities by country: an unknown country gives an empty report")
    void citiesByUnknownCountry() throws SQLException {

        assertEquals(0, run(new CitiesByCountryReport("Narnia")).totalRecords());
    }

    // cities in a district

    @Test
    @DisplayName("Cities by district: only cities of that district are listed")
    void citiesByDistrict() throws SQLException {

        ReportOutput output = run(new CitiesByDistrictReport("England"));

        assertWellFormed(output, "Cities in England District by Population", HEADINGS);
        assertEquals(71, output.totalRecords());
        assertAllRowsHave(output, "District", "England");
        assertAllRowsHave(output, "Country", "United Kingdom");
        assertEquals("London", output.firstRow().get("Name"));
    }

    @Test
    @DisplayName("Cities by district: spaces around the district are ignored")
    void citiesByDistrictTrimsInput() throws SQLException {

        assertEquals(71, run(new CitiesByDistrictReport("  England ")).totalRecords());
    }

    @Test
    @DisplayName("Cities by district: an unknown district gives an empty report")
    void citiesByUnknownDistrict() throws SQLException {

        assertEquals(0, run(new CitiesByDistrictReport("Mordor")).totalRecords());
    }

    // top N cities in the world

    @Test
    @DisplayName("Top N cities worldwide: the N most populated cities are listed")
    void topCitiesWorldwide() throws SQLException {

        ReportOutput output = run(new TopCitiesByWorldwideReport(5));

        assertWellFormed(output, "Top 5 Populated Cities Worldwide", HEADINGS);
        assertEquals(5, output.totalRecords());
        assertEquals(
                List.of(10_500_000L, 9_981_619L, 9_968_485L, 9_696_300L, 9_604_900L),
                output.populations());
        assertEquals("Mumbai (Bombay)", output.rows().get(0).get("Name"));
        assertEquals("Seoul", output.rows().get(1).get("Name"));
        assertEquals("Shanghai", output.rows().get(3).get("Name"));
        assertEquals("Jakarta", output.rows().get(4).get("Name"));
    }

    @Test
    @DisplayName("Top N cities worldwide: N = 1 gives only the largest city")
    void topCitiesWorldwideOne() throws SQLException {

        ReportOutput output = run(new TopCitiesByWorldwideReport(1));

        assertEquals(1, output.totalRecords());
        assertEquals("Mumbai (Bombay)", output.firstRow().get("Name"));
    }

    // top N cities in a continent

    @Test
    @DisplayName("Top N cities in a continent: the N largest cities of that continent")
    void topCitiesByContinent() throws SQLException {

        ReportOutput output = run(new TopCitiesByContinentReport("Europe", 3));

        assertWellFormed(output, "Top 3 Populated Cities in Europe", HEADINGS);
        assertEquals(List.of("Moscow", "London", "St Petersburg"), output.column("Name"));
    }

    // top N cities in a region

    @Test
    @DisplayName("Top N cities in a region: the N largest cities of that region")
    void topCitiesByRegion() throws SQLException {

        ReportOutput output = run(new TopCitiesByRegionReport("Southern Europe", 3));

        assertWellFormed(output, "Top 3 Populated Cities in Southern Europe", HEADINGS);
        assertEquals(List.of("Madrid", "Roma", "Barcelona"), output.column("Name"));
    }

    // top N cities in a country

    @Test
    @DisplayName("Top N cities in a country: the N largest cities of that country")
    void topCitiesByCountry() throws SQLException {

        ReportOutput output = run(new TopCitiesByCountryReport("United Kingdom", 5));

        assertWellFormed(output, "Top 5 Populated Cities in United Kingdom", HEADINGS);
        assertAllRowsHave(output, "Country", "United Kingdom");
        assertEquals(
                List.of("London", "Birmingham", "Glasgow", "Liverpool", "Edinburgh"),
                output.column("Name"));
    }

    // top N cities in a district

    @Test
    @DisplayName("Top N cities in a district: the N largest cities of that district")
    void topCitiesByDistrict() throws SQLException {

        ReportOutput output = run(new TopCitiesByDistrictReport("England", 3));

        assertWellFormed(output, "Top 3 Populated Cities in England", HEADINGS);
        assertAllRowsHave(output, "District", "England");
        assertEquals(List.of("London", "Birmingham", "Liverpool"), output.column("Name"));
    }

    @Test
    @DisplayName("Top N cities in a district: N larger than the district gives all of it")
    void topCitiesByDistrictMoreThanExist() throws SQLException {

        assertEquals(71, run(new TopCitiesByDistrictReport("England", 500)).totalRecords());
    }
}
