package com.team14.report.population;

import com.team14.input.UserInput;
import com.team14.report.Validation;

/**
 * The population of a specified district.
 * <p>
 * The database has no district table, so the district population is the
 * total of the populations of all cities recorded in that district.
 */
public final class DistrictPopulationReport extends TotalPopulationReport {

    private final String district;

    /**
     * Creates a district population report using user input.
     *
     * @param userInput source used to obtain the district
     */
    public DistrictPopulationReport(UserInput userInput) {
        this(Validation.requireInput(userInput).getDistrict());
    }

    /**
     * Creates a district population report with a specified district.
     *
     * @param district district to display
     */
    public DistrictPopulationReport(String district) {
        this.district = Validation.requireText(district, "District");
    }

    @Override
    public String title() {
        return "Population of " + district + " District";
    }

    @Override
    protected String select() {
        return """
                SELECT ci.District AS Name,
                       SUM(ci.Population) AS Population
                FROM city ci
                """;
    }

    @Override
    protected String clauses() {
        return """
                WHERE ci.District = ?
                GROUP BY ci.District
                """;
    }

    @Override
    protected Object[] parameters() {
        return new Object[]{district};
    }
}