package com.labsafety.system.training.repository;

import com.labsafety.system.training.SafetyTrainingAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SafetyTrainingAttemptRepository
        extends JpaRepository<SafetyTrainingAttempt, Long> {

    List<SafetyTrainingAttempt> findByCourse_IdAndStudent_IdOrderByCreatedAtDesc(Long courseId, Long studentId);

    List<SafetyTrainingAttempt> findByStudent_IdOrderByCreatedAtDesc(Long studentId);

    long countByCourse_Id(Long courseId);

    long countByCourse_IdAndPassedTrue(Long courseId);

    Optional<SafetyTrainingAttempt> findTopByCourse_IdAndStudent_IdOrderByCreatedAtDesc(Long courseId, Long studentId);

    @Query("""
    select a
    from SafetyTrainingAttempt a
    join fetch a.course c
    where c.id = :courseId
      and a.student.id = :studentId
    order by a.createdAt desc
    """)
    List<SafetyTrainingAttempt> findByCourseIdAndStudentIdWithCourse(
            @Param("courseId") Long courseId,
            @Param("studentId") Long studentId
    );
}
