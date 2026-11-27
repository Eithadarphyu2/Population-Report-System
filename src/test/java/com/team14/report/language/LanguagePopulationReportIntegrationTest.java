package com.team14.report.language;

import com.team14.report.ReportOutput;
import org.junit.jupiter.api.BeforeAll;
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

    // Expected number of rows in the language population report
    private static final int EXPECTED_RECORD_COUNT = 5;

    // Five target languages expected in the language population report
    private static final Set<String> EXPECTED_LANGUAGES = Set.of(
            "Chinese",
            "English",
            "Hindi",
            "Spanish",
            "Arabic"
    );

    // Captured report output shared across read-only integration assertions
    private static ReportOutput output;

    // Executes the language report once against the database before running tests
    @BeforeAll
    static void setUpReportOutput() throws SQLException {
        output = ReportOutput.capture(new LanguagePopulationReport());
    }

    @Test
    @DisplayName("Language report output has the expected title")
    void languageReportHasExpectedTitle() {
        // Confirm the rendered report title matches the specification
        assertEquals(
                "Language Population Report",
                output.title(),
                "Expected report title to be 'Language Population Report'"
        );
    }

    @Test
    @DisplayName("Language report lists five total records")
    void languageReportListsFiveRecords() {
        // Confirm total record count matches the five tracked languages
        assertEquals(
                EXPECTED_RECORD_COUNT,
                output.totalRecords(),
                "Expected report to contain exactly 5 language records"
        );
    }

    @Test
    @DisplayName("Language report lists all five specified languages")
    void languageReportListsFiveLanguages() {
        // Collect rendered language names and verify exact set match
        Set<String> actualLanguages = new HashSet<>(output.column("Language"));

        assertEquals(
                EXPECTED_LANGUAGES,
                actualLanguages,
                "Expected report languages to match the five specified languages"
        );
    }

    @Test
    @DisplayName("Language report returns five speaker count rows")
    void languageReportReturnsFiveSpeakerCounts() {
        // Confirm every report row includes a formatted speaker count
        List<String> speakers = output.column("Number of Speakers");

        assertEquals(
                EXPECTED_RECORD_COUNT,
                speakers.size(),
                "Expected 5 speaker count entries in the report"
        );
    }

    @Test
    @DisplayName("Language report orders languages by speakers, largest first")
    void languageReportOrdersLanguagesBySpeakers() {
        // Verify adjacent rows are ordered from highest to lowest speaker count
        List<String> speakers = output.column("Number of Speakers");

        assertTrue(
                hasDescendingSpeakerCounts(speakers),
                "Languages must be ordered from largest to smallest number of speakers"
        );
    }

    // Returns true when every adjacent speaker count pair is in non-increasing order
    private static boolean hasDescendingSpeakerCounts(List<String> speakers) {
        for (int i = 0; i < speakers.size() - 1; i++) {
            long current = parsePopulation(speakers.get(i));
            long next = parsePopulation(speakers.get(i + 1));

            if (current < next) {
                return false;
            }
        }
        return true;
    }

    // Strips thousands commas and parses speaker count as a long
    private static long parsePopulation(String population) {
        return Long.parseLong(population.replace(",", ""));
    }
}