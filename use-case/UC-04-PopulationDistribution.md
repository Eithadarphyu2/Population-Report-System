# USE CASE: UC-04 Generate Population Distribution Report

## CHARACTERISTIC INFORMATION

### Goal in Context

As a user, I want to generate population distribution reports so that I can compare total, city and non-city populations.

### Scope

Population Reporting System.

### Level

Primary task.

### Preconditions

The population database is available and contains country and city population data.

### Success End Condition

The requested population distribution report is generated and displayed successfully.

### Failed End Condition

The requested population distribution report is not generated.

### Primary Actor

User.

### Trigger

The user requests a population distribution report.

## MAIN SUCCESS SCENARIO

1. The user selects the Population Distribution Report option.
2. The system displays the available population distribution report types.
3. The user selects the required report type.
4. The user provides any required input, such as a continent, region, or country.
5. The system retrieves the relevant population data from the database.
6. The system calculates the total population, population living in cities, and population not living in cities.
7. The system calculates the percentage of population living in cities and not living in cities.
8. The system displays the population distribution report.
9. The report shows the name, total population, city population and percentage, and non-city population and percentage.

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

1. Population distribution for each continent.
2. Population distribution for each region.
3. Population distribution for each country.

## SCHEDULE

**DUE DATE**: Release 3.0