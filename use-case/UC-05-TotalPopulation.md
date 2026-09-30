         # USE CASE: UC-05 Generate Population Total Report

## CHARACTERISTIC INFORMATION

### Goal in Context

As a user, I want to generate population total reports so that I can view the total population of the world, a continent, a region, a country, a district, or a city.

### Scope

Population Reporting System.

### Level

Primary task.

### Preconditions

The population database is available and contains the required population data.

### Success End Condition

The requested population total is calculated and displayed successfully.

### Failed End Condition

The requested population total is not generated.

### Primary Actor

User.

### Trigger

The user requests a population total report.

## MAIN SUCCESS SCENARIO

1. The user selects the Population Total Report option.
2. The system displays the available population total report types:
    - Total population of the world.
    - Total population of a selected continent.
    - Total population of a selected region.
    - Total population of a selected country.
    - Total population of a selected district.
    - Total population of a selected city.
3. The user selects the required population total report type.
4. The user provides any required input, such as continent, region, country, district, or city.
5. The system retrieves the relevant population data from the database.
6. The system calculates or retrieves the total population for the selected level.
7. The system displays the population total.
8. The report shows the name of the selected geographical area and its total population.

## EXTENSIONS

4\. **Invalid input is provided**:
1. The system informs the user that the input is invalid.
2. The user enters valid input.
3. The use case continues from Step 5.

5\. **No matching population data is found**:
1. The system informs the user that no matching population data was found.
2. The use case ends.

5\. **Database connection fails**:
1. The system informs the user that the report cannot be generated.
2. The use case ends.

## SUB-VARIATIONS

The user may request:
1. The total population of the world.
2. The total population of a selected continent.
3. The total population of a selected region.
4. The total population of a selected country.
5. The total population of a selected district.
6. The total population of a selected city.

## SCHEDULE

**DUE DATE**: Release 3.0