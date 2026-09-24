# USE CASE: UC-02 Generate City Report

## CHARACTERISTIC INFORMATION

### Goal in Context

As a user, I want to generate city population reports so that I can view and compare city population information.

### Scope

Population Reporting System.

### Level

Primary task.

### Preconditions

The population database is available and contains city data.

### Success End Condition

The requested city report is generated and displayed successfully.

### Failed End Condition

The requested city report is not generated.

### Primary Actor

User.

### Trigger

The user requests a city population report.

## MAIN SUCCESS SCENARIO

1. The user selects the City Report option.
2. The system displays the available city report types:
    - All cities in the world.
    - All cities in a selected continent.
    - All cities in a selected region.
    - All cities in a selected country.
    - All cities in a selected district.
    - Top N populated cities in the world.
    - Top N populated cities in a selected continent.
    - Top N populated cities in a selected region.
    - Top N populated cities in a selected country.
    - Top N populated cities in a selected district.
3. The user selects the required city report type.
4. The user provides any required input, such as continent, region, country, district, or the number N.
5. The system retrieves the matching city data from the database.
6. The system orders the cities by population from largest to smallest.
7. The system displays the city report.
8. The report shows the city name, country, district, and population.

## EXTENSIONS

4\. **Invalid input is provided**:
1. The system informs the user that the input is invalid.
2. The user enters valid input.
3. The use case continues from Step 5.

5\. **No matching city data is found**:
1. The system informs the user that no matching cities were found.
2. The use case ends.

5\. **Database connection fails**:
1. The system informs the user that the report cannot be generated.
2. The use case ends.

## SUB-VARIATIONS

The user may request:
1. All cities in the world.
2. All cities in a selected continent.
3. All cities in a selected region.
4. All cities in a selected country.
5. All cities in a selected district.
6. The top N populated cities in the world.
7. The top N populated cities in a selected continent.
8. The top N populated cities in a selected region.
9. The top N populated cities in a selected country.
10. The top N populated cities in a selected district.

## SCHEDULE

**DUE DATE**: Release 2.0