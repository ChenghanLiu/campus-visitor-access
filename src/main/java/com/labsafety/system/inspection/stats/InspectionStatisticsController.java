package com.labsafety.system.inspection.stats;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inspection/statistics")
public class InspectionStatisticsController {

    private final InspectionStatisticsService statisticsService;

    public InspectionStatisticsController(InspectionStatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public InspectionStatisticsResponse.OverviewResponse overview() {
        return statisticsService.overview();
    }

    @GetMapping("/labs")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<InspectionStatisticsResponse.LabStatResponse> labStats(
            @PageableDefault(size = 10) Pageable pageable) {
        return statisticsService.labStats(pageable);
    }
}
