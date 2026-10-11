package com.team14.report.city;

import com.team14.input.UserInput;
import com.team14.report.Validation;

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
        this(Validation.requireInput(userInput).getRegion(),
                userInput.getTopN());
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

        this.region = Validation.requireText(region, "Region");
        this.topN = Validation.requirePositive(topN, "Number of cities");
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
