# Concurrent CSV Data Processor

A Spring Boot application that reads employee information from a CSV file, calculates salary increments based on predefined business rules, processes employees concurrently, and writes the results to an output CSV file.

## Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Business Rules](#business-rules)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Input CSV Format](#input-csv-format)
- [Output CSV Format](#output-csv-format)
- [Configuration](#configuration)
- [How to Run](#how-to-run)
- [Concurrency Implementation](#concurrency-implementation)
- [Error Handling and Validation](#error-handling-and-validation)
- [Future Improvements](#future-improvements)

## Project Overview

The Concurrent CSV Data Processor automates employee salary increment calculations using Java, Spring Boot, and multithreading.

The application reads employee records from a CSV file, validates employee information, calculates salary increments according to business rules, and exports the processing results to a new CSV file.

Concurrency allows multiple employee records to be processed by a thread pool while synchronization mechanisms help coordinate access to shared resources.

## Features

- Read employee records from CSV files.
- Validate employee information and project completion percentages.
- Calculate completed years of service.
- Determine salary increment eligibility.
- Calculate salary increment percentages, increment amounts, and final salaries.
- Process employees concurrently using a thread pool.
- Control simultaneous task execution with a semaphore.
- Protect shared result collections using a lock.
- Track completed tasks using an atomic counter.
- Export processing results to a CSV file.
- Configure processing settings through `application.properties`.

## Business Rules

### 1. Project Completion Eligibility

An employee qualifies for a salary increment when at least 60% of their projects are completed.

| Project completion | Eligibility |
|---|---|
| Below 60% | Not eligible |
| Exactly 60% | Eligible |
| Above 60% | Eligible |

Employees who are not eligible retain their original salaries.

### 2. Base Increment by Role

| Employee role | Base increment |
|---|---:|
| Director | 5% |
| Manager | 2% |
| Employee | 1% |

### 3. Years of Service

Employees receive an additional 2% for each completed year of service.

Only full years are counted. Partial years do not contribute to the additional increment.

### 4. Salary Calculation

For eligible employees:

**Total Increment Percentage**

`Role Base Percentage + (Completed Years × 2%)`

**Increment Amount**

`Original Salary × Total Increment Percentage / 100`

**Final Salary**

`Original Salary + Increment Amount`

For employees who are not eligible, the increment percentage and increment amount are zero, and the final salary remains unchanged.

Salary calculations use `BigDecimal` to improve decimal precision. Monetary values are rounded to two decimal places using `RoundingMode.HALF_UP`.

## Technologies Used

- **Java 17** — Core programming language.
- **Spring Boot 3.3.5** — Application configuration and dependency injection.
- **Apache Commons CSV 1.14.1** — CSV parsing and generation.
- **Maven** — Dependency and build management.
- **Java Concurrency Utilities** — Multithreaded processing and synchronization.
- **JUnit** — Testing framework through Spring Boot Test.

## Project Structure

```text
concurrent-csv-processor/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/ga/csv/
│   │   │       ├── CsvApplication.java
│   │   │       ├── config/
│   │   │       │   └── ProcessingConfig.java
│   │   │       ├── model/
│   │   │       │   ├── Employee.java
│   │   │       │   ├── EmployeeRole.java
│   │   │       │   └── EmployeeProcessingResult.java
│   │   │       ├── reader/
│   │   │       │   └── CsvEmployeeReader.java
│   │   │       ├── service/
│   │   │       │   ├── SalaryCalculator.java
│   │   │       │   ├── EmployeeProcessingService.java
│   │   │       │   └── ProcessingCoordinator.java
│   │   │       └── writer/
│   │   │           └── CsvResultWriter.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── employees.csv
│   └── test/
│       └── java/
├── pom.xml
└── README.md
```

*Note: Adjust the package names and file list to match the final project structure.*

## Input CSV Format

The input file is named `employees.csv` by default.

It must contain a header row with the following columns:

```csv
id,name,salary,joinedDate,role,completedProjectsPercentage
1,Alice,52000.0,2019-05-12,Employee,0.8
2,Bob,68000.0,2020-08-23,Manager,0.6
3,Charlie,75000.0,2021-03-19,Director,0.7
```

### Input Fields

| Column | Description |
|---|---|
| `id` | Employee identifier |
| `name` | Employee name |
| `salary` | Original salary |
| `joinedDate` | Joining date in `yyyy-MM-dd` format |
| `role` | Employee role: `Employee`, `Manager`, or `Director` |
| `completedProjectsPercentage` | Project completion ratio or percentage, according to the application's input conversion rules |

For decimal ratios, `0.8` represents 80% and `0.6` represents 60%.

The employee ID is included in the input format but is not included in the current output.

## Output CSV Format

The application generates `employee_salary_results.csv` by default.

The output contains the following columns:

| Column | Description |
|---|---|
| `name` | Employee name |
| `originalSalary` | Original salary |
| `role` | Employee role |
| `completedProjectsPercentage` | Project completion percentage |
| `eligibleForIncrease` | Whether the employee qualifies for an increment |
| `completedYears` | Completed years of service |
| `incrementPercentage` | Total salary increment percentage |
| `incrementAmount` | Calculated salary increase |
| `finalSalary` | Salary after applying the increment |

Example:

```csv
name,originalSalary,role,completedProjectsPercentage,eligibleForIncrease,completedYears,incrementPercentage,incrementAmount,finalSalary
Alice,52000.00,EMPLOYEE,80.0,true,7,15,7800.00,59800.00
Bob,68000.00,MANAGER,60.0,true,6,14,9520.00,77520.00
Diana,60000.00,MANAGER,50.0,false,7,0,0.00,60000.00
```

## Configuration

The application uses `src/main/resources/application.properties` to configure processing behavior.

```properties
spring.application.name=concurrent-csv-processor

app.processing.thread-pool-size=4
app.processing.max-concurrent-tasks=2

app.csv.input-file=employees.csv
app.csv.output-file=employee_salary_results.csv

# Optional fixed processing date
# app.processing-date=2026-10-20
```

| Property | Description | Default |
|---|---|---|
| `app.processing.thread-pool-size` | Number of worker threads | `4` |
| `app.processing.max-concurrent-tasks` | Maximum simultaneous processing tasks | `2` |
| `app.csv.input-file` | Input CSV path | `employees.csv` |
| `app.csv.output-file` | Output CSV path | `employee_salary_results.csv` |

The thread pool size and maximum number of concurrent tasks are separate settings. For example, four worker threads can be created while the semaphore limits active processing to two tasks at a time.

## How to Run

### Prerequisites

- Java Development Kit (JDK) 17 or later.
- IntelliJ IDEA or another Java IDE.
- Maven support.
- The input CSV file.


### Build the Project

Using the IntelliJ Maven tool window:

1. Expand **Lifecycle**.
2. Run `clean`.
3. Run `compile`.
4. Run `test` when tests are available.

If Maven is installed and configured in your terminal, you can also run:

```bash
mvn clean test
mvn spring-boot:run
```

## Concurrency Implementation

The application uses Java concurrency utilities to coordinate employee processing.

### ExecutorService

Creates a fixed-size thread pool and allows employee processing tasks to run concurrently.

### Semaphore

Limits the number of tasks allowed to process simultaneously, independently of the total number of worker threads.

### ReentrantLock

Protects shared result collection updates so that concurrent tasks do not modify the collection unsafely.

### AtomicInteger

Tracks completed processing tasks safely across multiple threads.

### Future

Allows the application to wait for submitted tasks to finish and detect task failures before exporting the results.

These mechanisms demonstrate thread-pool management, synchronization, shared-state protection, and coordination between concurrent tasks.

## Error Handling and Validation

The application validates employee information before processing.

Examples of validation include:

- Missing required CSV headers.
- Invalid numeric values.
- Invalid employee roles.
- Invalid joining dates.
- Negative salaries.
- Project completion values outside the accepted range.
- Joining dates in the future.

Invalid CSV records should be reported and skipped according to the reader's error-handling implementation. File access errors and missing required headers should be surfaced so that processing does not continue with an invalid input file.

## Future Improvements

Potential enhancements include:

- Add comprehensive unit tests for salary calculations and CSV parsing.
- Preserve input order in the output while processing records concurrently.
- Include employee IDs in the output.
- Generate a summary of eligible and ineligible employees.
- Improve logging using SLF4J instead of `System.out` and `System.err`.
- Support large CSV files through streaming or batched processing.
- Add configurable input formats and validation policies.
- Improve exception reporting and application shutdown handling.
