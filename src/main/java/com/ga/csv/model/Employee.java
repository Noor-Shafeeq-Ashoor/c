package com.ga.csv.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;


public class Employee {

    private final String name;
    private final BigDecimal salary;
    private final LocalDate joinedDate;
    private final EmployeeRole role;
    private final BigDecimal completedProjectsPercentage;


    public Employee(
            String name,
            BigDecimal salary,
            LocalDate joinedDate,
            EmployeeRole role,
            BigDecimal completedProjectsPercentage) {

        this.name = Objects.requireNonNull(
                name, "Employee name cannot be null").trim();

        this.salary = Objects.requireNonNull(
                salary, "Salary cannot be null");

        this.joinedDate = Objects.requireNonNull(
                joinedDate, "Joining date cannot be null");

        this.role = Objects.requireNonNull(
                role, "Employee role cannot be null");

        this.completedProjectsPercentage = Objects.requireNonNull(
                completedProjectsPercentage,
                "Project completion percentage cannot be null");

        if (this.name.isEmpty()) {
            throw new IllegalArgumentException(
                    "Employee name cannot be empty");
        }

        if (salary.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Salary cannot be negative");
        }

        if (completedProjectsPercentage.compareTo(BigDecimal.ZERO) < 0
                || completedProjectsPercentage.compareTo(
                new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException(
                    "Project completion must be between 0 and 100");
        }

        if (joinedDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Joining date cannot be in the future");
        }
    }

    public String getName() {
        return name;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public LocalDate getJoinedDate() {
        return joinedDate;
    }

    public EmployeeRole getRole() {
        return role;
    }

    public BigDecimal getCompletedProjectsPercentage() {
        return completedProjectsPercentage;
    }
}
