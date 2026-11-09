package com.team14.report.population;

/**
 * The population of people, people living in cities, and people not living
 * in cities in each country.
 */
public final class PopulationDistributionByCountryReport
        extends PopulationDistributionReport {

    @Override
    public String title() {
        return "Population Distribution by Country";
    }

    @Override
    protected String groupColumn() {
        return "c.Name";
    }
}