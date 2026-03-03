package com.labsafety.system.inspection.dto;

import com.labsafety.system.inspection.InspectionPlan;
import com.labsafety.system.inspection.InspectionRecord;

public class InspectionMapper {

    private InspectionMapper() {}

    public static InspectionPlanResponse toPlanResponse(InspectionPlan plan) {
        if (plan == null) return null;

        InspectionPlanResponse r = new InspectionPlanResponse();
        r.setId(plan.getId());

        // 只取 ID（通常 Hibernate proxy 的 id 可直接拿到，不需要初始化）
        r.setLabId(plan.getLab() != null ? plan.getLab().getId() : null);
        r.setInspectorId(plan.getInspector() != null ? plan.getInspector().getId() : null);

        r.setInspectionTime(plan.getInspectionTime());
        r.setContent(plan.getContent());
        r.setStatus(plan.getStatus());
        r.setCreatedAt(plan.getCreatedAt());
        return r;
    }

    public static InspectionRecordResponse toRecordResponse(InspectionRecord record) {
        if (record == null) return null;

        InspectionRecordResponse r = new InspectionRecordResponse();
        r.setId(record.getId());
        r.setPlanId(record.getPlan() != null ? record.getPlan().getId() : null);

        r.setIsSafe(record.getIsSafe());
        r.setProblemDescription(record.getProblemDescription());
        r.setSuggestion(record.getSuggestion());
        r.setCreatedAt(record.getCreatedAt());
        return r;
    }
}
