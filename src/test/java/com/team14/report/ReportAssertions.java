package com.team14.report;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test helper: checks that every population report prints a table in the
 * same, correct shape. Shared by the country and city integration tests so
 * the same assertions are not written twice.
 */
public final class ReportAssertions {

    private ReportAssertions() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Checks the title, the headings, that "Total records" matches the
     * printed rows, and that the rows run from largest population to smallest.
     *
     * @param output           the printed report
     * @param expectedTitle    title the report must show
     * @param expectedHeadings column headings the report must show
     */
    public static void assertWellFormed(ReportOutput output,
                                        String expectedTitle,
                                        List<String> expectedHeadings) {

        assertAll(
                () -> assertEquals(expectedTitle, output.title(), "title"),
                () -> assertEquals(expectedHeadings, output.headings(), "headings"),
                () -> assertEquals(output.rows().size(), output.totalRecords(),
                        "'Total records' must match the number of printed rows"),
                () -> assertSortedByPopulationDescending(output)
        );
    }

    /**
     * Checks that populations never increase from one row to the next.
     * Equal populations are allowed, because two rows can tie.
     */
    public static void assertSortedByPopulationDescending(ReportOutput output) {

        List<Long> populations = output.populations();

        for (int i = 1; i < populations.size(); i++) {
            assertTrue(populations.get(i - 1) >= populations.get(i),
                    "Row " + (i + 1) + " (" + populations.get(i)
                            + ") is larger than row " + i + " (" + populations.get(i - 1) + ")");
        }
    }
}
