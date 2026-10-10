package com.team14.report.city;

import com.team14.input.UserInput;
import com.team14.report.Report;

/**
 * Generates a report showing all cities in a selected region,
 * ordered by population from highest to lowest.
 *
 * This report implements City Report requirement 9.
 */
public final class CitiesByRegionReport extends CityReport {

    private final String region;

    /**
     * Creates a cities-by-region report using user input.
     *
     * @param userInput source used to obtain the region
     */
    public CitiesByRegionReport(UserInput userInput) {
        this(Report.requireInput(userInput).getRegion());
    }

    /**
     * Creates a cities-by-region report with a specified region.
     *
     * This constructor is useful for testing and non-interactive runs.
     *
     * @param region region to display
     */
    public CitiesByRegionReport(String region) {
        this.region = Report.requireText(region, "Region");
    }

    @Override
    public String title() {
        return "Cities in " + region + " by Population";
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