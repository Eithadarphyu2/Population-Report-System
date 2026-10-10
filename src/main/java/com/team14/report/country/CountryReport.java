package com.team14.report.country;

import com.team14.report.ReportDisplay.Alignment;
import com.team14.report.ReportDisplay.Column;
import com.team14.report.SqlReport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Base class for the country reports.
 *
 * It provides the country SELECT, columns and row reading. Query execution
 * and display are done by SqlReport. Subclasses only define the
 * report-specific SQL clauses and parameters.
 */
public abstract class CountryReport extends SqlReport {

    /** Columns required by all country reports. */
    private static final List<Column> COLUMNS = List.of(
            new Column("Code", Alignment.LEFT),
            new Column("Name", Alignment.LEFT),
            new Column("Continent", Alignment.LEFT),
            new Column("Region", Alignment.LEFT),
            Column.population(),
            new Column("Capital", Alignment.LEFT)
    );

    /** Common SQL used by every country report. */
    private static final String SELECT_COUNTRY = """
            SELECT c.Code, c.Name, c.Continent, c.Region,
                   c.Population, ci.Name AS Capital
            FROM country c
            LEFT JOIN city ci ON c.Capital = ci.ID
            """;

    @Override
    protected String select() {
        return SELECT_COUNTRY;
    }

    @Override
    protected List<Column> columns() {
        return COLUMNS;
    }

    @Override
    protected List<?> readRow(ResultSet rs) throws SQLException {
        return Arrays.asList(
                rs.getString("Code"),
                rs.getString("Name"),
                rs.getString("Continent"),
                rs.getString("Region"),
                rs.getLong("Population"),
                rs.getString("Capital")
        );
    }
}