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
        assertEquals(
                ReportParameters.TOP_N,
                Validation.requirePositive(ReportParameters.TOP_N, "N")
        );
    }

    @Test
    @DisplayName("Every text value is accepted and has no extra spaces")
    void textValuesAreValid() {

        String[] values = {
                ReportParameters.CONTINENT,
                ReportParameters.REGION,
                ReportParameters.COUNTRY,
                ReportParameters.DISTRICT,
                ReportParameters.CITY
        };

        for (String value : values) {
            assertEquals(value, Validation.requireText(value, "Value"));
        }
    }

    @Test
    @DisplayName("ReportParameters cannot be instantiated")
    void utilityClassCannotBeInstantiated() throws Exception {

        Constructor<ReportParameters> constructor =
                ReportParameters.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException error = assertThrows(
                InvocationTargetException.class,
                constructor::newInstance
        );

        assertTrue(error.getCause() instanceof UnsupportedOperationException);
    }
}