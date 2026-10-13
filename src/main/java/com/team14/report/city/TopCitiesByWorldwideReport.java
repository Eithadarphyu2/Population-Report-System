package com.team14.report.city;

import com.team14.input.UserInput;
import com.team14.report.Validation;

/**
 * Generates a report showing the top N most populated cities worldwide.
 */
public final class TopCitiesByWorldwideReport extends CityReport {

    private final int topN;

    /**
     * Creates a top-N worldwide city report using user input.
     *
     * @param userInput source used to obtain the number of cities
     */
    public TopCitiesByWorldwideReport(UserInput userInput) {
        this(Validation.requireInput(userInput).getTopN());
    }

    /**
     * Creates a top-N worldwide city report with a specified value.
     *
     * @param topN number of cities to display
     */
    public TopCitiesByWorldwideReport(int topN) {
        this.topN = Validation.requirePositive(topN, "Number of cities");
    }

    @Override
    public String title() {
        return "Top " + topN + " Populated Cities Worldwide";
    }

    @Override
    protected String clauses() {
        return """
                ORDER BY ci.Population DESC
                LIMIT ?
                """;
    }

    @Override
    protected Object[] parameters() {
        return new Object[]{topN};
    }
}