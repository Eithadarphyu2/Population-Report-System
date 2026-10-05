import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CountryReport {

    /**
     * Gets the top N populated countries in a selected region.
     *
     * @param con The database connection object
     * @param region The target region name (e.g., 'Caribbean', 'Western Europe')
     * @param n The number of top populated countries to retrieve
     * @return List of Country objects matching the criteria
     */
    public List<Country> getTopNCountriesByRegion(Connection con, String region, int n) {
        List<Country> countries = new ArrayList<>();

        if (con == null || region == null || n <= 0) {
            System.out.println("Invalid input parameters provided.");
            return countries;
        }

        String sql = "SELECT country.Code, country.Name, country.Continent, country.Region, " +
                "country.Population, city.Name AS Capital " +
                "FROM country " +
                "LEFT JOIN city ON country.Capital = city.ID " +
                "WHERE country.Region = ? " +
                "ORDER BY country.Population DESC " +
                "LIMIT ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, region);
            stmt.setInt(2, n);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String code = rs.getString("Code");
                    String name = rs.getString("Name");
                    String continent = rs.getString("Continent");
                    String reg = rs.getString("Region");
                    int population = rs.getInt("Population");
                    String capital = rs.getString("Capital");

                    // Handle potential NULL capitals
                    if (capital == null) {
                        capital = "N/A";
                    }

                    Country country = new Country(code, name, continent, reg, population, capital);
                    countries.add(country);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching top N countries by region: " + e.getMessage());
        }

        return countries;
    }

    /**
     * Prints the country report formatted with required columns.
     *
     * @param countries List of Country objects to display
     */
    public void printCountryReport(List<Country> countries) {
        if (countries == null || countries.isEmpty()) {
            System.out.println("No countries found to display.");
            return;
        }

        System.out.println(String.format("%-6s %-35s %-20s %-25s %-15s %-25s",
                "Code", "Name", "Continent", "Region", "Population", "Capital"));
        System.out.println("-".repeat(130));

        for (Country country : countries) {
            System.out.println(String.format("%-6s %-35s %-20s %-25s %-15d %-25s",
                    country.getCode(),
                    country.getName(),
                    country.getContinent(),
                    country.getRegion(),
                    country.getPopulation(),
                    country.getCapital()));
        }
    }
}