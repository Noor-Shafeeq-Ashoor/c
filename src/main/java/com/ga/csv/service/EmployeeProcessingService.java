package com.ga.csv.service;

import com.ga.csv.model.Employee;
import com.ga.csv.model.EmployeeProcessingResult;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Coordinates salary processing for individual employees.
 * The service delegates the actual salary calculation to
 * SalaryCalculator, keeping business logic in one place.
 */

@Service
public class EmployeeProcessingService {

    private final SalaryCalculator salaryCalculator;
    private final LocalDate processingDate;

    public EmployeeProcessingService(
            SalaryCalculator salaryCalculator,
            @Value("${app.processing-date:#{T(java.time.LocalDate).now()}}")
            LocalDate processingDate) {

        this.salaryCalculator = Objects.requireNonNull(
                salaryCalculator,
                "Salary calculator cannot be null");

        this.processingDate = Objects.requireNonNull(
                processingDate,
                "Processing date cannot be null");
    }

    public EmployeeProcessingResult processEmployee(Employee employee) {
        return salaryCalculator.calculate(employee, processingDate);
    }
}

