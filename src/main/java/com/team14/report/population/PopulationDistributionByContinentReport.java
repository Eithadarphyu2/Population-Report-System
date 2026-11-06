package com.team14.report.population;

/**
 * The population of people, people living in cities, and people not living
 * in cities in each continent.
 */
public final class PopulationDistributionByContinentReport
        extends PopulationDistributionReport {

    @Override
    public String title() {
        return "Population Distribution by Continent";
    }

    @Override
    protected String groupColumn() {
        return "c.Continent";
    }
}