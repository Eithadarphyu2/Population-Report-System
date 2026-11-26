package com.team14.report.population;

import com.team14.database.query.DatabaseQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PopulationReportIntegrationTest {

    private static final long WORLD_POPULATION = 6_078_749_450L;

    @Test
    @DisplayName("Population totals across all continents equal world population")
    void continentPopulationTotalsEqualWorldPopulation()
            throws SQLException {

        List<Long> continentTotals = DatabaseQuery.executeQuery(
                """
                SELECT SUM(Population) AS TotalPopulation
                FROM country
                GROUP BY Continent
                """,
                rs -> rs.getLong("TotalPopulation")
        );

        long totalPopulation = continentTotals.stream()
                .mapToLong(Long::longValue)
                .sum();

        assertEquals(
                WORLD_POPULATION,
                totalPopulation,
                "The sum of all continent populations should equal world population"
        );
    }

    @Test
    @DisplayName("No country population value is negative")
    void noCountryPopulationIsNegative() throws SQLException {

        List<Long> negativePopulations = DatabaseQuery.executeQuery(
                """
                SELECT COUNT(*) AS NegativeCount
                FROM country
                WHERE Population < 0
                """,
                rs -> rs.getLong("NegativeCount")
        );

        assertEquals(
                0L,
                negativePopulations.get(0),
                "No country should have a negative population"
        );
    }

    @Test
    @DisplayName("No city population value is negative")
    void noCityPopulationIsNegative() throws SQLException {

        List<Long> negativePopulations = DatabaseQuery.executeQuery(
                """
                SELECT COUNT(*) AS NegativeCount
                FROM city
                WHERE Population < 0
                """,
                rs -> rs.getLong("NegativeCount")
        );

        assertEquals(
                0L,
                negativePopulations.get(0),
                "No city should have a negative population"
        );
    }

    @Test
    @DisplayName("Every continent has a non-negative population total")
    void everyContinentHasNonNegativePopulation()
            throws SQLException {

        List<Long> continentTotals = DatabaseQuery.executeQuery(
                """
                SELECT SUM(Population) AS TotalPopulation
                FROM country
                GROUP BY Continent
                """,
                rs -> rs.getLong("TotalPopulation")
        );

        assertTrue(
                continentTotals.stream().allMatch(total -> total >= 0),
                "No continent should have a negative population total"
        );
    }
}