package com.team14.report.language;

import com.team14.report.SqlReport;
import com.team14.report.ReportDisplay.Alignment;
import com.team14.report.ReportDisplay.Column;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Generates a report showing the number of people
 * speaking the five specified languages and their
 * percentages of the world population.
 */
public final class LanguagePopulationReport extends SqlReport {

    private static final List<Column> COLUMNS = List.of(
            new Column("Language", Alignment.LEFT),
            new Column("Number of Speakers", Alignment.RIGHT, true),
            new Column("Percentage of World Population", Alignment.RIGHT)
    );

    @Override
    public String title() {
        return "Language Population Report";
    }

    /**
     * Provides the common SELECT and JOIN statements.
     *
     * @return SQL query for language population data
     */
    @Override
    protected String select() {
        return """
                SELECT
                    cl.Language AS Language,
                    ROUND(
                        SUM(cl.Percentage * c.Population / 100)
                    ) AS Speakers,
                    SUM(cl.Percentage * c.Population / 100)
                        * 100 / (
                            SELECT SUM(Population)
                            FROM country
                        ) AS WorldPercentage
                FROM countrylanguage cl
                JOIN country c ON cl.CountryCode = c.Code
                """;
    }

    /**
     * Provides language filtering, grouping and sorting.
     *
     * @return SQL clauses for the report
     */
    @Override
    protected String clauses() {
        return """
                WHERE cl.Language IN (
                    'Chinese',
                    'English',
                    'Hindi',
                    'Spanish',
                    'Arabic'
                )
                GROUP BY cl.Language
                ORDER BY Speakers DESC
                """;
    }

    /**
     * Provides the report table columns.
     *
     * @return report columns
     */
    @Override
    protected List<Column> columns() {
        return COLUMNS;
    }

    /**
     * Reads the current language result into display values.
     *
     * @param rs result set positioned on the current row
     * @return language population information
     * @throws SQLException if a database value cannot be read
     */
    @Override
    protected List<?> readRow(ResultSet rs) throws SQLException {
        return List.of(
                rs.getString("Language"),
                rs.getLong("Speakers"),
                String.format("%.2f%%", rs.getDouble("WorldPercentage"))
        );
    }
}
