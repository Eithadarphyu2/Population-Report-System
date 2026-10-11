package com.team14.report.city;

import com.team14.input.UserInput;
import com.team14.report.Validation;

/**
 * Generates a report showing all cities in a selected country,
 * ordered by population from highest to lowest.
 */
public final class CitiesByCountryReport extends CityReport {

    private final String country;

    /**
     * Creates a cities-by-country report using user input.
     *
     * @param userInput source used to obtain the country
     */
    public CitiesByCountryReport(UserInput userInput) {
        this(Validation.requireInput(userInput).getCountry());
    }

    /**
     * Creates a cities-by-country report with a specified country.
     *
     * @param country country name or code to display
     */
    public CitiesByCountryReport(String country) {
        this.country = Validation.requireText(country, "Country");
    }

    @Override
    public String title() {
        return "Cities in " + country + " by Population";
    }

    @Override
    protected String clauses() {
        return """
                WHERE c.Name = ? OR c.Code = ?
                ORDER BY ci.Population DESC
                """;
    }

    @Override
    protected Object[] parameters() {
        return new Object[]{ country, country };
    }
}
