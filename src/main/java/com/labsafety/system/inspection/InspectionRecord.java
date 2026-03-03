package com.labsafety.system.inspection;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inspection_records")
public class InspectionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private InspectionPlan plan;

    @Column(name = "is_safe", nullable = false)
    private Boolean isSafe;

    @Column(name = "problem_description", columnDefinition = "TEXT")
    private String problemDescription;

    @Column(columnDefinition = "TEXT")
    private String suggestion;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public InspectionRecord() {
    }

    public Long getId() {
        return id;
    }

    public InspectionPlan getPlan() {
        return plan;
    }

    public void setPlan(InspectionPlan plan) {
        this.plan = plan;
    }

    public Boolean getIsSafe() {
        return isSafe;
    }

    public void setIsSafe(Boolean safe) {
        isSafe = safe;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
