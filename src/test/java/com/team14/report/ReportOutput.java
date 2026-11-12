package com.team14.report;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Test helper: runs a report against the real database and reads the table
 * it prints, so integration tests can check what the user would see.
 * <p>
 * Reports print to the console and return nothing, so the output is
 * captured and then split into the title, the headings, the rows and the
 * "Total records" count.
 */
public final class ReportOutput {

    private static final String TOTAL_PREFIX = "Total records: ";

    private final String title;
    private final List<String> headings;
    private final List<Map<String, String>> rows;
    private final int totalRecords;

    private ReportOutput(String printed) {

        List<String> lines = printed.lines().toList();

        int titleRule = indexOfFirst(lines, line -> line.startsWith("="));
        this.title = lines.get(titleRule + 1);

        List<List<String>> tableLines = lines.stream()
                .filter(line -> line.startsWith("|"))
                .map(ReportOutput::splitCells)
                .toList();

        this.headings = tableLines.get(0);
        this.rows = new ArrayList<>();

        for (List<String> cells : tableLines.subList(1, tableLines.size())) {
            Map<String, String> row = new LinkedHashMap<>();
            for (int i = 0; i < headings.size(); i++) {
                row.put(headings.get(i), cells.get(i));
            }
            rows.add(row);
        }

        this.totalRecords = lines.stream()
                .filter(line -> line.startsWith(TOTAL_PREFIX))
                .map(line -> Integer.parseInt(line.substring(TOTAL_PREFIX.length()).trim()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No 'Total records' line was printed"));
    }

    /**
     * Runs a report and returns what it printed.
     *
     * @param report report to run
     * @return the parsed output
     * @throws SQLException if the report query fails
     */
    public static ReportOutput capture(Report report) throws SQLException {

        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
            report.run();
        } finally {
            System.setOut(originalOut);
        }

        return new ReportOutput(buffer.toString(StandardCharsets.UTF_8));
    }

    /** The title line printed above the table. */
    public String title() {
        return title;
    }

    /** The column headings, in order. */
    public List<String> headings() {
        return headings;
    }

    /** The data rows; each row maps a column heading to the printed text. */
    public List<Map<String, String>> rows() {
        return rows;
    }

    /** The number shown on the "Total records" line. */
    public int totalRecords() {
        return totalRecords;
    }

    /** The first data row. */
    public Map<String, String> firstRow() {
        return rows.get(0);
    }

    /** The printed text of one column, one entry per row. */
    public List<String> column(String heading) {
        return rows.stream().map(row -> row.get(heading)).toList();
    }

    /** The row whose given column has the given text. */
    public Map<String, String> rowWhere(String heading, String value) {
        return rows.stream()
                .filter(row -> value.equals(row.get(heading)))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "No row has " + heading + " = '" + value + "'"));
    }

    /** The Population column as numbers; "No population recorded" counts as 0. */
    public List<Long> populations() {
        return column("Population").stream()
                .map(text -> text.chars().allMatch(c -> Character.isDigit(c) || c == ',')
                        ? Long.parseLong(text.replace(",", ""))
                        : 0L)
                .toList();
    }

    private static List<String> splitCells(String line) {

        String inner = line.substring(1, line.length() - 1);
        List<String> cells = new ArrayList<>();

        for (String cell : inner.split("\\|", -1)) {
            cells.add(cell.trim());
        }

        return cells;
    }

    private static int indexOfFirst(List<String> lines,
                                    java.util.function.Predicate<String> test) {
        for (int i = 0; i < lines.size(); i++) {
            if (test.test(lines.get(i))) {
                return i;
            }
        }
        throw new AssertionError("The report printed no title");
    }
}
