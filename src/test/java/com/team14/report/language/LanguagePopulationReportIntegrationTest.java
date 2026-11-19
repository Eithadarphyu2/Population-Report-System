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

class LanguagePopulationReportIntegrationTest {

    private static final Set<String> EXPECTED_LANGUAGES = Set.of(
            "Chinese",
            "English",
            "Hindi",
            "Spanish",
            "Arabic"
    );

    private static ReportOutput run(LanguagePopulationReport report)
            throws SQLException {
        return ReportOutput.capture(report);
    }

    @Test
    @DisplayName("Language report lists all five specified languages")
    void languageReportListsFiveLanguages() throws SQLException {

        ReportOutput output = run(new LanguagePopulationReport());

        assertEquals(
                "Language Population Report",
                output.title()
        );

        assertEquals(5, output.totalRecords());

        Set<String> actualLanguages =
                new HashSet<>(output.column("Language"));

        assertEquals(EXPECTED_LANGUAGES, actualLanguages);
    }

    @Test
    @DisplayName("Language report orders languages by speakers, largest first")
    void languageReportOrdersLanguagesBySpeakers()
            throws SQLException {

        ReportOutput output = run(new LanguagePopulationReport());

        List<String> speakers =
                output.column("Number of Speakers");

        assertEquals(5, speakers.size());

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

    private static long parsePopulation(String population) {
        return Long.parseLong(population.replace(",", ""));
    }
}
