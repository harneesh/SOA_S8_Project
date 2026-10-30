package com.facultysync.faculty.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/** Table: faculty (subjects are stored in table faculty_subjects). */
@Entity
@Table(name = "faculty")
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;
    private String department;
    private String email;
    private String designation;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "faculty_subjects", joinColumns = @JoinColumn(name = "faculty_id"))
    @Column(name = "subject")
    private List<String> subjects = new ArrayList<>();

    public Faculty() {
    }

    public Faculty(String name, String department, String email, String designation, List<String> subjects) {
        this.name = name;
        this.department = department;
        this.email = email;
        this.designation = designation;
        this.subjects = new ArrayList<>(subjects);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public List<String> getSubjects() { return subjects; }
    public void setSubjects(List<String> subjects) { this.subjects = subjects; }
}
