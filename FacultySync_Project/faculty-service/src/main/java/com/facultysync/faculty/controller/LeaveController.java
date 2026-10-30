package com.facultysync.faculty.controller;

import com.facultysync.faculty.entity.*;
import com.facultysync.faculty.model.Models.*;
import com.facultysync.faculty.service.FacultyDataService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** REST endpoints for applying, approving and rejecting leave. */
@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    private final FacultyDataService service;

    public LeaveController(FacultyDataService service) {
        this.service = service;
    }

    @GetMapping
    public List<LeaveRequest> getAll(@RequestParam(required = false) LeaveStatus status) {
        return service.getLeaves(status);
    }

    @GetMapping("/{id}")
    public LeaveRequest getById(@PathVariable int id) {
        return service.getLeave(id);
    }

    /** Faculty applies for leave; it starts as PENDING. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeaveRequest apply(@RequestBody LeaveApplication application) {
        return service.applyLeave(application);
    }

    @PutMapping("/{id}/approve")
    public LeaveRequest approve(@PathVariable int id) {
        return service.decideLeave(id, LeaveStatus.APPROVED);
    }

    @PutMapping("/{id}/reject")
    public LeaveRequest reject(@PathVariable int id) {
        return service.decideLeave(id, LeaveStatus.REJECTED);
    }

    /** Classes missed during this leave (used by the AI Substitute Service). */
    @GetMapping("/{id}/affected-classes")
    public List<AffectedClass> affectedClasses(@PathVariable int id) {
        return service.getAffectedClasses(id);
    }
}
