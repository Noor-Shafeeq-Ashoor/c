package com.ga.csv.reader;

import com.ga.csv.model.Employee;
import com.ga.csv.model.EmployeeRole;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CsvEmployeeReader {

    private static final String NAME_COLUMN = "name";
    private static final String SALARY_COLUMN = "salary";
    private static final String JOINED_DATE_COLUMN = "joinedDate";
    private static final String ROLE_COLUMN = "role";
    private static final String COMPLETED_PROJECTS_COLUMN =
            "completedProjectsPercentage";


    public List<Employee> readEmployees(Path filePath) throws IOException {

        List<Employee> employees = new ArrayList<>();

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .get();

        try (Reader reader = Files.newBufferedReader(
                filePath, StandardCharsets.UTF_8);
             CSVParser parser = format.parse(reader)) {

            validateHeaders(parser);

            for (CSVRecord record : parser) {
                try {
                    employees.add(parseEmployee(record));
                } catch (IllegalArgumentException exception) {
                    System.err.println(
                            "Skipping invalid CSV record "
                                    + record.getRecordNumber()
                                    + ": " + exception.getMessage());
                }
            }
        }

        return employees;
    }


    private void validateHeaders(CSVParser parser) throws IOException {

        List<String> requiredHeaders = List.of(
                NAME_COLUMN,
                SALARY_COLUMN,
                JOINED_DATE_COLUMN,
                ROLE_COLUMN,
                COMPLETED_PROJECTS_COLUMN
        );

        for (String header : requiredHeaders) {
            if (!parser.getHeaderMap().containsKey(header)) {
                throw new IOException(
                        "Missing required CSV column: " + header);
            }
        }
    }


    private Employee parseEmployee(CSVRecord record) {

        String name = record.get(NAME_COLUMN).trim();

        BigDecimal salary = new BigDecimal(
                record.get(SALARY_COLUMN).trim());

        LocalDate joinedDate = LocalDate.parse(
                record.get(JOINED_DATE_COLUMN).trim());

        EmployeeRole role = EmployeeRole.valueOf(
                record.get(ROLE_COLUMN)
                        .trim()
                        .toUpperCase(Locale.ROOT));

        BigDecimal percentage = new BigDecimal(
                record.get(COMPLETED_PROJECTS_COLUMN).trim());

        if (percentage.compareTo(BigDecimal.ZERO) < 0
                || percentage.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException(
                    "Project completion must be between 0 and 100");
        }

        return new Employee(
                name,
                salary,
                joinedDate,
                role,
                percentage);
    }
}
