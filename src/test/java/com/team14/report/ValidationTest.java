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
        assertEquals("Europe", Validation.requireText("Europe", "Continent"));
    }

    @Test
    @DisplayName("requireText removes spaces around the text")
    void requireTextTrimsText() {
        assertEquals("Europe", Validation.requireText("  Europe  ", "Continent"));
    }

    @Test
    @DisplayName("requireText rejects null and names the field")
    void requireTextRejectsNull() {

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> Validation.requireText(null, "Continent")
        );

        assertEquals("Continent cannot be empty.", error.getMessage());
    }

    @Test
    @DisplayName("requireText rejects empty and blank text")
    void requireTextRejectsBlank() {

        assertThrows(IllegalArgumentException.class,
                () -> Validation.requireText("", "Region"));

        assertThrows(IllegalArgumentException.class,
                () -> Validation.requireText("   ", "Region"));
    }

    // ----------------------------------------------------------- requirePositive

    @Test
    @DisplayName("requirePositive returns a number greater than 0")
    void requirePositiveReturnsPositiveNumber() {
        assertEquals(1, Validation.requirePositive(1, "N"));
        assertEquals(50, Validation.requirePositive(50, "N"));
    }

    @Test
    @DisplayName("requirePositive rejects 0 and names the field")
    void requirePositiveRejectsZero() {

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> Validation.requirePositive(0, "Number of cities")
        );

        assertEquals("Number of cities must be greater than 0.", error.getMessage());
    }

    @Test
    @DisplayName("requirePositive rejects negative numbers")
    void requirePositiveRejectsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> Validation.requirePositive(-3, "N"));
    }

    // ------------------------------------------------------------- requireInput

    @Test
    @DisplayName("requireInput returns the same user input source")
    void requireInputReturnsSameObject() {

        UserInput userInput = new UserInput(new Scanner(""));

        assertSame(userInput, Validation.requireInput(userInput));
    }

    @Test
    @DisplayName("requireInput rejects null")
    void requireInputRejectsNull() {

        NullPointerException error = assertThrows(
                NullPointerException.class,
                () -> Validation.requireInput(null)
        );

        assertEquals("User input cannot be null.", error.getMessage());
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
                constructor::newInstance
        );

        assertTrue(error.getCause() instanceof UnsupportedOperationException);
    }
}