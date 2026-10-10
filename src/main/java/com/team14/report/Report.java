package com.team14.report;

import com.team14.input.UserInput;

import java.sql.SQLException;
import java.util.Objects;

/**
 * A report that can be printed by the application.
 * Every report in the system implements this interface, so App can run
 * them all in the same way.
 *
 * The static methods are shared parameter checks used by the report
 * constructors, so the same validation is not repeated in every report.
 */
public interface Report {

    /**
     * Gets the report title.
     *
     * @return the title shown above the report table
     */
    String title();

    /**
     * Runs the report query and prints the result.
     *
     * @throws SQLException if the database query fails
     */
    void run() throws SQLException;

    /**
     * Checks that a text parameter is not empty.
     *
     * @param value     text to check
     * @param fieldName name used in the error message
     * @return the trimmed text
     */
    static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be empty."
            );
        }
        return value.trim();
    }

    /**
     * Checks that a number parameter is greater than 0.
     *
     * @param value     number to check
     * @param fieldName name used in the error message
     * @return the same number
     */
    static int requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(
                    fieldName + " must be greater than 0."
            );
        }
        return value;
    }

    /**
     * Checks that the user input source is not null.
     *
     * @param userInput user input source to check
     * @return the same user input source
     */
    static UserInput requireInput(UserInput userInput) {
        return Objects.requireNonNull(
                userInput,
                "User input cannot be null."
        );
    }
}