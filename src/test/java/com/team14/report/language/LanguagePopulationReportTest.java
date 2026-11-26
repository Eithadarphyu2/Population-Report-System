package com.team14.report.language;

import com.team14.report.ReportDisplay.Alignment;
import com.team14.report.ReportDisplay.Column;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link LanguagePopulationReport} covering title, SQL clauses,
 * column definitions, and row formatting without requiring a database.
 */
class LanguagePopulationReportTest {

    @Test
    @DisplayName("Language report has the correct title")
    void languageReportHasCorrectTitle() {
        // Instantiate report and verify its display title
        LanguagePopulationReport report =
                new LanguagePopulationReport();

        assertEquals(
                "Language Population Report",
                report.title(),
                "Expected report title to be 'Language Population Report'"
        );
    }

    @Test
    @DisplayName("Language report uses the correct SQL clauses")
    void languageReportUsesCorrectClauses() {
        // Verify filter, grouping, and ordering clauses in the report SQL
        LanguagePopulationReport report =
                new LanguagePopulationReport();

        String clauses = report.clauses();

        assertTrue(
                clauses.contains("WHERE cl.Language IN"),
                "Expected SQL clauses to contain WHERE cl.Language IN filter"
        );
        assertTrue(
                clauses.contains("'Chinese'"),
                "Expected SQL clauses to filter for 'Chinese'"
        );
        assertTrue(
                clauses.contains("'English'"),
                "Expected SQL clauses to filter for 'English'"
        );
        assertTrue(
                clauses.contains("'Hindi'"),
                "Expected SQL clauses to filter for 'Hindi'"
        );
        assertTrue(
                clauses.contains("'Spanish'"),
                "Expected SQL clauses to filter for 'Spanish'"
        );
        assertTrue(
                clauses.contains("'Arabic'"),
                "Expected SQL clauses to filter for 'Arabic'"
        );

        assertTrue(
                clauses.contains("GROUP BY cl.Language"),
                "Expected SQL clauses to group by language"
        );
        assertTrue(
                clauses.contains("ORDER BY Speakers DESC"),
                "Expected SQL clauses to order by speakers descending"
        );
    }

    @Test
    @DisplayName("Language report has the correct column headings")
    void languageReportHasCorrectColumns() {
        // Check column headings and alignment configuration
        LanguagePopulationReport report =
                new LanguagePopulationReport();

        List<Column> columns = report.columns();

        assertEquals(
                List.of(
                        "Language",
                        "Number of Speakers",
                        "Percentage of World Population"
                ),
                columns.stream()
                        .map(Column::heading)
                        .toList(),
                "Expected column headings to match language report specification"
        );

        assertEquals(
                Alignment.LEFT,
                columns.get(0).alignment(),
                "Expected Language column to be left-aligned"
        );
        assertEquals(
                Alignment.RIGHT,
                columns.get(1).alignment(),
                "Expected Number of Speakers column to be right-aligned"
        );
        assertEquals(
                Alignment.RIGHT,
                columns.get(2).alignment(),
                "Expected Percentage of World Population column to be right-aligned"
        );
    }

    @Test
    @DisplayName("Language report formats percentage to two decimal places")
    void languageReportFormatsPercentageCorrectly()
            throws SQLException {

        LanguagePopulationReport report =
                new LanguagePopulationReport();

        // Create a dynamic proxy ResultSet returning mock values for a single row
        ResultSet resultSet = (ResultSet) Proxy.newProxyInstance(
                ResultSet.class.getClassLoader(),
                new Class<?>[]{ResultSet.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getString" -> "English";
                    case "getLong" -> 123456L;
                    case "getDouble" -> 12.3456;
                    default -> null;
                }
        );

        List<?> row = report.readRow(resultSet);

        // Verify percentage is rounded/formatted to two decimal places with '%'
        assertEquals(
                List.of("English", 123456L, "12.35%"),
                row,
                "Expected row values with percentage formatted to two decimal places"
        );
    }
}