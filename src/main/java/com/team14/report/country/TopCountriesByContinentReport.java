package com.team14.report.country;

import com.team14.input.UserInput;
import com.team14.report.Validation;

/**
 * Generates a report showing the top N most populated countries
 * in a selected continent.
 *
 * This report implements Country Report requirement 5.
 */
public final class TopCountriesByContinentReport extends CountryReport {

    private final String continent;
    private final int topN;

    /**
     * Creates a report using user input.
     *
     * @param userInput source used to obtain the continent and N
     */
    public TopCountriesByContinentReport(UserInput userInput) {
        this(Validation.requireInput(userInput).getContinent(),
                userInput.getTopN());
    }

    /**
     * Creates a report with a specified continent and number of countries.
     *
     * This constructor is useful for testing and application configuration.
     *
     * @param continent continent to display
     * @param topN number of countries to display
     */
    public TopCountriesByContinentReport(
            String continent,
            int topN) {

        this.continent = Validation.requireText(continent, "Continent");
        this.topN = Validation.requirePositive(topN, "Number of countries");
    }

    @Override
    public String title() {
        return "Top " + topN
                + " Populated Countries in "
                + continent;
    }

    @Override
    protected String clauses() {
        return """
                WHERE c.Continent = ?
                ORDER BY c.Population DESC
                LIMIT ?
                """;
    }

    @Override
    protected Object[] parameters() {
        return new Object[]{
                continent,
                topN
        };
    }
}
