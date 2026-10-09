package com.team14.report.city;

import java.util.Objects;

/**
 * Requirement 8: all cities in a specified continent organised by
 * largest population to smallest.
 */
public final class CitiesByContinentReport extends CityReport {

    private final String continent;

    /**
     * Creates a cities-by-continent report.
     *
     * @param continent the continent to search
     */
    public CitiesByContinentReport(String continent) {

        Objects.requireNonNull(
                continent,
                "Continent cannot be null."
        );

        if (continent.isBlank()) {
            throw new IllegalArgumentException(
                    "Continent cannot be empty."
            );
        }

        this.continent = continent.trim();
    }

    @Override
    public String title() {
        return "Cities in " + continent + " by population";
    }

    @Override
    protected String clauses() {
        return """
                WHERE c.Continent = ?
                ORDER BY ci.Population DESC
                """;
    }

    @Override
    protected Object[] parameters() {
        return new Object[]{continent};
    }
}