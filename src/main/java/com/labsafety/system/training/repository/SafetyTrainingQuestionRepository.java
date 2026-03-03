package com.labsafety.system.training.repository;

import com.labsafety.system.training.SafetyTrainingQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SafetyTrainingQuestionRepository
        extends JpaRepository<SafetyTrainingQuestion, Long> {

    List<SafetyTrainingQuestion> findByCourse_IdOrderBySortOrderAsc(Long courseId);

    long countByCourse_Id(Long courseId);
}
