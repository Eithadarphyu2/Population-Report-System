package com.team14.report.city;

import com.team14.input.UserInput;

import java.util.Objects;

/**
 * Generates a report showing the top N most populated cities
 * in a selected region.
 *
 * This report implements the City Report requirement.
 */
public final class TopCitiesByRegionReport extends CityReport {

    private final String region;
    private final int topN;

    /**
     * Creates a report using user input.
     *
     * @param userInput source used to obtain the region and N
     */
    public TopCitiesByRegionReport(UserInput userInput) {
        Objects.requireNonNull(
                userInput,
                "User input cannot be null."
        );

        this.region = userInput.getRegion();
        this.topN = userInput.getTopN();
    }

    /**
     * Creates a report with a specified region and number of cities.
     *
     * @param region region to display
     * @param topN number of cities to display
     */
    public TopCitiesByRegionReport(
            String region,
            int topN) {

        if (region == null || region.isBlank()) {
            throw new IllegalArgumentException(
                    "Region cannot be empty."
            );
        }

        if (topN <= 0) {
            throw new IllegalArgumentException(
                    "Number of cities must be greater than 0."
            );
        }

        this.region = region;
        this.topN = topN;
    }

    @Override
    public String title() {
        return "Top " + topN
                + " Populated Cities in "
                + region;
    }

    @Override
    protected String clauses() {
        return """
                WHERE c.Region = ?
                ORDER BY ci.Population DESC
                LIMIT ?
                """;
    }

    @Override
    protected Object[] parameters() {
        return new Object[]{
                region,
                topN
        };
    }
}