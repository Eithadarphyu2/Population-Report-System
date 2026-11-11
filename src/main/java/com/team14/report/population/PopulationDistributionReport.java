package com.team14.report.population;

import com.team14.report.ReportDisplay.Alignment;
import com.team14.report.ReportDisplay.Column;
import com.team14.report.SqlReport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Base class for the population distribution reports (one row for each
 * continent, region or country).
 * <p>
 * It provides the shared SELECT, the columns and the row reading. Each row
 * shows the total population, the population living in cities and the
 * population not living in cities, with percentages. Query execution and
 * display are done by SqlReport. Subclasses only say what to group by.
 */
public abstract class PopulationDistributionReport extends SqlReport {

    /** Columns required by the population distribution reports. */
    private static final List<Column> COLUMNS = List.of(
            new Column("Name", Alignment.LEFT),
            new Column("Total Population", Alignment.RIGHT, true),
            new Column("Population in Cities", Alignment.RIGHT),
            new Column("Population Not in Cities", Alignment.RIGHT)
    );

    /**
     * Common SQL used by every distribution report. City populations are
     * added up for each country first, so a country population is never
     * counted once for every city. A few countries in the database have
     * more city population than country population, so the city total is
     * capped at the country population to avoid a negative "not in cities"
     * value.
     */
    private static final String SELECT_DISTRIBUTION = """
            SELECT %s AS Name,
                   SUM(c.Population) AS TotalPopulation,
                   SUM(LEAST(COALESCE(cp.CityPopulation, 0), c.Population))
                       AS CityPopulation
            FROM country c
            LEFT JOIN (
                SELECT CountryCode, SUM(Population) AS CityPopulation
                FROM city
                GROUP BY CountryCode
            ) cp ON cp.CountryCode = c.Code
            """;

    /**
     * Provides the column the report groups by. It is a fixed value chosen
     * by the subclass, never user input.
     *
     * @return the SQL column to group by, for example "c.Continent"
     */
    protected abstract String groupColumn();

    @Override
    protected String select() {
        return SELECT_DISTRIBUTION.formatted(groupColumn());
    }

    @Override
    protected String clauses() {
        return "GROUP BY " + groupColumn() + "\nORDER BY Name\n";
    }

    @Override
    protected List<Column> columns() {
        return COLUMNS;
    }

    @Override
    protected List<?> readRow(ResultSet rs) throws SQLException {

        long total = rs.getLong("TotalPopulation");
        long inCities = rs.getLong("CityPopulation");

        return Arrays.asList(
                rs.getString("Name"),
                total,
                withPercentage(inCities, total),
                withPercentage(total - inCities, total)
        );
    }

    /**
     * Formats a population with its percentage of the total.
     *
     * @param value population to show
     * @param total total population the percentage is based on
     * @return text such as "1,234 (45.67%)"
     */
    private static String withPercentage(long value, long total) {

        double percentage = total == 0 ? 0.0 : value * 100.0 / total;

        return String.format(Locale.US, "%,d (%.2f%%)", value, percentage);
    }
}