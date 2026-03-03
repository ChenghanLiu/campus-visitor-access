package com.labsafety.system.experiment.controller;

import com.labsafety.system.experiment.service.ExperimentProjectReservationService;
import com.labsafety.system.reservation.Reservation;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/experiment-projects")
public class ExperimentProjectReservationController {

    private final ExperimentProjectReservationService experimentProjectReservationService;

    public ExperimentProjectReservationController(
            ExperimentProjectReservationService experimentProjectReservationService) {
        this.experimentProjectReservationService = experimentProjectReservationService;
    }

    /**
     * Create reservation for a specific experiment project.
     * STUDENT only.
     */
    @PostMapping("/{projectId}/reservations")
    @PreAuthorize("hasRole('STUDENT')")
    public Reservation createReservationForProject(
            @PathVariable Long projectId,
            @RequestParam @NotNull Long labId,
            @RequestParam @NotNull Long equipmentId,
            @RequestParam @NotNull LocalDateTime startTime,
            @RequestParam @NotNull LocalDateTime endTime,
            @RequestParam(required = false) String purpose,
            @RequestParam(required = false) String remark,
            Authentication authentication
    ) {

        String studentUsername = authentication.getName();

        return experimentProjectReservationService.createReservationWithProject(
                labId,
                equipmentId,
                studentUsername,
                startTime,
                endTime,
                purpose,
                remark,
                projectId
        );
    }
}
