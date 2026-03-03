package com.labsafety.system.reservation;

import com.labsafety.system.reservation.dto.CreateReservationRequest;
import com.labsafety.system.reservation.dto.ReservationDecisionRequest;
import com.labsafety.system.reservation.dto.ReservationResponse;
import com.labsafety.system.reservation.dto.ReservationStatsResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationQueryService reservationQueryService;
    private final ReservationStatsService reservationStatsService;

    public ReservationController(ReservationService reservationService,
                                 ReservationQueryService reservationQueryService,
                                 ReservationStatsService reservationStatsService) {
        this.reservationService = reservationService;
        this.reservationQueryService = reservationQueryService;
        this.reservationStatsService = reservationStatsService;
    }

    /**
     * STUDENT creates a reservation request.
     */
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ReservationResponse create(@Valid @RequestBody CreateReservationRequest request,
                                      Authentication authentication) {

        String username = authentication.getName();

        Reservation reservation = reservationService.createReservation(
                request.getLabId(),
                request.getEquipmentId(),
                username,
                request.getStartTime(),
                request.getEndTime(),
                request.getPurpose(),
                request.getRemark()
        );

        return ReservationMapper.toResponse(reservation);
    }

    /**
     * TEACHER approves a reservation.
     */
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('TEACHER')")
    public ReservationResponse approve(@PathVariable Long id,
                                       @Valid @RequestBody ReservationDecisionRequest request,
                                       Authentication authentication) {

        String approverUsername = authentication.getName();

        Reservation reservation = reservationService.approve(
                id,
                approverUsername,
                request.getDecisionNote()
        );

        return ReservationMapper.toResponse(reservation);
    }

    /**
     * TEACHER rejects a reservation.
     */
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('TEACHER')")
    public ReservationResponse reject(@PathVariable Long id,
                                      @Valid @RequestBody ReservationDecisionRequest request,
                                      Authentication authentication) {

        String approverUsername = authentication.getName();

        Reservation reservation = reservationService.reject(
                id,
                approverUsername,
                request.getDecisionNote()
        );

        return ReservationMapper.toResponse(reservation);
    }

    /**
     * STUDENT cancels their own reservation.
     */
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('STUDENT')")
    public ReservationResponse cancelAsStudent(@PathVariable Long id,
                                               @Valid @RequestBody ReservationDecisionRequest request,
                                               Authentication authentication) {

        String studentUsername = authentication.getName();

        Reservation reservation = reservationService.cancelAsStudent(
                id,
                studentUsername,
                request.getDecisionNote()
        );

        return ReservationMapper.toResponse(reservation);
    }

    /**
     * ADMIN cancels any reservation.
     */
    @PutMapping("/{id}/cancel/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ReservationResponse cancelAsAdmin(@PathVariable Long id,
                                             @Valid @RequestBody ReservationDecisionRequest request,
                                             Authentication authentication) {

        String adminUsername = authentication.getName();

        Reservation reservation = reservationService.cancelAsAdmin(
                id,
                adminUsername,
                request.getDecisionNote()
        );

        return ReservationMapper.toResponse(reservation);
    }

    /**
     * STUDENT: list my reservations (pageable).
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public Page<ReservationResponse> myReservations(Authentication authentication,
                                                    @PageableDefault(size = 10) Pageable pageable) {
        String username = authentication.getName();
        return reservationQueryService.myReservations(username, pageable);
    }

    /**
     * TEACHER: list pending reservations to review (pageable).
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('TEACHER')")
    public Page<ReservationResponse> pending(@PageableDefault(size = 10) Pageable pageable) {
        return reservationQueryService.pendingReservations(pageable);
    }

    /**
     * ADMIN: list/search all reservations with optional filters (pageable).
     *
     * Example:
     * /api/reservations?status=APPROVED&labId=4&page=0&size=10
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<ReservationResponse> adminSearch(
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) Long labId,
            @RequestParam(required = false) Long equipmentId,
            @RequestParam(required = false) String studentUsername,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return reservationQueryService.adminSearch(status, labId, equipmentId, studentUsername, pageable);
    }

    /**
     * ADMIN: simple stats summary by status.
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ReservationStatsResponse stats() {
        return reservationStatsService.getStats();
    }
}
