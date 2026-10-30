package com.facultysync.faculty.repository;

import com.facultysync.faculty.entity.LeaveRequest;
import com.facultysync.faculty.entity.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRepository extends JpaRepository<LeaveRequest, Integer> {

    List<LeaveRequest> findByStatus(LeaveStatus status);

    boolean existsByFacultyId(int facultyId);

    /** Is the faculty on approved leave on this date? */
    @Query("""
            SELECT COUNT(l) > 0 FROM LeaveRequest l
            WHERE l.facultyId = :facultyId AND l.status = com.facultysync.faculty.entity.LeaveStatus.APPROVED
              AND :date BETWEEN l.fromDate AND l.toDate""")
    boolean isOnApprovedLeave(int facultyId, LocalDate date);
}
