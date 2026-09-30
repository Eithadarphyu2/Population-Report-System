package com.team14.database;

public class ReportDisplay {

    // ==========================================
    // COUNTRY REPORT
    // ==========================================

    public static void printCountryHeader() {
        System.out.println();
        System.out.println("==========================================================================================");
        System.out.printf("%-8s %-25s %-18s %-25s %-15s %-20s%n",
                "Code",
                "Name",
                "Continent",
                "Region",
                "Population",
                "Capital");
        System.out.println("==========================================================================================");
    }

    public static void printCountry(
            String code,
            String name,
            String continent,
            String region,
            long population,
            String capital) {

        System.out.printf("%-8s %-25s %-18s %-25s %-15d %-20s%n",
                code,
                name,
                continent,
                region,
                population,
                capital);
    }


    // ==========================================
    // CITY REPORT
    // ==========================================

    public static void printCityHeader() {
        System.out.println();
        System.out.println("================================================================================");
        System.out.printf("%-25s %-25s %-25s %-15s %-20s%n",
                "Name",
                "Country",
                "District",
                "Population",
                "Capital");
        System.out.println("================================================================================");
    }

    public static void printCity(
            String name,
            String country,
            String district,
            long population,
            String capital) {

        System.out.printf("%-25s %-25s %-25s %-15d %-20s%n",
                name,
                country,
                district,
                population,
                capital);
    }


    // ==========================================
    // CAPITAL CITY REPORT
    // ==========================================

    public static void printCapitalHeader() {
        System.out.println();
        System.out.println("==================================================================");
        System.out.printf("%-30s %-25s %-15s%n",
                "Name",
                "Country",
                "Population");
        System.out.println("==================================================================");
    }

    public static void printCapital(
            String name,
            String country,
            long population) {

        System.out.printf("%-30s %-25s %-15d%n",
                name,
                country,
                population);
    }


    // ==========================================
    // POPULATION REPORT
    // ==========================================

    public static void printPopulationHeader() {
        System.out.println();
        System.out.println("==========================================================================================");
        System.out.printf("%-25s %-18s %-25s %-25s%n",
                "Location",
                "Total Population",
                "Population in Cities",
                "Population outside Cities");
        System.out.println("==========================================================================================");
    }

    public static void printPopulation(
            String location,
            long totalPopulation,
            long cityPopulation,
            double cityPercentage,
            long nonCityPopulation,
            double nonCityPercentage) {

        System.out.printf(
                "%-25s %-18d %-12d (%6.2f%%) %-12d (%6.2f%%)%n",
                location,
                totalPopulation,
                cityPopulation,
                cityPercentage,
                nonCityPopulation,
                nonCityPercentage);
    }


    // ==========================================
    // GENERAL DISPLAY METHODS
    // ==========================================

    public static void printTitle(String title) {

        System.out.println();
        System.out.println("========================================");
        System.out.println(title);
        System.out.println("========================================");
    }

    public static void printNoResults() {

        System.out.println();
        System.out.println("No results found.");
    }
}