package com.team14.database;

import java.util.Scanner;

public class UserInput {

    private final Scanner scanner;

    public UserInput(Scanner scanner) {
        this.scanner = scanner;
    }


    // ==========================================
    // MENU CHOICE
    // ==========================================

    public int getMenuChoice() {

        while (true) {

            System.out.print("Enter your choice: ");

            String input = scanner.nextLine().trim();

            try {

                int choice = Integer.parseInt(input);

                if (choice >= 0) {
                    return choice;
                }

                System.out.println(
                        "Please enter a valid number.");

            } catch (NumberFormatException e) {

                System.out.println(
                        "Invalid input. Please enter a number.");
            }
        }
    }


    // ==========================================
    // POSITIVE NUMBER
    // ==========================================

    public int getPositiveNumber(String message) {

        while (true) {

            System.out.print(message);

            String input = scanner.nextLine().trim();

            try {

                int number = Integer.parseInt(input);

                if (number > 0) {
                    return number;
                }

                System.out.println(
                        "Please enter a number greater than 0.");

            } catch (NumberFormatException e) {

                System.out.println(
                        "Invalid number. Please try again.");
            }
        }
    }


    // ==========================================
    // TEXT INPUT
    // ==========================================

    public String getText(String message) {

        while (true) {

            System.out.print(message);

            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println(
                    "Input cannot be empty. Please try again.");
        }
    }


    // ==========================================
    // LOCATION INPUTS
    // ==========================================

    public String getContinent() {
        return getText("Enter continent: ");
    }

    public String getRegion() {
        return getText("Enter region: ");
    }

    public String getCountry() {
        return getText("Enter country: ");
    }

    public String getDistrict() {
        return getText("Enter district: ");
    }

    public String getCity() {
        return getText("Enter city: ");
    }


    // ==========================================
    // TOP N INPUT
    // ==========================================

    public int getTopN() {

        return getPositiveNumber(
                "Enter number of results (N): ");
    }
}