package com.labsafety.system.inspection.dto;

import com.labsafety.system.inspection.InspectionPlan;

import java.time.LocalDateTime;

public class InspectionPlanResponse {

    private Long id;

    private Long labId;
    private Long inspectorId;

    private LocalDateTime inspectionTime;
    private String content;

    private InspectionPlan.InspectionStatus status;

    private LocalDateTime createdAt;

    public InspectionPlanResponse() {}

    public Long getId() { return id; }
    public Long getLabId() { return labId; }
    public Long getInspectorId() { return inspectorId; }
    public LocalDateTime getInspectionTime() { return inspectionTime; }
    public String getContent() { return content; }
    public InspectionPlan.InspectionStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setLabId(Long labId) { this.labId = labId; }
    public void setInspectorId(Long inspectorId) { this.inspectorId = inspectorId; }
    public void setInspectionTime(LocalDateTime inspectionTime) { this.inspectionTime = inspectionTime; }
    public void setContent(String content) { this.content = content; }
    public void setStatus(InspectionPlan.InspectionStatus status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
