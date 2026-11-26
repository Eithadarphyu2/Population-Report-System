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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    @DisplayName("Reports that work are all run and no failure is counted")
    void runsEveryReport() {

        FakeReport first = new FakeReport("First", false);
        FakeReport second = new FakeReport("Second", false);

        int failures = App.runReports(List.of(first, second));

        assertEquals(0, failures);
        assertTrue(first.ran);
        assertTrue(second.ran);
        // Decode captured bytes using UTF-8 instead of relying on JVM default charset
        assertEquals("", capturedErr.toString(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("A failing report is counted and its title and reason are printed")
    void failingReportIsReported() {

        int failures = App.runReports(List.of(new FakeReport("Broken report", true)));

        assertEquals(1, failures);
        // Verify the error message in UTF-8 includes both the report title and exception reason
        assertTrue(capturedErr.toString(StandardCharsets.UTF_8)
                .contains("Report failed: Broken report - boom"));
    }

    @Test
    @DisplayName("A failing report does not stop the reports after it")
    void failingReportDoesNotStopOthers() {

        FakeReport broken = new FakeReport("Broken", true);
        FakeReport after = new FakeReport("After", false);

        int failures = App.runReports(List.of(broken, after));

        assertEquals(1, failures);
        assertTrue(after.ran);
    }

    @Test
    @DisplayName("Every failing report is counted")
    void everyFailureIsCounted() {

        int failures = App.runReports(List.of(
                new FakeReport("One", true),
                new FakeReport("Two", false),
                new FakeReport("Three", true)
        ));

        assertEquals(2, failures);
    }

    @Test
    @DisplayName("No reports means no failures")
    void noReportsMeansNoFailures() {
        assertEquals(0, App.runReports(List.of()));
        // Decode captured bytes using UTF-8 to avoid default-charset warnings and ensure consistency
        assertFalse(capturedErr.toString(StandardCharsets.UTF_8).contains("Report failed"));
    }

    @Test
    @DisplayName("App cannot be instantiated")
    void appCannotBeInstantiated() throws Exception {

        // Access the private constructor via reflection to test utility-class protection
        Constructor<App> constructor = App.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException error = assertThrows(
                InvocationTargetException.class,
                constructor::newInstance
        );

        assertTrue(error.getCause() instanceof UnsupportedOperationException);
    }
}