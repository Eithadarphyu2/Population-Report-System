package com.team14.report;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Provides a consistent console display format for all population reports.
 *
 * This class is responsible only for formatting and displaying report data.
 * It does not contain database access, SQL queries, or report calculations.
 */
public final class ReportDisplay {

    private static final String COLUMN_SEPARATOR = "|";
    private static final String BORDER_CORNER = "+";
    private static final String BORDER_HORIZONTAL = "-";
    private static final String EMPTY_VALUE = "N/A";
    private static final String TITLE_RULE = "=".repeat(60);

    private static final int MIN_COLUMN_WIDTH = 12;
    private static final int CELL_PADDING = 1;

    private static final NumberFormat INTEGER_FORMAT =
            NumberFormat.getIntegerInstance(Locale.US);

    /**
     * Prevents instantiation of this utility class.
     */
    private ReportDisplay() {
        throw new UnsupportedOperationException(
                "Utility class cannot be instantiated"
        );
    }

    /**
     * Defines how a value is aligned within a report column.
     */
    public enum Alignment {
        LEFT,
        RIGHT
    }

    /**
     * Defines a report column and its alignment.
     *
     * @param heading   column heading
     * @param alignment column alignment
     */
    public record Column(
            String heading,
            Alignment alignment) {

        public Column {
            if (heading == null || heading.isBlank()) {
                throw new IllegalArgumentException(
                        "Column heading cannot be empty."
                );
            }

            if (alignment == null) {
                throw new IllegalArgumentException(
                        "Column alignment cannot be null."
                );
            }
        }
    }

    /**
     * Displays any supported population report.
     *
     * The method is shared by all report types:
     *
     * Country:
     * Code | Name | Continent | Region | Population | Capital
     *
     * City:
     * Name | Country | District | Population
     *
     * Capital City:
     * Name | Country | Population
     *
     * Population:
     * Location | Total Population | City Population | Non-City Population
     *
     * The City Population and Non-City Population values should contain
     * both the population number and its percentage.
     *
     * @param title   report title
     * @param columns report column definitions
     * @param rows    report data
     */
    public static void displayReport(
            String title,
            List<Column> columns,
            List<List<?>> rows) {

        validateReport(title, columns, rows);

        List<List<String>> formattedRows = formatRows(rows);

        int[] columnWidths = calculateColumnWidths(
                columns,
                formattedRows
        );

        printTitle(title);

        printSeparator(columnWidths);

        printRow(
                getHeadings(columns),
                columnWidths,
                columns
        );

        printSeparator(columnWidths);

        for (List<String> row : formattedRows) {
            printRow(row, columnWidths, columns);
        }

        printSeparator(columnWidths);

        System.out.println(
                "Total records: " + formattedRows.size()
        );

        System.out.println();
    }

    /**
     * Converts report values into display-ready strings.
     *
     * @param rows report rows
     * @return formatted rows
     */
    private static List<List<String>> formatRows(
            List<List<?>> rows) {

        List<List<String>> formattedRows = new ArrayList<>();

        for (List<?> row : rows) {

            List<String> formattedRow = new ArrayList<>();

            for (Object value : row) {
                formattedRow.add(formatValue(value));
            }

            formattedRows.add(formattedRow);
        }

        return formattedRows;
    }

    /**
     * Formats an individual report value.
     *
     * Numeric values receive thousands separators.
     *
     * @param value value to format
     * @return formatted value
     */
    private static String formatValue(Object value) {

        if (value == null) {
            return EMPTY_VALUE;
        }

        if (value instanceof Number number) {
            return formatNumber(number);
        }

        return value.toString();
    }

    /**
     * Formats numeric values for readable output.
     *
     * Whole numbers are displayed with thousands separators.
     * Decimal values are displayed with two decimal places.
     *
     * @param number numeric value
     * @return formatted number
     */
    private static String formatNumber(Number number) {

        if (number instanceof Double
                || number instanceof Float) {

            return String.format(
                    Locale.US,
                    "%,.2f",
                    number.doubleValue()
            );
        }

        return INTEGER_FORMAT.format(number.longValue());
    }

    /**
     * Gets column headings from the column definitions.
     *
     * @param columns report columns
     * @return column headings
     */
    private static List<String> getHeadings(
            List<Column> columns) {

        return columns.stream()
                .map(Column::heading)
                .toList();
    }

    /**
     * Calculates the required width of each column.
     *
     * @param columns       report columns
     * @param formattedRows formatted report rows
     * @return column widths
     */
    private static int[] calculateColumnWidths(
            List<Column> columns,
            List<List<String>> formattedRows) {

        int[] widths = new int[columns.size()];

        for (int i = 0; i < columns.size(); i++) {

            widths[i] = Math.max(
                    MIN_COLUMN_WIDTH,
                    columns.get(i).heading().length()
            );
        }

        for (List<String> row : formattedRows) {

            for (int i = 0;
                 i < row.size() && i < widths.length;
                 i++) {

                widths[i] = Math.max(
                        widths[i],
                        row.get(i).length()
                );
            }
        }

        return widths;
    }

    /**
     * Prints the report title.
     *
     * @param title report title
     */
    private static void printTitle(String title) {

        System.out.println();
        System.out.println(TITLE_RULE);
        System.out.println(title);
        System.out.println(TITLE_RULE);
    }

    /**
     * Prints a horizontal table separator.
     *
     * @param widths column widths
     */
    private static void printSeparator(int[] widths) {

        StringBuilder separator = new StringBuilder();

        separator.append(BORDER_CORNER);

        for (int width : widths) {

            separator.append(
                    BORDER_HORIZONTAL.repeat(
                            width + (CELL_PADDING * 2)
                    )
            );

            separator.append(BORDER_CORNER);
        }

        System.out.println(separator);
    }

    /**
     * Prints one report row using the configured column alignments.
     *
     * @param row     row values
     * @param widths  column widths
     * @param columns column definitions
     */
    private static void printRow(
            List<String> row,
            int[] widths,
            List<Column> columns) {

        StringBuilder output = new StringBuilder();

        output.append(COLUMN_SEPARATOR);

        for (int i = 0; i < widths.length; i++) {

            String value = "";

            if (i < row.size()) {
                value = row.get(i);
            }

            output.append(" ");

            // "-" flag left-aligns; no flag right-aligns.
            String alignFlag =
                    columns.get(i).alignment() == Alignment.RIGHT
                            ? ""
                            : "-";

            output.append(
                    String.format(
                            Locale.US,
                            "%" + alignFlag + widths[i] + "s",
                            value
                    )
            );

            output.append(" ");
            output.append(COLUMN_SEPARATOR);
        }

        System.out.println(output);
    }

    /**
     * Validates report data before displaying it.
     *
     * @param title   report title
     * @param columns report columns
     * @param rows    report rows
     */
    private static void validateReport(
            String title,
            List<Column> columns,
            List<List<?>> rows) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Report title cannot be empty."
            );
        }

        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException(
                    "Report must contain at least one column."
            );
        }

        if (rows == null) {
            throw new IllegalArgumentException(
                    "Report rows cannot be null."
            );
        }

        for (List<?> row : rows) {

            if (row == null) {
                throw new IllegalArgumentException(
                        "Report row cannot be null."
                );
            }

            if (row.size() != columns.size()) {
                throw new IllegalArgumentException(
                        "Every report row must contain exactly "
                                + columns.size()
                                + " values."
                );
            }
        }
    }
}