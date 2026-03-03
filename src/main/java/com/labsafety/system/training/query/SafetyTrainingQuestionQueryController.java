package com.labsafety.system.training.query;

import com.labsafety.system.training.query.dto.SafetyTrainingQuestionResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/training/courses")
public class SafetyTrainingQuestionQueryController {

    private final SafetyTrainingQuestionQueryService queryService;

    public SafetyTrainingQuestionQueryController(SafetyTrainingQuestionQueryService queryService) {
        this.queryService = queryService;
    }

    /**
     * ADMIN / STUDENT: 获取课程题目（不包含正确答案）
     */
    @GetMapping("/{courseId}/questions")
    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    public List<SafetyTrainingQuestionResponse> getQuestions(
            @PathVariable Long courseId
    ) {
        return queryService.getQuestions(courseId);
    }
}
