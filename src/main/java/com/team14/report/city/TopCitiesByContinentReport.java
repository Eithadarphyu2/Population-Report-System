package com.team14.report.city;

/**
 * Generates a report showing the top N most populated cities
 * within a specified continent.
 *
 * This report implements the Top N Cities by Continent requirement.
 */
public final class TopCitiesByContinentReport extends CityReport {

    private final String continent;
    private final int topN;

    /**
     * Creates a top-N city report for a specified continent.
     *
     * @param continent continent to report on
     * @param topN number of cities to display
     */
    public TopCitiesByContinentReport(String continent, int topN) {

        if (continent == null || continent.isBlank()) {
            throw new IllegalArgumentException(
                    "Continent cannot be null or blank."
            );
        }

        if (topN <= 0) {
            throw new IllegalArgumentException(
                    "Number of cities must be greater than 0."
            );
        }

        this.continent = continent;
        this.topN = topN;
    }

    @Override
    public String title() {
        return "Top " + topN + " Populated Cities in " + continent;
    }

    @Override
    protected String clauses() {
        return """
                WHERE c.Continent = ?
                ORDER BY ci.Population DESC
                LIMIT ?
                """;
    }

    @Override
    protected Object[] parameters() {
        return new Object[]{continent, topN};
    }
}