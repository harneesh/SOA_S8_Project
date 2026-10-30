package com.facultysync.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI Substitute Service (port 8082).
 * Fetches data from the Faculty Management Service and recommends the best substitute for each affected class.
 */
@SpringBootApplication
public class AiSubstituteServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiSubstituteServiceApplication.class, args);
    }
}
