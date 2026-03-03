package com.labsafety.system.experiment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExperimentProjectReservationRepository
        extends JpaRepository<ExperimentProjectReservation, Long> {

    Optional<ExperimentProjectReservation> findByReservation_Id(Long reservationId);

    List<ExperimentProjectReservation> findByExperimentProject_Id(Long experimentProjectId);

    boolean existsByReservation_Id(Long reservationId);

    @Query("""
        SELECT epr.experimentProject.id,
               epr.experimentProject.name,
               COUNT(epr.id)
        FROM ExperimentProjectReservation epr
        GROUP BY epr.experimentProject.id, epr.experimentProject.name
    """)
    List<Object[]> countReservationsByProject();
}
