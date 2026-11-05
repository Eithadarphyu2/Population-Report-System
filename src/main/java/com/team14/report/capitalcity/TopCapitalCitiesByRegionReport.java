package com.team14.report.capitalcity;

import com.team14.input.UserInput;
import com.team14.report.Validation;

/**
 * Generates a report showing the top N most populated capital cities
 * in a selected region.
 * <p>
 * This report implements the Capital City Report requirement.
 */
public final class TopCapitalCitiesByRegionReport extends CapitalCityReport {

    private final String region;
    private final int topN;

    /**
     * Creates a report using user input.
     *
     * @param userInput source used to obtain the region and N
     */
    public TopCapitalCitiesByRegionReport(UserInput userInput) {
        this(Validation.requireInput(userInput).getRegion(), userInput.getTopN());
    }

    /**
     * Creates a report with a specified region and number of capital cities.
     *
     * @param region region to display
     * @param topN   number of capital cities to display
     */
    public TopCapitalCitiesByRegionReport(String region, int topN) {
        this.region = Validation.requireText(region, "Region");
        this.topN = Validation.requirePositive(topN, "Number of capital cities");
    }

    @Override
    public String title() {
        return "Top " + topN + " Populated Capital Cities in " + region;
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
        return new Object[]{region, topN};
    }
}