package com.labsafety.system.training.dto;

/**
 * Statistics response DTOs for safety training.
 *
 * Pass rule:
 * - Only attempts with submittedAt != null are counted as "attempted"
 * - Passed attempts are those with passed = true
 */
public class SafetyTrainingStatisticsResponse {

    private SafetyTrainingStatisticsResponse() {
    }

    public record OverviewResponse(
            long totalCourses,
            long totalEnrollments,
            long submittedAttempts,
            long passedAttempts,
            double passRate
    ) {
    }

    public record CourseStatResponse(
            Long courseId,
            String courseTitle,
            long submittedAttempts,
            long passedAttempts,
            double passRate
    ) {
    }

    /**
     * Internal projection row from JPQL constructor.
     * (Used by repository; then service converts to CourseStatResponse and computes passRate.)
     */
    public record CourseStatRow(
            Long courseId,
            String courseTitle,
            long submittedAttempts,
            long passedAttempts
    ) {
    }
}
