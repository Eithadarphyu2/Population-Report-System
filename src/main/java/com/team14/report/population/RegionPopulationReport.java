package com.team14.report.population;

/**
 * Generates a report showing the total population
 * for each region.
 */
public final class RegionPopulationReport extends TotalPopulationReport {

    @Override
    public String title() {
        return "Population by Region";
    }

    @Override
    protected String select() {
        return """
                SELECT Region AS Name,
                       SUM(Population) AS Population
                FROM country
                GROUP BY Region
                ORDER BY Population DESC
                """;
    }
}
