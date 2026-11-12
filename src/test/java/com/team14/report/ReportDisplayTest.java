package com.team14.report;

import com.team14.report.ReportDisplay.Alignment;
import com.team14.report.ReportDisplay.Column;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for ReportDisplay: number formatting, alignment, layout and
 * input validation. They need no database.
 * The text shown for missing values is tested in
 * ReportDisplayMissingValueTest.
 */
class ReportDisplayTest {

    private static final Column NAME = new Column("Name", Alignment.LEFT);
    private static final Column AMOUNT = new Column("Amount", Alignment.RIGHT);

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

    // helpers

    /**
     * Displays a report and returns everything that was printed.
     */
    private String render(List<Column> columns, List<?>... rows) {
        captured.reset();

        ReportDisplay.displayReport("Test report", columns, List.of(rows));

        return captured.toString();
    }

    /**
     * Displays a one-column report holding one value.
     */
    private String renderValue(Column column, Object value) {
        return render(List.of(column), Arrays.asList(value));
    }

    /**
     * Gets the printed line that contains the given text.
     */
    private static String lineContaining(String output, String text) {
        return Arrays.stream(output.split("\\R"))
                .filter(line -> line.contains(text))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "No line contains '" + text + "'"));
    }

    private static void assertRejected(Executable action) {
        assertThrows(IllegalArgumentException.class, action);
    }

    // number formatting

    @Test
    @DisplayName("Whole numbers get thousands separators")
    void wholeNumbersUseThousandsSeparators() {
        assertTrue(renderValue(AMOUNT, 1234567L).contains("1,234,567"));
        assertTrue(renderValue(AMOUNT, 1000).contains("1,000"));
    }

    @Test
    @DisplayName("Small whole numbers are not changed")
    void smallNumbersAreKept() {
        assertTrue(renderValue(AMOUNT, 7).contains(" 7 "));
    }

    @Test
    @DisplayName("Decimal numbers are shown with two decimal places")
    void decimalsUseTwoPlaces() {
        assertTrue(renderValue(AMOUNT, 1234.5).contains("1,234.50"));
        assertTrue(renderValue(AMOUNT, 2.5f).contains("2.50"));
    }

    @Test
    @DisplayName("Text is shown exactly as given")
    void textIsKept() {
        assertTrue(renderValue(NAME, "Saint-Denis").contains("Saint-Denis"));
    }

    // layout

    @Test
    @DisplayName("The title, headings and record count are printed")
    void titleHeadingsAndCountArePrinted() {

        String output = render(
                List.of(NAME, AMOUNT),
                List.of("A", 1),
                List.of("B", 2)
        );

        assertTrue(output.contains("Test report"));
        assertTrue(output.contains("Name"));
        assertTrue(output.contains("Amount"));
        assertTrue(output.contains("Total records: 2"));
    }

    @Test
    @DisplayName("A report with no rows shows zero records")
    void emptyReportShowsZeroRecords() {

        String output = render(List.of(NAME, AMOUNT));

        assertTrue(output.contains("Total records: 0"));
    }

    @Test
    @DisplayName("Left-aligned values start at the left of the cell")
    void leftAlignmentStartsAtTheLeft() {

        String line = lineContaining(
                render(List.of(NAME), List.of("Hamilton")),
                "Hamilton"
        );

        assertTrue(line.startsWith("| Hamilton "));
    }

    @Test
    @DisplayName("Right-aligned values end at the right of the cell")
    void rightAlignmentEndsAtTheRight() {

        String line = lineContaining(
                render(List.of(AMOUNT), List.of(1200)),
                "1,200"
        );

        assertTrue(line.endsWith(" 1,200 |"));
        assertFalse(line.startsWith("| 1,200"));
    }

    @Test
    @DisplayName("Every table line has the same width, even for long values")
    void allTableLinesHaveTheSameWidth() {

        String output = render(
                List.of(NAME, AMOUNT),
                List.of("A very long name that is wider than the heading", 5),
                List.of("B", 123456789L)
        );

        List<Integer> widths = new ArrayList<>();

        for (String line : output.split("\\R")) {
            if (line.startsWith("|") || line.startsWith("+")) {
                widths.add(line.length());
            }
        }

        assertFalse(widths.isEmpty());
        assertTrue(widths.stream().allMatch(w -> w.equals(widths.get(0))));
    }

    // validation

    @Test
    @DisplayName("A null or blank title is rejected")
    void invalidTitleIsRejected() {
        List<Column> columns = List.of(NAME);
        List<List<?>> rows = List.of();

        assertRejected(() -> ReportDisplay.displayReport(null, columns, rows));
        assertRejected(() -> ReportDisplay.displayReport("  ", columns, rows));
    }

    @Test
    @DisplayName("Null or empty columns are rejected")
    void invalidColumnsAreRejected() {
        List<List<?>> rows = List.of();

        assertRejected(() -> ReportDisplay.displayReport("T", null, rows));
        assertRejected(() -> ReportDisplay.displayReport("T", List.of(), rows));
    }

    @Test
    @DisplayName("Null rows are rejected")
    void nullRowsAreRejected() {
        assertRejected(() ->
                ReportDisplay.displayReport("T", List.of(NAME), null));
    }

    @Test
    @DisplayName("A null row inside the report is rejected")
    void nullRowIsRejected() {
        List<List<?>> rows = Arrays.asList((List<?>) null);

        assertRejected(() ->
                ReportDisplay.displayReport("T", List.of(NAME), rows));
    }

    @Test
    @DisplayName("A row with the wrong number of values is rejected")
    void wrongRowSizeIsRejected() {
        List<List<?>> rows = List.of(List.of("A", "B"));

        assertRejected(() ->
                ReportDisplay.displayReport("T", List.of(NAME), rows));
    }

    @Test
    @DisplayName("A column needs a heading and an alignment")
    void invalidColumnIsRejected() {
        assertRejected(() -> new Column(null, Alignment.LEFT));
        assertRejected(() -> new Column("  ", Alignment.LEFT));
        assertRejected(() -> new Column("Name", null));
    }

    //  other checks

    @Test
    @DisplayName("The standard population column is right-aligned")
    void populationColumnDefinition() {

        Column population = Column.population();

        assertEquals("Population", population.heading());
        assertEquals(Alignment.RIGHT, population.alignment());
        assertTrue(population.zeroIsMissing());
    }

    @Test
    @DisplayName("ReportDisplay cannot be instantiated")
    void utilityClassCannotBeInstantiated() throws Exception {

        Constructor<ReportDisplay> constructor =
                ReportDisplay.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException error = assertThrows(
                InvocationTargetException.class,
                constructor::newInstance
        );

        assertTrue(error.getCause() instanceof UnsupportedOperationException);
    }
}