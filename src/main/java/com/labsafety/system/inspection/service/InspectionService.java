package com.labsafety.system.inspection.service;

import com.labsafety.system.inspection.InspectionPlan;
import com.labsafety.system.inspection.InspectionRecord;
import com.labsafety.system.inspection.repository.InspectionPlanRepository;
import com.labsafety.system.inspection.repository.InspectionRecordRepository;
import com.labsafety.system.lab.Lab;
import com.labsafety.system.lab.LabRepository;
import com.labsafety.system.user.User;
import com.labsafety.system.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class InspectionService {

    private final InspectionPlanRepository planRepository;
    private final InspectionRecordRepository recordRepository;
    private final LabRepository labRepository;
    private final UserRepository userRepository;

    public InspectionService(
            InspectionPlanRepository planRepository,
            InspectionRecordRepository recordRepository,
            LabRepository labRepository,
            UserRepository userRepository) {
        this.planRepository = planRepository;
        this.recordRepository = recordRepository;
        this.labRepository = labRepository;
        this.userRepository = userRepository;
    }

    // ===============================
    // 1️⃣ 创建检查计划（ADMIN）
    // ===============================
    public InspectionPlan createPlan(Long labId,
                                     Long inspectorId,
                                     LocalDateTime inspectionTime,
                                     String content) {

        Lab lab = labRepository.findById(labId)
                .orElseThrow(() -> new RuntimeException("Lab not found"));

        User inspector = userRepository.findById(inspectorId)
                .orElseThrow(() -> new RuntimeException("Inspector not found"));

        InspectionPlan plan = new InspectionPlan();
        plan.setLab(lab);
        plan.setInspector(inspector);
        plan.setInspectionTime(inspectionTime);
        plan.setContent(content);
        plan.setStatus(InspectionPlan.InspectionStatus.SCHEDULED);

        return planRepository.save(plan);
    }

    // ===============================
    // 2️⃣ 提交检查记录（检查人）
    // ===============================
    public InspectionRecord submitRecord(Long planId,
                                         Boolean isSafe,
                                         String problemDescription,
                                         String suggestion) {

        InspectionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Inspection plan not found"));

        if (plan.getStatus() == InspectionPlan.InspectionStatus.COMPLETED) {
            throw new RuntimeException("Inspection already completed");
        }

        InspectionRecord record = new InspectionRecord();
        record.setPlan(plan);
        record.setIsSafe(isSafe);
        record.setProblemDescription(problemDescription);
        record.setSuggestion(suggestion);

        plan.setStatus(InspectionPlan.InspectionStatus.COMPLETED);

        recordRepository.save(record);
        return planRepository.save(plan) != null ? record : record;
    }

    // ===============================
    // 3️⃣ 查看实验室检查历史（分页）
    // ===============================
    @Transactional(readOnly = true)
    public Page<InspectionPlan> getPlansByLab(Long labId, Pageable pageable) {
        return planRepository.findByLab_Id(labId, pageable);
    }

    // ===============================
    // 4️⃣ 查看单个检查记录
    // ===============================
    @Transactional(readOnly = true)
    public InspectionRecord getRecordByPlan(Long planId) {
        return recordRepository.findByPlan_Id(planId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Inspection record not found"));
    }
}
