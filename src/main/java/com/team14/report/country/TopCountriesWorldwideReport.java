package com.team14.report.country;

import com.team14.input.UserInput;

import java.util.Objects;

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

//    public TopCountriesWorldwideReport(UserInput userInput) {
//        Objects.requireNonNull(userInput, "User input cannot be null.");
//        this.topN = userInput.getTopN();
//    }

    /**
     * Creates a top-N worldwide country report with a specified value.
     *
     * This constructor is useful for testing.
     *
     * @param topN number of countries to display
     */
    public TopCountriesWorldwideReport(int topN) {
        if (topN <= 0) {
            throw new IllegalArgumentException(
                    "Number of countries must be greater than 0."
            );
        }

        this.topN = topN;
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