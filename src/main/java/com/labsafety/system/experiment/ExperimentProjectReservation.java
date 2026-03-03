package com.labsafety.system.experiment;

import com.labsafety.system.reservation.Reservation;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "experiment_project_reservations",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "reservation_id")
        })
public class ExperimentProjectReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "experiment_project_id", nullable = false)
    private ExperimentProject experimentProject;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public ExperimentProjectReservation() {
    }

    public Long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public ExperimentProject getExperimentProject() {
        return experimentProject;
    }

    public void setExperimentProject(ExperimentProject experimentProject) {
        this.experimentProject = experimentProject;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
