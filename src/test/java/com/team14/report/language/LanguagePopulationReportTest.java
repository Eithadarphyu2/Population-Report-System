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

class LanguagePopulationReportTest {

    @Test
    @DisplayName("Language report has the correct title")
    void languageReportHasCorrectTitle() {
        LanguagePopulationReport report =
                new LanguagePopulationReport();

        assertEquals(
                "Language Population Report",
                report.title()
        );
    }

    @Test
    @DisplayName("Language report uses the correct SQL clauses")
    void languageReportUsesCorrectClauses() {
        LanguagePopulationReport report =
                new LanguagePopulationReport();

        String clauses = report.clauses();

        assertTrue(clauses.contains("WHERE cl.Language IN"));
        assertTrue(clauses.contains("'Chinese'"));
        assertTrue(clauses.contains("'English'"));
        assertTrue(clauses.contains("'Hindi'"));
        assertTrue(clauses.contains("'Spanish'"));
        assertTrue(clauses.contains("'Arabic'"));

        assertTrue(clauses.contains("GROUP BY cl.Language"));
        assertTrue(clauses.contains("ORDER BY Speakers DESC"));
    }

    @Test
    @DisplayName("Language report has the correct column headings")
    void languageReportHasCorrectColumns() {
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
                        .toList()
        );

        assertEquals(Alignment.LEFT, columns.get(0).alignment());
        assertEquals(Alignment.RIGHT, columns.get(1).alignment());
        assertEquals(Alignment.RIGHT, columns.get(2).alignment());
    }

    @Test
    @DisplayName("Language report formats percentage to two decimal places")
    void languageReportFormatsPercentageCorrectly()
            throws SQLException {

        LanguagePopulationReport report =
                new LanguagePopulationReport();

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

        assertEquals(
                List.of("English", 123456L, "12.35%"),
                row
        );
    }
}