package com.team14.input;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
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

    // Save original System.out so it can be restored after each test
    private final PrintStream originalOut = System.out;

    // Buffer to capture console output written to System.out during a test
    private final ByteArrayOutputStream captured = new ByteArrayOutputStream();

    /**
     * Redirects System.out to an in-memory stream using UTF-8 with auto-flush
     * so printed prompts and error messages can be verified in assertions.
     */
    @BeforeEach
    void captureOutput() {
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
    }

    /**
     * Restores the original System.out stream after each test finishes.
     */
    @AfterEach
    void restoreOutput() {
        System.setOut(originalOut);
    }

    // ---------------------------------------------------------------- helpers

    /**
     * Creates a UserInput backed by a Scanner reading the given simulated lines
     * (each separated by a newline) instead of real keyboard input.
     */
    private static UserInput typing(String... lines) {
        return new UserInput(new Scanner(String.join("\n", lines) + "\n"));
    }

    /**
     * Returns all console output captured so far, decoded explicitly as UTF-8.
     */
    private String printed() {
        return captured.toString(StandardCharsets.UTF_8);
    }

    // ----------------------------------------------------------- menu choice

    @Test
    @DisplayName("getMenuChoice returns a valid choice and asks for it")
    void menuChoiceReturnsNumber() {
        // Valid menu number is returned and the prompt message is shown
        assertEquals(3, typing("3").getMenuChoice(), "Expected menu choice 3 for input '3'");
        assertTrue(printed().contains("Enter your choice: "), "Expected menu prompt to be displayed");
    }

    @Test
    @DisplayName("getMenuChoice accepts 0 and ignores spaces")
    void menuChoiceAcceptsZeroAndTrims() {
        // 0 is a valid exit/menu option, and surrounding whitespace is trimmed
        assertEquals(0, typing("0").getMenuChoice(), "Expected menu choice 0 for input '0'");
        assertEquals(4, typing("  4  ").getMenuChoice(), "Expected trimmed menu choice 4 for input '  4  '");
    }

    @Test
    @DisplayName("getMenuChoice asks again after text that is not a number")
    void menuChoiceRepeatsAfterText() {
        // First input ("abc") is invalid; "2" is accepted on retry
        assertEquals(2, typing("abc", "2").getMenuChoice(), "Expected retry to accept valid choice 2 after 'abc'");
        assertTrue(
                printed().contains("Invalid input. Please enter a number."),
                "Expected invalid input message after non-numeric text"
        );
    }

    @Test
    @DisplayName("getMenuChoice asks again after a negative number")
    void menuChoiceRepeatsAfterNegative() {
        // Negative numbers are rejected; valid positive/zero choice ("1") succeeds
        assertEquals(1, typing("-5", "1").getMenuChoice(), "Expected retry to accept choice 1 after negative input '-5'");
        assertTrue(
                printed().contains("Please enter a valid number."),
                "Expected valid-number warning after negative input"
        );
    }

    // -------------------------------------------------------- positive number

    @Test
    @DisplayName("getPositiveNumber returns a number and shows the message")
    void positiveNumberReturnsNumber() {
        // Prompt text is printed and positive integer is parsed and returned
        assertEquals(5, typing("5").getPositiveNumber("How many? "), "Expected positive number 5 for input '5'");
        assertTrue(printed().contains("How many? "), "Expected custom prompt 'How many? ' to be printed");
    }

    @Test
    @DisplayName("getPositiveNumber asks again after 0")
    void positiveNumberRepeatsAfterZero() {
        // 0 is not greater than 0, so it prompts again before accepting 7
        assertEquals(7, typing("0", "7").getPositiveNumber("N: "), "Expected retry to return 7 after entering 0");
        assertTrue(
                printed().contains("Please enter a number greater than 0."),
                "Expected message requiring a number greater than 0 after entering 0"
        );
    }

    @Test
    @DisplayName("getPositiveNumber asks again after a negative number")
    void positiveNumberRepeatsAfterNegative() {
        // Negative numbers are not greater than 0, so it retries until 2
        assertEquals(2, typing("-1", "2").getPositiveNumber("N: "), "Expected retry to return 2 after entering -1");
        assertTrue(
                printed().contains("Please enter a number greater than 0."),
                "Expected message requiring a number greater than 0 after negative input"
        );
    }

    @Test
    @DisplayName("getPositiveNumber asks again after text that is not a number")
    void positiveNumberRepeatsAfterText() {
        // Non-numeric text ("x") triggers an invalid number message before 3 is accepted
        assertEquals(3, typing("x", "3").getPositiveNumber("N: "), "Expected retry to return 3 after non-numeric input 'x'");
        assertTrue(
                printed().contains("Invalid number. Please try again."),
                "Expected invalid number message after non-numeric input"
        );
    }

    // ------------------------------------------------------------------- text

    @Test
    @DisplayName("getText returns the text without spaces around it")
    void textIsTrimmed() {
        // Leading and trailing spaces around "Asia" should be removed
        assertEquals("Asia", typing("  Asia  ").getText("Name: "), "Expected trimmed text 'Asia'");
        assertTrue(printed().contains("Name: "), "Expected prompt 'Name: ' to be printed");
    }

    @Test
    @DisplayName("getText asks again after empty input")
    void textRepeatsAfterEmpty() {
        // Empty and blank-only lines are rejected until non-blank text ("Europe") is given
        assertEquals(
                "Europe",
                typing("", "   ", "Europe").getText("Name: "),
                "Expected 'Europe' after skipping empty and blank inputs"
        );
        assertTrue(
                printed().contains("Input cannot be empty. Please try again."),
                "Expected non-empty prompt message when blank input is supplied"
        );
    }

    // -------------------------------------------------------- location inputs

    @Test
    @DisplayName("getContinent asks for a continent")
    void continentPrompt() {
        assertEquals("Asia", typing("Asia").getContinent(), "Expected continent 'Asia'");
        assertTrue(printed().contains("Enter continent: "), "Expected continent prompt");
    }

    @Test
    @DisplayName("getRegion asks for a region")
    void regionPrompt() {
        assertEquals("Caribbean", typing("Caribbean").getRegion(), "Expected region 'Caribbean'");
        assertTrue(printed().contains("Enter region: "), "Expected region prompt");
    }

    @Test
    @DisplayName("getCountry asks for a country")
    void countryPrompt() {
        assertEquals("France", typing("France").getCountry(), "Expected country 'France'");
        assertTrue(printed().contains("Enter country: "), "Expected country prompt");
    }

    @Test
    @DisplayName("getDistrict asks for a district")
    void districtPrompt() {
        assertEquals("England", typing("England").getDistrict(), "Expected district 'England'");
        assertTrue(printed().contains("Enter district: "), "Expected district prompt");
    }

    @Test
    @DisplayName("getCity asks for a city")
    void cityPrompt() {
        assertEquals("Edinburgh", typing("Edinburgh").getCity(), "Expected city 'Edinburgh'");
        assertTrue(printed().contains("Enter city: "), "Expected city prompt");
    }

    // ------------------------------------------------------------------ top N

    @Test
    @DisplayName("getTopN asks for N and returns it")
    void topNPrompt() {
        assertEquals(10, typing("10").getTopN(), "Expected top N value 10");
        assertTrue(printed().contains("Enter number of results (N): "), "Expected top N prompt");
    }

    // ------------------------------------------------------ no input available

    @Test
    @DisplayName("Reading fails when there is no input, as in Docker")
    void noInputThrowsException() {
        // An empty Scanner simulates missing stdin (e.g., non-interactive Docker run)
        UserInput userInput = new UserInput(new Scanner(""));

        assertThrows(
                NoSuchElementException.class,
                userInput::getContinent,
                "Expected NoSuchElementException when no input line is available"
        );
    }
}