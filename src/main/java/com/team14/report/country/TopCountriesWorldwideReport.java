package com.team14.report.country;

import com.team14.input.UserInput;
import com.team14.report.Validation;

/**
 * Generates a report showing the top N most populated countries worldwide.
 *
 * This report implements Country Report requirement 4.
 */
public final class TopCountriesWorldwideReport extends CountryReport {

    private final int topN;

    /**
     * Creates a top-N worldwide country report using user input.
     *
     * @param userInput source used to obtain the number of countries
     */
    public TopCountriesWorldwideReport(UserInput userInput) {
        this(Validation.requireInput(userInput).getTopN());
    }

    /**
     * Creates a top-N worldwide country report with a specified value.
     *
     * This constructor is useful for testing.
     *
     * @param topN number of countries to display
     */
    public TopCountriesWorldwideReport(int topN) {
        this.topN = Validation.requirePositive(topN, "Number of countries");
    }

    @Override
    public String title() {
        return "Top " + topN + " Populated Countries Worldwide";
    }

    @Override
    protected String clauses() {
        return """
                ORDER BY c.Population DESC
                LIMIT ?
                """;
    }

    @Override
    protected Object[] parameters() {
        return new Object[]{topN};
    }
}