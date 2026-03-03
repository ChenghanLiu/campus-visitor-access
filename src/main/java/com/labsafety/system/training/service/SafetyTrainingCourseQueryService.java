package com.labsafety.system.training.service;

import com.labsafety.system.training.SafetyTrainingCourse;
import com.labsafety.system.training.dto.SafetyTrainingCourseResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SafetyTrainingCourseQueryService {

    @PersistenceContext
    private EntityManager em;

    public Page<SafetyTrainingCourseResponse> listCourses(Pageable pageable) {

        Long total = em.createQuery(
                        "select count(c) from SafetyTrainingCourse c",
                        Long.class
                )
                .getSingleResult();

        List<SafetyTrainingCourse> rows = em.createQuery(
                        "select c from SafetyTrainingCourse c order by c.id desc",
                        SafetyTrainingCourse.class
                )
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        List<SafetyTrainingCourseResponse> content = rows.stream()
                .map(this::toResponse)
                .toList();

        return new PageImpl<>(content, pageable, total);
    }

    public SafetyTrainingCourseResponse getCourse(Long courseId) {
        SafetyTrainingCourse course = em.find(SafetyTrainingCourse.class, courseId);
        if (course == null) {
            throw new RuntimeException("Training course not found: " + courseId);
        }
        return toResponse(course);
    }

    private SafetyTrainingCourseResponse toResponse(SafetyTrainingCourse c) {
        return new SafetyTrainingCourseResponse(
                c.getId(),
                c.getTitle(),
                c.getContent(),
                c.getStartTime(),
                c.getEndTime(),
                c.getMode() == null ? null : c.getMode().name(),
                c.getPassingScore(),
                c.getDurationMinutes(),
                c.getCreatedAt()
        );
    }
}
