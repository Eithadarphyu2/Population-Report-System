package com.team14;

import com.team14.report.Report;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-end test of the whole application.
 * <p>
 * It runs App against the real MySQL world database, exactly as the Docker
 * container does, and checks what the application prints. The application is
 * run once and every test checks the same result.
 */
class AppIntegrationTest {

    /** The coursework asks for 32 reports. */
    private static final int REQUIRED_REPORTS = 32;

    private static int exitCode;
    private static String output;
    private static String errors;

    @BeforeAll
    static void runApplication() {

        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        ByteArrayOutputStream capturedOut = new ByteArrayOutputStream();
        ByteArrayOutputStream capturedErr = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(capturedOut, true, StandardCharsets.UTF_8));
            System.setErr(new PrintStream(capturedErr, true, StandardCharsets.UTF_8));

            exitCode = App.run();

        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }

        output = capturedOut.toString(StandardCharsets.UTF_8);
        errors = capturedErr.toString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("The application finishes without an error")
    void finishesWithExitCodeZero() {
        assertEquals(0, exitCode, "Error output: " + errors);
    }

    @Test
    @DisplayName("Nothing is printed to the error output")
    void printsNoErrors() {
        assertEquals("", errors);
    }

    @Test
    @DisplayName("The application reports a working database connection")
    void reportsDatabaseConnection() {
        assertTrue(output.contains("Database connection OK: true"));
    }

    @Test
    @DisplayName("All 32 reports are registered, each with its own title")
    void allReportsAreRegistered() {

        List<Report> reports = App.reports();

        Set<String> titles = new HashSet<>();
        reports.forEach(report -> titles.add(report.title()));

        assertEquals(REQUIRED_REPORTS, reports.size());
        assertEquals(REQUIRED_REPORTS, titles.size());
    }

    @Test
    @DisplayName("The output shows the title of every report")
    void outputContainsEveryTitle() {

        List<String> missing = App.reports().stream()
                .map(Report::title)
                .filter(title -> !output.contains(title))
                .toList();

        assertEquals(List.of(), missing);
    }

    @Test
    @DisplayName("Every report prints one table with a record count")
    void everyReportPrintsOneTable() {

        int tables = output.split("Total records: ", -1).length - 1;

        assertEquals(REQUIRED_REPORTS, tables);
    }
}