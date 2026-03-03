package com.labsafety.system.training.controller;

import com.labsafety.system.training.dto.SafetyTrainingStatisticsResponse;
import com.labsafety.system.training.service.SafetyTrainingStatisticsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/training/statistics")
public class SafetyTrainingStatisticsController {

    private final SafetyTrainingStatisticsService statisticsService;

    public SafetyTrainingStatisticsController(SafetyTrainingStatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**
     * ADMIN: overview statistics
     * - total courses
     * - total enrollments
     * - submitted attempts
     * - passed attempts
     * - pass rate (passed/submitted)
     */
    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public SafetyTrainingStatisticsResponse.OverviewResponse overview() {
        return statisticsService.overview();
    }

    /**
     * ADMIN: course-level pass rate (paged)
     */
    @GetMapping("/courses")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<SafetyTrainingStatisticsResponse.CourseStatResponse> courseStats(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return statisticsService.courseStats(pageable);
    }
}
