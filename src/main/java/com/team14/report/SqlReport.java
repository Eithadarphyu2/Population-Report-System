package com.team14.report;

import com.team14.database.query.DatabaseQuery;
import com.team14.report.ReportDisplay.Column;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * Base class for every report that runs one SQL query and prints a table.
 *
 * The shared steps (build the query, run it, read each row, display the
 * table) are done once here in run(). Subclasses only provide their own
 * SELECT, columns, row reading and report-specific SQL clauses.
 */
public abstract class SqlReport implements Report {

    /**
     * Provides the common SELECT ... FROM ... JOIN part of the SQL.
     *
     * @return SQL text that the report clauses are appended to
     */
    protected abstract String select();

    /**
     * Provides the column definitions of the report table.
     *
     * @return the report columns
     */
    protected abstract List<Column> columns();

    /**
     * Reads the current result-set row into display values.
     *
     * @param rs result set positioned on the row to read
     * @return the row values, in the same order as columns()
     * @throws SQLException if a value cannot be read
     */
    protected abstract List<?> readRow(ResultSet rs) throws SQLException;

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
     * Executes the report query and displays the results.
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
                select() + reportClauses,
                rs -> readRow(rs),
                reportParameters
        );

        ReportDisplay.displayReport(
                title(),
                columns(),
                rows
        );
    }
}