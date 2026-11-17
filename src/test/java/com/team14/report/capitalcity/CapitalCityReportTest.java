
package com.team14.report.capitalcity;

import com.team14.input.UserInput;
import com.team14.report.ReportDefinition;
import com.team14.report.ReportDisplay.Column;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CapitalCityReportTest {

    private static UserInput typing(String... lines) {
        return new UserInput(
                new Scanner(String.join("\n", lines) + "\n")
        );
    }

    // --------------------------------------------------
    // Common Capital City Report Tests
    // --------------------------------------------------

    @Test
    void baseReportUsesCommonCapitalCityQueryAndColumns() {
        CapitalCityReport report = new AllCapitalCitiesInWorldReport();

        String select = ReportDefinition.select(report);
        List<Column> columns = ReportDefinition.columns(report);

        assertTrue(select.contains(
                "SELECT ci.Name, c.Name AS Country, ci.Population"
        ));
        assertTrue(select.contains("FROM country c"));
        assertTrue(select.contains(
                "JOIN city ci ON c.Capital = ci.ID"
        ));

        assertEquals(3, columns.size());
    }

    // --------------------------------------------------
    // All Capital Cities in the World
    // --------------------------------------------------

    @Test
    void allCapitalCitiesInWorldReportHasCorrectTitleAndOrdering() {
        AllCapitalCitiesInWorldReport report =
                new AllCapitalCitiesInWorldReport();

        assertEquals(
                "All Capital Cities in the World by Population",
                report.title()
        );

        assertTrue(
                ReportDefinition.clauses(report)
                        .contains("ORDER BY ci.Population DESC")
        );
    }

    @Test
    void allCapitalCitiesInWorldReportHasNoParameters() {
        AllCapitalCitiesInWorldReport report =
                new AllCapitalCitiesInWorldReport();

        assertArrayEquals(
                new Object[]{},
                ReportDefinition.parameters(report)
        );
    }

    // --------------------------------------------------
    // Capital Cities by Continent
    // --------------------------------------------------

    @Test
    void capitalCitiesByContinentReportUsesContinentFilter() {
        CapitalCitiesByContinentReport report =
                new CapitalCitiesByContinentReport("Europe");

        assertEquals(
                "Capital Cities in Europe by Population",
                report.title()
        );

        String clauses = ReportDefinition.clauses(report);

        assertTrue(clauses.contains("WHERE c.Continent = ?"));
        assertTrue(clauses.contains("ORDER BY ci.Population DESC"));

        assertArrayEquals(
                new Object[]{"Europe"},
                ReportDefinition.parameters(report)
        );
    }

    @Test
    void capitalCitiesByContinentReportRejectsBlankContinent() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CapitalCitiesByContinentReport(" ")
        );
    }

    @Test
    void capitalCitiesByContinentReportRejectsNullUserInput() {
        assertThrows(
                NullPointerException.class,
                () -> new CapitalCitiesByContinentReport((UserInput) null)
        );
    }

    @Test
    void capitalCitiesByContinentReportAcceptsUserInput() {
        CapitalCitiesByContinentReport report =
                new CapitalCitiesByContinentReport(
                        typing("Europe")
                );

        assertEquals(
                "Capital Cities in Europe by Population",
                report.title()
        );

        assertArrayEquals(
                new Object[]{"Europe"},
                ReportDefinition.parameters(report)
        );
    }

    // --------------------------------------------------
    // Capital Cities by Region
    // --------------------------------------------------

    @Test
    void capitalCitiesByRegionReportUsesRegionFilter() {
        CapitalCitiesByRegionReport report =
                new CapitalCitiesByRegionReport("Southern Europe");

        assertEquals(
                "Capital Cities in Southern Europe by Population",
                report.title()
        );

        String clauses = ReportDefinition.clauses(report);

        assertTrue(clauses.contains("WHERE c.Region = ?"));
        assertTrue(clauses.contains("ORDER BY ci.Population DESC"));

        assertArrayEquals(
                new Object[]{"Southern Europe"},
                ReportDefinition.parameters(report)
        );
    }

    @Test
    void capitalCitiesByRegionReportRejectsBlankRegion() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CapitalCitiesByRegionReport(" ")
        );
    }

    @Test
    void capitalCitiesByRegionReportRejectsNullUserInput() {
        assertThrows(
                NullPointerException.class,
                () -> new CapitalCitiesByRegionReport((UserInput) null)
        );
    }

    @Test
    void capitalCitiesByRegionReportAcceptsUserInput() {
        CapitalCitiesByRegionReport report =
                new CapitalCitiesByRegionReport(
                        typing("Southern Europe")
                );

        assertEquals(
                "Capital Cities in Southern Europe by Population",
                report.title()
        );

        assertArrayEquals(
                new Object[]{"Southern Europe"},
                ReportDefinition.parameters(report)
        );
    }

    // --------------------------------------------------
    // Top Capital Cities by Continent
    // --------------------------------------------------

    @Test
    void topCapitalCitiesByContinentReportUsesContinentAndLimitParameters() {
        TopCapitalCitiesByContinentReport report =
                new TopCapitalCitiesByContinentReport("Europe", 5);

        assertEquals(
                "Top 5 Populated Capital Cities in Europe",
                report.title()
        );

        String clauses = ReportDefinition.clauses(report);

        assertTrue(clauses.contains("WHERE c.Continent = ?"));
        assertTrue(clauses.contains("ORDER BY ci.Population DESC"));
        assertTrue(clauses.contains("LIMIT ?"));

        assertArrayEquals(
                new Object[]{"Europe", 5},
                ReportDefinition.parameters(report)
        );
    }

    @Test
    void topCapitalCitiesByContinentReportRejectsInvalidLimit() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new TopCapitalCitiesByContinentReport("Europe", 0)
        );
    }

    // --------------------------------------------------
    // Top Capital Cities by Region
    // --------------------------------------------------

    @Test
    void topCapitalCitiesByRegionReportUsesRegionAndLimitParameters() {
        TopCapitalCitiesByRegionReport report =
                new TopCapitalCitiesByRegionReport("Southern Europe", 5);

        assertEquals(
                "Top 5 Populated Capital Cities in Southern Europe",
                report.title()
        );

        String clauses = ReportDefinition.clauses(report);

        assertTrue(clauses.contains("WHERE c.Region = ?"));
        assertTrue(clauses.contains("ORDER BY ci.Population DESC"));
        assertTrue(clauses.contains("LIMIT ?"));

        assertArrayEquals(
                new Object[]{"Southern Europe", 5},
                ReportDefinition.parameters(report)
        );
    }

    @Test
    void topCapitalCitiesByRegionReportRejectsInvalidLimit() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new TopCapitalCitiesByRegionReport(
                        "Southern Europe", 0
                )
        );
    }

    // --------------------------------------------------
    // Top Capital Cities Worldwide
    // --------------------------------------------------

    @Test
    void topCapitalCitiesWorldwideReportUsesLimitParameter() {
        TopCapitalCitiesWorldwideReport report =
                new TopCapitalCitiesWorldwideReport(5);

        assertEquals(
                "Top 5 Populated Capital Cities Worldwide",
                report.title()
        );

        String clauses = ReportDefinition.clauses(report);

        assertTrue(clauses.contains("ORDER BY ci.Population DESC"));
        assertTrue(clauses.contains("LIMIT ?"));

        assertArrayEquals(
                new Object[]{5},
                ReportDefinition.parameters(report)
        );
    }

    @Test
    void topCapitalCitiesWorldwideReportRejectsInvalidLimit() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new TopCapitalCitiesWorldwideReport(0)
        );
    }

    @Test
    void topCapitalCitiesWorldwideReportRejectsNullUserInput() {
        assertThrows(
                NullPointerException.class,
                () -> new TopCapitalCitiesWorldwideReport((UserInput) null)
        );
    }

    @Test
    void topCapitalCitiesWorldwideReportAcceptsUserInput() {
        TopCapitalCitiesWorldwideReport report =
                new TopCapitalCitiesWorldwideReport(
                        typing("5")
                );

        assertEquals(
                "Top 5 Populated Capital Cities Worldwide",
                report.title()
        );

        assertArrayEquals(
                new Object[]{5},
                ReportDefinition.parameters(report)
        );
    }
}
