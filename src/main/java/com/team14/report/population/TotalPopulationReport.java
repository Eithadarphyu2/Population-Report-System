package com.team14.report.population;

import com.team14.report.ReportDisplay.Alignment;
import com.team14.report.ReportDisplay.Column;
import com.team14.report.SqlReport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Base class for the total population reports (the population of the world,
 * a continent, a region, a country, a district or a city).
 * <p>
 * It provides the two columns (Name and Population) and the row reading.
 * Query execution and display are done by SqlReport. Subclasses write a short
 * query that returns a Name and a Population.
 */
public abstract class TotalPopulationReport extends SqlReport {

    /** Columns required by the total population reports. */
    private static final List<Column> COLUMNS = List.of(
            new Column("Name", Alignment.LEFT),
            Column.population()
    );

    /**
     * Provides the report-specific SQL clauses. Reports that need no WHERE
     * clause use this empty default.
     *
     * @return SQL clauses appended to the SELECT statement
     */
    @Override
    protected String clauses() {
        return "";
    }

    @Override
    protected List<Column> columns() {
        return COLUMNS;
    }

    @Override
    protected List<?> readRow(ResultSet rs) throws SQLException {
        return Arrays.asList(
                rs.getString("Name"),
                rs.getLong("Population")
        );
    }
}
