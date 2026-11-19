package com.team14.report.capitalcity;

import com.team14.report.ReportOutput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CapitalCityReportIntegrationTest {

    private static ReportOutput run(CapitalCityReport report)
            throws SQLException {
        return ReportOutput.capture(report);
    }

    private static long population(String value) {
        return Long.parseLong(value.replace(",", ""));
    }

    @Test
    @DisplayName("All capital cities are sorted by population, largest first")
    void allCapitalCitiesAreSortedByPopulation() throws SQLException {

        ReportOutput output =
                run(new AllCapitalCitiesInWorldReport());

        List<String> populations = output.column("Population");

        assertTrue(populations.size() > 0);

        for (int i = 1; i < populations.size(); i++) {
            assertTrue(
                    population(populations.get(i - 1))
                            >= population(populations.get(i)),
                    "Capital cities should be sorted by population descending"
            );
        }
    }

    @Test
    @DisplayName("Top N capital cities worldwide returns exactly N rows")
    void topCapitalCitiesWorldwideReturnsNRows() throws SQLException {

        int topN = 5;

        ReportOutput output =
                run(new TopCapitalCitiesWorldwideReport(topN));

        assertEquals(topN, output.totalRecords());
    }

    @Test
    @DisplayName("Top N capital cities worldwide are sorted by population")
    void topCapitalCitiesWorldwideAreSorted() throws SQLException {

        ReportOutput output =
                run(new TopCapitalCitiesWorldwideReport(5));

        List<String> populations = output.column("Population");

        for (int i = 1; i < populations.size(); i++) {
            assertTrue(
                    population(populations.get(i - 1))
                            >= population(populations.get(i)),
                    "Top capital cities should be sorted by population descending"
            );
        }
    }

    @Test
    @DisplayName("Top N capital cities in a continent returns N rows")
    void topCapitalCitiesByContinentReturnsNRows()
            throws SQLException {

        int topN = 5;

        ReportOutput output = run(
                new TopCapitalCitiesByContinentReport("Europe", topN)
        );

        assertEquals(topN, output.totalRecords());
    }

    @Test
    @DisplayName("Top N capital cities in a region returns N rows")
    void topCapitalCitiesByRegionReturnsNRows()
            throws SQLException {

        int topN = 5;

        ReportOutput output = run(
                new TopCapitalCitiesByRegionReport(
                        "Southern Europe", topN
                )
        );

        assertEquals(topN, output.totalRecords());
    }
}