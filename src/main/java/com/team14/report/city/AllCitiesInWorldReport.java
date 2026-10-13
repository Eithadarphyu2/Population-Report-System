package com.team14.report.city;

/**
 * All the cities in the world organized by largest population to smallest.
 */
public final class AllCitiesInWorldReport extends CityReport {

    @Override
    public String title() {
        return "All Cities in the World by Population";
    }

    @Override
    protected String clauses() {
        return "ORDER BY ci.Population DESC";
    }
}