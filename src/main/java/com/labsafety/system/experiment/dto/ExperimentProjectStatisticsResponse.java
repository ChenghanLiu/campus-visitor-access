package com.labsafety.system.experiment.dto;

public class ExperimentProjectStatisticsResponse {

    private Long experimentProjectId;
    private String experimentProjectName;
    private Long reservationCount;

    public ExperimentProjectStatisticsResponse() {
    }

    public ExperimentProjectStatisticsResponse(Long experimentProjectId,
                                               String experimentProjectName,
                                               Long reservationCount) {
        this.experimentProjectId = experimentProjectId;
        this.experimentProjectName = experimentProjectName;
        this.reservationCount = reservationCount;
    }

    public Long getExperimentProjectId() {
        return experimentProjectId;
    }

    public void setExperimentProjectId(Long experimentProjectId) {
        this.experimentProjectId = experimentProjectId;
    }

    public String getExperimentProjectName() {
        return experimentProjectName;
    }

    public void setExperimentProjectName(String experimentProjectName) {
        this.experimentProjectName = experimentProjectName;
    }

    public Long getReservationCount() {
        return reservationCount;
    }

    public void setReservationCount(Long reservationCount) {
        this.reservationCount = reservationCount;
    }
}
