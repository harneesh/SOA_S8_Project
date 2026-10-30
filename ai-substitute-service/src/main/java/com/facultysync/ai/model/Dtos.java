package com.facultysync.ai.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/** Data exchanged with the Faculty Management Service and returned to clients. */
public final class Dtos {

    private Dtos() {
    }

    // ---- Received from Faculty Management Service ----

    public record Faculty(int id, String name, String department, String email,
                          String designation, List<String> subjects) {
    }

    public record LeaveRequest(int id, int facultyId, LocalDate fromDate, LocalDate toDate,
                               String reason, String status, LocalDate appliedOn) {
    }

    public record AffectedClass(int slotId, LocalDate date, DayOfWeek day, int period,
                                String subject, String className, String room,
                                Integer substituteFacultyId) {
    }

    // ---- Returned by this service ----

    public record Candidate(int facultyId, String name, String department, int score, List<String> reasons) {
    }

    public record ClassRecommendation(AffectedClass affectedClass, Candidate recommended,
                                      List<Candidate> alternatives) {
    }

    public record LeaveRecommendation(int leaveId, String absentFaculty, String leaveStatus,
                                      List<ClassRecommendation> classes) {
    }
}
