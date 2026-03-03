package com.labsafety.system.training.controller;

import com.labsafety.system.training.SafetyTrainingAttempt;
import com.labsafety.system.training.SafetyTrainingAttemptAnswer;
import com.labsafety.system.training.SafetyTrainingCourse;
import com.labsafety.system.training.dto.SafetyTrainingAttemptResponse;
import com.labsafety.system.training.service.SafetyTrainingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/training")
public class SafetyTrainingController {

    private final SafetyTrainingService trainingService;

    public SafetyTrainingController(SafetyTrainingService trainingService) {
        this.trainingService = trainingService;
    }

    // ===============================
    // ADMIN 创建课程
    // ===============================
    @PostMapping("/courses")
    @PreAuthorize("hasRole('ADMIN')")
    public SafetyTrainingCourse createCourse(@RequestBody SafetyTrainingCourse course) {
        return trainingService.createCourse(course);
    }

    // ===============================
    // STUDENT 报名课程
    // ===============================
    @PostMapping("/courses/{courseId}/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public void enroll(@PathVariable Long courseId, Authentication authentication) {
        trainingService.enroll(courseId, authentication.getName());
    }

    // ===============================
    // STUDENT 开始考试
    // ===============================
    @PostMapping("/courses/{courseId}/attempts/start")
    @PreAuthorize("hasRole('STUDENT')")
    public SafetyTrainingAttempt startAttempt(@PathVariable Long courseId,
                                              Authentication authentication) {
        return trainingService.startAttempt(courseId, authentication.getName());
    }

    // ===============================
    // STUDENT 提交考试
    // Body 示例：
    // {
    //   "1": "A",
    //   "2": "C",
    //   "3": "B"
    // }
    // ===============================
    @PostMapping("/attempts/{attemptId}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public Map<String, Object> submitAttempt(
            @PathVariable Long attemptId,
            @RequestBody Map<Long, SafetyTrainingAttemptAnswer.SelectedOption> answers) {

        SafetyTrainingAttempt saved = trainingService.submitAttempt(attemptId, answers);

        return Map.of(
                "attemptId", saved.getId(),
                "score", saved.getScore(),
                "passed", saved.getPassed(),
                "submittedAt", saved.getSubmittedAt()
        );
    }

    @GetMapping("/courses/{courseId}/attempts")
    @PreAuthorize("hasRole('STUDENT')")
    public List<SafetyTrainingAttemptResponse> getAttempts(
            @PathVariable Long courseId,
            Authentication authentication) {

        List<SafetyTrainingAttempt> attempts =
                trainingService.getStudentAttempts(courseId, authentication.getName());

        return attempts.stream()
                .map(a -> new SafetyTrainingAttemptResponse(
                        a.getId(),
                        a.getCourse().getId(),
                        a.getCourse().getTitle(),
                        a.getStartedAt(),
                        a.getSubmittedAt(),
                        a.getScore(),
                        a.getPassed(),
                        a.getCreatedAt()
                ))
                .toList();
    }

}
