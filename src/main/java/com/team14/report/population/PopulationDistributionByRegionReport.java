package com.team14.report.population;

/**
 * Generates a report showing population distribution
 * for each region.
 */
public final class PopulationDistributionByRegionReport
        extends PopulationDistributionReport {

    @Override
    public String title() {
        return "Population Distribution by Region";
    }

    @Override
    protected String groupColumn() {
        return "c.Region";
    }
}
