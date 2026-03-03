package com.labsafety.system.training.repository;

import com.labsafety.system.training.SafetyTrainingEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SafetyTrainingEnrollmentRepository
        extends JpaRepository<SafetyTrainingEnrollment, Long> {

    Optional<SafetyTrainingEnrollment> findByCourse_IdAndStudent_Id(Long courseId, Long studentId);

    List<SafetyTrainingEnrollment> findByStudent_Id(Long studentId);

    long countByCourse_Id(Long courseId);

    boolean existsByCourse_IdAndStudent_Id(Long courseId, Long studentId);
}
