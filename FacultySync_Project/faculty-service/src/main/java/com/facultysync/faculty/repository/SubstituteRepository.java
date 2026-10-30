package com.facultysync.faculty.repository;

import com.facultysync.faculty.entity.SubstituteAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SubstituteRepository extends JpaRepository<SubstituteAssignment, Integer> {

    Optional<SubstituteAssignment> findBySlotIdAndDate(int slotId, LocalDate date);

    List<SubstituteAssignment> findBySubstituteFacultyIdAndDate(int substituteFacultyId, LocalDate date);

    boolean existsBySlotId(int slotId);

    boolean existsBySubstituteFacultyId(int substituteFacultyId);
}
