package com.team14.report.population;

import com.team14.input.UserInput;
import com.team14.report.Validation;

/**
 * The population of a specified country.
 */
public final class CountryPopulationReport extends TotalPopulationReport {

    private final String country;

    /**
     * Creates a country population report using user input.
     *
     * @param userInput source used to obtain the country
     */
    public CountryPopulationReport(UserInput userInput) {
        this(Validation.requireInput(userInput).getCountry());
    }

    /**
     * Creates a country population report with a specified country.
     *
     * @param country country to display
     */
    public CountryPopulationReport(String country) {
        this.country = Validation.requireText(country, "Country");
    }

    @Override
    public String title() {
        return "Population of " + country;
    }

    @Override
    protected String select() {
        return """
                SELECT c.Name AS Name,
                       c.Population AS Population
                FROM country c
                """;
    }

    @Override
    protected String clauses() {
        return """
                WHERE c.Name = ?
                """;
    }

    @Override
    protected Object[] parameters() {
        return new Object[]{country};
    }
}