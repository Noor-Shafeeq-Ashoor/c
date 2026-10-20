
package com.ga.csv.writer;

import com.ga.csv.model.EmployeeProcessingResult;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Writes employee salary processing results to a CSV file.
 *
 * Each row contains the employee's original salary,
 * eligibility status, increment percentage, increment amount,
 * and final salary.
 */
@Component
public class CsvResultWriter {

    /**
     * Writes all processing results to the specified output file.
     *
     * @param results the employee processing results
     * @param outputPath the destination CSV file path
     * @throws IOException if the file cannot be written
     */
    public void writeResults(
            List<EmployeeProcessingResult> results,
            Path outputPath) throws IOException {

        // Create the output directory if it does not exist.
        Path parent = outputPath.toAbsolutePath().getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        // Define the CSV column names.
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader(
                        "name",
                        "originalSalary",
                        "role",
                        "completedProjectsPercentage",
                        "eligibleForIncrease",
                        "completedYears",
                        "incrementPercentage",
                        "incrementAmount",
                        "finalSalary"
                )
                .get();

        // Automatically close the writer and CSV printer.
        try (BufferedWriter writer = Files.newBufferedWriter(
                outputPath, StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer, format)) {

            for (EmployeeProcessingResult result : results) {

                // Get the original employee information.
                var employee = result.getEmployee();

                printer.printRecord(
                        employee.getName(),
                        employee.getSalary().setScale(
                                2, RoundingMode.HALF_UP),
                        employee.getRole(),
                        employee.getCompletedProjectsPercentage(),
                        result.isEligibleForIncrease(),
                        result.getCompletedYears(),
                        result.getIncrementPercentage(),
                        result.getIncrementAmount().setScale(
                                2, RoundingMode.HALF_UP),
                        result.getFinalSalary().setScale(
                                2, RoundingMode.HALF_UP)
                );
            }
        }
    }
}

