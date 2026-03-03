package com.labsafety.system.experiment.service;

import com.labsafety.system.experiment.ExperimentProject;
import com.labsafety.system.experiment.ExperimentProjectRepository;
import com.labsafety.system.experiment.ExperimentProjectReservation;
import com.labsafety.system.experiment.ExperimentProjectReservationRepository;
import com.labsafety.system.experiment.dto.ExperimentProjectStatisticsResponse;
import com.labsafety.system.reservation.Reservation;
import com.labsafety.system.reservation.ReservationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ExperimentProjectReservationService {

    private final ReservationService reservationService;
    private final ExperimentProjectRepository experimentProjectRepository;
    private final ExperimentProjectReservationRepository eprRepository;

    public ExperimentProjectReservationService(
            ReservationService reservationService,
            ExperimentProjectRepository experimentProjectRepository,
            ExperimentProjectReservationRepository eprRepository) {
        this.reservationService = reservationService;
        this.experimentProjectRepository = experimentProjectRepository;
        this.eprRepository = eprRepository;
    }

    public Reservation createReservationWithProject(
            Long labId,
            Long equipmentId,
            String studentUsername,
            java.time.LocalDateTime startTime,
            java.time.LocalDateTime endTime,
            String purpose,
            String remark,
            Long experimentProjectId) {

        Reservation reservation = reservationService.createReservation(
                labId,
                equipmentId,
                studentUsername,
                startTime,
                endTime,
                purpose,
                remark
        );

        if (experimentProjectId == null) {
            return reservation;
        }

        ExperimentProject project = experimentProjectRepository.findById(experimentProjectId)
                .orElseThrow(() -> new RuntimeException("Experiment project not found"));

        ExperimentProjectReservation link = new ExperimentProjectReservation();
        link.setReservation(reservation);
        link.setExperimentProject(project);

        eprRepository.save(link);

        return reservation;
    }

    @Transactional(readOnly = true)
    public List<ExperimentProjectStatisticsResponse> getStatistics() {

        List<Object[]> rawResults = eprRepository.countReservationsByProject();
        List<ExperimentProjectStatisticsResponse> results = new ArrayList<>();

        for (Object[] row : rawResults) {
            Long projectId = (Long) row[0];
            String projectName = (String) row[1];
            Long count = (Long) row[2];

            results.add(new ExperimentProjectStatisticsResponse(
                    projectId,
                    projectName,
                    count
            ));
        }

        return results;
    }
}
