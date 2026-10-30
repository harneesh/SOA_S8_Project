package com.facultysync.faculty.model;

import java.time.DayOfWeek;
import java.time.LocalDate;

/** Request/response DTOs that are not stored in the database. */
public final class Models {

    private Models() {
    }

    /** A concrete class (timetable slot on a specific date) missed because of an approved leave. */
    public record AffectedClass(int slotId, LocalDate date, DayOfWeek day, int period,
                                String subject, String className, String room,
                                Integer substituteFacultyId) {
    }

    // ---- Request bodies ----

    public record LeaveApplication(int facultyId, LocalDate fromDate, LocalDate toDate, String reason) {
    }

    public record AssignRequest(int leaveId, int slotId, LocalDate date, int substituteFacultyId) {
    }

    /** Body for adding or editing a timetable period. */
    public record SlotRequest(int facultyId, DayOfWeek day, int period, String subject, String className, String room) {
    }
}
