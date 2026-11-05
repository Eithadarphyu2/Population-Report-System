package com.team14.report.capitalcity;

import com.team14.input.UserInput;
import com.team14.report.Validation;

/**
 * Generates a report showing the top N most populated capital cities
 * in the world.
 * <p>
 * This report implements Capital City Report requirement 20.
 */
public final class TopCapitalCitiesWorldwideReport extends CapitalCityReport {

    private final int topN;

    /**
     * Creates a top-N worldwide capital city report using user input.
     *
     * @param userInput source used to obtain the number of capital cities
     */
    public TopCapitalCitiesWorldwideReport(UserInput userInput) {
        this(Validation.requireInput(userInput).getTopN());
    }

    /**
     * Creates a top-N worldwide capital city report with a specified value.
     * <p>
     * This constructor is useful for testing.
     *
     * @param topN number of capital cities to display
     */
    public TopCapitalCitiesWorldwideReport(int topN) {
        this.topN = Validation.requirePositive(topN, "Number of capital cities");
    }

    @Override
    public String title() {
        return "Top " + topN + " Populated Capital Cities Worldwide";
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