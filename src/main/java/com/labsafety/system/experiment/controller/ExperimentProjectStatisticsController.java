package com.labsafety.system.experiment.controller;

import com.labsafety.system.experiment.dto.ExperimentProjectStatisticsResponse;
import com.labsafety.system.experiment.service.ExperimentProjectReservationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/experiment-projects")
public class ExperimentProjectStatisticsController {

    private final ExperimentProjectReservationService experimentProjectReservationService;

    public ExperimentProjectStatisticsController(
            ExperimentProjectReservationService experimentProjectReservationService) {
        this.experimentProjectReservationService = experimentProjectReservationService;
    }

    /**
     * Get reservation statistics grouped by experiment project.
     * ADMIN only.
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ExperimentProjectStatisticsResponse> getStatistics() {
        return experimentProjectReservationService.getStatistics();
    }
}
