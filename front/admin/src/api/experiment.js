import http from "@/utils/http";

// 分页查询（支持 labId、keyword、sort）
export function listExperimentProjects(params = {}) {
  return http({
    url: "/api/experiment-projects",
    method: "GET",
    params
  });
}

export function getExperimentProjectById(id) {
  return http({
    url: `/api/experiment-projects/${id}`,
    method: "GET"
  });
}

// teacher/admin create
export function createExperimentProject(data) {
  return http({
    url: "/api/experiment-projects",
    method: "POST",
    data
  });
}

export function updateExperimentProject(id, data) {
  return http({
    url: `/api/experiment-projects/${id}`,
    method: "PUT",
    data
  });
}

// 你没给 delete，但通常是这个路径；如果后端没有 DELETE，会在前端提示失败
export function deleteExperimentProject(id) {
  return http({
    url: `/api/experiment-projects/${id}`,
    method: "DELETE"
  });
}

// student reserve experiment (query params)
export function reserveExperimentProject(experimentId, params = {}) {
  return http({
    url: `/api/experiment-projects/${experimentId}/reservations`,
    method: "POST",
    params
  });
}

// admin only
export function getExperimentStatistics() {
  return http({
    url: "/api/experiment-projects/statistics",
    method: "GET"
  });
}
