package com.team14.report.city;

import com.team14.input.UserInput;
import com.team14.report.Validation;

/**
 * Generates a report showing the top N most populated cities
 * within a specified continent.
 *
 * This report implements the Top N Cities by Continent requirement.
 */
public final class TopCitiesByContinentReport extends CityReport {

    private final String continent;
    private final int topN;

    /**
     * Creates a report using user input.
     *
     * @param userInput source used to obtain the continent and N
     */
    public TopCitiesByContinentReport(UserInput userInput) {
        this(Validation.requireInput(userInput).getContinent(),
                userInput.getTopN());
    }

    /**
     * Creates a top-N city report for a specified continent.
     *
     * @param continent continent to report on
     * @param topN number of cities to display
     */
    public TopCitiesByContinentReport(String continent, int topN) {
        this.continent = Validation.requireText(continent, "Continent");
        this.topN = Validation.requirePositive(topN, "Number of cities");
    }

    @Override
    public String title() {
        return "Top " + topN + " Populated Cities in " + continent;
    }

    @Override
    protected String clauses() {
        return """
                WHERE c.Continent = ?
                ORDER BY ci.Population DESC
                LIMIT ?
                """;
    }

    @Override
    protected Object[] parameters() {
        return new Object[]{continent, topN};
    }
}