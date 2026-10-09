 package com.team14.report.city;

import com.team14.input.UserInput;

import java.util.Objects;

/**
 * Generates a report showing the top N most populated cities
 * in a selected country.
 *
 * This report implements the City Report requirement.
 */
public final class TopCitiesByCountryReport extends CityReport {

    private final String country;
    private final int topN;

    /**
     * Creates a report using user input.
     *
     * @param userInput source used to obtain the country and N
     */
    public TopCitiesByCountryReport(UserInput userInput) {
        Objects.requireNonNull(
                userInput,
                "User input cannot be null."
        );

        this.country = userInput.getCountry();
        this.topN = userInput.getTopN();
    }

    /**
     * Creates a report with a specified country and number of cities.
     *
     * @param country country to display
     * @param topN number of cities to display
     */
    public TopCitiesByCountryReport(
            String country,
            int topN) {

        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException(
                    "Country cannot be empty."
            );
        }

        if (topN <= 0) {
            throw new IllegalArgumentException(
                    "Number of cities must be greater than 0."
            );
        }

        this.country = country;
        this.topN = topN;
    }

    @Override
    public String title() {
        return "Top " + topN
                + " Populated Cities in "
                + country;
    }

    @Override
    protected String clauses() {
        return """
                WHERE c.Name = ?
                ORDER BY ci.Population DESC
                LIMIT ?
                """;
    }

    @Override
    protected Object[] parameters() {
        return new Object[]{
                country,
                topN
        };
    }
}