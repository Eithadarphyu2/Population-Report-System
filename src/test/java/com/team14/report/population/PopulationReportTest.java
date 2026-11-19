package com.team14.report.population;

import com.team14.input.UserInput;
import com.team14.report.SqlReport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the 9 population reports (requirements 23 to 31).
 * <p>
 * They check title(), clauses() and parameters() of every report, and the
 * percentage text of the three distribution reports. They need no database:
 * the row reading is tested with a fake ResultSet.
 */
class PopulationReportTest {

    // helpers

    /**
     * Creates a fake ResultSet that returns the given values by column name.
     */
    private static ResultSet fakeResultSet(Map<String, Object> values) {
        return (ResultSet) Proxy.newProxyInstance(
                ResultSet.class.getClassLoader(),
                new Class<?>[]{ResultSet.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getString")) {
                        return (String) values.get((String) args[0]);
                    }
                    if (method.getName().equals("getLong")) {
                        return (Long) values.get((String) args[0]);
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
    }

    /**
     * Reads one distribution row for a country-like record with the given
     * total population and city population.
     */
    private static List<?> readDistributionRow(long total, long inCities) throws SQLException {
        ResultSet rs = fakeResultSet(Map.of(
                "Name", "Test",
                "TotalPopulation", total,
                "CityPopulation", inCities));

        return new PopulationDistributionByCountryReport().readRow(rs);
    }

    /**
     * Reads the parameters of a report. SqlReport.parameters() is protected
     * and some reports do not override it, so it is not reachable from this
     * package without reflection.
     */
    private static Object[] parametersOf(SqlReport report) {
        try {
            var method = SqlReport.class.getDeclaredMethod("parameters");
            method.setAccessible(true);
            return (Object[]) method.invoke(report);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Could not read the report parameters", e);
        }
    }

    /**
     * Normalises the white space of a SQL text so a test does not depend on
     * line breaks or indentation.
     */
    private static String oneLine(String sql) {
        return sql.trim().replaceAll("\\s+", " ");
    }

    // total population reports (requirements 26 to 31)

    @Nested
    @DisplayName("Total population reports")
    class TotalPopulationReports {

        @Test
        @DisplayName("World: title, no WHERE clause and no parameters")
        void world() {
            WorldPopulationReport report = new WorldPopulationReport();

            assertAll(
                    () -> assertEquals("World Population", report.title()),
                    () -> assertEquals("", report.clauses()),
                    () -> assertEquals(0, parametersOf(report).length),
                    () -> assertTrue(oneLine(report.select()).contains("FROM country"))
            );
        }

        @Test
        @DisplayName("Continent: title, WHERE c.Continent = ? and the continent as parameter")
        void continent() {
            ContinentPopulationReport report = new ContinentPopulationReport("Asia");

            assertAll(
                    () -> assertEquals("Population of Asia", report.title()),
                    () -> assertEquals("WHERE c.Continent = ? GROUP BY c.Continent",
                            oneLine(report.clauses())),
                    () -> assertArrayEquals(new Object[]{"Asia"}, report.parameters())
            );
        }

        @Test
        @DisplayName("Region: title, WHERE c.Region = ? and the region as parameter")
        void region() {
            RegionPopulationReport report = new RegionPopulationReport("Caribbean");

            assertAll(
                    () -> assertEquals("Population of Caribbean", report.title()),
                    () -> assertEquals("WHERE c.Region = ? GROUP BY c.Region",
                            oneLine(report.clauses())),
                    () -> assertArrayEquals(new Object[]{"Caribbean"}, report.parameters())
            );
        }

        @Test
        @DisplayName("Country: title, WHERE c.Name = ? and the country as parameter")
        void country() {
            CountryPopulationReport report = new CountryPopulationReport("France");

            assertAll(
                    () -> assertEquals("Population of France", report.title()),
                    () -> assertEquals("WHERE c.Name = ?", oneLine(report.clauses())),
                    () -> assertArrayEquals(new Object[]{"France"}, report.parameters())
            );
        }

        @Test
        @DisplayName("District: title, WHERE ci.District = ? and the district as parameter")
        void district() {
            DistrictPopulationReport report = new DistrictPopulationReport("England");

            assertAll(
                    () -> assertEquals("Population of England District", report.title()),
                    () -> assertEquals("WHERE ci.District = ? GROUP BY ci.District",
                            oneLine(report.clauses())),
                    () -> assertArrayEquals(new Object[]{"England"}, report.parameters())
            );
        }

        @Test
        @DisplayName("City: title, WHERE ci.Name = ? and the city as parameter")
        void city() {
            CityPopulationReport report = new CityPopulationReport("Edinburgh");

            assertAll(
                    () -> assertEquals("Population of Edinburgh", report.title()),
                    () -> assertEquals("WHERE ci.Name = ?", oneLine(report.clauses())),
                    () -> assertArrayEquals(new Object[]{"Edinburgh"}, report.parameters())
            );
        }

        @Test
        @DisplayName("The value is trimmed before it is used")
        void valueIsTrimmed() {
            ContinentPopulationReport report = new ContinentPopulationReport("  Europe  ");

            assertEquals("Population of Europe", report.title());
            assertArrayEquals(new Object[]{"Europe"}, report.parameters());
        }

        @Test
        @DisplayName("A blank or null value is rejected")
        void blankValueIsRejected() {
            assertAll(
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> new ContinentPopulationReport(" ")),
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> new RegionPopulationReport("")),
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> new CountryPopulationReport((String) null)),
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> new DistrictPopulationReport("\t")),
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> new CityPopulationReport(""))
            );
        }

        @Test
        @DisplayName("A null UserInput is rejected")
        void nullUserInputIsRejected() {
            assertThrows(NullPointerException.class,
                    () -> new CityPopulationReport((UserInput) null));
        }

        @Test
        @DisplayName("The value can be read from UserInput")
        void valueFromUserInput() {
            UserInput input = new UserInput(new Scanner("Africa\n"));

            ContinentPopulationReport report = new ContinentPopulationReport(input);

            assertEquals("Population of Africa", report.title());
        }
    }

    // population distribution reports (requirements 23 to 25)

    @Nested
    @DisplayName("Population distribution reports")
    class DistributionReports {

        @Test
        @DisplayName("Continent: title, GROUP BY c.Continent and no parameters")
        void continent() {
            PopulationDistributionByContinentReport report =
                    new PopulationDistributionByContinentReport();

            assertAll(
                    () -> assertEquals("Population Distribution by Continent", report.title()),
                    () -> assertEquals("GROUP BY c.Continent ORDER BY Name",
                            oneLine(report.clauses())),
                    () -> assertEquals(0, parametersOf(report).length)
            );
        }

        @Test
        @DisplayName("Region: title, GROUP BY c.Region and no parameters")
        void region() {
            PopulationDistributionByRegionReport report =
                    new PopulationDistributionByRegionReport();

            assertAll(
                    () -> assertEquals("Population Distribution by Region", report.title()),
                    () -> assertEquals("GROUP BY c.Region ORDER BY Name",
                            oneLine(report.clauses())),
                    () -> assertEquals(0, parametersOf(report).length)
            );
        }

        @Test
        @DisplayName("Country: title, GROUP BY c.Name and no parameters")
        void country() {
            PopulationDistributionByCountryReport report =
                    new PopulationDistributionByCountryReport();

            assertAll(
                    () -> assertEquals("Population Distribution by Country", report.title()),
                    () -> assertEquals("GROUP BY c.Name ORDER BY Name",
                            oneLine(report.clauses())),
                    () -> assertEquals(0, parametersOf(report).length)
            );
        }

        @Test
        @DisplayName("The SELECT uses the group column as Name")
        void selectUsesGroupColumn() {
            assertAll(
                    () -> assertTrue(new PopulationDistributionByContinentReport()
                            .select().contains("c.Continent AS Name")),
                    () -> assertTrue(new PopulationDistributionByRegionReport()
                            .select().contains("c.Region AS Name")),
                    () -> assertTrue(new PopulationDistributionByCountryReport()
                            .select().contains("c.Name AS Name"))
            );
        }

        @Test
        @DisplayName("The three reports have the 4 required columns")
        void columns() {
            List<String> headings = new PopulationDistributionByContinentReport()
                    .columns().stream()
                    .map(column -> column.heading())
                    .toList();

            assertEquals(List.of("Name", "Total Population",
                    "Population in Cities", "Population Not in Cities"), headings);
        }
    }

    // percentage text of the distribution reports

    @Nested
    @DisplayName("Distribution percentage text")
    class PercentageText {

        @Test
        @DisplayName("A normal row shows the number and the percentage of both parts")
        void normalRow() throws SQLException {
            List<?> row = readDistributionRow(1000, 250);

            assertEquals(List.of("Test", 1000L, "250 (25.00%)", "750 (75.00%)"), row);
        }

        @Test
        @DisplayName("Zero total population shows 0 (0.00%) and does not divide by zero")
        void zeroTotal() throws SQLException {
            List<?> row = readDistributionRow(0, 0);

            assertEquals(List.of("Test", 0L, "0 (0.00%)", "0 (0.00%)"), row);
        }

        @Test
        @DisplayName("Everyone living in cities shows 100.00% and 0.00%")
        void everyoneInCities() throws SQLException {
            List<?> row = readDistributionRow(500, 500);

            assertEquals("500 (100.00%)", row.get(2));
            assertEquals("0 (0.00%)", row.get(3));
        }

        @Test
        @DisplayName("Nobody living in cities shows 0.00% and 100.00%")
        void nobodyInCities() throws SQLException {
            List<?> row = readDistributionRow(500, 0);

            assertEquals("0 (0.00%)", row.get(2));
            assertEquals("500 (100.00%)", row.get(3));
        }

        @Test
        @DisplayName("Large numbers use thousands separators")
        void thousandsSeparators() throws SQLException {
            List<?> row = readDistributionRow(1_000_000, 250_000);

            assertEquals("250,000 (25.00%)", row.get(2));
            assertEquals("750,000 (75.00%)", row.get(3));
        }

        @Test
        @DisplayName("Percentages are rounded to 2 decimal places")
        void rounding() throws SQLException {
            List<?> row = readDistributionRow(3, 1);

            assertEquals("1 (33.33%)", row.get(2));
            assertEquals("2 (66.67%)", row.get(3));
        }

        @Test
        @DisplayName("The two numbers always add up to the total")
        void partsAddUpToTotal() throws SQLException {
            long total = 7_654_321;
            long inCities = 2_345_678;

            List<?> row = readDistributionRow(total, inCities);

            assertEquals("2,345,678 (30.65%)", row.get(2));
            assertEquals("5,308,643 (69.35%)", row.get(3));
        }
    }
}