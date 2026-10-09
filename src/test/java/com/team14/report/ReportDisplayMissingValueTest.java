package com.team14.report;

import com.team14.report.ReportDisplay.Alignment;
import com.team14.report.ReportDisplay.Column;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the text shown instead of missing values.
 * They need no database.
 */
class ReportDisplayMissingValueTest {

    private static final Column DISTRICT =
            new Column("District", Alignment.LEFT);

    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream captured = new ByteArrayOutputStream();

    @BeforeEach
    void captureOutput() {
        System.setOut(new PrintStream(captured));
    }

    @AfterEach
    void restoreOutput() {
        System.setOut(originalOut);
    }

    /**
     * Displays a one-column, one-row report and returns what was printed.
     */
    private String render(Column column, Object value) {
        captured.reset();

        ReportDisplay.displayReport(
                "Test",
                List.of(column),
                List.of(Arrays.asList(value))
        );

        return captured.toString();
    }

    private void assertShows(Column column, Object value, String expected) {
        assertTrue(
                render(column, value).contains(expected),
                "Expected '" + expected + "' for value: " + value
        );
    }

    private void assertDoesNotShow(Column column, Object value, String text) {
        assertFalse(
                render(column, value).contains(text),
                "Did not expect '" + text + "' for value: " + value
        );
    }

    @Test
    @DisplayName("A null or blank text shows 'No [heading] recorded'")
    void missingTextIsDescribed() {
        assertShows(DISTRICT, null, "No district recorded");
        assertShows(DISTRICT, "", "No district recorded");
        assertShows(DISTRICT, "   ", "No district recorded");
    }

    @Test
    @DisplayName("A text that is only a dash shows 'No [heading] recorded'")
    void dashPlaceholderIsDescribed() {
        assertShows(DISTRICT, "-", "No district recorded");
        assertShows(DISTRICT, "\u2013", "No district recorded");
        assertShows(DISTRICT, "\u2014", "No district recorded");
    }

    @Test
    @DisplayName("A text that merely contains a dash is kept")
    void textWithDashIsKept() {
        assertShows(DISTRICT, "Saint-Denis", "Saint-Denis");
        assertDoesNotShow(DISTRICT, "Saint-Denis", "No district recorded");
    }

    @Test
    @DisplayName("A population of 0 shows 'No population recorded'")
    void zeroPopulationIsDescribed() {
        assertShows(Column.population(), 0L, "No population recorded");
    }

    @Test
    @DisplayName("A real population is formatted as a number")
    void realPopulationIsKept() {
        assertShows(Column.population(), 1500L, "1,500");
        assertDoesNotShow(Column.population(), 1500L, "No population recorded");
    }

    @Test
    @DisplayName("0 stays 0 in a column that is not a population column")
    void zeroIsKeptInOtherColumns() {
        assertDoesNotShow(
                new Column("Percentage", Alignment.RIGHT),
                0L,
                "No percentage recorded"
        );
    }
}