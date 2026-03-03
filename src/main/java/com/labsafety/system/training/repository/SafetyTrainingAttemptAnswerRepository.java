package com.labsafety.system.training.repository;

import com.labsafety.system.training.SafetyTrainingAttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SafetyTrainingAttemptAnswerRepository
        extends JpaRepository<SafetyTrainingAttemptAnswer, Long> {

    List<SafetyTrainingAttemptAnswer> findByAttempt_Id(Long attemptId);

    long countByQuestion_IdAndIsCorrectTrue(Long questionId);
}
