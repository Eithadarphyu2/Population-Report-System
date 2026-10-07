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

    private String display(List<Column> columns, List<?> row) {
        ReportDisplay.displayReport("Test", columns, List.of(row));
        return captured.toString();
    }

    @Test
    @DisplayName("A null or blank text value shows 'No [heading] recorded'")
    void missingTextIsDescribed() {

        String output = display(
                List.of(
                        new Column("Capital", Alignment.LEFT),
                        new Column("Region", Alignment.LEFT)
                ),
                Arrays.asList(null, "  ")
        );

        assertTrue(output.contains("No capital recorded"));
        assertTrue(output.contains("No region recorded"));
    }

    @Test
    @DisplayName("A population of 0 shows 'No population recorded'")
    void zeroPopulationIsDescribed() {

        String output = display(
                List.of(Column.population()),
                List.of(0L)
        );

        assertTrue(output.contains("No population recorded"));
    }

    @Test
    @DisplayName("A real population is formatted as a number")
    void realPopulationIsKept() {

        String output = display(
                List.of(Column.population()),
                List.of(1500L)
        );

        assertTrue(output.contains("1,500"));
        assertFalse(output.contains("No population recorded"));
    }

    @Test
    @DisplayName("0 stays 0 in a column that is not a population column")
    void zeroIsKeptInOtherColumns() {

        String output = display(
                List.of(new Column("Percentage", Alignment.RIGHT)),
                List.of(0L)
        );

        assertFalse(output.contains("No percentage recorded"));
    }
}