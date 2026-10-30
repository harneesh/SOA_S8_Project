package com.facultysync.portal.hod;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * HOD Portal (port 8084).
 * Front-end service for the HOD: approve/reject leave, view AI substitute recommendations, confirm substitutes.
 * The page calls the Faculty Management Service (8081) and the AI Substitute Service (8082) REST APIs.
 */
@SpringBootApplication
public class HodPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(HodPortalApplication.class, args);
    }
}
