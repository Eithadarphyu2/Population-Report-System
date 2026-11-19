package com.team14.database.query;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for DatabaseQuery.
 * They need the MySQL container with the world database.
 */
class DatabaseQueryIntegrationTest {

    /**
     * Simple result object used by the tests.
     */
    private record CountryResult(String code, String name, String continent, long population) {
    }

    /**
     * Maps one country row; shared so every test uses the same mapping.
     */
    private static final DatabaseQuery.RowMapper<CountryResult> COUNTRY_MAPPER = rs -> new CountryResult(rs.getString("Code"), rs.getString("Name"), rs.getString("Continent"), rs.getLong("Population"));

    private static final String SELECT_COUNTRY = """
            SELECT Code, Name, Continent, Population
            FROM country
            """;

    // ------------------------------------------------------ rows are returned

    @Test
    @DisplayName("Execute a SELECT query and return results")
    void testExecuteQuery() throws Exception {

        List<CountryResult> results = DatabaseQuery.executeQuery(SELECT_COUNTRY + "LIMIT 5", COUNTRY_MAPPER);

        assertEquals(5, results.size());
    }

    @Test
    @DisplayName("A query without a LIMIT returns every row")
    void allRowsAreReturned() throws Exception {

        List<CountryResult> results = DatabaseQuery.executeQuery(SELECT_COUNTRY, COUNTRY_MAPPER);

        assertEquals(239, results.size());
    }

    @Test
    @DisplayName("The values of a row are mapped correctly")
    void rowValuesAreMapped() throws Exception {

        List<CountryResult> results = DatabaseQuery.executeQuery(SELECT_COUNTRY + "ORDER BY Population DESC LIMIT 1", COUNTRY_MAPPER);

        assertEquals(List.of(new CountryResult("CHN", "China", "Asia", 1277558000L)), results);
    }

    @Test
    @DisplayName("The rows come back in the order the SQL asks for")
    void resultOrderFollowsTheSql() throws Exception {

        List<String> names = DatabaseQuery.executeQuery("SELECT Name FROM country ORDER BY Population DESC LIMIT 3", rs -> rs.getString("Name"));

        assertEquals(List.of("China", "India", "United States"), names);
    }

    @Test
    @DisplayName("A NULL value in the database is mapped as null")
    void nullValueIsMapped() throws Exception {

        List<Object> capitals = DatabaseQuery.executeQuery("SELECT Capital FROM country WHERE Code = ?", rs -> rs.getObject("Capital"), "ATA");

        assertEquals(1, capitals.size());
        assertNull(capitals.get(0));
    }

    // ----------------------------------------------------------- parameters

    @Test
    @DisplayName("Execute a parameterized SELECT query")
    void testParameterizedQuery() throws Exception {

        List<CountryResult> results = DatabaseQuery.executeQuery(SELECT_COUNTRY + "WHERE Continent = ? ORDER BY Population DESC LIMIT ?", COUNTRY_MAPPER, "Asia", 5);

        assertEquals(5, results.size());

        for (CountryResult country : results) {
            assertEquals("Asia", country.continent());
        }
    }

    @Test
    @DisplayName("A text parameter finds the matching row")
    void textParameterFindsRow() throws Exception {

        List<String> names = DatabaseQuery.executeQuery("SELECT Name FROM country WHERE Code = ?", rs -> rs.getString("Name"), "FRA");

        assertEquals(List.of("France"), names);
    }

    @Test
    @DisplayName("A number parameter filters the rows")
    void numberParameterFiltersRows() throws Exception {

        List<CountryResult> results = DatabaseQuery.executeQuery(SELECT_COUNTRY + "WHERE Population > ? ORDER BY Population DESC", COUNTRY_MAPPER, 100_000_000L);

        assertEquals(10, results.size());
        assertEquals("China", results.get(0).name());
    }

    @Test
    @DisplayName("A null parameter list is the same as no parameters")
    void nullParameterListIsAllowed() throws Exception {

        List<CountryResult> results = DatabaseQuery.executeQuery(SELECT_COUNTRY + "LIMIT 5", COUNTRY_MAPPER, (Object[]) null);

        assertEquals(5, results.size());
    }

    @Test
    @DisplayName("Return an empty list when no records match")
    void testNoMatchingResults() throws Exception {

        List<CountryResult> results = DatabaseQuery.executeQuery(SELECT_COUNTRY + "WHERE Continent = ?", COUNTRY_MAPPER, "NonExistentContinent");

        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("SQL typed into a parameter is treated as plain text")
    void sqlInParameterIsPlainText() throws Exception {

        List<CountryResult> results = DatabaseQuery.executeQuery(SELECT_COUNTRY + "WHERE Continent = ?", COUNTRY_MAPPER, "Asia' OR '1'='1");

        assertTrue(results.isEmpty());
        assertEquals(239, DatabaseQuery.executeQuery(SELECT_COUNTRY, COUNTRY_MAPPER).size());
    }

    // --------------------------------------------------------------- errors

    @Test
    @DisplayName("SQL for a table that does not exist throws an SQLException")
    void unknownTableThrows() {

        assertThrows(SQLException.class, () -> DatabaseQuery.executeQuery("SELECT * FROM table_that_does_not_exist", COUNTRY_MAPPER));
    }

    @Test
    @DisplayName("A missing parameter value throws an SQLException")
    void missingParameterThrows() {

        assertThrows(SQLException.class, () -> DatabaseQuery.executeQuery(SELECT_COUNTRY + "WHERE Continent = ?", COUNTRY_MAPPER));
    }

    @Test
    @DisplayName("A mapper that reads an unknown column throws an SQLException")
    void unknownColumnInMapperThrows() {

        assertThrows(SQLException.class, () -> DatabaseQuery.executeQuery(SELECT_COUNTRY + "LIMIT 1", rs -> rs.getString("NoSuchColumn")));
    }
}