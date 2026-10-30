package com.facultysync.faculty.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

/** Table: substitute_assignment. A substitute chosen by the HOD for one class on one date. */
@Entity
@Table(name = "substitute_assignment",
        uniqueConstraints = @UniqueConstraint(columnNames = {"slot_id", "date"}))
public class SubstituteAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int leaveId;
    private int slotId;
    private LocalDate date;
    private int originalFacultyId;
    private int substituteFacultyId;

    public SubstituteAssignment() {
    }

    public SubstituteAssignment(int leaveId, int slotId, LocalDate date, int originalFacultyId, int substituteFacultyId) {
        this.leaveId = leaveId;
        this.slotId = slotId;
        this.date = date;
        this.originalFacultyId = originalFacultyId;
        this.substituteFacultyId = substituteFacultyId;
    }

    public int getId() { return id; }
    public int getLeaveId() { return leaveId; }
    public int getSlotId() { return slotId; }
    public LocalDate getDate() { return date; }
    public int getOriginalFacultyId() { return originalFacultyId; }
    public int getSubstituteFacultyId() { return substituteFacultyId; }
}
