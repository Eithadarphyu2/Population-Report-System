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

    private final PrintStream originalErr = System.err;
    private final ByteArrayOutputStream capturedErr = new ByteArrayOutputStream();

    @BeforeEach
    void captureErrors() {
        System.setErr(new PrintStream(capturedErr));
    }

    @AfterEach
    void restoreErrors() {
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
        assertEquals("", capturedErr.toString());
    }

    @Test
    @DisplayName("A failing report is counted and its title and reason are printed")
    void failingReportIsReported() {

        int failures = App.runReports(List.of(new FakeReport("Broken report", true)));

        assertEquals(1, failures);
        assertTrue(capturedErr.toString().contains("Report failed: Broken report - boom"));
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
        assertFalse(capturedErr.toString().contains("Report failed"));
    }

    @Test
    @DisplayName("App cannot be instantiated")
    void appCannotBeInstantiated() throws Exception {

        Constructor<App> constructor = App.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException error = assertThrows(
                InvocationTargetException.class,
                constructor::newInstance
        );

        assertTrue(error.getCause() instanceof UnsupportedOperationException);
    }
}