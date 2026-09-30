# USE CASE: UC-03 Generate Capital City Report

## CHARACTERISTIC INFORMATION

### Goal in Context

As a user, I want to generate capital city population reports so that I can view and compare the populations of capital cities.

### Scope

Population Reporting System.

### Level

Primary task.

### Preconditions

The population database is available and contains country and capital city data.

### Success End Condition

The requested capital city report is generated and displayed successfully.

### Failed End Condition

The requested capital city report is not generated.

### Primary Actor

User.

### Trigger

The user requests a capital city population report.

## MAIN SUCCESS SCENARIO

1. The user selects the Capital City Report option.
2. The system displays the available capital city report types:
   - All capital cities in the world.
   - All capital cities in a selected continent.
   - All capital cities in a selected region.
   - Top N populated capital cities in the world.
   - Top N populated capital cities in a selected continent.
   - Top N populated capital cities in a selected region.
3. The user selects the required capital city report type.
4. The user provides any required input, such as continent, region, or the number N.
5. The system retrieves the matching capital city data from the database.
6. The system orders the capital cities by population from largest to smallest where required.
7. The system displays the capital city report.
8. The report shows the capital city name, country, and population.

## EXTENSIONS

4\. **Invalid input is provided**:
1. The system informs the user that the input is invalid.
2. The user enters valid input.
3. The use case continues from Step 5.

5\. **No matching capital city data is found**:
1. The system informs the user that no matching capital cities were found.
2. The use case ends.

5\. **Database connection fails**:
1. The system informs the user that the report cannot be generated.
2. The use case ends.

## SUB-VARIATIONS

The user may request:
1. All capital cities in the world.
2. All capital cities in a selected continent.
3. All capital cities in a selected region.
4. The top N populated capital cities in the world.
5. The top N populated capital cities in a selected continent.
6. The top N populated capital cities in a selected region.

## SCHEDULE

**DUE DATE**: Release 2.0