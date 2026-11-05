package com.team14.report.capitalcity;

/**
 * All the capital cities in the world organised by largest population
 * to smallest.
 */
public final class AllCapitalCitiesInWorldReport extends CapitalCityReport {

    @Override
    public String title() {
        return "All Capital Cities in the World by Population";
    }

    @Override
    protected String clauses() {
        return "ORDER BY ci.Population DESC";
    }
}