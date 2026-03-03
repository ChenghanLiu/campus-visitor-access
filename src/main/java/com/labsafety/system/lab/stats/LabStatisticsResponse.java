package com.labsafety.system.lab.stats;

public class LabStatisticsResponse {

    private LabStatisticsResponse() {
    }

    public record OverviewResponse(
            long totalLabs,
            long totalCapacity,
            long totalEquipment,
            long totalReservations,
            long activeReservations,
            long totalInspectionPlans,
            long unsafeInspectionCount
    ) {
    }

    public record LabStatResponse(
            Long labId,
            String labName,
            int capacity,
            long equipmentCount,
            long totalReservations,
            long activeReservations,
            long inspectionPlans,
            long unsafeInspections
    ) {
    }

    /**
     * Internal projection row from JPQL constructor.
     */
    public record LabStatRow(
            Long labId,
            String labName,
            Integer capacity,
            long equipmentCount,
            long totalReservations,
            long activeReservations,
            long inspectionPlans,
            long unsafeInspections
    ) {
    }
}
