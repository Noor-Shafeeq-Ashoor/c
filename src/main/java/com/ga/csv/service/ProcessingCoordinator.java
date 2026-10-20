
package com.ga.csv.service;

import com.ga.csv.model.Employee;
import com.ga.csv.model.EmployeeProcessingResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Coordinates concurrent processing of employee salary records.
 * 1- ExecutorService: manages the worker threads.
 * 2- Semaphore: limits simultaneous processing tasks.
 * 3- ReentrantLock: protects the shared results list.
 * 4- AtomicInteger: tracks the number of completed tasks safely.
 */
@Service
public class ProcessingCoordinator {

    private final int numberOfThreads;
    private final int maxConcurrentTasks;

    /**
     * Creates a coordinator with the requested concurrency limits.
     */
    public ProcessingCoordinator(int numberOfThreads, int maxConcurrentTasks) {

        if (numberOfThreads <= 0 || maxConcurrentTasks <= 0) {
            throw new IllegalArgumentException(
                    "Thread and concurrency limits must be positive");
        }

        this.numberOfThreads = numberOfThreads;
        this.maxConcurrentTasks = maxConcurrentTasks;
    }

    /**
     * Processes employees concurrently and returns their results.
     * All submitted tasks finish before the method returns.
    */
    public List<EmployeeProcessingResult> processEmployees(
            List<Employee> employees,
            EmployeeProcessingService processingService)
            throws InterruptedException, ExecutionException {

        Objects.requireNonNull(employees, "Employees cannot be null");
        Objects.requireNonNull(
                processingService, "Processing service cannot be null");

        // Shared state accessed by multiple worker threads.
        List<EmployeeProcessingResult> results = new ArrayList<>();

        // Protects modifications to the shared results list.
        ReentrantLock resultsLock = new ReentrantLock();

        // Tracks how many employees have finished processing.
        AtomicInteger completedCount = new AtomicInteger(0);

        // Limits how many tasks can process at the same time.
        Semaphore semaphore = new Semaphore(maxConcurrentTasks);

        // Stores task handles so we can wait for every submitted task.
        List<Future<?>> futures = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        try {
            for (Employee employee : employees) {

                Future<?> future = executor.submit(() -> {
                    boolean permitAcquired = false;

                    try {
                        // Wait until a concurrency permit is available.
                        semaphore.acquire();
                        permitAcquired = true;

                        // Calculate this employee's salary result.
                        EmployeeProcessingResult result = processingService.processEmployee(employee);

                        // Protect the shared list while adding the result.
                        resultsLock.lock();
                        try {
                            results.add(result);
                        } finally {
                            resultsLock.unlock();
                        }

                        // Atomic increment is safe across multiple threads.
                        int completed = completedCount.incrementAndGet();

                        System.out.println(
                                "Processed " + employee.getName()
                                        + " | Completed: " + completed
                                        + "/" + employees.size());

                    } catch (InterruptedException exception) {
                        // Preserve the interrupted status for this thread.
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(
                                "Employee processing was interrupted",
                                exception);
                    } finally {
                        // Always release a permit if one was acquired.
                        if (permitAcquired) {
                            semaphore.release();
                        }
                    }
                });

                futures.add(future);
            }

            // Wait for all submitted tasks and detect task failures.
            for (Future<?> future : futures) {
                future.get();
            }

            return results;

        } catch (InterruptedException exception) {
            // Restore the calling thread's interrupted status.
            Thread.currentThread().interrupt();
            throw exception;

        } finally {
            executor.shutdownNow();
        }
    }
}
