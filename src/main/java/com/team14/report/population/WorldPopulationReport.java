package com.team14.report.population;

/**
 * Generates a report showing the total population of the world.
 */
public final class WorldPopulationReport extends TotalPopulationReport {

    @Override
    public String title() {
        return "World Population";
    }

    @Override
    protected String select() {
        return """
                SELECT 'World' AS Name,
                       SUM(Population) AS Population
                FROM country
                """;
    }
}