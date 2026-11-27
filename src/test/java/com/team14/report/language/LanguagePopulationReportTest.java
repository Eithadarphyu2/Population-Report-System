package com.team14.report.language;

import com.team14.report.ReportDisplay.Alignment;
import com.team14.report.ReportDisplay.Column;
import org.junit.jupiter.api.BeforeEach;
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

    // Report instance under test, initialized fresh before each test case
    private LanguagePopulationReport report;

    @BeforeEach
    void setUp() {
        report = new LanguagePopulationReport();
    }

    @Test
    @DisplayName("Language report has the correct title")
    void languageReportHasCorrectTitle() {
        // Verify the report exposes its expected display title
        assertEquals(
                "Language Population Report",
                report.title(),
                "Expected report title to be 'Language Population Report'"
        );
    }

    @Test
    @DisplayName("Language report filters using WHERE cl.Language IN")
    void languageReportHasLanguageInFilter() {
        // Verify the SQL WHERE clause filters on cl.Language
        assertTrue(
                report.clauses().contains("WHERE cl.Language IN"),
                "Expected SQL clauses to contain WHERE cl.Language IN filter"
        );
    }

    @Test
    @DisplayName("Language report includes Chinese in the language filter")
    void languageReportFiltersChinese() {
        // Verify 'Chinese' is included among the filtered languages
        assertTrue(
                report.clauses().contains("'Chinese'"),
                "Expected SQL clauses to filter for 'Chinese'"
        );
    }

    @Test
    @DisplayName("Language report includes English in the language filter")
    void languageReportFiltersEnglish() {
        // Verify 'English' is included among the filtered languages
        assertTrue(
                report.clauses().contains("'English'"),
                "Expected SQL clauses to filter for 'English'"
        );
    }

    @Test
    @DisplayName("Language report includes Hindi in the language filter")
    void languageReportFiltersHindi() {
        // Verify 'Hindi' is included among the filtered languages
        assertTrue(
                report.clauses().contains("'Hindi'"),
                "Expected SQL clauses to filter for 'Hindi'"
        );
    }

    @Test
    @DisplayName("Language report includes Spanish in the language filter")
    void languageReportFiltersSpanish() {
        // Verify 'Spanish' is included among the filtered languages
        assertTrue(
                report.clauses().contains("'Spanish'"),
                "Expected SQL clauses to filter for 'Spanish'"
        );
    }

    @Test
    @DisplayName("Language report includes Arabic in the language filter")
    void languageReportFiltersArabic() {
        // Verify 'Arabic' is included among the filtered languages
        assertTrue(
                report.clauses().contains("'Arabic'"),
                "Expected SQL clauses to filter for 'Arabic'"
        );
    }

    @Test
    @DisplayName("Language report groups results by cl.Language")
    void languageReportGroupsByLanguage() {
        // Verify speaker counts are grouped per language
        assertTrue(
                report.clauses().contains("GROUP BY cl.Language"),
                "Expected SQL clauses to group by language"
        );
    }

    @Test
    @DisplayName("Language report orders results by Speakers descending")
    void languageReportOrdersBySpeakersDesc() {
        // Verify results are ordered from highest to lowest speaker count
        assertTrue(
                report.clauses().contains("ORDER BY Speakers DESC"),
                "Expected SQL clauses to order by speakers descending"
        );
    }

    @Test
    @DisplayName("Language report has the correct column headings")
    void languageReportHasCorrectColumns() {
        // Verify column headings match the report specification in order
        List<String> headings = report.columns().stream()
                .map(Column::heading)
                .toList();

        assertEquals(
                List.of(
                        "Language",
                        "Number of Speakers",
                        "Percentage of World Population"
                ),
                headings,
                "Expected column headings to match language report specification"
        );
    }

    @Test
    @DisplayName("Language column is left-aligned")
    void languageColumnIsLeftAligned() {
        // Text column should be left-aligned
        assertEquals(
                Alignment.LEFT,
                report.columns().get(0).alignment(),
                "Expected Language column to be left-aligned"
        );
    }

    @Test
    @DisplayName("Number of Speakers column is right-aligned")
    void speakersColumnIsRightAligned() {
        // Numeric speaker count column should be right-aligned
        assertEquals(
                Alignment.RIGHT,
                report.columns().get(1).alignment(),
                "Expected Number of Speakers column to be right-aligned"
        );
    }

    @Test
    @DisplayName("Percentage of World Population column is right-aligned")
    void percentageColumnIsRightAligned() {
        // Numeric percentage column should be right-aligned
        assertEquals(
                Alignment.RIGHT,
                report.columns().get(2).alignment(),
                "Expected Percentage of World Population column to be right-aligned"
        );
    }

    @Test
    @DisplayName("Language report formats percentage to two decimal places")
    void languageReportFormatsPercentageCorrectly() throws SQLException {
        // Create a dynamic proxy ResultSet returning deterministic mock values for a single row
        ResultSet resultSet = (ResultSet) Proxy.newProxyInstance(
                ResultSet.class.getClassLoader(),
                new Class<?>[]{ResultSet.class},
                (proxy, method, args) -> {
                    String methodName = method.getName();
                    Object result;

                    if ("getString".equals(methodName)) {
                        result = "English";
                    } else if ("getLong".equals(methodName)) {
                        result = 123456L;
                    } else if ("getDouble".equals(methodName)) {
                        result = 12.3456;
                    } else {
                        result = "";
                    }

                    return result;
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