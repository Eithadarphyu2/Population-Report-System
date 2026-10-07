package com.team14.report.country;

import com.team14.database.query.DatabaseQuery;
import com.team14.report.Report;
import com.team14.report.ReportDisplay;
import com.team14.report.ReportDisplay.Alignment;
import com.team14.report.ReportDisplay.Column;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Base class for the country reports.
 *
 * The common SELECT statement, country columns, database query execution,
 * result mapping and report display are handled here. Subclasses only
 * define the report-specific SQL clauses and parameters.
 */
public abstract class CountryReport implements Report {

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

    /**
     * Provides the report-specific SQL clauses.
     *
     * @return SQL clauses appended to the common SELECT statement
     */
    protected abstract String clauses();

    /**
     * Provides values for the placeholders in the SQL clauses.
     *
     * @return parameter values in the same order as the SQL placeholders
     */
    protected Object[] parameters() {
        return new Object[0];
    }

    /**
     * Executes the country report and displays the results.
     *
     * @throws SQLException if the database query fails
     */
    @Override
    public final void run() throws SQLException {

        String reportClauses = Objects.requireNonNull(
                clauses(),
                "Report SQL clauses cannot be null."
        );

        Object[] reportParameters = Objects.requireNonNullElse(
                parameters(),
                new Object[0]
        );

        List<List<?>> rows = DatabaseQuery.executeQuery(
                SELECT_COUNTRY + reportClauses,
                rs -> Arrays.asList(
                        rs.getString("Code"),
                        rs.getString("Name"),
                        rs.getString("Continent"),
                        rs.getString("Region"),
                        rs.getLong("Population"),
                        rs.getString("Capital")
                ),
                reportParameters
        );

        ReportDisplay.displayReport(
                title(),
                COLUMNS,
                rows
        );
    }
}