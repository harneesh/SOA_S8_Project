package com.facultysync.faculty.entity;

import jakarta.persistence.*;

import java.time.DayOfWeek;

/** Table: timetable_slot. One weekly class, e.g. Monday period 2, "Data Structures" for CSE-2A. */
@Entity
@Table(name = "timetable_slot")
public class TimetableSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int facultyId;

    @Enumerated(EnumType.STRING)
    private DayOfWeek day;

    private int period;
    private String subject;
    private String className;
    private String room;

    public TimetableSlot() {
    }

    public TimetableSlot(int facultyId, DayOfWeek day, int period, String subject, String className, String room) {
        this.facultyId = facultyId;
        this.day = day;
        this.period = period;
        this.subject = subject;
        this.className = className;
        this.room = room;
    }

    public int getId() { return id; }
    public int getFacultyId() { return facultyId; }
    public void setFacultyId(int facultyId) { this.facultyId = facultyId; }
    public DayOfWeek getDay() { return day; }
    public void setDay(DayOfWeek day) { this.day = day; }
    public int getPeriod() { return period; }
    public void setPeriod(int period) { this.period = period; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
}
