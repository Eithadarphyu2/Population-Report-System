package com.team14.report.city;

import com.team14.report.ReportDisplay.Alignment;
import com.team14.report.ReportDisplay.Column;
import com.team14.report.SqlReport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Base class for the city reports.
 *
 * It provides the city SELECT, columns and row reading. Query execution
 * and display are done by SqlReport. Subclasses only define the
 * report-specific SQL clauses and parameters.
 */
public abstract class CityReport extends SqlReport {

    /** Columns required by all city reports. */
    private static final List<Column> COLUMNS = List.of(
            new Column("Name", Alignment.LEFT),
            new Column("Country", Alignment.LEFT),
            new Column("District", Alignment.LEFT),
            Column.population()
    );

    /** Common SQL used by every city report. */
    private static final String SELECT_CITY = """
            SELECT ci.Name, c.Name AS Country,
                   ci.District, ci.Population
            FROM city ci
            JOIN country c ON ci.CountryCode = c.Code
            """;

    @Override
    protected String select() {
        return SELECT_CITY;
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
                rs.getString("District"),
                rs.getLong("Population")
        );
    }
}