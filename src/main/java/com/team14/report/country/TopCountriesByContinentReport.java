package com.team14.report.country;

import com.team14.input.UserInput;

import java.util.Objects;

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
        Objects.requireNonNull(
                userInput,
                "User input cannot be null."
        );

        this.continent = userInput.getContinent();
        this.topN = userInput.getTopN();
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

        if (continent == null || continent.isBlank()) {
            throw new IllegalArgumentException(
                    "Continent cannot be empty."
            );
        }

        if (topN <= 0) {
            throw new IllegalArgumentException(
                    "Number of countries must be greater than 0."
            );
        }

        this.continent = continent;
        this.topN = topN;
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
