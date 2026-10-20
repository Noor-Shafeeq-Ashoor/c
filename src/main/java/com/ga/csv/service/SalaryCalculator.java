package com.ga.csv.service;

import com.ga.csv.model.Employee;
import com.ga.csv.model.EmployeeProcessingResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

@Service
public class SalaryCalculator {

    private static final BigDecimal MINIMUM_PROJECT_COMPLETION = new BigDecimal("60");

    private static final BigDecimal YEARLY_INCREMENT_PERCENTAGE = new BigDecimal("2");

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private static final int MONEY_SCALE = 2;


    public EmployeeProcessingResult calculate(
            Employee employee,
            LocalDate processingDate) {

        Objects.requireNonNull(employee, "Employee cannot be null");
        Objects.requireNonNull(processingDate, "Processing date cannot be null");

        if (processingDate.isBefore(employee.getJoinedDate())) {
            throw new IllegalArgumentException("Processing date cannot be before the joining date");
        }

        // Count only fully completed years of service.
        int completedYears = Period.between(employee.getJoinedDate(), processingDate).getYears();

        // Employees below 60% project completion receive no increase.
        boolean eligibleForIncrease = employee.getCompletedProjectsPercentage()
                .compareTo(MINIMUM_PROJECT_COMPLETION) >= 0;

        BigDecimal incrementPercentage = BigDecimal.ZERO;
        BigDecimal incrementAmount = BigDecimal.ZERO;
        BigDecimal finalSalary = employee.getSalary();

        if (eligibleForIncrease) {

            // Calculate the role's base percentage.
            BigDecimal rolePercentage = BigDecimal.valueOf(
                    employee.getRole().getBaseIncrementPercentage());

            // Add 2% for every fully completed service year.
            incrementPercentage = rolePercentage.add(
                    YEARLY_INCREMENT_PERCENTAGE.multiply(
                            BigDecimal.valueOf(completedYears)));

            // Calculate the monetary increase and round to two decimals.
            incrementAmount = employee.getSalary()
                    .multiply(incrementPercentage)
                    .divide(ONE_HUNDRED, MONEY_SCALE,
                            RoundingMode.HALF_UP);

            // Add the increase to the original salary.
            finalSalary = employee.getSalary().add(incrementAmount)
                    .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        }

        return new EmployeeProcessingResult(
                employee,
                eligibleForIncrease,
                completedYears,
                incrementPercentage,
                incrementAmount,
                finalSalary
        );
    }
}
