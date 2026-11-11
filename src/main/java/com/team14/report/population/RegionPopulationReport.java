package com.team14.report.population;

import com.team14.input.UserInput;
import com.team14.report.Validation;

/**
 * The total population of a specified region.
 */
public final class RegionPopulationReport extends TotalPopulationReport {

    private final String region;

    /**
     * Creates a region population report using user input.
     *
     * @param userInput source used to obtain the region
     */
    public RegionPopulationReport(UserInput userInput) {
        this(Validation.requireInput(userInput).getRegion());
    }

    /**
     * Creates a region population report with a specified region.
     *
     * @param region region to display
     */
    public RegionPopulationReport(String region) {
        this.region = Validation.requireText(region, "Region");
    }

    @Override
    public String title() {
        return "Population of " + region;
    }

    @Override
    protected String select() {
        return """
                SELECT c.Region AS Name,
                       SUM(c.Population) AS Population
                FROM country c
                """;
    }

    @Override
    protected String clauses() {
        return """
                WHERE c.Region = ?
                GROUP BY c.Region
                """;
    }

    @Override
    protected Object[] parameters() {
        return new Object[]{region};
    }
}