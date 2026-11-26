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
 * Unit tests for the nine population reports (requirements 23 to 31).
 * These tests do not require a database. ResultSet values are supplied by
 * a small proxy so the report row-reading methods can be tested directly.
 */
class PopulationReportTest {

    private static ResultSet fakeResultSet(Map<String, Object> values) {
        return (ResultSet) Proxy.newProxyInstance(
                ResultSet.class.getClassLoader(),
                new Class<?>[]{ResultSet.class},
                (proxy, method, args) -> {
                    if ("getString".equals(method.getName())) {
                        return (String) values.get((String) args[0]);
                    }
                    if ("getLong".equals(method.getName())) {
                        return (Long) values.get((String) args[0]);
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
    }

    private static UserInput input(String value) {
        return new UserInput(new Scanner(value + "\n"));
    }

    /** Calls the protected parameters() method, including its inherited default. */
    private static Object[] parametersOf(SqlReport report) {
        try {
            var method = SqlReport.class.getDeclaredMethod("parameters");
            method.setAccessible(true);
            return (Object[]) method.invoke(report);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Could not read report parameters", e);
        }
    }

    private static String oneLine(String sql) {
        return sql.trim().replaceAll("\\s+", " ");
    }

    @Nested
    @DisplayName("Total population reports - SQL, titles and parameters")
    class TotalPopulationSqlTests {

        @Test
        @DisplayName("World population report")
        void worldReport() {
            WorldPopulationReport report = new WorldPopulationReport();
            assertAll(
                    () -> assertEquals("World Population", report.title()),
                    () -> assertTrue(oneLine(report.select()).contains("'World' AS Name")),
                    () -> assertTrue(oneLine(report.select()).contains("FROM country")),
                    () -> assertEquals("", report.clauses()),
                    () -> assertArrayEquals(new Object[0], parametersOf(report))
            );
        }

        @Test
        @DisplayName("Continent population report")
        void continentReport() {
            ContinentPopulationReport report = new ContinentPopulationReport("Asia");
            assertAll(
                    () -> assertEquals("Population of Asia", report.title()),
                    () -> assertTrue(oneLine(report.select()).contains("c.Continent AS Name")),
                    () -> assertTrue(oneLine(report.select()).contains("SUM(c.Population) AS Population")),
                    () -> assertEquals("WHERE c.Continent = ? GROUP BY c.Continent",
                            oneLine(report.clauses())),
                    () -> assertArrayEquals(new Object[]{"Asia"}, report.parameters())
            );
        }

        @Test
        @DisplayName("Region population report")
        void regionReport() {
            RegionPopulationReport report = new RegionPopulationReport("Caribbean");
            assertAll(
                    () -> assertEquals("Population of Caribbean", report.title()),
                    () -> assertTrue(oneLine(report.select()).contains("c.Region AS Name")),
                    () -> assertTrue(oneLine(report.select()).contains("SUM(c.Population) AS Population")),
                    () -> assertEquals("WHERE c.Region = ? GROUP BY c.Region",
                            oneLine(report.clauses())),
                    () -> assertArrayEquals(new Object[]{"Caribbean"}, report.parameters())
            );
        }

        @Test
        @DisplayName("Country population report")
        void countryReport() {
            CountryPopulationReport report = new CountryPopulationReport("France");
            assertAll(
                    () -> assertEquals("Population of France", report.title()),
                    () -> assertTrue(oneLine(report.select()).contains("c.Name AS Name")),
                    () -> assertTrue(oneLine(report.select()).contains("c.Population AS Population")),
                    () -> assertEquals("WHERE c.Name = ?", oneLine(report.clauses())),
                    () -> assertArrayEquals(new Object[]{"France"}, report.parameters())
            );
        }

        @Test
        @DisplayName("District population report")
        void districtReport() {
            DistrictPopulationReport report = new DistrictPopulationReport("England");
            assertAll(
                    () -> assertEquals("Population of England District", report.title()),
                    () -> assertTrue(oneLine(report.select()).contains("ci.District AS Name")),
                    () -> assertTrue(oneLine(report.select()).contains("SUM(ci.Population) AS Population")),
                    () -> assertTrue(oneLine(report.select()).contains("FROM city ci")),
                    () -> assertEquals("WHERE ci.District = ? GROUP BY ci.District",
                            oneLine(report.clauses())),
                    () -> assertArrayEquals(new Object[]{"England"}, report.parameters())
            );
        }

        @Test
        @DisplayName("City population report")
        void cityReport() {
            CityPopulationReport report = new CityPopulationReport("Edinburgh");
            assertAll(
                    () -> assertEquals("Population of Edinburgh", report.title()),
                    () -> assertTrue(oneLine(report.select()).contains("ci.Name AS Name")),
                    () -> assertTrue(oneLine(report.select()).contains("ci.Population AS Population")),
                    () -> assertTrue(oneLine(report.select()).contains("FROM city ci")),
                    () -> assertEquals("WHERE ci.Name = ?", oneLine(report.clauses())),
                    () -> assertArrayEquals(new Object[]{"Edinburgh"}, report.parameters())
            );
        }

        @Test
        @DisplayName("Default clauses and columns for total population reports")
        void sharedTotalPopulationBehaviour() {
            TotalPopulationReport report = new WorldPopulationReport();
            List<String> headings = report.columns().stream()
                    .map(column -> column.heading())
                    .toList();

            assertAll(
                    () -> assertEquals("", report.clauses()),
                    () -> assertEquals(List.of("Name", "Population"), headings),
                    () -> assertEquals(0, parametersOf(report).length)
            );
        }

        @Test
        @DisplayName("Total population row reads Name and Population")
        void totalPopulationReadRow() throws SQLException {
            ResultSet rs = fakeResultSet(Map.of("Name", "France", "Population", 68_000_000L));
            List<?> row = new WorldPopulationReport().readRow(rs);
            assertEquals(List.of("France", 68_000_000L), row);
        }
    }

    @Nested
    @DisplayName("UserInput constructors")
    class UserInputConstructorTests {

        @Test
        @DisplayName("All five location reports accept values from UserInput")
        void allUserInputConstructors() {
            ContinentPopulationReport continent = new ContinentPopulationReport(input("Asia"));
            RegionPopulationReport region = new RegionPopulationReport(input("Caribbean"));
            CountryPopulationReport country = new CountryPopulationReport(input("France"));
            DistrictPopulationReport district = new DistrictPopulationReport(input("England"));
            CityPopulationReport city = new CityPopulationReport(input("Edinburgh"));

            assertAll(
                    () -> assertEquals("Population of Asia", continent.title()),
                    () -> assertEquals("Population of Caribbean", region.title()),
                    () -> assertEquals("Population of France", country.title()),
                    () -> assertEquals("Population of England District", district.title()),
                    () -> assertEquals("Population of Edinburgh", city.title())
            );
        }

        @Test
        @DisplayName("All five reports reject null UserInput")
        void allNullUserInputsAreRejected() {
            assertAll(
                    () -> assertThrows(NullPointerException.class,
                            () -> new ContinentPopulationReport((UserInput) null)),
                    () -> assertThrows(NullPointerException.class,
                            () -> new RegionPopulationReport((UserInput) null)),
                    () -> assertThrows(NullPointerException.class,
                            () -> new CountryPopulationReport((UserInput) null)),
                    () -> assertThrows(NullPointerException.class,
                            () -> new DistrictPopulationReport((UserInput) null)),
                    () -> assertThrows(NullPointerException.class,
                            () -> new CityPopulationReport((UserInput) null))
            );
        }
    }

    @Nested
    @DisplayName("Text validation")
    class TextValidationTests {

        @Test
        @DisplayName("All five report parameters are trimmed")
        void allValuesAreTrimmed() {
            ContinentPopulationReport continent = new ContinentPopulationReport("  Europe  ");
            RegionPopulationReport region = new RegionPopulationReport("  Caribbean  ");
            CountryPopulationReport country = new CountryPopulationReport("  France  ");
            DistrictPopulationReport district = new DistrictPopulationReport("  England  ");
            CityPopulationReport city = new CityPopulationReport("  Edinburgh  ");

            assertAll(
                    () -> assertEquals("Population of Europe", continent.title()),
                    () -> assertEquals("Population of Caribbean", region.title()),
                    () -> assertEquals("Population of France", country.title()),
                    () -> assertEquals("Population of England District", district.title()),
                    () -> assertEquals("Population of Edinburgh", city.title()),
                    () -> assertArrayEquals(new Object[]{"Europe"}, continent.parameters()),
                    () -> assertArrayEquals(new Object[]{"Caribbean"}, region.parameters()),
                    () -> assertArrayEquals(new Object[]{"France"}, country.parameters()),
                    () -> assertArrayEquals(new Object[]{"England"}, district.parameters()),
                    () -> assertArrayEquals(new Object[]{"Edinburgh"}, city.parameters())
            );
        }

        @Test
        @DisplayName("All five reports reject null or blank text")
        void invalidTextIsRejected() {
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
    }

    @Nested
    @DisplayName("Population distribution report SQL and columns")
    class DistributionSqlTests {

        @Test
        @DisplayName("Continent distribution report uses the continent grouping")
        void continentDistribution() {
            PopulationDistributionByContinentReport report =
                    new PopulationDistributionByContinentReport();
            assertAll(
                    () -> assertEquals("Population Distribution by Continent", report.title()),
                    () -> assertTrue(oneLine(report.select()).contains("c.Continent AS Name")),
                    () -> assertTrue(oneLine(report.select()).contains("LEFT JOIN")),
                    () -> assertTrue(oneLine(report.select()).contains("GROUP BY CountryCode")),
                    () -> assertEquals("GROUP BY c.Continent ORDER BY Name", oneLine(report.clauses())),
                    () -> assertEquals(0, parametersOf(report).length)
            );
        }

        @Test
        @DisplayName("Region distribution report uses the region grouping")
        void regionDistribution() {
            PopulationDistributionByRegionReport report =
                    new PopulationDistributionByRegionReport();
            assertAll(
                    () -> assertEquals("Population Distribution by Region", report.title()),
                    () -> assertTrue(oneLine(report.select()).contains("c.Region AS Name")),
                    () -> assertEquals("GROUP BY c.Region ORDER BY Name", oneLine(report.clauses())),
                    () -> assertEquals(0, parametersOf(report).length)
            );
        }

        @Test
        @DisplayName("Country distribution report uses the country grouping")
        void countryDistribution() {
            PopulationDistributionByCountryReport report =
                    new PopulationDistributionByCountryReport();
            assertAll(
                    () -> assertEquals("Population Distribution by Country", report.title()),
                    () -> assertTrue(oneLine(report.select()).contains("c.Name AS Name")),
                    () -> assertEquals("GROUP BY c.Name ORDER BY Name", oneLine(report.clauses())),
                    () -> assertEquals(0, parametersOf(report).length)
            );
        }

        @Test
        @DisplayName("Distribution reports expose all four required columns")
        void distributionColumns() {
            List<String> headings = new PopulationDistributionByContinentReport()
                    .columns().stream()
                    .map(column -> column.heading())
                    .toList();

            assertEquals(List.of("Name", "Total Population", "Population in Cities",
                    "Population Not in Cities"), headings);
        }
    }

    @Nested
    @DisplayName("Population distribution row and percentage formatting")
    class DistributionRowTests {

        private List<?> readRow(long total, long inCities) throws SQLException {
            ResultSet rs = fakeResultSet(Map.of(
                    "Name", "Test",
                    "TotalPopulation", total,
                    "CityPopulation", inCities));
            return new PopulationDistributionByCountryReport().readRow(rs);
        }

        @Test
        @DisplayName("Normal row calculates both population parts and percentages")
        void normalRow() throws SQLException {
            assertEquals(List.of("Test", 1000L, "250 (25.00%)", "750 (75.00%)"),
                    readRow(1000, 250));
        }

        @Test
        @DisplayName("Zero total avoids division by zero")
        void zeroTotal() throws SQLException {
            assertEquals(List.of("Test", 0L, "0 (0.00%)", "0 (0.00%)"), readRow(0, 0));
        }

        @Test
        @DisplayName("Everyone living in cities")
        void everyoneInCities() throws SQLException {
            List<?> row = readRow(500, 500);
            assertEquals("500 (100.00%)", row.get(2));
            assertEquals("0 (0.00%)", row.get(3));
        }

        @Test
        @DisplayName("Nobody living in cities")
        void nobodyInCities() throws SQLException {
            List<?> row = readRow(500, 0);
            assertEquals("0 (0.00%)", row.get(2));
            assertEquals("500 (100.00%)", row.get(3));
        }

        @Test
        @DisplayName("Large population values use thousands separators")
        void thousandsSeparators() throws SQLException {
            List<?> row = readRow(1_000_000, 250_000);
            assertEquals("250,000 (25.00%)", row.get(2));
            assertEquals("750,000 (75.00%)", row.get(3));
        }

        @Test
        @DisplayName("Percentages are rounded to two decimal places")
        void percentageRounding() throws SQLException {
            List<?> row = readRow(3, 1);
            assertEquals("1 (33.33%)", row.get(2));
            assertEquals("2 (66.67%)", row.get(3));
        }

        @Test
        @DisplayName("City and non-city populations add up to the total")
        void populationPartsAddUp() throws SQLException {
            List<?> row = readRow(7_654_321, 2_345_678);
            assertEquals("2,345,678 (30.65%)", row.get(2));
            assertEquals("5,308,643 (69.35%)", row.get(3));
        }
    }
}
