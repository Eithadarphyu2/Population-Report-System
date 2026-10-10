package com.team14.report;

import com.team14.input.UserInput;

import java.util.Objects;

/**
 * Shared parameter checks used by the report constructors.
 */
public final class Validation {

    private Validation() {
        throw new UnsupportedOperationException(
                "Utility class cannot be instantiated"
        );
    }

    /**
     * Checks that a text parameter is not empty.
     *
     * @param value     text to check
     * @param fieldName name used in the error message
     * @return the trimmed text
     */
    public static String requireText(String value, String fieldName) {
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
    public static int requirePositive(int value, String fieldName) {
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
    public static UserInput requireInput(UserInput userInput) {
        return Objects.requireNonNull(
                userInput,
                "User input cannot be null."
        );
    }
}