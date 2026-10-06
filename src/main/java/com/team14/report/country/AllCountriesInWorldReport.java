package com.team14.report.country;

/**
 * Requirement 1: all the countries in the world organised by largest
 * population to smallest.
 */
public final class AllCountriesInWorldReport extends CountryReport {

    @Override
    public String title() {
        return "All countries in the world by population";
    }

    @Override
    protected String clauses() {
        return "ORDER BY c.Population DESC";
    }
}