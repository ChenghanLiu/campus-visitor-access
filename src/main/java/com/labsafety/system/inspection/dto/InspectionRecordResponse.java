package com.labsafety.system.inspection.dto;

import java.time.LocalDateTime;

public class InspectionRecordResponse {

    private Long id;
    private Long planId;

    private Boolean isSafe;
    private String problemDescription;
    private String suggestion;

    private LocalDateTime createdAt;

    public InspectionRecordResponse() {}

    public Long getId() { return id; }
    public Long getPlanId() { return planId; }
    public Boolean getIsSafe() { return isSafe; }
    public String getProblemDescription() { return problemDescription; }
    public String getSuggestion() { return suggestion; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setPlanId(Long planId) { this.planId = planId; }
    public void setIsSafe(Boolean safe) { isSafe = safe; }
    public void setProblemDescription(String problemDescription) { this.problemDescription = problemDescription; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
