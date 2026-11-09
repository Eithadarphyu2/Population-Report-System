package com.team14.report.population;

import com.team14.input.UserInput;
import com.team14.report.Validation;

/**
 * The population of a specified continent.
 */
public final class ContinentPopulationReport extends TotalPopulationReport {

    private final String continent;

    /**
     * Creates a continent population report using user input.
     *
     * @param userInput source used to obtain the continent
     */
    public ContinentPopulationReport(UserInput userInput) {
        this(Validation.requireInput(userInput).getContinent());
    }

    /**
     * Creates a continent population report with a specified continent.
     *
     * @param continent continent to display
     */
    public ContinentPopulationReport(String continent) {
        this.continent = Validation.requireText(continent, "Continent");
    }

    @Override
    public String title() {
        return "Population of " + continent;
    }

    @Override
    protected String select() {
        return """
                SELECT c.Continent AS Name,
                       SUM(c.Population) AS Population
                FROM country c
                """;
    }

    @Override
    protected String clauses() {
        return """
                WHERE c.Continent = ?
                GROUP BY c.Continent
                """;
    }

    @Override
    protected Object[] parameters() {
        return new Object[]{continent};
    }
}