package com.facultysync.faculty.repository;

import com.facultysync.faculty.entity.TimetableSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

/** Spring Data generates the SQL from the method names. */
public interface TimetableRepository extends JpaRepository<TimetableSlot, Integer> {

    List<TimetableSlot> findByFacultyId(int facultyId);

    List<TimetableSlot> findByFacultyIdAndDayOrderByPeriod(int facultyId, DayOfWeek day);

    boolean existsByFacultyIdAndDayAndPeriod(int facultyId, DayOfWeek day, int period);

    // Used to detect clashes when adding/editing a period
    List<TimetableSlot> findByFacultyIdAndDayAndPeriod(int facultyId, DayOfWeek day, int period);

    List<TimetableSlot> findByClassNameIgnoreCaseAndDayAndPeriod(String className, DayOfWeek day, int period);

    boolean existsByFacultyId(int facultyId);
}
