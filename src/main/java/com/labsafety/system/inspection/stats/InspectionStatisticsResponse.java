package com.labsafety.system.inspection.stats;

public class InspectionStatisticsResponse {

    private InspectionStatisticsResponse() {}

    public record OverviewResponse(
            long totalPlans,
            long completedPlans,
            long totalRecords,
            long unsafeCount,
            double safeRate
    ) {}

    public record LabStatResponse(
            Long labId,
            String labName,
            long totalPlans,
            long completedPlans,
            long totalRecords,
            long unsafeCount,
            double safeRate
    ) {}

    public record LabStatRow(
            Long labId,
            String labName,
            long totalPlans,
            long completedPlans,
            long totalRecords,
            long unsafeCount
    ) {}
}
