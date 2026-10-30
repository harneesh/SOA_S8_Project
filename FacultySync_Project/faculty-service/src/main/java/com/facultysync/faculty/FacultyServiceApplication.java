package com.facultysync.faculty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Faculty Management Service (port 8081).
 * Owns faculty details, leave requests, availability, timetable and substitute assignments.
 */
@SpringBootApplication
public class FacultyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FacultyServiceApplication.class, args);
    }
}
