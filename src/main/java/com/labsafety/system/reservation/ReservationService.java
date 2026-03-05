package com.labsafety.system.reservation;

import com.labsafety.system.equipment.Equipment;
import com.labsafety.system.equipment.EquipmentRepository;
import com.labsafety.system.lab.Lab;
import com.labsafety.system.lab.LabRepository;
import com.labsafety.system.reservation.dto.RegisterVisitorRequest;
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
     * PENDING / APPROVED / CHECKED_IN block
     */
    private static final EnumSet<ReservationStatus> BLOCKING_STATUSES =
            EnumSet.of(
                    ReservationStatus.PENDING,
                    ReservationStatus.APPROVED,
                    ReservationStatus.CHECKED_IN
            );

    // ===================== CREATE =====================

    public Reservation createReservation(Long labId,
                                         Long equipmentId,
                                         String studentUsername,
                                         LocalDateTime startTime,
                                         LocalDateTime endTime,
                                         String purpose,
                                         String remark,
                                         String idCardPhotoUrl,
                                         String facePhotoUrl) {

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
        reservation.setIdCardPhotoUrl(idCardPhotoUrl);
        reservation.setFacePhotoUrl(facePhotoUrl);


        Reservation saved = reservationRepository.save(reservation);
        initializeForResponse(saved);
        return saved;
    }

    // ===================== APPROVE / REJECT =====================

    public Reservation approve(Long reservationId, String approverUsername, String decisionNote) {
        Reservation reservation = findAndEnsureStatus(reservationId, ReservationStatus.PENDING);

        User approver = userRepository.findByUsername(approverUsername)
                .orElseThrow(() -> new RuntimeException("Approver not found"));

        reservation.setApprover(approver);
        reservation.setDecidedAt(LocalDateTime.now());
        reservation.setDecisionNote(decisionNote);
        reservation.setStatus(ReservationStatus.APPROVED);

        return saveAndInit(reservation);
    }

    public Reservation reject(Long reservationId, String approverUsername, String decisionNote) {
        Reservation reservation = findAndEnsureStatus(reservationId, ReservationStatus.PENDING);

        User approver = userRepository.findByUsername(approverUsername)
                .orElseThrow(() -> new RuntimeException("Approver not found"));

        reservation.setApprover(approver);
        reservation.setDecidedAt(LocalDateTime.now());
        reservation.setDecisionNote(decisionNote);
        reservation.setStatus(ReservationStatus.REJECTED);

        return saveAndInit(reservation);
    }

    // ===================== CHECK IN =====================

    public Reservation checkIn(Long reservationId, String adminUsername) {
        Reservation reservation = findAndEnsureStatus(reservationId, ReservationStatus.APPROVED);

        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        reservation.setStatus(ReservationStatus.CHECKED_IN);
        reservation.setCheckInTime(LocalDateTime.now());   // ✅ 新增
        reservation.setApprover(admin);
        reservation.setDecidedAt(LocalDateTime.now());

        return saveAndInit(reservation);
    }

    public Reservation checkOut(Long reservationId, String adminUsername) {
        Reservation reservation = findAndEnsureStatus(reservationId, ReservationStatus.CHECKED_IN);

        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        reservation.setStatus(ReservationStatus.CHECKED_OUT);
        reservation.setCheckOutTime(LocalDateTime.now());  // ✅ 新增
        reservation.setApprover(admin);
        reservation.setDecidedAt(LocalDateTime.now());

        return saveAndInit(reservation);
    }

    @Transactional
    public Reservation registerVisitor(Long id, String verifierUsername, RegisterVisitorRequest req) {

        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found: " + id));

        // 1) status guard (only APPROVED can register)
        if (r.getStatus() != ReservationStatus.APPROVED) {
            throw new RuntimeException("Only APPROVED reservations can be registered, current=" + r.getStatus());
        }

        // 2) set fields (example)
        r.setIdCardPhotoUrl(req.getIdCardPhotoUrl());
        r.setFacePhotoUrl(req.getFacePhotoUrl());
        r.setVerifyNote(req.getVerifyNote());
        r.setVerifiedAt(LocalDateTime.now());

        // verifiedById / verifiedByUsername — use whatever you decided (FK or String)
        // if you use FK:
        User verifier = userRepository.findByUsername(verifierUsername)
                .orElseThrow(() -> new RuntimeException("Verifier not found: " + verifierUsername));
        r.setVerifiedBy(verifier);

        // 3) optional: set status to CHECKED_IN (must be allowed by DB check constraint)
        r.setStatus(ReservationStatus.CHECKED_IN);
        r.setCheckInTime(LocalDateTime.now());

        reservationRepository.save(r);

        // ✅ 4) Re-fetch with fetch-join, so mapper won't lazy-crash
        return reservationRepository.findDetailById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found after save: " + id));
    }
    // ===================== CANCEL =====================

    public Reservation cancelAsStudent(Long reservationId, String studentUsername, String cancelNote) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (!reservation.getStudent().getUsername().equals(studentUsername)) {
            throw new AccessDeniedException("Cannot cancel other user's reservation");
        }

        ensureCancelable(reservation);

        User canceller = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        applyCancel(reservation, canceller, cancelNote);

        return saveAndInit(reservation);
    }

    public Reservation cancelAsAdmin(Long reservationId, String adminUsername, String cancelNote) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        ensureCancelable(reservation);

        User canceller = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        applyCancel(reservation, canceller, cancelNote);

        return saveAndInit(reservation);
    }

    private void applyCancel(Reservation reservation, User canceller, String cancelNote) {
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setApprover(canceller);
        reservation.setDecidedAt(LocalDateTime.now());
        reservation.setDecisionNote(cancelNote);
    }

    // ===================== HELPERS =====================

    private Reservation findAndEnsureStatus(Long reservationId, ReservationStatus expected) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (reservation.getStatus() != expected) {
            throw new RuntimeException("Invalid status transition");
        }
        return reservation;
    }

    private void ensureCancelable(Reservation reservation) {
        if (reservation.getStatus() == ReservationStatus.REJECTED) {
            throw new RuntimeException("REJECTED reservation cannot be cancelled");
        }
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new RuntimeException("Reservation already cancelled");
        }
        if (reservation.getStatus() == ReservationStatus.CHECKED_OUT) {
            throw new RuntimeException("Already checked out");
        }
    }

    private void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new RuntimeException("Start time and end time are required");
        }
        if (!startTime.isBefore(endTime)) {
            throw new RuntimeException("Invalid time range");
        }
    }

    private Reservation saveAndInit(Reservation reservation) {
        Reservation saved = reservationRepository.save(reservation);
        initializeForResponse(saved);
        return saved;
    }

    private void initializeForResponse(Reservation reservation) {
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