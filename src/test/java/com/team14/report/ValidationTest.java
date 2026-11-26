package com.team14.report;

import com.team14.input.UserInput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the shared parameter checks used by the report constructors.
 */
class ValidationTest {

    // ------------------------------------------------------------ requireText

    @Test
    @DisplayName("requireText returns a valid text")
    void requireTextReturnsValidText() {
        assertEquals(
                "Europe",
                Validation.requireText("Europe", "Continent"),
                "Valid text should be returned unchanged"
        );
    }

    @Test
    @DisplayName("requireText removes spaces around the text")
    void requireTextTrimsText() {
        assertEquals(
                "Europe",
                Validation.requireText("  Europe  ", "Continent"),
                "Surrounding whitespace should be trimmed from valid text"
        );
    }

    @Test
    @DisplayName("requireText rejects null and names the field")
    void requireTextRejectsNull() {

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> Validation.requireText(null, "Continent"),
                "Null text should throw IllegalArgumentException"
        );

        assertEquals(
                "Continent cannot be empty.",
                error.getMessage(),
                "Error message should name the empty field"
        );
    }

    @Test
    @DisplayName("requireText rejects empty and blank text")
    void requireTextRejectsBlank() {

        assertThrows(
                IllegalArgumentException.class,
                () -> Validation.requireText("", "Region"),
                "Empty text should throw IllegalArgumentException"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> Validation.requireText("   ", "Region"),
                "Blank text should throw IllegalArgumentException"
        );
    }

    // ----------------------------------------------------------- requirePositive

    @Test
    @DisplayName("requirePositive returns a number greater than 0")
    void requirePositiveReturnsPositiveNumber() {
        assertEquals(
                1,
                Validation.requirePositive(1, "N"),
                "Smallest positive integer (1) should be accepted"
        );
        assertEquals(
                50,
                Validation.requirePositive(50, "N"),
                "Positive integer should be returned unchanged"
        );
    }

    @Test
    @DisplayName("requirePositive rejects 0 and names the field")
    void requirePositiveRejectsZero() {

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> Validation.requirePositive(0, "Number of cities"),
                "Zero should throw IllegalArgumentException"
        );

        assertEquals(
                "Number of cities must be greater than 0.",
                error.getMessage(),
                "Error message should explain that the field must be greater than 0"
        );
    }

    @Test
    @DisplayName("requirePositive rejects negative numbers")
    void requirePositiveRejectsNegative() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Validation.requirePositive(-3, "N"),
                "Negative number should throw IllegalArgumentException"
        );
    }

    // ------------------------------------------------------------- requireInput

    @Test
    @DisplayName("requireInput returns the same user input source")
    void requireInputReturnsSameObject() {

        UserInput userInput = new UserInput(new Scanner(""));

        assertSame(
                userInput,
                Validation.requireInput(userInput),
                "requireInput should return the same UserInput instance"
        );
    }

    @Test
    @DisplayName("requireInput rejects null")
    void requireInputRejectsNull() {

        NullPointerException error = assertThrows(
                NullPointerException.class,
                () -> Validation.requireInput(null),
                "Null user input should throw NullPointerException"
        );

        assertEquals(
                "User input cannot be null.",
                error.getMessage(),
                "Error message should state that user input cannot be null"
        );
    }

    // ------------------------------------------------------------ utility class

    @Test
    @DisplayName("Validation cannot be instantiated")
    void utilityClassCannotBeInstantiated() throws Exception {

        Constructor<Validation> constructor =
                Validation.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException error = assertThrows(
                InvocationTargetException.class,
                constructor::newInstance,
                "Validation constructor should throw when invoked via reflection"
        );

        assertTrue(
                error.getCause() instanceof UnsupportedOperationException,
                "Validation constructor should fail with UnsupportedOperationException"
        );
    }
}