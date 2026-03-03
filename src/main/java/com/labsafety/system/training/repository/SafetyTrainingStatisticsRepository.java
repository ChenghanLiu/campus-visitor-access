package com.labsafety.system.training.repository;

import com.labsafety.system.training.SafetyTrainingAttempt;
import com.labsafety.system.training.SafetyTrainingCourse;
import com.labsafety.system.training.SafetyTrainingEnrollment;
import com.labsafety.system.training.dto.SafetyTrainingStatisticsResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SafetyTrainingStatisticsRepository {

    @PersistenceContext
    private EntityManager em;

    public long countCourses() {
        Long v = em.createQuery("select count(c) from SafetyTrainingCourse c", Long.class)
                .getSingleResult();
        return v == null ? 0L : v;
    }

    public long countEnrollments() {
        Long v = em.createQuery("select count(e) from SafetyTrainingEnrollment e", Long.class)
                .getSingleResult();
        return v == null ? 0L : v;
    }

    public long countSubmittedAttempts() {
        Long v = em.createQuery(
                        "select count(a) from SafetyTrainingAttempt a where a.submittedAt is not null",
                        Long.class
                )
                .getSingleResult();
        return v == null ? 0L : v;
    }

    public long countPassedSubmittedAttempts() {
        Long v = em.createQuery(
                        "select count(a) from SafetyTrainingAttempt a " +
                                "where a.submittedAt is not null and a.passed = true",
                        Long.class
                )
                .getSingleResult();
        return v == null ? 0L : v;
    }

    /**
     * Course-level stats rows:
     * - include courses with 0 attempts (left join)
     * - only count submitted attempts as "attempted"
     */
    public List<SafetyTrainingStatisticsResponse.CourseStatRow> courseStatRows() {
        return em.createQuery(
                        "select new com.labsafety.system.training.dto.SafetyTrainingStatisticsResponse$CourseStatRow(" +
                                "c.id, c.title, " +
                                "sum(case when a.submittedAt is not null then 1 else 0 end), " +
                                "sum(case when a.submittedAt is not null and a.passed = true then 1 else 0 end)" +
                                ") " +
                                "from SafetyTrainingCourse c " +
                                "left join SafetyTrainingAttempt a on a.course = c " +
                                "group by c.id, c.title " +
                                "order by c.id desc",
                        SafetyTrainingStatisticsResponse.CourseStatRow.class
                )
                .getResultList();
    }
}
