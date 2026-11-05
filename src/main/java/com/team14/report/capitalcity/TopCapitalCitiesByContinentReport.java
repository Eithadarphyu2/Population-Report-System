package com.team14.report.capitalcity;

import com.team14.input.UserInput;
import com.team14.report.Validation;

/**
 * Generates a report showing the top N most populated capital cities
 * in a selected continent.
 * <p>
 * This report implements the Capital City Report requirement.
 */
public final class TopCapitalCitiesByContinentReport extends CapitalCityReport {

    private final String continent;
    private final int topN;

    public TopCapitalCitiesByContinentReport(UserInput userInput) {
        this(Validation.requireInput(userInput).getContinent(), userInput.getTopN());
    }

    public TopCapitalCitiesByContinentReport(String continent, int topN) {
        this.continent = Validation.requireText(continent, "Continent");
        this.topN = Validation.requirePositive(topN, "Number of capital cities");
    }

    @Override
    public String title() {
        return "Top " + topN + " Populated Capital Cities in " + continent;
    }

    @Override
    protected String clauses() {
        return """
                WHERE c.Continent = ?
                ORDER BY ci.Population DESC
                LIMIT ?
                """;
    }

    @Override
    protected Object[] parameters() {
        return new Object[]{continent, topN};
    }
}