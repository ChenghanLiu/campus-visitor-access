package com.labsafety.system.training.controller;

import com.labsafety.system.training.dto.SafetyTrainingCourseResponse;
import com.labsafety.system.training.service.SafetyTrainingCourseQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/training/courses")
public class SafetyTrainingCourseQueryController {

    private final SafetyTrainingCourseQueryService queryService;

    public SafetyTrainingCourseQueryController(SafetyTrainingCourseQueryService queryService) {
        this.queryService = queryService;
    }

    /**
     * ADMIN / STUDENT: 查看课程列表（分页）
     * 返回课程完整字段（与创建课程返回字段对齐）
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    public Page<SafetyTrainingCourseResponse> list(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return queryService.listCourses(pageable);
    }

    /**
     * ADMIN / STUDENT: 查看课程详情（完整字段）
     */
    @GetMapping("/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    public SafetyTrainingCourseResponse getById(@PathVariable Long courseId) {
        return queryService.getCourse(courseId);
    }
}
