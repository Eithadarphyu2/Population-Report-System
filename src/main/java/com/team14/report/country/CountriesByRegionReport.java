package com.team14.report.country;

import com.team14.input.UserInput;
import com.team14.report.Report;

/**
 * Requirement 3: all countries in a specified region organised by
 * largest population to smallest.
 */
public final class CountriesByRegionReport extends CountryReport {

    private final String region;

    /**
     * Creates a country-by-region report using user input.
     *
     * @param userInput source used to obtain the region
     */
    public CountriesByRegionReport(UserInput userInput) {
        this(Report.requireInput(userInput).getRegion());
    }

    /**
     * Creates a country-by-region report with a specified region.
     *
     * @param region the region to search
     */
    public CountriesByRegionReport(String region) {
        this.region = Report.requireText(region, "Region");
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