package com.team14.report.city;

import com.team14.input.UserInput;

import java.util.Objects;

/**
 * Generates a report showing all cities in a selected district,
 * ordered by population from highest to lowest.
 */
public final class CitiesByDistrictReport extends CityReport {

    private final String district;

    public CitiesByDistrictReport(UserInput userInput) {
        Objects.requireNonNull(userInput, "User input cannot be null.");
        this.district = userInput.getDistrict();
    }

    public CitiesByDistrictReport(String district) {
        if (district == null || district.isBlank()) {
            throw new IllegalArgumentException("District cannot be empty.");
        }
        this.district = district;
    }

    @Override
    public String title() {
        return "Cities in " + district + " District by Population";
    }

    @Override
    protected String clauses() {
        return """
                WHERE ci.District = ?
                ORDER BY ci.Population DESC
                """;
    }

    @Override
    protected Object[] parameters() {
        return new Object[]{ district };
    }
}