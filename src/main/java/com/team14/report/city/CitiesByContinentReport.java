package com.team14.report.city;

import com.team14.input.UserInput;
import com.team14.report.Validation;

/**
 * Requirement 8: all cities in a specified continent organised by
 * largest population to smallest.
 */
public final class CitiesByContinentReport extends CityReport {

    private final String continent;

    /**
     * Creates a cities-by-continent report using user input.
     *
     * @param userInput source used to obtain the continent
     */
    public CitiesByContinentReport(UserInput userInput) {
        this(Validation.requireInput(userInput).getContinent());
    }

    /**
     * Creates a cities-by-continent report with a specified continent.
     *
     * @param continent the continent to search
     */
    public CitiesByContinentReport(String continent) {
        this.continent = Validation.requireText(continent, "Continent");
    }

    @Override
    public String title() {
        return "Cities in " + continent + " by population";
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