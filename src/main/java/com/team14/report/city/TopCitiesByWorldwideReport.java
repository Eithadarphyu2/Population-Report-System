package com.team14.report.city;

import com.team14.input.UserInput;

import java.util.Objects;

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
        Objects.requireNonNull(userInput, "User input cannot be null.");
        this.topN = userInput.getTopN();
    }

    /**
     * Creates a top-N worldwide city report with a specified value.
     *
     * @param topN number of cities to display
     */
    public TopCitiesByWorldwideReport(int topN) {
        if (topN <= 0) {
            throw new IllegalArgumentException(
                    "Number of cities must be greater than 0."
            );
        }

        this.topN = topN;
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