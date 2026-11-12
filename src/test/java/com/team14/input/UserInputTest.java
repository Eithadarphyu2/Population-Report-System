package com.team14.input;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for UserInput. A Scanner that reads from a text stands in for
 * the keyboard, so no real input is needed.
 */
class UserInputTest {

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

    // ---------------------------------------------------------------- helpers

    /**
     * Creates a UserInput that reads the given lines instead of the keyboard.
     */
    private static UserInput typing(String... lines) {
        return new UserInput(new Scanner(String.join("\n", lines) + "\n"));
    }

    private String printed() {
        return captured.toString();
    }

    // ----------------------------------------------------------- menu choice

    @Test
    @DisplayName("getMenuChoice returns a valid choice and asks for it")
    void menuChoiceReturnsNumber() {
        assertEquals(3, typing("3").getMenuChoice());
        assertTrue(printed().contains("Enter your choice: "));
    }

    @Test
    @DisplayName("getMenuChoice accepts 0 and ignores spaces")
    void menuChoiceAcceptsZeroAndTrims() {
        assertEquals(0, typing("0").getMenuChoice());
        assertEquals(4, typing("  4  ").getMenuChoice());
    }

    @Test
    @DisplayName("getMenuChoice asks again after text that is not a number")
    void menuChoiceRepeatsAfterText() {
        assertEquals(2, typing("abc", "2").getMenuChoice());
        assertTrue(printed().contains("Invalid input. Please enter a number."));
    }

    @Test
    @DisplayName("getMenuChoice asks again after a negative number")
    void menuChoiceRepeatsAfterNegative() {
        assertEquals(1, typing("-5", "1").getMenuChoice());
        assertTrue(printed().contains("Please enter a valid number."));
    }

    // -------------------------------------------------------- positive number

    @Test
    @DisplayName("getPositiveNumber returns a number and shows the message")
    void positiveNumberReturnsNumber() {
        assertEquals(5, typing("5").getPositiveNumber("How many? "));
        assertTrue(printed().contains("How many? "));
    }

    @Test
    @DisplayName("getPositiveNumber asks again after 0")
    void positiveNumberRepeatsAfterZero() {
        assertEquals(7, typing("0", "7").getPositiveNumber("N: "));
        assertTrue(printed().contains("Please enter a number greater than 0."));
    }

    @Test
    @DisplayName("getPositiveNumber asks again after a negative number")
    void positiveNumberRepeatsAfterNegative() {
        assertEquals(2, typing("-1", "2").getPositiveNumber("N: "));
        assertTrue(printed().contains("Please enter a number greater than 0."));
    }

    @Test
    @DisplayName("getPositiveNumber asks again after text that is not a number")
    void positiveNumberRepeatsAfterText() {
        assertEquals(3, typing("x", "3").getPositiveNumber("N: "));
        assertTrue(printed().contains("Invalid number. Please try again."));
    }

    // ------------------------------------------------------------------- text

    @Test
    @DisplayName("getText returns the text without spaces around it")
    void textIsTrimmed() {
        assertEquals("Asia", typing("  Asia  ").getText("Name: "));
        assertTrue(printed().contains("Name: "));
    }

    @Test
    @DisplayName("getText asks again after empty input")
    void textRepeatsAfterEmpty() {
        assertEquals("Europe", typing("", "   ", "Europe").getText("Name: "));
        assertTrue(printed().contains("Input cannot be empty. Please try again."));
    }

    // -------------------------------------------------------- location inputs

    @Test
    @DisplayName("getContinent asks for a continent")
    void continentPrompt() {
        assertEquals("Asia", typing("Asia").getContinent());
        assertTrue(printed().contains("Enter continent: "));
    }

    @Test
    @DisplayName("getRegion asks for a region")
    void regionPrompt() {
        assertEquals("Caribbean", typing("Caribbean").getRegion());
        assertTrue(printed().contains("Enter region: "));
    }

    @Test
    @DisplayName("getCountry asks for a country")
    void countryPrompt() {
        assertEquals("France", typing("France").getCountry());
        assertTrue(printed().contains("Enter country: "));
    }

    @Test
    @DisplayName("getDistrict asks for a district")
    void districtPrompt() {
        assertEquals("England", typing("England").getDistrict());
        assertTrue(printed().contains("Enter district: "));
    }

    @Test
    @DisplayName("getCity asks for a city")
    void cityPrompt() {
        assertEquals("Edinburgh", typing("Edinburgh").getCity());
        assertTrue(printed().contains("Enter city: "));
    }

    // ------------------------------------------------------------------ top N

    @Test
    @DisplayName("getTopN asks for N and returns it")
    void topNPrompt() {
        assertEquals(10, typing("10").getTopN());
        assertTrue(printed().contains("Enter number of results (N): "));
    }

    // ------------------------------------------------------ no input available

    @Test
    @DisplayName("Reading fails when there is no input, as in Docker")
    void noInputThrowsException() {

        UserInput userInput = new UserInput(new Scanner(""));

        assertThrows(NoSuchElementException.class, userInput::getContinent);
    }
}