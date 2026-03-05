package com.labsafety.system.reservation.dto;

import com.labsafety.system.reservation.ReservationStatus;

import java.time.LocalDateTime;

public class ReservationResponse {

    private Long id;

    private Long labId;
    private String labName;

    private Long equipmentId;
    private String equipmentName;

    private Long studentId;
    private String studentUsername;

    private ReservationStatus status;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String purpose;
    private String remark;

    private Long approverId;
    private String approverUsername;

    private LocalDateTime decidedAt;
    private String decisionNote;

    private LocalDateTime createdAt;

    private String idCardPhotoUrl;
    private String facePhotoUrl;

    private LocalDateTime verifiedAt;
    private Long verifiedById;
    private String verifiedByUsername;
    private String verifyNote;

    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    public ReservationResponse() {
    }

    // ===== getters & setters =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLabId() { return labId; }
    public void setLabId(Long labId) { this.labId = labId; }

    public String getLabName() { return labName; }
    public void setLabName(String labName) { this.labName = labName; }

    public Long getEquipmentId() { return equipmentId; }
    public void setEquipmentId(Long equipmentId) { this.equipmentId = equipmentId; }

    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentUsername() { return studentUsername; }
    public void setStudentUsername(String studentUsername) { this.studentUsername = studentUsername; }

    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Long getApproverId() { return approverId; }
    public void setApproverId(Long approverId) { this.approverId = approverId; }

    public String getApproverUsername() { return approverUsername; }
    public void setApproverUsername(String approverUsername) { this.approverUsername = approverUsername; }

    public LocalDateTime getDecidedAt() { return decidedAt; }
    public void setDecidedAt(LocalDateTime decidedAt) { this.decidedAt = decidedAt; }

    public String getDecisionNote() { return decisionNote; }
    public void setDecisionNote(String decisionNote) { this.decisionNote = decisionNote; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // ===== getters & setters =====
    public String getIdCardPhotoUrl() { return idCardPhotoUrl; }
    public void setIdCardPhotoUrl(String idCardPhotoUrl) { this.idCardPhotoUrl = idCardPhotoUrl; }

    public String getFacePhotoUrl() { return facePhotoUrl; }
    public void setFacePhotoUrl(String facePhotoUrl) { this.facePhotoUrl = facePhotoUrl; }

    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }

    public Long getVerifiedById() { return verifiedById; }
    public void setVerifiedById(Long verifiedById) { this.verifiedById = verifiedById; }

    public String getVerifiedByUsername() { return verifiedByUsername; }
    public void setVerifiedByUsername(String verifiedByUsername) { this.verifiedByUsername = verifiedByUsername; }

    public String getVerifyNote() { return verifyNote; }
    public void setVerifyNote(String verifyNote) { this.verifyNote = verifyNote; }

    public LocalDateTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalDateTime checkInTime) { this.checkInTime = checkInTime; }

    public LocalDateTime getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(LocalDateTime checkOutTime) { this.checkOutTime = checkOutTime; }
}
