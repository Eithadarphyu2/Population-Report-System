package com.team14;

import com.team14.report.Report;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the way App runs its reports. Fake reports are used, so
 * these tests need no database.
 * The whole application is tested against MySQL in AppIntegrationTest.
 */
class AppTest {

    // Preserve the original System.err stream so it can be restored after each test
    private final PrintStream originalErr = System.err;

    // Buffer used to capture error messages written to System.err during tests
    private final ByteArrayOutputStream capturedErr = new ByteArrayOutputStream();

    @BeforeEach
    void captureErrors() {
        // Explicitly specify UTF-8 with auto-flush enabled so PMD RelianceOnDefaultCharset
        // is satisfied and error output is immediately flushed to the buffer
        System.setErr(new PrintStream(capturedErr, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreErrors() {
        // Restore original System.err to avoid affecting other tests
        System.setErr(originalErr);
    }

    /**
     * A report that records that it ran, and can be told to fail.
     */
    private static final class FakeReport implements Report {

        private final String title;
        private final boolean fails;
        private boolean ran;

        FakeReport(String title, boolean fails) {
            this.title = title;
            this.fails = fails;
        }

        @Override
        public String title() {
            return title;
        }

        @Override
        public void run() throws SQLException {
            ran = true;

            if (fails) {
                throw new SQLException("boom");
            }
        }
    }

    @Test
    @DisplayName("Reports that work produce no failures")
    void successfulReportsProduceNoFailures() {

        FakeReport first = new FakeReport("First", false);
        FakeReport second = new FakeReport("Second", false);

        int failures = App.runReports(List.of(first, second));

        assertEquals(0, failures, "Expected no failures when all reports succeed");
    }

    @Test
    @DisplayName("First working report is run")
    void firstReportRuns() {

        FakeReport first = new FakeReport("First", false);
        FakeReport second = new FakeReport("Second", false);

        App.runReports(List.of(first, second));

        assertTrue(first.ran, "Expected first report to be executed");
    }

    @Test
    @DisplayName("Second working report is run")
    void secondReportRuns() {

        FakeReport first = new FakeReport("First", false);
        FakeReport second = new FakeReport("Second", false);

        App.runReports(List.of(first, second));

        assertTrue(second.ran, "Expected second report to be executed");
    }

    @Test
    @DisplayName("Working reports write nothing to standard error")
    void successfulReportsPrintNoErrors() {

        FakeReport first = new FakeReport("First", false);
        FakeReport second = new FakeReport("Second", false);

        App.runReports(List.of(first, second));

        // Decode captured bytes using UTF-8 instead of relying on JVM default charset
        assertEquals("", capturedErr.toString(StandardCharsets.UTF_8), "Expected no error output");
    }

    @Test
    @DisplayName("A failing report is counted as a failure")
    void failingReportIsCounted() {

        int failures = App.runReports(List.of(new FakeReport("Broken report", true)));

        assertEquals(1, failures, "Expected one failure for a single failing report");
    }

    @Test
    @DisplayName("A failing report prints its title and reason to standard error")
    void failingReportPrintsTitleAndReason() {

        App.runReports(List.of(new FakeReport("Broken report", true)));

        // Verify the error message in UTF-8 includes both the report title and exception reason
        assertTrue(
                capturedErr.toString(StandardCharsets.UTF_8)
                        .contains("Report failed: Broken report - boom"),
                "Expected error output to include report title and failure reason"
        );
    }

    @Test
    @DisplayName("A failing report counts only that report as a failure")
    void failingReportCountsOnlyFailure() {

        FakeReport broken = new FakeReport("Broken", true);
        FakeReport after = new FakeReport("After", false);

        int failures = App.runReports(List.of(broken, after));

        assertEquals(1, failures, "Expected only the broken report to count as a failure");
    }

    @Test
    @DisplayName("A failing report does not stop the report after it from running")
    void failingReportDoesNotStopSubsequentReport() {

        FakeReport broken = new FakeReport("Broken", true);
        FakeReport after = new FakeReport("After", false);

        App.runReports(List.of(broken, after));

        assertTrue(after.ran, "Expected subsequent report to still run after a failure");
    }

    @Test
    @DisplayName("Every failing report is counted")
    void everyFailureIsCounted() {

        int failures = App.runReports(List.of(
                new FakeReport("One", true),
                new FakeReport("Two", false),
                new FakeReport("Three", true)
        ));

        assertEquals(2, failures, "Expected two failures out of three reports");
    }

    @Test
    @DisplayName("No reports means zero failures")
    void noReportsMeansZeroFailures() {

        int failures = App.runReports(List.of());

        assertEquals(0, failures, "Expected zero failures for an empty report list");
    }

    @Test
    @DisplayName("No reports prints no failure message to standard error")
    void noReportsPrintsNoFailureMessage() {

        App.runReports(List.of());

        // Decode captured bytes using UTF-8 to avoid default-charset warnings and ensure consistency
        assertFalse(
                capturedErr.toString(StandardCharsets.UTF_8).contains("Report failed"),
                "Expected no failure message when no reports are run"
        );
    }

    @Test
    @DisplayName("App constructor throws InvocationTargetException")
    void appConstructorThrowsInvocationTargetException() throws Exception {

        // Access the private constructor via reflection to test utility-class protection
        Constructor<App> constructor = App.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThrows(
                InvocationTargetException.class,
                constructor::newInstance,
                "Expected constructor invocation to fail with InvocationTargetException"
        );
    }

    @Test
    @DisplayName("App constructor fails with UnsupportedOperationException cause")
    void appConstructorCauseIsUnsupportedOperationException() throws Exception {

        Constructor<App> constructor = App.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        Throwable cause = null;
        try {
            constructor.newInstance();
        } catch (InvocationTargetException error) {
            cause = error.getCause();
        }

        assertInstanceOf(
                UnsupportedOperationException.class,
                cause,
                "Expected UnsupportedOperationException as the underlying cause"
        );
    }
}