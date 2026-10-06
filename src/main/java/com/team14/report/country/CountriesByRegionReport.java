package com.team14.report.country;

import java.util.Objects;

/**
 * Requirement 3: all countries in a specified region organised by
 * largest population to smallest.
 */
public final class CountriesByRegionReport extends CountryReport {

    private final String region;

    /**
     * Creates a country-by-region report.
     *
     * @param region the region to search
     */
    public CountriesByRegionReport(String region) {

        Objects.requireNonNull(
                region,
                "Region cannot be null."
        );

        if (region.isBlank()) {
            throw new IllegalArgumentException(
                    "Region cannot be empty."
            );
        }

        this.region = region.trim();
    }

    @Override
    public String title() {
        return "Countries in " + region + " by population";
    }

    @Override
    protected String clauses() {
        return """
                WHERE c.Region = ?
                ORDER BY c.Population DESC
                """;
    }

    @Override
    protected Object[] parameters() {
        return new Object[]{region};
    }
}