package com.labsafety.system.training.repository;

import com.labsafety.system.training.SafetyTrainingCourse;
import com.labsafety.system.training.SafetyTrainingCourse.SafetyTrainingMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SafetyTrainingCourseRepository extends JpaRepository<SafetyTrainingCourse, Long> {

    Page<SafetyTrainingCourse> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    Page<SafetyTrainingCourse> findByMode(SafetyTrainingMode mode, Pageable pageable);

    Page<SafetyTrainingCourse> findByModeAndTitleContainingIgnoreCase(SafetyTrainingMode mode, String keyword, Pageable pageable);
}
