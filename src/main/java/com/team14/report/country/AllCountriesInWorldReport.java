package com.team14.report.country;

/**
 * Requirement 1: all the countries in the world organized by largest
 * population to smallest.
 */
public final class AllCountriesInWorldReport extends CountryReport {

    @Override
    public String title() {
        return "All Countries in the World by Population";
    }

    @Override
    protected String clauses() {
        return "ORDER BY c.Population DESC";
    }
}