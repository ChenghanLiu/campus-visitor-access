package com.labsafety.system.training.dto;

import java.time.LocalDateTime;

public class SafetyTrainingAttemptResponse {

    private Long id;

    private Long courseId;
    private String courseTitle;

    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;

    private Integer score;
    private Boolean passed;

    private LocalDateTime createdAt;

    public SafetyTrainingAttemptResponse() {
    }

    public SafetyTrainingAttemptResponse(
            Long id,
            Long courseId,
            String courseTitle,
            LocalDateTime startedAt,
            LocalDateTime submittedAt,
            Integer score,
            Boolean passed,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.startedAt = startedAt;
        this.submittedAt = submittedAt;
        this.score = score;
        this.passed = passed;
        this.createdAt = createdAt;
    }

    // =========================
    // Getter / Setter
    // =========================

    public Long getId() {
        return id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public Integer getScore() {
        return score;
    }

    public Boolean getPassed() {
        return passed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void setPassed(Boolean passed) {
        this.passed = passed;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
