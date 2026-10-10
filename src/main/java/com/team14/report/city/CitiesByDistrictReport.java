package com.team14.report.city;

import com.team14.input.UserInput;
import com.team14.report.Validation;

/**
 * Generates a report showing all cities in a selected district,
 * ordered by population from highest to lowest.
 */
public final class CitiesByDistrictReport extends CityReport {

    private final String district;

    /**
     * Creates a cities-by-district report using user input.
     *
     * @param userInput source used to obtain the district
     */
    public CitiesByDistrictReport(UserInput userInput) {
        this(Validation.requireInput(userInput).getDistrict());
    }

    /**
     * Creates a cities-by-district report with a specified district.
     *
     * @param district district to display
     */
    public CitiesByDistrictReport(String district) {
        this.district = Validation.requireText(district, "District");
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
