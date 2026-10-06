package com.team14.report.country;

import com.team14.input.UserInput;

import java.util.Objects;

/**
 * Generates a report showing all countries in a selected continent,
 * ordered by population from highest to lowest.
 *
 * This report implements Country Report requirement 2.
 */
public final class CountriesByContinentReport extends CountryReport {

    private final String continent;

    /**
     * Creates a country-by-continent report using user input.
     *
     * @param userInput source used to obtain the continent
     */
    public CountriesByContinentReport(UserInput userInput) {
        Objects.requireNonNull(
                userInput,
                "User input cannot be null."
        );

        this.continent = userInput.getContinent();
    }

    /**
     * Creates a country-by-continent report with a specified continent.
     *
     * This constructor is useful for testing.
     *
     * @param continent continent to display
     */
    public CountriesByContinentReport(String continent) {
        if (continent == null || continent.isBlank()) {
            throw new IllegalArgumentException(
                    "Continent cannot be empty."
            );
        }

        this.continent = continent;
    }

    @Override
    public String title() {
        return "Countries in " + continent
                + " by Population";
    }

    @Override
    protected String clauses() {
        return """
                WHERE c.Continent = ?
                ORDER BY c.Population DESC
                """;
    }

    @Override
    protected Object[] parameters() {
        return new Object[]{continent};
    }
}