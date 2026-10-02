package com.team14.report;

import java.sql.SQLException;

/**
 * A report that can be printed by the application.
 * Every report in the system implements this interface, so App can run
 * them all in the same way.
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
}
