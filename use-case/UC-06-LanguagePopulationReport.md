# USE CASE: UC-06 Generate Language Population Report

## CHARACTERISTIC INFORMATION

### Goal in Context

As a user, I want to generate a language population report so that I can compare the number and percentage of people who speak selected languages.

### Scope

Population Reporting System.

### Level

Primary task.

### Preconditions

The population database is available and contains language and population data.

### Success End Condition

The language population report is generated and displayed successfully.

### Failed End Condition

The language population report is not generated.

### Primary Actor

User.

### Trigger

The user requests a language population report.

## MAIN SUCCESS SCENARIO

1. The user selects the Language Population Report option.
2. The system retrieves the language and population data from the database.
3. The system calculates the number of people speaking each required language.
4. The system calculates the percentage of the world population for each language.
5. The system orders the languages from the greatest number of speakers to the smallest.
6. The system displays the language population report.
7. The report shows the language, number of speakers, and percentage of the world population.

## EXTENSIONS

2. **Language data is unavailable**:
    1. The system informs the user that the language report cannot be generated.
    2. The use case ends.

2. **Database connection fails**:
    1. The system informs the user that the report cannot be generated.
    2. The use case ends.

## SUB-VARIATIONS

The report includes:

1. Chinese.
2. English.
3. Hindi.
4. Spanish.
5. Arabic.

## SCHEDULE

**DUE DATE**: Release 4.0