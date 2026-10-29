package com.team14.report.capitalcity;

import com.team14.report.ReportDisplay.Alignment;
import com.team14.report.ReportDisplay.Column;
import com.team14.report.SqlReport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Base class for the capital city reports.
 * It provides the capital city SELECT, columns and row reading.
 * Query execution and display are done by SqlReport. Subclasses only
 * define the report-specific SQL clauses and parameters.
 */
public abstract class CapitalCityReport extends SqlReport {

    /** Columns required by all capital city reports. */
    private static final List<Column> COLUMNS = List.of(
            new Column("Name", Alignment.LEFT),
            new Column("Country", Alignment.LEFT),
            Column.population()
    );

    /** Common SQL used by every capital city report. */
    private static final String SELECT_CAPITAL_CITY = """
            SELECT ci.Name, c.Name AS Country, ci.Population
            FROM country c
            JOIN city ci ON c.Capital = ci.ID
            """;

    @Override
    protected String select() {
        return SELECT_CAPITAL_CITY;
    }

    @Override
    protected List<Column> columns() {
        return COLUMNS;
    }

    @Override
    protected List<?> readRow(ResultSet rs) throws SQLException {
        return Arrays.asList(
                rs.getString("Name"),
                rs.getString("Country"),
                rs.getLong("Population")
        );
    }
}