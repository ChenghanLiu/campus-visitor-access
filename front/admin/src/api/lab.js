import http from "@/utils/http";

// 获取所有实验室
export function getAllLabs() {
  return http({
    url: "/api/labs",
    method: "GET"
  });
}

// 根据ID获取
export function getLabById(id) {
  return http({
    url: `/api/labs/${id}`,
    method: "GET"
  });
}

// 创建（ADMIN）
export function createLab(data) {
  return http({
    url: "/api/labs",
    method: "POST",
    data
  });
}

// 更新（ADMIN）
export function updateLab(id, data) {
  return http({
    url: `/api/labs/${id}`,
    method: "PUT",
    data
  });
}

// 删除（ADMIN）
export function deleteLab(id) {
  return http({
    url: `/api/labs/${id}`,
    method: "DELETE"
  });
}
