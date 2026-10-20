package com.ga.csv.config;

import com.ga.csv.service.ProcessingCoordinator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the application's employee processing components.
 */
@Configuration
public class ProcessingConfig {


    @Bean
    public ProcessingCoordinator processingCoordinator(
            @Value("${app.processing.thread-pool-size:4}")
            int threadPoolSize,
            @Value("${app.processing.max-concurrent-tasks:2}")
            int maxConcurrentTasks) {

        return new ProcessingCoordinator(
                threadPoolSize,
                maxConcurrentTasks);
    }
}
