package com.facultysync.faculty.service;

import com.facultysync.faculty.entity.*;
import com.facultysync.faculty.model.Models.*;
import com.facultysync.faculty.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

/** Business logic of the Faculty Management Service. Data is stored in MySQL through JPA repositories. */
@Service
@Transactional
public class FacultyDataService {

    private final FacultyRepository facultyRepo;
    private final TimetableRepository timetableRepo;
    private final LeaveRepository leaveRepo;
    private final SubstituteRepository substituteRepo;

    public FacultyDataService(FacultyRepository facultyRepo, TimetableRepository timetableRepo,
                              LeaveRepository leaveRepo, SubstituteRepository substituteRepo) {
        this.facultyRepo = facultyRepo;
        this.timetableRepo = timetableRepo;
        this.leaveRepo = leaveRepo;
        this.substituteRepo = substituteRepo;
    }

    // ------------------------------------------------------------------ Faculty

    public List<Faculty> getAllFaculty() {
        return facultyRepo.findAll();
    }

    public Faculty getFaculty(int id) {
        return facultyRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Faculty " + id + " not found"));
    }

    public Faculty addFaculty(Faculty input) {
        if (input.getName() == null || input.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }
        input.setId(0);
        if (input.getSubjects() == null) {
            input.setSubjects(new ArrayList<>());
        }
        return facultyRepo.save(input);
    }

    public Faculty updateFaculty(int id, Faculty input) {
        Faculty f = getFaculty(id);
        if (input.getName() == null || input.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }
        f.setName(input.getName());
        f.setDepartment(input.getDepartment());
        f.setEmail(input.getEmail());
        f.setDesignation(input.getDesignation());
        f.setSubjects(input.getSubjects() == null ? new ArrayList<>() : new ArrayList<>(input.getSubjects()));
        return facultyRepo.save(f);
    }

    /** A faculty can only be deleted when nothing else refers to them (keeps the history consistent). */
    public void deleteFaculty(int id) {
        getFaculty(id);
        if (timetableRepo.existsByFacultyId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Remove this faculty's timetable periods first");
        }
        if (leaveRepo.existsByFacultyId(id) || substituteRepo.existsBySubstituteFacultyId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Faculty has leave or substitution records and cannot be deleted");
        }
        facultyRepo.deleteById(id);
    }

    /** Workload = regular weekly classes + substitute classes already assigned. */
    public Map<Integer, Integer> getWorkload() {
        Map<Integer, Integer> load = new LinkedHashMap<>();
        facultyRepo.findAll().forEach(f -> load.put(f.getId(), 0));
        timetableRepo.findAll().forEach(s -> load.merge(s.getFacultyId(), 1, Integer::sum));
        substituteRepo.findAll().forEach(a -> load.merge(a.getSubstituteFacultyId(), 1, Integer::sum));
        return load;
    }

    // ---------------------------------------------------------------- Timetable

    public List<TimetableSlot> getTimetable(Integer facultyId) {
        return facultyId == null ? timetableRepo.findAll() : timetableRepo.findByFacultyId(facultyId);
    }

    public TimetableSlot addSlot(SlotRequest req) {
        TimetableSlot slot = new TimetableSlot();
        applySlot(slot, req);
        return timetableRepo.save(slot);
    }

    public TimetableSlot updateSlot(int id, SlotRequest req) {
        TimetableSlot slot = timetableRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Period " + id + " not found"));
        applySlot(slot, req);
        return timetableRepo.save(slot);
    }

    public void deleteSlot(int id) {
        if (!timetableRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Period " + id + " not found");
        }
        if (substituteRepo.existsBySlotId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This period has substitution records and cannot be deleted");
        }
        timetableRepo.deleteById(id);
    }

    /** Validates a period and checks that neither the faculty nor the class is already busy at that time. */
    private void applySlot(TimetableSlot slot, SlotRequest req) {
        Faculty faculty = getFaculty(req.facultyId());
        if (req.day() == null || req.period() < 1 || req.period() > 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Day and a period between 1 and 8 are required");
        }
        if (req.subject() == null || req.subject().isBlank() || req.className() == null || req.className().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subject and class are required");
        }
        boolean facultyBusy = timetableRepo.findByFacultyIdAndDayAndPeriod(req.facultyId(), req.day(), req.period())
                .stream().anyMatch(s -> s.getId() != slot.getId());
        if (facultyBusy) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    faculty.getName() + " already has a class on " + req.day() + " period " + req.period());
        }
        boolean classBusy = timetableRepo.findByClassNameIgnoreCaseAndDayAndPeriod(req.className().trim(), req.day(), req.period())
                .stream().anyMatch(s -> s.getId() != slot.getId());
        if (classBusy) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    req.className().trim() + " already has a class on " + req.day() + " period " + req.period());
        }
        slot.setFacultyId(req.facultyId());
        slot.setDay(req.day());
        slot.setPeriod(req.period());
        slot.setSubject(req.subject().trim());
        slot.setClassName(req.className().trim());
        slot.setRoom(req.room());
    }

    // ------------------------------------------------------------------- Leaves

    public List<LeaveRequest> getLeaves(LeaveStatus status) {
        return status == null ? leaveRepo.findAll() : leaveRepo.findByStatus(status);
    }

    public LeaveRequest getLeave(int id) {
        return leaveRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Leave " + id + " not found"));
    }

    public LeaveRequest applyLeave(LeaveApplication app) {
        getFaculty(app.facultyId());
        if (app.fromDate() == null || app.toDate() == null || app.toDate().isBefore(app.fromDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid leave dates");
        }
        return leaveRepo.save(new LeaveRequest(app.facultyId(), app.fromDate(), app.toDate(), app.reason()));
    }

    public LeaveRequest decideLeave(int id, LeaveStatus decision) {
        LeaveRequest leave = getLeave(id);
        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Leave is already " + leave.getStatus());
        }
        leave.setStatus(decision);
        return leaveRepo.save(leave);
    }

    /** Every class the absent faculty would have taught during the leave period. */
    @Transactional(readOnly = true)
    public List<AffectedClass> getAffectedClasses(int leaveId) {
        LeaveRequest leave = getLeave(leaveId);
        List<AffectedClass> result = new ArrayList<>();
        for (LocalDate d = leave.getFromDate(); !d.isAfter(leave.getToDate()); d = d.plusDays(1)) {
            for (TimetableSlot s : timetableRepo.findByFacultyIdAndDayOrderByPeriod(leave.getFacultyId(), d.getDayOfWeek())) {
                Integer substitute = substituteRepo.findBySlotIdAndDate(s.getId(), d)
                        .map(SubstituteAssignment::getSubstituteFacultyId).orElse(null);
                result.add(new AffectedClass(s.getId(), d, s.getDay(), s.getPeriod(),
                        s.getSubject(), s.getClassName(), s.getRoom(), substitute));
            }
        }
        return result;
    }

    // ------------------------------------------------------------- Availability

    /** A faculty is available if not on approved leave, has no class and no substitution in that period. */
    @Transactional(readOnly = true)
    public boolean isAvailable(int facultyId, LocalDate date, int period) {
        if (leaveRepo.isOnApprovedLeave(facultyId, date)) {
            return false;
        }
        if (timetableRepo.existsByFacultyIdAndDayAndPeriod(facultyId, date.getDayOfWeek(), period)) {
            return false;
        }
        return substituteRepo.findBySubstituteFacultyIdAndDate(facultyId, date).stream()
                .noneMatch(a -> timetableRepo.findById(a.getSlotId())
                        .map(s -> s.getPeriod() == period).orElse(false));
    }

    @Transactional(readOnly = true)
    public List<Faculty> getAvailableFaculty(LocalDate date, int period) {
        return facultyRepo.findAll().stream()
                .filter(f -> isAvailable(f.getId(), date, period))
                .toList();
    }

    // ------------------------------------------------------------- Substitutes

    public List<SubstituteAssignment> getAssignments() {
        return substituteRepo.findAll();
    }

    /** Final substitute selection made by the HOD/admin. */
    public SubstituteAssignment assignSubstitute(AssignRequest req) {
        LeaveRequest leave = getLeave(req.leaveId());
        if (leave.getStatus() != LeaveStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Leave must be approved first");
        }
        boolean affected = getAffectedClasses(leave.getId()).stream()
                .anyMatch(c -> c.slotId() == req.slotId() && c.date().equals(req.date()));
        if (!affected) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Class is not affected by this leave");
        }
        if (substituteRepo.findBySlotIdAndDate(req.slotId(), req.date()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Substitute already assigned");
        }
        getFaculty(req.substituteFacultyId());
        TimetableSlot slot = timetableRepo.findById(req.slotId()).orElseThrow();
        if (!isAvailable(req.substituteFacultyId(), req.date(), slot.getPeriod())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Selected faculty is not available");
        }
        return substituteRepo.save(new SubstituteAssignment(leave.getId(), slot.getId(), req.date(),
                leave.getFacultyId(), req.substituteFacultyId()));
    }
}
