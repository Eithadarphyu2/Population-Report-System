package com.team14.report.population;

import com.team14.input.UserInput;
import com.team14.report.ReportDisplay.Column;
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
                        return values.get((String) args[0]);
                    }
                    if ("getLong".equals(method.getName())) {
                        return values.get((String) args[0]);
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
                    "World population report definition",
                    () -> assertEquals("World Population", report.title(),
                            "World population report title should match"),
                    () -> assertTrue(oneLine(report.select()).contains("'World' AS Name"),
                            "World SQL should select 'World' AS Name"),
                    () -> assertTrue(oneLine(report.select()).contains("FROM country"),
                            "World SQL should select from the country table"),
                    () -> assertEquals("", report.clauses(),
                            "World population report should have empty clauses"),
                    () -> assertArrayEquals(new Object[0], parametersOf(report),
                            "World population report should have no parameters")
            );
        }

        @Test
        @DisplayName("Continent population report")
        void continentReport() {
            ContinentPopulationReport report = new ContinentPopulationReport("Asia");
            assertAll(
                    "Continent population report definition",
                    () -> assertEquals("Population of Asia", report.title(),
                            "Continent population report title should match"),
                    () -> assertTrue(oneLine(report.select()).contains("c.Continent AS Name"),
                            "Continent SQL should select c.Continent AS Name"),
                    () -> assertTrue(oneLine(report.select()).contains("SUM(c.Population) AS Population"),
                            "Continent SQL should sum country population"),
                    () -> assertEquals("WHERE c.Continent = ? GROUP BY c.Continent",
                            oneLine(report.clauses()),
                            "Continent report clauses should filter and group by continent"),
                    () -> assertArrayEquals(new Object[]{"Asia"}, report.parameters(),
                            "Continent report parameter should be Asia")
            );
        }

        @Test
        @DisplayName("Region population report")
        void regionReport() {
            RegionPopulationReport report = new RegionPopulationReport("Caribbean");
            assertAll(
                    "Region population report definition",
                    () -> assertEquals("Population of Caribbean", report.title(),
                            "Region population report title should match"),
                    () -> assertTrue(oneLine(report.select()).contains("c.Region AS Name"),
                            "Region SQL should select c.Region AS Name"),
                    () -> assertTrue(oneLine(report.select()).contains("SUM(c.Population) AS Population"),
                            "Region SQL should sum country population"),
                    () -> assertEquals("WHERE c.Region = ? GROUP BY c.Region",
                            oneLine(report.clauses()),
                            "Region report clauses should filter and group by region"),
                    () -> assertArrayEquals(new Object[]{"Caribbean"}, report.parameters(),
                            "Region report parameter should be Caribbean")
            );
        }

        @Test
        @DisplayName("Country population report")
        void countryReport() {
            CountryPopulationReport report = new CountryPopulationReport("France");
            assertAll(
                    "Country population report definition",
                    () -> assertEquals("Population of France", report.title(),
                            "Country population report title should match"),
                    () -> assertTrue(oneLine(report.select()).contains("c.Name AS Name"),
                            "Country SQL should select c.Name AS Name"),
                    () -> assertTrue(oneLine(report.select()).contains("c.Population AS Population"),
                            "Country SQL should select c.Population AS Population"),
                    () -> assertEquals("WHERE c.Name = ?", oneLine(report.clauses()),
                            "Country report clauses should filter by country name"),
                    () -> assertArrayEquals(new Object[]{"France"}, report.parameters(),
                            "Country report parameter should be France")
            );
        }

        @Test
        @DisplayName("District population report")
        void districtReport() {
            DistrictPopulationReport report = new DistrictPopulationReport("England");
            assertAll(
                    "District population report definition",
                    () -> assertEquals("Population of England District", report.title(),
                            "District population report title should match"),
                    () -> assertTrue(oneLine(report.select()).contains("ci.District AS Name"),
                            "District SQL should select ci.District AS Name"),
                    () -> assertTrue(oneLine(report.select()).contains("SUM(ci.Population) AS Population"),
                            "District SQL should sum city population"),
                    () -> assertTrue(oneLine(report.select()).contains("FROM city ci"),
                            "District SQL should select from the city table"),
                    () -> assertEquals("WHERE ci.District = ? GROUP BY ci.District",
                            oneLine(report.clauses()),
                            "District report clauses should filter and group by district"),
                    () -> assertArrayEquals(new Object[]{"England"}, report.parameters(),
                            "District report parameter should be England")
            );
        }

        @Test
        @DisplayName("City population report")
        void cityReport() {
            CityPopulationReport report = new CityPopulationReport("Edinburgh");
            assertAll(
                    "City population report definition",
                    () -> assertEquals("Population of Edinburgh", report.title(),
                            "City population report title should match"),
                    () -> assertTrue(oneLine(report.select()).contains("ci.Name AS Name"),
                            "City SQL should select ci.Name AS Name"),
                    () -> assertTrue(oneLine(report.select()).contains("ci.Population AS Population"),
                            "City SQL should select ci.Population AS Population"),
                    () -> assertTrue(oneLine(report.select()).contains("FROM city ci"),
                            "City SQL should select from the city table"),
                    () -> assertEquals("WHERE ci.Name = ?", oneLine(report.clauses()),
                            "City report clauses should filter by city name"),
                    () -> assertArrayEquals(new Object[]{"Edinburgh"}, report.parameters(),
                            "City report parameter should be Edinburgh")
            );
        }

        @Test
        @DisplayName("Default clauses and columns for total population reports")
        void sharedTotalPopulationBehaviour() {
            TotalPopulationReport report = new WorldPopulationReport();
            List<String> headings = report.columns().stream()
                    .map(Column::heading)
                    .toList();

            assertAll(
                    "Shared total population report behaviour",
                    () -> assertEquals("", report.clauses(),
                            "Default clauses should be empty"),
                    () -> assertEquals(List.of("Name", "Population"), headings,
                            "Total population report should have Name and Population columns"),
                    () -> assertEquals(0, parametersOf(report).length,
                            "Default total population report should have zero parameters")
            );
        }

        @Test
        @DisplayName("Total population row reads Name and Population")
        void totalPopulationReadRow() throws SQLException {
            ResultSet rs = fakeResultSet(Map.of("Name", "France", "Population", 68_000_000L));
            List<?> row = new WorldPopulationReport().readRow(rs);
            assertEquals(List.of("France", 68_000_000L), row,
                    "Total population row should read Name and Population values");
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
                    "All five location reports should read titles from UserInput",
                    () -> assertEquals("Population of Asia", continent.title(),
                            "Continent title should reflect UserInput value"),
                    () -> assertEquals("Population of Caribbean", region.title(),
                            "Region title should reflect UserInput value"),
                    () -> assertEquals("Population of France", country.title(),
                            "Country title should reflect UserInput value"),
                    () -> assertEquals("Population of England District", district.title(),
                            "District title should reflect UserInput value"),
                    () -> assertEquals("Population of Edinburgh", city.title(),
                            "City title should reflect UserInput value")
            );
        }

        @Test
        @DisplayName("All five reports reject null UserInput")
        void allNullUserInputsAreRejected() {
            assertAll(
                    "All five location reports should reject null UserInput",
                    () -> assertThrows(NullPointerException.class,
                            () -> new ContinentPopulationReport((UserInput) null),
                            "ContinentPopulationReport should reject null UserInput"),
                    () -> assertThrows(NullPointerException.class,
                            () -> new RegionPopulationReport((UserInput) null),
                            "RegionPopulationReport should reject null UserInput"),
                    () -> assertThrows(NullPointerException.class,
                            () -> new CountryPopulationReport((UserInput) null),
                            "CountryPopulationReport should reject null UserInput"),
                    () -> assertThrows(NullPointerException.class,
                            () -> new DistrictPopulationReport((UserInput) null),
                            "DistrictPopulationReport should reject null UserInput"),
                    () -> assertThrows(NullPointerException.class,
                            () -> new CityPopulationReport((UserInput) null),
                            "CityPopulationReport should reject null UserInput")
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
                    "All five report titles and parameters should trim surrounding whitespace",
                    () -> assertEquals("Population of Europe", continent.title(),
                            "Continent title should use trimmed value"),
                    () -> assertEquals("Population of Caribbean", region.title(),
                            "Region title should use trimmed value"),
                    () -> assertEquals("Population of France", country.title(),
                            "Country title should use trimmed value"),
                    () -> assertEquals("Population of England District", district.title(),
                            "District title should use trimmed value"),
                    () -> assertEquals("Population of Edinburgh", city.title(),
                            "City title should use trimmed value"),
                    () -> assertArrayEquals(new Object[]{"Europe"}, continent.parameters(),
                            "Continent parameter should be trimmed"),
                    () -> assertArrayEquals(new Object[]{"Caribbean"}, region.parameters(),
                            "Region parameter should be trimmed"),
                    () -> assertArrayEquals(new Object[]{"France"}, country.parameters(),
                            "Country parameter should be trimmed"),
                    () -> assertArrayEquals(new Object[]{"England"}, district.parameters(),
                            "District parameter should be trimmed"),
                    () -> assertArrayEquals(new Object[]{"Edinburgh"}, city.parameters(),
                            "City parameter should be trimmed")
            );
        }

        @Test
        @DisplayName("All five reports reject null or blank text")
        void invalidTextIsRejected() {
            assertAll(
                    "All five reports should reject null or blank text",
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> new ContinentPopulationReport(" "),
                            "ContinentPopulationReport should reject blank text"),
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> new RegionPopulationReport(""),
                            "RegionPopulationReport should reject empty text"),
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> new CountryPopulationReport((String) null),
                            "CountryPopulationReport should reject null text"),
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> new DistrictPopulationReport("\t"),
                            "DistrictPopulationReport should reject whitespace-only text"),
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> new CityPopulationReport(""),
                            "CityPopulationReport should reject empty text")
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
                    "Continent distribution report definition",
                    () -> assertEquals("Population Distribution by Continent", report.title(),
                            "Continent distribution title should match"),
                    () -> assertTrue(oneLine(report.select()).contains("c.Continent AS Name"),
                            "Continent distribution SQL should select c.Continent AS Name"),
                    () -> assertTrue(oneLine(report.select()).contains("LEFT JOIN"),
                            "Continent distribution SQL should use LEFT JOIN"),
                    () -> assertTrue(oneLine(report.select()).contains("GROUP BY CountryCode"),
                            "Continent distribution SQL should group city totals by CountryCode"),
                    () -> assertEquals("GROUP BY c.Continent ORDER BY Name", oneLine(report.clauses()),
                            "Continent distribution clauses should group by continent and order by name"),
                    () -> assertEquals(0, parametersOf(report).length,
                            "Continent distribution report should have no parameters")
            );
        }

        @Test
        @DisplayName("Region distribution report uses the region grouping")
        void regionDistribution() {
            PopulationDistributionByRegionReport report =
                    new PopulationDistributionByRegionReport();
            assertAll(
                    "Region distribution report definition",
                    () -> assertEquals("Population Distribution by Region", report.title(),
                            "Region distribution title should match"),
                    () -> assertTrue(oneLine(report.select()).contains("c.Region AS Name"),
                            "Region distribution SQL should select c.Region AS Name"),
                    () -> assertEquals("GROUP BY c.Region ORDER BY Name", oneLine(report.clauses()),
                            "Region distribution clauses should group by region and order by name"),
                    () -> assertEquals(0, parametersOf(report).length,
                            "Region distribution report should have no parameters")
            );
        }

        @Test
        @DisplayName("Country distribution report uses the country grouping")
        void countryDistribution() {
            PopulationDistributionByCountryReport report =
                    new PopulationDistributionByCountryReport();
            assertAll(
                    "Country distribution report definition",
                    () -> assertEquals("Population Distribution by Country", report.title(),
                            "Country distribution title should match"),
                    () -> assertTrue(oneLine(report.select()).contains("c.Name AS Name"),
                            "Country distribution SQL should select c.Name AS Name"),
                    () -> assertEquals("GROUP BY c.Name ORDER BY Name", oneLine(report.clauses()),
                            "Country distribution clauses should group by country name and order by name"),
                    () -> assertEquals(0, parametersOf(report).length,
                            "Country distribution report should have no parameters")
            );
        }

        @Test
        @DisplayName("Distribution reports expose all four required columns")
        void distributionColumns() {
            List<String> headings = new PopulationDistributionByContinentReport()
                    .columns().stream()
                    .map(Column::heading)
                    .toList();

            assertEquals(List.of("Name", "Total Population", "Population in Cities",
                            "Population Not in Cities"), headings,
                    "Distribution reports should expose all four required column headings");
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
                    readRow(1000, 250),
                    "Normal row should format city and non-city populations with percentages");
        }

        @Test
        @DisplayName("Zero total avoids division by zero")
        void zeroTotal() throws SQLException {
            assertEquals(List.of("Test", 0L, "0 (0.00%)", "0 (0.00%)"), readRow(0, 0),
                    "Zero total population should format as 0 (0.00%) without dividing by zero");
        }

        @Test
        @DisplayName("Everyone living in cities")
        void everyoneInCities() throws SQLException {
            List<?> row = readRow(500, 500);
            assertEquals("500 (100.00%)", row.get(2),
                    "City population should be 100.00% when everyone lives in cities");
            assertEquals("0 (0.00%)", row.get(3),
                    "Non-city population should be 0.00% when everyone lives in cities");
        }

        @Test
        @DisplayName("Nobody living in cities")
        void nobodyInCities() throws SQLException {
            List<?> row = readRow(500, 0);
            assertEquals("0 (0.00%)", row.get(2),
                    "City population should be 0.00% when nobody lives in cities");
            assertEquals("500 (100.00%)", row.get(3),
                    "Non-city population should be 100.00% when nobody lives in cities");
        }

        @Test
        @DisplayName("Large population values use thousands separators")
        void thousandsSeparators() throws SQLException {
            List<?> row = readRow(1_000_000, 250_000);
            assertEquals("250,000 (25.00%)", row.get(2),
                    "Large city population should include thousands separators");
            assertEquals("750,000 (75.00%)", row.get(3),
                    "Large non-city population should include thousands separators");
        }

        @Test
        @DisplayName("Percentages are rounded to two decimal places")
        void percentageRounding() throws SQLException {
            List<?> row = readRow(3, 1);
            assertEquals("1 (33.33%)", row.get(2),
                    "City percentage should be rounded to two decimal places");
            assertEquals("2 (66.67%)", row.get(3),
                    "Non-city percentage should be rounded to two decimal places");
        }

        @Test
        @DisplayName("City and non-city populations add up to the total")
        void populationPartsAddUp() throws SQLException {
            List<?> row = readRow(7_654_321, 2_345_678);
            assertEquals("2,345,678 (30.65%)", row.get(2),
                    "City population and percentage should match expected values");
            assertEquals("5,308,643 (69.35%)", row.get(3),
                    "Non-city population should equal total minus city population");
        }
    }
}