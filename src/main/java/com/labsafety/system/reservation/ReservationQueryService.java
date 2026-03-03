package com.labsafety.system.reservation;

import com.labsafety.system.reservation.dto.ReservationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReservationQueryService {

    private final ReservationRepository reservationRepository;

    public ReservationQueryService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }


    /**
     * STUDENT: list my reservations (pageable).
     */
    public Page<ReservationResponse> myReservations(String studentUsername, Pageable pageable) {
        Page<Reservation> page = reservationRepository.findMyReservations(studentUsername, pageable);
        page.forEach(this::initializeForResponse);
        return page.map(ReservationMapper::toResponse);
    }

    /**
     * TEACHER: list pending reservations to review.
     */
    public Page<ReservationResponse> pendingReservations(Pageable pageable) {
        Page<Reservation> page = reservationRepository.findByStatusOrderByCreatedAtAsc(ReservationStatus.PENDING, pageable);
        page.forEach(this::initializeForResponse);
        return page.map(ReservationMapper::toResponse);
    }

    /**
     * ADMIN: list/search all reservations with optional filters.
     */
    public Page<ReservationResponse> adminSearch(ReservationStatus status,
                                                 Long labId,
                                                 Long equipmentId,
                                                 String studentUsername,
                                                 Pageable pageable) {
        Page<Reservation> page = reservationRepository.adminSearch(status, labId, equipmentId, studentUsername, pageable);
        page.forEach(this::initializeForResponse);
        return page.map(ReservationMapper::toResponse);
    }

    /**
     * Initialize LAZY relations that ReservationMapper reads.
     * Prevents LazyInitializationException during mapping/serialization.
     */
    private void initializeForResponse(Reservation reservation) {
        if (reservation == null) {
            return;
        }

        if (reservation.getLab() != null) {
            reservation.getLab().getId();
            reservation.getLab().getName();
        }
        if (reservation.getEquipment() != null) {
            reservation.getEquipment().getId();
            reservation.getEquipment().getName();
        }
        if (reservation.getStudent() != null) {
            reservation.getStudent().getId();
            reservation.getStudent().getUsername();
        }
        if (reservation.getApprover() != null) {
            reservation.getApprover().getId();
            reservation.getApprover().getUsername();
        }
    }
}
