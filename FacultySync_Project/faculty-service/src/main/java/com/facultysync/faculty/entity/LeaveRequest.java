package com.facultysync.faculty.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

/** Table: leave_request ("leave" alone is a reserved word in MySQL). */
@Entity
@Table(name = "leave_request")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int facultyId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String reason;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    private LocalDate appliedOn;

    public LeaveRequest() {
    }

    public LeaveRequest(int facultyId, LocalDate fromDate, LocalDate toDate, String reason) {
        this.facultyId = facultyId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.reason = reason;
        this.status = LeaveStatus.PENDING;
        this.appliedOn = LocalDate.now();
    }

    public int getId() { return id; }
    public int getFacultyId() { return facultyId; }
    public LocalDate getFromDate() { return fromDate; }
    public LocalDate getToDate() { return toDate; }
    public String getReason() { return reason; }
    public LeaveStatus getStatus() { return status; }
    public void setStatus(LeaveStatus status) { this.status = status; }
    public LocalDate getAppliedOn() { return appliedOn; }
}
