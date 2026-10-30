package com.facultysync.ai.client;

import com.facultysync.ai.model.Dtos.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/** REST client used by the AI service to talk to the Faculty Management Service. */
@Component
public class FacultyServiceClient {

    private final RestClient restClient;

    public FacultyServiceClient(@Value("${faculty.service.url}") String baseUrl) {
        this.restClient = RestClient.create(baseUrl);
    }

    public LeaveRequest getLeave(int leaveId) {
        return restClient.get().uri("/api/leaves/{id}", leaveId).retrieve().body(LeaveRequest.class);
    }

    public Faculty getFaculty(int facultyId) {
        return restClient.get().uri("/api/faculty/{id}", facultyId).retrieve().body(Faculty.class);
    }

    public List<AffectedClass> getAffectedClasses(int leaveId) {
        return restClient.get().uri("/api/leaves/{id}/affected-classes", leaveId)
                .retrieve().body(new ParameterizedTypeReference<>() {});
    }

    public List<Faculty> getAvailableFaculty(LocalDate date, int period) {
        return restClient.get().uri("/api/faculty/available?date={date}&period={period}", date, period)
                .retrieve().body(new ParameterizedTypeReference<>() {});
    }

    public Map<Integer, Integer> getWorkload() {
        return restClient.get().uri("/api/faculty/workload")
                .retrieve().body(new ParameterizedTypeReference<>() {});
    }
}
