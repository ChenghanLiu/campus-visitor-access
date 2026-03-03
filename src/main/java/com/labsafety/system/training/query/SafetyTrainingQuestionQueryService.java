package com.labsafety.system.training.query;

import com.labsafety.system.training.SafetyTrainingQuestion;
import com.labsafety.system.training.query.dto.SafetyTrainingQuestionResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SafetyTrainingQuestionQueryService {

    @PersistenceContext
    private EntityManager em;

    public List<SafetyTrainingQuestionResponse> getQuestions(Long courseId) {

        return em.createQuery(
                        "select q from SafetyTrainingQuestion q " +
                                "where q.course.id = :courseId " +
                                "order by q.sortOrder asc",
                        SafetyTrainingQuestion.class
                )
                .setParameter("courseId", courseId)
                .getResultList()
                .stream()
                .map(q -> new SafetyTrainingQuestionResponse(
                        q.getId(),
                        q.getQuestionText(),
                        q.getOptionA(),
                        q.getOptionB(),
                        q.getOptionC(),
                        q.getOptionD()
                ))
                .toList();
    }
}
