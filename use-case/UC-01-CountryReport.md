# USE CASE: UC-01 Generate Country Report

## CHARACTERISTIC INFORMATION

### Goal in Context

As a user, I want to generate country population reports so that I can view and compare country population information.

### Scope

Population Reporting System.

### Level

Primary task.

### Preconditions

The population database is available and contains country data.

### Success End Condition

The requested country report is generated and displayed successfully.

### Failed End Condition

The requested country report is not generated.

### Primary Actor

User.

### Trigger

The user requests a country population report.

## MAIN SUCCESS SCENARIO

1. The user selects the Country Report option.
2. The system displays the available country report types:
    - All countries in the world.
    - All countries in a selected continent.
    - All countries in a selected region.
    - Top N populated countries in the world.
    - Top N populated countries in a selected continent.
    - Top N populated countries in a selected region.
3. The user selects the required country report type.
4. The user provides any required input, such as a continent, region, or the number N.
5. The system retrieves the matching country data from the database.
6. The system orders the countries by population from largest to smallest.
7. The system displays the country report.
8. The report shows the country code, name, continent, region, population, and capital.

## EXTENSIONS

## EXTENSIONS

4\. **Invalid input is provided**:
1. The system informs the user that the input is invalid.
2. The user enters valid input.
3. The use case continues from Step 5.

5\. **No matching country data is found**:
1. The system informs the user that no matching countries were found.
2. The use case ends.

5\. **Database connection fails**:
1. The system informs the user that the report cannot be generated.
2. The use case ends.

## SUB-VARIATIONS

The user may request:
1. All countries in the world.
2. All countries in a selected continent.
3. All countries in a selected region.
4. The top N populated countries in the world.
5. The top N populated countries in a selected continent.
6. The top N populated countries in a selected region.

## SCHEDULE

**DUE DATE**: Release 1.0