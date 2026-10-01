package com.team14.database.query;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for DatabaseQuery (needs the MySQL container).
 */
class DatabaseQueryTest {

    /**
     * Simple result object used by the tests.
     */
    private record CountryResult(
            String code,
            String name,
            String continent,
            long population
    ) {
    }

    /**
     * Maps one country row; shared so every test uses the same mapping.
     */
    private static final DatabaseQuery.RowMapper<CountryResult> COUNTRY_MAPPER =
            rs -> new CountryResult(
                    rs.getString("Code"),
                    rs.getString("Name"),
                    rs.getString("Continent"),
                    rs.getLong("Population")
            );

    private static final String SELECT_COUNTRY = """
            SELECT Code, Name, Continent, Population
            FROM country
            """;

    @Test
    @DisplayName("Execute a SELECT query and return results")
    void testExecuteQuery() throws Exception {

        List<CountryResult> results = DatabaseQuery.executeQuery(
                SELECT_COUNTRY + "LIMIT 5",
                COUNTRY_MAPPER
        );

        assertEquals(5, results.size());
    }

    @Test
    @DisplayName("Execute a parameterized SELECT query")
    void testParameterizedQuery() throws Exception {

        List<CountryResult> results = DatabaseQuery.executeQuery(
                SELECT_COUNTRY
                        + "WHERE Continent = ? ORDER BY Population DESC LIMIT ?",
                COUNTRY_MAPPER,
                "Asia",
                5
        );

        assertEquals(5, results.size());

        for (CountryResult country : results) {
            assertEquals("Asia", country.continent());
        }
    }

    @Test
    @DisplayName("Return an empty list when no records match")
    void testNoMatchingResults() throws Exception {

        List<CountryResult> results = DatabaseQuery.executeQuery(
                SELECT_COUNTRY + "WHERE Continent = ?",
                COUNTRY_MAPPER,
                "NonExistentContinent"
        );

        assertTrue(results.isEmpty());
    }
}
