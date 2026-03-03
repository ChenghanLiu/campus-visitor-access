package com.labsafety.system.lab.stats;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/labs/statistics")
public class LabStatisticsController {

    private final LabStatisticsService statisticsService;

    public LabStatisticsController(LabStatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**
     * ADMIN: overall lab statistics
     */
    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public LabStatisticsResponse.OverviewResponse overview() {
        return statisticsService.overview();
    }

    /**
     * ADMIN: per-lab statistics (paged)
     */
    @GetMapping("/labs")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<LabStatisticsResponse.LabStatResponse> labStats(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return statisticsService.labStats(pageable);
    }
}
