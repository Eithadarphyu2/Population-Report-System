package com.team14.config;

/**
 * Values used by the parameterized reports (continent, region, top N, ...).
 *
 * The application runs in Docker without user input, so the values are
 * defined here, in one place, and every report reads them from this class.
 */
public final class ReportParameters {

    /** Number of rows for the "top N" reports. */
    public static final int TOP_N = 5;

    /** Continent used by the continent reports. */
    public static final String CONTINENT = "Europe";

    /** Region used by the region reports. */
    public static final String REGION = "British Islands";

    /** Country used by the country reports. */
    public static final String COUNTRY = "United Kingdom";

    /** District used by the district reports. */
    public static final String DISTRICT = "England";

    /** City used by the city population lookup. */
    public static final String CITY = "Edinburgh";

    private ReportParameters() {
        throw new UnsupportedOperationException(
                "Utility class cannot be instantiated"
        );
    }
}
