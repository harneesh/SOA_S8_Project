package com.facultysync.ai.controller;

import com.facultysync.ai.model.Dtos.LeaveRecommendation;
import com.facultysync.ai.service.RecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/** REST endpoints of the AI Substitute Service. */
@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")
public class RecommendationController {

    private final RecommendationService service;

    public RecommendationController(RecommendationService service) {
        this.service = service;
    }

    /** Recommended substitute for every class affected by the given leave. */
    @GetMapping("/leave/{leaveId}")
    public LeaveRecommendation forLeave(@PathVariable int leaveId) {
        try {
            return service.recommendForLeave(leaveId);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Leave " + leaveId + " not found");
        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Faculty Service is not reachable");
        }
    }

    /** Explains the scoring model (useful for the project demo / viva). */
    @GetMapping("/model")
    public Map<String, Object> model() {
        return Map.of(
                "name", "Weighted multi-factor scoring",
                "filters", "Candidate must be free in that period and not on approved leave",
                "weights", Map.of("subjectExpertise", 50, "sameDepartment", 20, "lowWorkload", 30),
                "maxScore", 100);
    }
}
