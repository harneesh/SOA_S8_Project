package com.facultysync.portal.faculty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Faculty Portal (port 8083).
 * Front-end service for faculty members: apply for leave, track leave status, view own timetable.
 * It has no business logic; the page calls the Faculty Management Service REST API (port 8081).
 */
@SpringBootApplication
public class FacultyPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(FacultyPortalApplication.class, args);
    }
}
