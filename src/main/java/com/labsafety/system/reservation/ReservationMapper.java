package com.labsafety.system.reservation;

import com.labsafety.system.reservation.dto.ReservationResponse;

public final class ReservationMapper {

    private ReservationMapper() {
    }

    public static ReservationResponse toResponse(Reservation reservation) {
        if (reservation == null) {
            return null;
        }

        ReservationResponse resp = new ReservationResponse();

        resp.setId(reservation.getId());

        if (reservation.getLab() != null) {
            resp.setLabId(reservation.getLab().getId());
            // Lab name may trigger lazy loading if not fetched; OK for now.
            resp.setLabName(reservation.getLab().getName());
        }

        if (reservation.getEquipment() != null) {
            resp.setEquipmentId(reservation.getEquipment().getId());
            resp.setEquipmentName(reservation.getEquipment().getName());
        }

        if (reservation.getStudent() != null) {
            resp.setStudentId(reservation.getStudent().getId());
            resp.setStudentUsername(reservation.getStudent().getUsername());
        }

        resp.setStatus(reservation.getStatus());

        resp.setStartTime(reservation.getStartTime());
        resp.setEndTime(reservation.getEndTime());

        resp.setPurpose(reservation.getPurpose());
        resp.setRemark(reservation.getRemark());

        if (reservation.getApprover() != null) {
            resp.setApproverId(reservation.getApprover().getId());
            resp.setApproverUsername(reservation.getApprover().getUsername());
        }

        resp.setDecidedAt(reservation.getDecidedAt());
        resp.setDecisionNote(reservation.getDecisionNote());
        resp.setCreatedAt(reservation.getCreatedAt());
        // === 照片/核检/出入（新增）===
        resp.setIdCardPhotoUrl(reservation.getIdCardPhotoUrl());
        resp.setFacePhotoUrl(reservation.getFacePhotoUrl());

        resp.setVerifiedAt(reservation.getVerifiedAt());
        resp.setVerifyNote(reservation.getVerifyNote());

        if (reservation.getVerifiedBy() != null) {
            resp.setVerifiedById(reservation.getVerifiedBy().getId());
            resp.setVerifiedByUsername(reservation.getVerifiedBy().getUsername());
        }

        resp.setCheckInTime(reservation.getCheckInTime());
        resp.setCheckOutTime(reservation.getCheckOutTime());

        return resp;
    }
}
