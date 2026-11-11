package com.team14.report.population;

import com.team14.input.UserInput;
import com.team14.report.Validation;

/**
 * The population of a specified city.
 */
public final class CityPopulationReport extends TotalPopulationReport {

    private final String city;

    /**
     * Creates a city population report using user input.
     *
     * @param userInput source used to obtain the city
     */
    public CityPopulationReport(UserInput userInput) {
        this(Validation.requireInput(userInput).getCity());
    }

    /**
     * Creates a city population report with a specified city.
     *
     * @param city city to display
     */
    public CityPopulationReport(String city) {
        this.city = Validation.requireText(city, "City");
    }

    @Override
    public String title() {
        return "Population of " + city;
    }

    @Override
    protected String select() {
        return """
                SELECT ci.Name AS Name,
                       ci.Population AS Population
                FROM city ci
                """;
    }

    @Override
    protected String clauses() {
        return """
                WHERE ci.Name = ?
                """;
    }

    @Override
    protected Object[] parameters() {
        return new Object[]{city};
    }
}