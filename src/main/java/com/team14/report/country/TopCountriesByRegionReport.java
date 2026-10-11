package com.team14.report.country;

import com.team14.input.UserInput;
import com.team14.report.Validation;

/**
 * Generates a report showing the top N most populated countries
 * in a selected region.
 *
 * This report implements Country Report requirement 6.
 */
public final class TopCountriesByRegionReport extends CountryReport {

    private final String region;
    private final int topN;

    /**
     * Creates a report using user input.
     *
     * @param userInput source used to obtain the region and N
     */
    public TopCountriesByRegionReport(UserInput userInput) {
        this(Validation.requireInput(userInput).getRegion(),
                userInput.getTopN());
    }

    /**
     * Creates a report with a specified region and number of countries.
     *
     * This constructor is useful for testing and application configuration.
     *
     * @param region region to display
     * @param topN number of countries to display
     */
    public TopCountriesByRegionReport(
            String region,
            int topN) {

        this.region = Validation.requireText(region, "Region");
        this.topN = Validation.requirePositive(topN, "Number of countries");
    }

    @Override
    public String title() {
        return "Top " + topN
                + " Populated Countries in "
                + region;
    }

    @Override
    protected String clauses() {
        return """
                WHERE c.Region = ?
                ORDER BY c.Population DESC
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
