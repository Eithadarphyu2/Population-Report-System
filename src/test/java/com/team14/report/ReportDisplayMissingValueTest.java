package com.team14.report;

import com.team14.report.ReportDisplay.Alignment;
import com.team14.report.ReportDisplay.Column;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the text shown instead of missing values.
 * They need no database.
 */
class ReportDisplayMissingValueTest {

    // Sample left-aligned text column used for missing-value checks
    private static final Column DISTRICT = new Column("District", Alignment.LEFT);

    // Save original System.out so it can be restored after each test
    private final PrintStream originalOut = System.out;

    // Buffer to capture console output written to System.out
    private final ByteArrayOutputStream captured = new ByteArrayOutputStream();

    /**
     * Redirects System.out to a UTF-8 PrintStream with auto-flush before each test.
     */
    @BeforeEach
    void captureOutput() {
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
    }

    /**
     * Restores the original System.out after each test.
     */
    @AfterEach
    void restoreOutput() {
        System.setOut(originalOut);
    }

    /**
     * Clears previous output, renders a single-column, single-row report,
     * and returns the captured output decoded as UTF-8.
     */
    private String render(Column column, Object value) {
        captured.reset();

        ReportDisplay.displayReport("Test", List.of(column), List.of(Arrays.asList(value)));

        return captured.toString(StandardCharsets.UTF_8);
    }

    // Helper asserting that the rendered report contains the expected text
    private void assertShows(Column column, Object value, String expected) {
        assertTrue(render(column, value).contains(expected), "Expected '" + expected + "' for value: " + value);
    }

    // Helper asserting that the rendered report does not contain the given text
    private void assertDoesNotShow(Column column, Object value, String text) {
        assertFalse(render(column, value).contains(text), "Did not expect '" + text + "' for value: " + value);
    }

    @Test
    @DisplayName("A null or blank text shows 'No [heading] recorded'")
    void missingTextIsDescribed() {
        // Null, empty, and whitespace-only strings should all be treated as missing
        assertShows(DISTRICT, null, "No district recorded");
        assertShows(DISTRICT, "", "No district recorded");
        assertShows(DISTRICT, "   ", "No district recorded");
    }

    @Test
    @DisplayName("A text that is only a dash shows 'No [heading] recorded'")
    void dashPlaceholderIsDescribed() {
        // Hyphen, en-dash (\u2013), and em-dash (\u2014) placeholders show missing text
        assertShows(DISTRICT, "-", "No district recorded");
        assertShows(DISTRICT, "\u2013", "No district recorded");
        assertShows(DISTRICT, "\u2014", "No district recorded");
    }

    @Test
    @DisplayName("A text that merely contains a dash is kept")
    void textWithDashIsKept() {
        // Valid names containing a dash (such as "Saint-Denis") should not be replaced
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
        // Non-zero population values are formatted with grouping commas
        assertShows(Column.population(), 1500L, "1,500");
        assertDoesNotShow(Column.population(), 1500L, "No population recorded");
    }

    @Test
    @DisplayName("0 stays 0 in a column that is not a population column")
    void zeroIsKeptInOtherColumns() {
        // Zero in non-population columns should not display "No percentage recorded"
        assertDoesNotShow(new Column("Percentage", Alignment.RIGHT), 0L, "No percentage recorded");
    }
}