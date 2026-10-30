package com.facultysync.faculty.controller;

import com.facultysync.faculty.entity.Faculty;
import com.facultysync.faculty.service.FacultyDataService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/** REST endpoints for faculty details, workload and availability. */
@RestController
@RequestMapping("/api/faculty")
public class FacultyController {

    private final FacultyDataService service;

    public FacultyController(FacultyDataService service) {
        this.service = service;
    }

    @GetMapping
    public List<Faculty> getAll() {
        return service.getAllFaculty();
    }

    @GetMapping("/{id}")
    public Faculty getById(@PathVariable int id) {
        return service.getFaculty(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Faculty add(@RequestBody Faculty faculty) {
        return service.addFaculty(faculty);
    }

    @PutMapping("/{id}")
    public Faculty update(@PathVariable int id, @RequestBody Faculty faculty) {
        return service.updateFaculty(id, faculty);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        service.deleteFaculty(id);
    }

    /** Map of facultyId -> number of classes per week (regular + substitutions). */
    @GetMapping("/workload")
    public Map<Integer, Integer> workload() {
        return service.getWorkload();
    }

    /** Faculty who are free on the given date and period. Example: /api/faculty/available?date=2026-11-02&period=2 */
    @GetMapping("/available")
    public List<Faculty> available(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                   @RequestParam int period) {
        return service.getAvailableFaculty(date, period);
    }
}
