package com.labsafety.system.inspection.controller;

import com.labsafety.system.inspection.InspectionPlan;
import com.labsafety.system.inspection.InspectionRecord;
import com.labsafety.system.inspection.dto.InspectionMapper;
import com.labsafety.system.inspection.dto.InspectionPlanResponse;
import com.labsafety.system.inspection.dto.InspectionRecordResponse;
import com.labsafety.system.inspection.service.InspectionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/inspection")
public class InspectionController {

    private final InspectionService inspectionService;

    public InspectionController(InspectionService inspectionService) {
        this.inspectionService = inspectionService;
    }

    // ADMIN 创建检查计划
    @PostMapping("/plans")
    @PreAuthorize("hasRole('ADMIN')")
    public InspectionPlanResponse createPlan(
            @RequestParam Long labId,
            @RequestParam Long inspectorId,
            @RequestParam String inspectionTime,
            @RequestParam String content) {

        InspectionPlan plan = inspectionService.createPlan(
                labId,
                inspectorId,
                LocalDateTime.parse(inspectionTime),
                content
        );

        return InspectionMapper.toPlanResponse(plan);
    }

    // 提交检查记录（ADMIN / TEACHER）
    @PostMapping("/plans/{planId}/record")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public InspectionRecordResponse submitRecord(
            @PathVariable Long planId,
            @RequestParam Boolean isSafe,
            @RequestParam(required = false) String problemDescription,
            @RequestParam(required = false) String suggestion) {

        InspectionRecord record = inspectionService.submitRecord(
                planId,
                isSafe,
                problemDescription,
                suggestion
        );

        return InspectionMapper.toRecordResponse(record);
    }

    // 查看某实验室检查历史（分页）
    @GetMapping("/labs/{labId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public Page<InspectionPlanResponse> getPlansByLab(
            @PathVariable Long labId,
            Pageable pageable) {

        return inspectionService.getPlansByLab(labId, pageable)
                .map(InspectionMapper::toPlanResponse);
    }

    // 查看单个检查报告
    @GetMapping("/plans/{planId}/record")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public InspectionRecordResponse getRecord(@PathVariable Long planId) {

        InspectionRecord record = inspectionService.getRecordByPlan(planId);
        return InspectionMapper.toRecordResponse(record);
    }
}
