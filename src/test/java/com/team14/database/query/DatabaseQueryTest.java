package com.team14.database.query;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DatabaseQueryTest {

    /**
     * Simple result object used by the integration tests.
     */
    private record CountryResult(
            String code,
            String name,
            String continent,
            long population
    ) {
    }

    @Test
    @DisplayName("Execute a SELECT query and return results")
    void testExecuteQuery() throws Exception {

        String sql = """
                SELECT Code, Name, Continent, Population
                FROM country
                LIMIT 5
                """;

        List<CountryResult> results =
                DatabaseQuery.executeQuery(
                        sql,
                        rs -> new CountryResult(
                                rs.getString("Code"),
                                rs.getString("Name"),
                                rs.getString("Continent"),
                                rs.getLong("Population")
                        )
                );

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(5, results.size());
    }

    @Test
    @DisplayName("Execute a parameterized SELECT query")
    void testParameterizedQuery() throws Exception {

        String sql = """
                SELECT Code, Name, Continent, Population
                FROM country
                WHERE Continent = ?
                ORDER BY Population DESC
                LIMIT ?
                """;

        List<CountryResult> results =
                DatabaseQuery.executeQuery(
                        sql,
                        rs -> new CountryResult(
                                rs.getString("Code"),
                                rs.getString("Name"),
                                rs.getString("Continent"),
                                rs.getLong("Population")
                        ),
                        "Asia",
                        5
                );

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(5, results.size());

        for (CountryResult country : results) {
            assertEquals("Asia", country.continent());
        }
    }

    @Test
    @DisplayName("Return an empty list when no records match")
    void testNoMatchingResults() throws Exception {

        String sql = """
                SELECT Code, Name, Continent, Population
                FROM country
                WHERE Continent = ?
                """;

        List<CountryResult> results =
                DatabaseQuery.executeQuery(
                        sql,
                        rs -> new CountryResult(
                                rs.getString("Code"),
                                rs.getString("Name"),
                                rs.getString("Continent"),
                                rs.getLong("Population")
                        ),
                        "NonExistentContinent"
                );

        assertNotNull(results);
        assertEquals(0, results.size());
    }
}