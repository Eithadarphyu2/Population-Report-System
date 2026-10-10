package com.team14.report.city;

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
 * Base class for the city reports.
 *
 * The common SELECT statement, city columns, database query execution,
 * result mapping and report display are handled here. Subclasses only
 * define the report-specific SQL clauses and parameters.
 */
public abstract class CityReport implements Report {

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

    /**
     * Provides the report-specific SQL clauses.
     *
     * Examples:
     * WHERE c.Continent = ? ORDER BY ci.Population DESC
     *
     * or:
     * ORDER BY ci.Population DESC LIMIT ?
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
     * Executes the city report and displays the results.
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
                SELECT_CITY + reportClauses,
                rs -> Arrays.asList(
                        rs.getString("Name"),
                        rs.getString("Country"),
                        rs.getString("District"),
                        rs.getLong("Population")
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