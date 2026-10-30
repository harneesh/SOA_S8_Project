package com.facultysync.ai.service;

import com.facultysync.ai.client.FacultyServiceClient;
import com.facultysync.ai.model.Dtos.*;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * The "AI" part of FacultySync: a weighted scoring model that ranks eligible faculty
 * for each affected class. Scores are out of 100:
 *
 *   Subject expertise   50  - candidate already teaches the subject
 *   Same department     20  - candidate belongs to the absent faculty's department
 *   Low workload        30  - fewer weekly classes = higher score (fair load sharing)
 *
 * Candidates with a timetable conflict or on leave are filtered out before scoring
 * (availability comes from the Faculty Management Service).
 * The HOD makes the final choice; this service only recommends.
 */
@Service
public class RecommendationService {

    static final int SUBJECT_WEIGHT = 50;
    static final int DEPARTMENT_WEIGHT = 20;
    static final int WORKLOAD_WEIGHT = 30;

    private final FacultyServiceClient client;

    public RecommendationService(FacultyServiceClient client) {
        this.client = client;
    }

    public LeaveRecommendation recommendForLeave(int leaveId) {
        LeaveRequest leave = client.getLeave(leaveId);
        Faculty absent = client.getFaculty(leave.facultyId());
        Map<Integer, Integer> workload = client.getWorkload();
        int maxLoad = Math.max(1, Collections.max(workload.values()));

        List<ClassRecommendation> result = new ArrayList<>();
        for (AffectedClass cls : client.getAffectedClasses(leaveId)) {
            if (cls.substituteFacultyId() != null) {
                // Already handled by the HOD - nothing to recommend
                result.add(new ClassRecommendation(cls, null, List.of()));
                continue;
            }
            List<Candidate> ranked = client.getAvailableFaculty(cls.date(), cls.period()).stream()
                    .filter(f -> f.id() != absent.id())
                    .map(f -> score(f, cls, absent, workload.getOrDefault(f.id(), 0), maxLoad))
                    .sorted(Comparator.comparingInt(Candidate::score).reversed()
                            .thenComparing(Candidate::name))
                    .toList();

            Candidate best = ranked.isEmpty() ? null : ranked.get(0);
            List<Candidate> alternatives = ranked.stream().skip(1).limit(3).toList();
            result.add(new ClassRecommendation(cls, best, alternatives));
        }
        return new LeaveRecommendation(leave.id(), absent.name(), leave.status(), result);
    }

    Candidate score(Faculty f, AffectedClass cls, Faculty absent, int load, int maxLoad) {
        int score = 0;
        List<String> reasons = new ArrayList<>();

        if (f.subjects().stream().anyMatch(s -> s.equalsIgnoreCase(cls.subject()))) {
            score += SUBJECT_WEIGHT;
            reasons.add("Expert in " + cls.subject());
        }
        if (Objects.equals(f.department(), absent.department())) {
            score += DEPARTMENT_WEIGHT;
            reasons.add("Same department (" + f.department() + ")");
        }
        int workloadScore = WORKLOAD_WEIGHT * (maxLoad - load) / maxLoad;
        score += workloadScore;
        reasons.add("Workload: " + load + " classes/week");
        reasons.add("Free in period " + cls.period());

        return new Candidate(f.id(), f.name(), f.department(), score, reasons);
    }
}
