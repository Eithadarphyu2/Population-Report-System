package com.team14.report.language;

import com.team14.report.ReportOutput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for {@link LanguagePopulationReport} against the MySQL
 * "world" database.
 */
class LanguagePopulationReportIntegrationTest {

    // Five target languages expected in the language population report
    private static final Set<String> EXPECTED_LANGUAGES = Set.of(
            "Chinese",
            "English",
            "Hindi",
            "Spanish",
            "Arabic"
    );

    // Runs the language report and captures its formatted output
    private static ReportOutput run(LanguagePopulationReport report)
            throws SQLException {
        return ReportOutput.capture(report);
    }

    @Test
    @DisplayName("Language report lists all five specified languages")
    void languageReportListsFiveLanguages() throws SQLException {
        // Execute report and inspect title, row count, and language set
        ReportOutput output = run(new LanguagePopulationReport());

        assertEquals(
                "Language Population Report",
                output.title(),
                "Expected report title to be 'Language Population Report'"
        );

        assertEquals(
                5,
                output.totalRecords(),
                "Expected report to contain exactly 5 language records"
        );

        Set<String> actualLanguages =
                new HashSet<>(output.column("Language"));

        assertEquals(
                EXPECTED_LANGUAGES,
                actualLanguages,
                "Expected report languages to match the five specified languages"
        );
    }

    @Test
    @DisplayName("Language report orders languages by speakers, largest first")
    void languageReportOrdersLanguagesBySpeakers()
            throws SQLException {
        // Fetch speaker counts and verify descending order
        ReportOutput output = run(new LanguagePopulationReport());

        List<String> speakers =
                output.column("Number of Speakers");

        assertEquals(
                5,
                speakers.size(),
                "Expected 5 speaker count entries in the report"
        );

        // Compare each adjacent pair so earlier rows have >= speakers than later rows
        for (int i = 0; i < speakers.size() - 1; i++) {

            long current = parsePopulation(speakers.get(i));
            long next = parsePopulation(speakers.get(i + 1));

            assertTrue(
                    current >= next,
                    "Languages must be ordered from largest "
                            + "to smallest number of speakers"
            );
        }
    }

    // Strips thousands commas and parses speaker count as a long
    private static long parsePopulation(String population) {
        return Long.parseLong(population.replace(",", ""));
    }
}