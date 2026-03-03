import http from "@/utils/http";

// 分页：按实验室查询检查计划
export function listInspectionPlansByLab(labId, params = {}) {
    return http({
        url: `/api/inspection/labs/${labId}`,
        method: "GET",
        params
    });
}

// 创建检查计划（用 query params）
export function createInspectionPlan(params = {}) {
    return http({
        url: "/api/inspection/plans",
        method: "POST",
        params
    });
}

// 录入检查结果（用 query params）
export function recordInspection(planId, params = {}) {
    return http({
        url: `/api/inspection/plans/${planId}/record`,
        method: "POST",
        params
    });
}

// 查询检查结果（按 planId）
export function getInspectionRecord(planId) {
    return http({
        url: `/api/inspection/plans/${planId}/record`,
        method: "GET"
    });
}
