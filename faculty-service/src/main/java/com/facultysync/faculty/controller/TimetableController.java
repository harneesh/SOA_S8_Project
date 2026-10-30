package com.facultysync.faculty.controller;

import com.facultysync.faculty.entity.*;
import com.facultysync.faculty.model.Models.*;
import com.facultysync.faculty.service.FacultyDataService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** REST endpoints for the timetable and the final substitute assignment. */
@RestController
@RequestMapping("/api")
public class TimetableController {

    private final FacultyDataService service;

    public TimetableController(FacultyDataService service) {
        this.service = service;
    }

    @GetMapping("/timetable")
    public List<TimetableSlot> timetable(@RequestParam(required = false) Integer facultyId) {
        return service.getTimetable(facultyId);
    }

    @PostMapping("/timetable")
    @ResponseStatus(HttpStatus.CREATED)
    public TimetableSlot addSlot(@RequestBody SlotRequest request) {
        return service.addSlot(request);
    }

    @PutMapping("/timetable/{id}")
    public TimetableSlot updateSlot(@PathVariable int id, @RequestBody SlotRequest request) {
        return service.updateSlot(id, request);
    }

    @DeleteMapping("/timetable/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSlot(@PathVariable int id) {
        service.deleteSlot(id);
    }

    @GetMapping("/substitutes")
    public List<SubstituteAssignment> substitutes() {
        return service.getAssignments();
    }

    /** HOD confirms a substitute (usually one recommended by the AI service). */
    @PostMapping("/substitutes")
    @ResponseStatus(HttpStatus.CREATED)
    public SubstituteAssignment assign(@RequestBody AssignRequest request) {
        return service.assignSubstitute(request);
    }
}
