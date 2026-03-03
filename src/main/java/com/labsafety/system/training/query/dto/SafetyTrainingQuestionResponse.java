package com.labsafety.system.training.query.dto;

public record SafetyTrainingQuestionResponse(
        Long questionId,
        String questionText,
        String optionA,
        String optionB,
        String optionC,
        String optionD
) {
}
