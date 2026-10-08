package com.team14.report.city;

import com.team14.input.UserInput;

import java.util.Objects;

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
        Objects.requireNonNull(
                userInput,
                "User input cannot be null."
        );

        this.district = userInput.getDistrict();
        this.topN = userInput.getTopN();
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

        if (district == null || district.isBlank()) {
            throw new IllegalArgumentException(
                    "District cannot be empty."
            );
        }

        if (topN <= 0) {
            throw new IllegalArgumentException(
                    "Number of cities must be greater than 0."
            );
        }

        this.district = district;
        this.topN = topN;
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