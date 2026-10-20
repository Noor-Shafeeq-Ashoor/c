package com.ga.csv.model;

import java.math.BigDecimal;
import java.util.Objects;

//Stores the result of processing an employee's salary.

public class EmployeeProcessingResult {

    private final Employee employee;
    private final boolean eligibleForIncrease;
    private final int completedYears;
    private final BigDecimal incrementPercentage;
    private final BigDecimal incrementAmount;
    private final BigDecimal finalSalary;

    public EmployeeProcessingResult(
            Employee employee,
            boolean eligibleForIncrease,
            int completedYears,
            BigDecimal incrementPercentage,
            BigDecimal incrementAmount,
            BigDecimal finalSalary) {

        this.employee = Objects.requireNonNull(employee);
        this.eligibleForIncrease = eligibleForIncrease;
        this.completedYears = completedYears;
        this.incrementPercentage = Objects.requireNonNull(incrementPercentage);
        this.incrementAmount = Objects.requireNonNull(incrementAmount);
        this.finalSalary = Objects.requireNonNull(finalSalary);

        if (completedYears < 0) {
            throw new IllegalArgumentException(
                    "Completed years cannot be negative");
        }

        if (incrementPercentage.compareTo(BigDecimal.ZERO) < 0
                || incrementAmount.compareTo(BigDecimal.ZERO) < 0
                || finalSalary.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Salary calculation values cannot be negative");
        }
    }

    public Employee getEmployee() {
        return employee;
    }

    public boolean isEligibleForIncrease() {
        return eligibleForIncrease;
    }

    public int getCompletedYears() {
        return completedYears;
    }

    public BigDecimal getIncrementPercentage() {
        return incrementPercentage;
    }

    public BigDecimal getIncrementAmount() {
        return incrementAmount;
    }

    public BigDecimal getFinalSalary() {
        return finalSalary;
    }
}
