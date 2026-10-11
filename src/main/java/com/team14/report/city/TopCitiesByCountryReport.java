package com.team14.report.city;

import com.team14.input.UserInput;
import com.team14.report.Validation;

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
        this(Validation.requireInput(userInput).getCountry(),
                userInput.getTopN());
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

        this.country = Validation.requireText(country, "Country");
        this.topN = Validation.requirePositive(topN, "Number of cities");
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
