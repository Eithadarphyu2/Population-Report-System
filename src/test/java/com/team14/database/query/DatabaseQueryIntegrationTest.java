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
    private static final DatabaseQuery.RowMapper<CountryResult> COUNTRY_MAPPER = rs -> new CountryResult(
            rs.getString("Code"),
            rs.getString("Name"),
            rs.getString("Continent"),
            rs.getLong("Population")
    );

    // Base SELECT projection used across country query tests
    private static final String SELECT_COUNTRY = """
            SELECT Code, Name, Continent, Population
            FROM country
            """;

    // ------------------------------------------------------ rows are returned

    @Test
    @DisplayName("Execute a SELECT query and return results")
    void testExecuteQuery() throws Exception {
        // Verify LIMIT restricts the number of returned rows to 5
        List<CountryResult> results = DatabaseQuery.executeQuery(SELECT_COUNTRY + "LIMIT 5", COUNTRY_MAPPER);

        assertEquals(5, results.size(), "Expected 5 rows when LIMIT 5 is applied");
    }

    @Test
    @DisplayName("A query without a LIMIT returns every row")
    void allRowsAreReturned() throws Exception {
        // Without LIMIT, all rows in the world 'country' table should be returned
        List<CountryResult> results = DatabaseQuery.executeQuery(SELECT_COUNTRY, COUNTRY_MAPPER);

        assertEquals(239, results.size(), "Expected all 239 country rows without a LIMIT clause");
    }

    @Test
    @DisplayName("The values of a row are mapped correctly")
    void rowValuesAreMapped() throws Exception {
        // Query the highest-population country and verify all mapped record fields
        List<CountryResult> results = DatabaseQuery.executeQuery(
                SELECT_COUNTRY + "ORDER BY Population DESC LIMIT 1",
                COUNTRY_MAPPER
        );

        assertEquals(
                List.of(new CountryResult("CHN", "China", "Asia", 1277558000L)),
                results,
                "Expected mapped CountryResult for the most populous country (China)"
        );
    }

    @Test
    @DisplayName("The rows come back in the order the SQL asks for")
    void resultOrderFollowsTheSql() throws Exception {
        // Confirm result list preserves descending population ordering from SQL
        List<String> names = DatabaseQuery.executeQuery(
                "SELECT Name FROM country ORDER BY Population DESC LIMIT 3",
                rs -> rs.getString("Name")
        );

        assertEquals(
                List.of("China", "India", "United States"),
                names,
                "Expected top 3 countries ordered by population descending"
        );
    }

    @Test
    @DisplayName("A query with a NULL column returns one row for ATA")
    void nullValueReturnsOneRow() throws Exception {
        // Antarctica ('ATA') has a NULL Capital in the world dataset
        List<Object> capitals = DatabaseQuery.executeQuery(
                "SELECT Capital FROM country WHERE Code = ?",
                rs -> rs.getObject("Capital"),
                "ATA"
        );

        assertEquals(1, capitals.size(), "Expected exactly one row for country code ATA");
    }

    @Test
    @DisplayName("A NULL value in the database is mapped as null")
    void nullValueIsMappedToNull() throws Exception {
        // Antarctica ('ATA') has a NULL Capital in the world dataset
        List<Object> capitals = DatabaseQuery.executeQuery(
                "SELECT Capital FROM country WHERE Code = ?",
                rs -> rs.getObject("Capital"),
                "ATA"
        );

        assertNull(capitals.getFirst(), "Expected SQL NULL Capital value to map to Java null");
    }

    // ----------------------------------------------------------- parameters

    @Test
    @DisplayName("A parameterized SELECT query returns the expected row count")
    void parameterizedQueryReturnsExpectedRowCount() throws Exception {
        // Test combining string and integer bound parameters with ORDER BY and LIMIT
        List<CountryResult> results = DatabaseQuery.executeQuery(
                SELECT_COUNTRY + "WHERE Continent = ? ORDER BY Population DESC LIMIT ?",
                COUNTRY_MAPPER,
                "Asia",
                5
        );

        assertEquals(5, results.size(), "Expected 5 Asian countries in result");
    }

    @Test
    @DisplayName("A parameterized SELECT query filters rows by continent")
    void parameterizedQueryFiltersContinent() throws Exception {
        // Test combining string and integer bound parameters with ORDER BY and LIMIT
        List<CountryResult> results = DatabaseQuery.executeQuery(
                SELECT_COUNTRY + "WHERE Continent = ? ORDER BY Population DESC LIMIT ?",
                COUNTRY_MAPPER,
                "Asia",
                5
        );

        // Verify every returned row matches the bound continent parameter
        for (CountryResult country : results) {
            assertEquals("Asia", country.continent(), "Expected continent 'Asia' for row: " + country.name());
        }
    }

    @Test
    @DisplayName("A text parameter finds the matching row")
    void textParameterFindsRow() throws Exception {
        // Look up a single country by its ISO code parameter
        List<String> names = DatabaseQuery.executeQuery(
                "SELECT Name FROM country WHERE Code = ?",
                rs -> rs.getString("Name"),
                "FRA"
        );

        assertEquals(List.of("France"), names, "Expected 'France' for country code 'FRA'");
    }

    @Test
    @DisplayName("A number parameter returns the expected filtered row count")
    void numberParameterFiltersMatchingRowCount() throws Exception {
        // Filter countries whose population exceeds 100 million, ordered largest first
        List<CountryResult> results = DatabaseQuery.executeQuery(
                SELECT_COUNTRY + "WHERE Population > ? ORDER BY Population DESC",
                COUNTRY_MAPPER,
                100_000_000L
        );

        assertEquals(10, results.size(), "Expected 10 countries with population over 100,000,000");
    }

    @Test
    @DisplayName("A number parameter preserves SQL ordering on filtered rows")
    void numberParameterPreservesOrder() throws Exception {
        // Filter countries whose population exceeds 100 million, ordered largest first
        List<CountryResult> results = DatabaseQuery.executeQuery(
                SELECT_COUNTRY + "WHERE Population > ? ORDER BY Population DESC",
                COUNTRY_MAPPER,
                100_000_000L
        );

        assertEquals("China", results.getFirst().name(), "Expected 'China' to be first when ordered by population DESC");
    }

    @Test
    @DisplayName("A null parameter list is the same as no parameters")
    void nullParameterListIsAllowed() throws Exception {
        // Passing a null varargs array should behave like an empty parameter list
        List<CountryResult> results = DatabaseQuery.executeQuery(
                SELECT_COUNTRY + "LIMIT 5",
                COUNTRY_MAPPER,
                (Object[]) null
        );

        assertEquals(5, results.size(), "Expected 5 rows when passing a null parameter array");
    }

    @Test
    @DisplayName("Return an empty list when no records match")
    void testNoMatchingResults() throws Exception {
        // Non-matching filter value should return an empty list rather than null or error
        List<CountryResult> results = DatabaseQuery.executeQuery(
                SELECT_COUNTRY + "WHERE Continent = ?",
                COUNTRY_MAPPER,
                "NonExistentContinent"
        );

        assertTrue(results.isEmpty(), "Expected an empty list when no rows match the filter");
    }

    @Test
    @DisplayName("SQL typed into a parameter returns no rows")
    void sqlInParameterReturnsNoRows() throws Exception {
        // Ensure SQL-injection-like input is bound safely as a literal string rather than executed as SQL
        List<CountryResult> results = DatabaseQuery.executeQuery(
                SELECT_COUNTRY + "WHERE Continent = ?",
                COUNTRY_MAPPER,
                "Asia' OR '1'='1"
        );

        assertTrue(results.isEmpty(), "Expected no rows because SQL string in parameter should not inject logic");
    }

    @Test
    @DisplayName("SQL typed into a parameter leaves all table rows intact")
    void sqlInParameterLeavesTableUnchanged() throws Exception {
        // Ensure SQL-injection-like input does not alter or delete rows in the country table
        DatabaseQuery.executeQuery(
                SELECT_COUNTRY + "WHERE Continent = ?",
                COUNTRY_MAPPER,
                "Asia' OR '1'='1"
        );

        assertEquals(
                239,
                DatabaseQuery.executeQuery(SELECT_COUNTRY, COUNTRY_MAPPER).size(),
                "Expected full 239-row table to remain intact"
        );
    }

    // --------------------------------------------------------------- errors

    @Test
    @DisplayName("SQL for a table that does not exist throws an SQLException")
    void unknownTableThrows() {
        // Querying a non-existent table should propagate an SQLException
        assertThrows(
                SQLException.class,
                () -> DatabaseQuery.executeQuery("SELECT * FROM table_that_does_not_exist", COUNTRY_MAPPER),
                "Expected SQLException when querying a non-existent table"
        );
    }

    @Test
    @DisplayName("A missing parameter value throws an SQLException")
    void missingParameterThrows() {
        // Query has one '?' placeholder but no parameter value supplied
        assertThrows(
                SQLException.class,
                () -> DatabaseQuery.executeQuery(SELECT_COUNTRY + "WHERE Continent = ?", COUNTRY_MAPPER),
                "Expected SQLException when a required query parameter is missing"
        );
    }

    @Test
    @DisplayName("A mapper that reads an unknown column throws an SQLException")
    void unknownColumnInMapperThrows() {
        // Accessing a column name absent from the ResultSet should throw SQLException
        assertThrows(
                SQLException.class,
                () -> DatabaseQuery.executeQuery(SELECT_COUNTRY + "LIMIT 1", rs -> rs.getString("NoSuchColumn")),
                "Expected SQLException when RowMapper accesses a non-existent column"
        );
    }
}