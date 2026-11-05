package com.team14.report.capitalcity;

import com.team14.input.UserInput;
import com.team14.report.Validation;

/**
 * Generates a report showing all capital cities in a selected continent,
 * ordered by population from highest to lowest.
 */
public final class CapitalCitiesByContinentReport extends CapitalCityReport {

    private final String continent;

    /**
     * Creates a capital city report using user input.
     *
     * @param userInput source used to obtain the continent
     */
    public CapitalCitiesByContinentReport(UserInput userInput) {
        this(Validation.requireInput(userInput).getContinent());
    }

    /**
     * Creates a capital city report with a specified continent.
     *
     * @param continent continent to display
     */
    public CapitalCitiesByContinentReport(String continent) {
        this.continent = Validation.requireText(continent, "Continent");
    }

    @Override
    public String title() {
        return "Capital Cities in " + continent + " by Population";
    }

    @Override
    protected String clauses() {
        return """
                WHERE c.Continent = ?
                ORDER BY ci.Population DESC
                """;
    }

    @Override
    protected Object[] parameters() {
        return new Object[]{continent};
    }
}