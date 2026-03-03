package com.labsafety.system.training;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "training_attempt_answers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"attempt_id", "question_id"})
        }
)
public class SafetyTrainingAttemptAnswer {

    public enum SelectedOption {
        A, B, C, D
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attempt_id", nullable = false)
    private SafetyTrainingAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private SafetyTrainingQuestion question;

    @Enumerated(EnumType.STRING)
    @Column(name = "selected_option", nullable = false, length = 1)
    private SelectedOption selectedOption;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public SafetyTrainingAttemptAnswer() {
    }

    public Long getId() {
        return id;
    }

    public SafetyTrainingAttempt getAttempt() {
        return attempt;
    }

    public void setAttempt(SafetyTrainingAttempt attempt) {
        this.attempt = attempt;
    }

    public SafetyTrainingQuestion getQuestion() {
        return question;
    }

    public void setQuestion(SafetyTrainingQuestion question) {
        this.question = question;
    }

    public SelectedOption getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(SelectedOption selectedOption) {
        this.selectedOption = selectedOption;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean correct) {
        isCorrect = correct;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
