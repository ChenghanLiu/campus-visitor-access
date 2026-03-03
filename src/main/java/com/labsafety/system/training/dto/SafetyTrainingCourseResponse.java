package com.labsafety.system.training.dto;

import java.time.LocalDateTime;

public record SafetyTrainingCourseResponse(
        Long id,
        String title,
        String content,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String mode,
        Integer passingScore,
        Integer durationMinutes,
        LocalDateTime createdAt
) {
}
