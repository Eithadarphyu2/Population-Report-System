package com.team14.report.capitalcity;

import com.team14.input.UserInput;
import com.team14.report.Validation;

/**
 * Generates a report showing all capital cities in a selected region,
 * ordered by population from highest to lowest.
 * <p>
 * This report implements the capital city report requirement for regions.
 */
public final class CapitalCitiesByRegionReport extends CapitalCityReport {

    private final String region;

    /**
     * Creates a capital city report using user input.
     *
     * @param userInput source used to obtain the region
     */
    public CapitalCitiesByRegionReport(UserInput userInput) {
        this(Validation.requireInput(userInput).getRegion());
    }

    /**
     * Creates a capital city report with a specified region.
     * <p>
     * This constructor is useful for testing.
     *
     * @param region region to display
     */
    public CapitalCitiesByRegionReport(String region) {
        this.region = Validation.requireText(region, "Region");
    }

    @Override
    public String title() {
        return "Capital Cities in " + region + " by Population";
    }

    @Override
    protected String clauses() {
        return """
                WHERE c.Region = ?
                ORDER BY ci.Population DESC
                """;
    }

    @Override
    protected Object[] parameters() {
        return new Object[]{region};
    }
}
