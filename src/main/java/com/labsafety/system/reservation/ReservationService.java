package com.labsafety.system.reservation;

import com.labsafety.system.equipment.Equipment;
import com.labsafety.system.equipment.EquipmentRepository;
import com.labsafety.system.lab.Lab;
import com.labsafety.system.lab.LabRepository;
import com.labsafety.system.user.User;
import com.labsafety.system.user.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final LabRepository labRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              LabRepository labRepository,
                              EquipmentRepository equipmentRepository,
                              UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.labRepository = labRepository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
    }

    /**
     * Blocking statuses for time conflict checks:
     * - PENDING blocks
     * - APPROVED blocks
     * - REJECTED/CANCELLED do NOT block
     */
    private static final EnumSet<ReservationStatus> BLOCKING_STATUSES =
            EnumSet.of(ReservationStatus.PENDING, ReservationStatus.APPROVED);

    public Reservation createReservation(Long labId,
                                         Long equipmentId,
                                         String studentUsername,
                                         LocalDateTime startTime,
                                         LocalDateTime endTime,
                                         String purpose,
                                         String remark) {

        validateTimeRange(startTime, endTime);

        Lab lab = labRepository.findById(labId)
                .orElseThrow(() -> new RuntimeException("Lab not found"));

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        User student = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (reservationRepository.existsLabTimeConflict(lab.getId(), startTime, endTime, BLOCKING_STATUSES)) {
            throw new RuntimeException("Lab time conflict");
        }

        if (reservationRepository.existsEquipmentTimeConflict(equipment.getId(), startTime, endTime, BLOCKING_STATUSES)) {
            throw new RuntimeException("Equipment time conflict");
        }

        Reservation reservation = new Reservation();
        reservation.setLab(lab);
        reservation.setEquipment(equipment);
        reservation.setStudent(student);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setPurpose(purpose);
        reservation.setRemark(remark);

        Reservation saved = reservationRepository.save(reservation);
        initializeForResponse(saved);
        return saved;
    }

    public Reservation approve(Long reservationId, String approverUsername, String decisionNote) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        ensurePending(reservation);

        User approver = userRepository.findByUsername(approverUsername)
                .orElseThrow(() -> new RuntimeException("Approver not found"));

        reservation.setApprover(approver);
        reservation.setDecidedAt(LocalDateTime.now());
        reservation.setDecisionNote(decisionNote);
        reservation.setStatus(ReservationStatus.APPROVED);

        Reservation saved = reservationRepository.save(reservation);
        initializeForResponse(saved);
        return saved;
    }

    public Reservation reject(Long reservationId, String approverUsername, String decisionNote) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        ensurePending(reservation);

        User approver = userRepository.findByUsername(approverUsername)
                .orElseThrow(() -> new RuntimeException("Approver not found"));

        reservation.setApprover(approver);
        reservation.setDecidedAt(LocalDateTime.now());
        reservation.setDecisionNote(decisionNote);
        reservation.setStatus(ReservationStatus.REJECTED);

        Reservation saved = reservationRepository.save(reservation);
        initializeForResponse(saved);
        return saved;
    }

    /**
     * STUDENT cancels their own reservation.
     * Allowed statuses: PENDING, APPROVED
     */
    public Reservation cancelAsStudent(Long reservationId, String studentUsername, String cancelNote) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (reservation.getStudent() == null || reservation.getStudent().getUsername() == null) {
            throw new RuntimeException("Reservation student missing");
        }

        if (!reservation.getStudent().getUsername().equals(studentUsername)) {
            throw new AccessDeniedException("Cannot cancel other user's reservation");
        }

        ensureCancelable(reservation);

        User canceller = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        applyCancel(reservation, canceller, cancelNote);

        Reservation saved = reservationRepository.save(reservation);
        initializeForResponse(saved);
        return saved;
    }

    /**
     * ADMIN cancels any reservation.
     * Allowed statuses: PENDING, APPROVED
     */
    public Reservation cancelAsAdmin(Long reservationId, String adminUsername, String cancelNote) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        ensureCancelable(reservation);

        User canceller = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        applyCancel(reservation, canceller, cancelNote);

        Reservation saved = reservationRepository.save(reservation);
        initializeForResponse(saved);
        return saved;
    }

    private void applyCancel(Reservation reservation, User canceller, String cancelNote) {
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setApprover(canceller); // audit: who cancelled (student/admin)
        reservation.setDecidedAt(LocalDateTime.now());
        reservation.setDecisionNote(cancelNote);
    }

    private void ensurePending(Reservation reservation) {
        if (reservation.getStatus() == null) {
            throw new RuntimeException("Reservation status is missing");
        }
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new RuntimeException("Only PENDING reservations can be decided");
        }
    }

    private void ensureCancelable(Reservation reservation) {
        if (reservation.getStatus() == null) {
            throw new RuntimeException("Reservation status is missing");
        }
        if (reservation.getStatus() == ReservationStatus.REJECTED) {
            throw new RuntimeException("REJECTED reservation cannot be cancelled");
        }
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new RuntimeException("Reservation already cancelled");
        }
        // PENDING / APPROVED are OK
    }

    private void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new RuntimeException("Start time and end time are required");
        }
        if (!startTime.isBefore(endTime)) {
            throw new RuntimeException("Invalid time range: startTime must be before endTime");
        }
    }

    /**
     * Initialize LAZY relations that ReservationMapper reads.
     * Prevents LazyInitializationException when mapping happens outside transaction.
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
