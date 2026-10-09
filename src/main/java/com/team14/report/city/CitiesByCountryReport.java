package com.team14.report.city;

import com.team14.input.UserInput;

import java.util.Objects;

/**
 * Generates a report showing all cities in a selected country,
 * ordered by population from highest to lowest.
 */
public final class CitiesByCountryReport extends CityReport {

    private final String country;

    public CitiesByCountryReport(UserInput userInput) {
        Objects.requireNonNull(userInput, "User input cannot be null.");
        this.country = userInput.getCountry();
    }

    public CitiesByCountryReport(String country) {
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("Country cannot be empty.");
        }
        this.country = country;
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