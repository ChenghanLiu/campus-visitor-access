package com.labsafety.system.training.service;

import com.labsafety.system.training.dto.SafetyTrainingStatisticsResponse;
import com.labsafety.system.training.repository.SafetyTrainingStatisticsRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SafetyTrainingStatisticsService {

    private final SafetyTrainingStatisticsRepository statisticsRepository;

    public SafetyTrainingStatisticsService(SafetyTrainingStatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    public SafetyTrainingStatisticsResponse.OverviewResponse overview() {
        long totalCourses = statisticsRepository.countCourses();
        long totalEnrollments = statisticsRepository.countEnrollments();
        long submittedAttempts = statisticsRepository.countSubmittedAttempts();
        long passedAttempts = statisticsRepository.countPassedSubmittedAttempts();

        double passRate = calcRate(passedAttempts, submittedAttempts);

        return new SafetyTrainingStatisticsResponse.OverviewResponse(
                totalCourses,
                totalEnrollments,
                submittedAttempts,
                passedAttempts,
                passRate
        );
    }

    public Page<SafetyTrainingStatisticsResponse.CourseStatResponse> courseStats(Pageable pageable) {
        // Fetch all course stats (includes courses with zero attempts) then page in memory.
        // This avoids needing a fragile "count query" for JPQL constructor expressions with joins.
        List<SafetyTrainingStatisticsResponse.CourseStatRow> rows = statisticsRepository.courseStatRows();

        int total = rows.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        if (start >= total) {
            return new PageImpl<>(List.of(), pageable, total);
        }

        List<SafetyTrainingStatisticsResponse.CourseStatResponse> content = rows.subList(start, end).stream()
                .map(r -> new SafetyTrainingStatisticsResponse.CourseStatResponse(
                        r.courseId(),
                        r.courseTitle(),
                        r.submittedAttempts(),
                        r.passedAttempts(),
                        calcRate(r.passedAttempts(), r.submittedAttempts())
                ))
                .toList();

        return new PageImpl<>(content, pageable, total);
    }

    private double calcRate(long numerator, long denominator) {
        if (denominator <= 0) {
            return 0.0;
        }
        // keep 4 decimals for frontend display flexibility (e.g., 0.8750)
        BigDecimal rate = BigDecimal.valueOf(numerator)
                .divide(BigDecimal.valueOf(denominator), 4, RoundingMode.HALF_UP);
        return rate.doubleValue();
    }
}
