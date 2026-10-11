package com.team14.report.city;

import com.team14.input.UserInput;
import com.team14.report.Validation;

/**
 * Generates a report showing the top N most populated cities
 * in a selected district.
 *
 * This report implements City Report requirement 11.
 */
public final class TopCitiesByDistrictReport extends CityReport {

    private final String district;
    private final int topN;

    /**
     * Creates a report using user input.
     *
     * @param userInput source used to obtain the district and N
     */
    public TopCitiesByDistrictReport(UserInput userInput) {
        this(Validation.requireInput(userInput).getDistrict(),
                userInput.getTopN());
    }

    /**
     * Creates a report with a specified district and number of cities.
     *
     * This constructor is useful for testing and application configuration.
     *
     * @param district district to display
     * @param topN number of cities to display
     */
    public TopCitiesByDistrictReport(
            String district,
            int topN) {

        this.district = Validation.requireText(district, "District");
        this.topN = Validation.requirePositive(topN, "Number of cities");
    }

    @Override
    public String title() {
        return "Top " + topN
                + " Populated Cities in "
                + district;
    }

    @Override
    protected String clauses() {
        return """
                WHERE ci.District = ?
                ORDER BY ci.Population DESC
                LIMIT ?
                """;
    }

    @Override
    protected Object[] parameters() {
        return new Object[]{
                district,
                topN
        };
    }
}
