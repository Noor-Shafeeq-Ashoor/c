
package com.ga.csv;

import com.ga.csv.model.Employee;
import com.ga.csv.model.EmployeeProcessingResult;
import com.ga.csv.reader.CsvEmployeeReader;
import com.ga.csv.service.ProcessingCoordinator;
import com.ga.csv.service.EmployeeProcessingService;
import com.ga.csv.writer.CsvResultWriter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


import java.nio.file.Path;
import java.util.List;

/**
 * Main entry point for the Concurrent CSV Data Processor.
 * Reads employee records, processes salary increments concurrently,
 * and writes the results to an output CSV file.
 */
@SpringBootApplication
public class CsvApplication {

    public static void main(String[] args) {
        SpringApplication.run(CsvApplication.class, args);
    }

    @Bean
    public CommandLineRunner processCsv(
            CsvEmployeeReader reader,
            EmployeeProcessingService processingService,
            ProcessingCoordinator coordinator,
            CsvResultWriter writer,
            @Value("${app.csv.input-file:employees.csv}")
            String inputFile,
            @Value("${app.csv.output-file:employee_salary_results.csv}")
            String outputFile) {

        return args -> {
            Path inputPath = Path.of(inputFile);

            if (!java.nio.file.Files.exists(inputPath)) {
                var resource = new org.springframework.core.io.ClassPathResource(inputFile);

                if (resource.exists()) {
                    inputPath = resource.getFile().toPath();
                }
            }
            Path outputPath = Path.of(outputFile);

            System.out.println("Starting employee salary processing...");
            System.out.println("Input file: " + inputPath.toAbsolutePath());

            List<Employee> employees = reader.readEmployees(inputPath);

            if (employees.isEmpty()) {
                System.out.println(
                        "No valid employee records found. No results written.");
                return;
            }

            System.out.println(
                    "Valid employees loaded: " + employees.size());

            List<EmployeeProcessingResult> results =
                    coordinator.processEmployees(employees, processingService);

            writer.writeResults(results, outputPath);

            System.out.println(
                    "Processing completed successfully.");
            System.out.println(
                    "Results saved to: " + outputPath.toAbsolutePath());
            System.out.println(
                    "Employees processed: " + results.size());
        };
    }
}
