package com.team14.config;

import com.team14.report.Validation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the values the reports read from ReportParameters.
 * Every value must be accepted by the checks the report constructors use,
 * otherwise a report would fail when the application starts.
 */
class ReportParametersTest {

    @Test
    @DisplayName("TOP_N is accepted as a positive number")
    void topNIsValid() {
        // Verify TOP_N passes positive-number validation unchanged
        assertEquals(
                ReportParameters.TOP_N,
                Validation.requirePositive(ReportParameters.TOP_N, "N"),
                "Expected TOP_N to be a valid positive number"
        );
    }

    @Test
    @DisplayName("Every text value is accepted and has no extra spaces")
    void textValuesAreValid() {

        // Check all configured filter parameters used by report constructors
        String[] values = {
                ReportParameters.CONTINENT,
                ReportParameters.REGION,
                ReportParameters.COUNTRY,
                ReportParameters.DISTRICT,
                ReportParameters.CITY
        };

        for (String value : values) {
            assertEquals(
                    value,
                    Validation.requireText(value, "Value"),
                    "Expected valid non-blank text without extra spaces for: " + value
            );
        }
    }

    @Test
    @DisplayName("ReportParameters cannot be instantiated")
    void utilityClassCannotBeInstantiated() throws Exception {

        // Access the private constructor via reflection to ensure utility-class protection
        Constructor<ReportParameters> constructor =
                ReportParameters.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException error = assertThrows(
                InvocationTargetException.class,
                constructor::newInstance,
                "Expected instantiation of ReportParameters to throw InvocationTargetException"
        );

        assertTrue(
                error.getCause() instanceof UnsupportedOperationException,
                "Expected UnsupportedOperationException as the underlying cause"
        );
    }
}