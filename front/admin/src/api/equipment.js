import http from "@/utils/http";

// 列表（分页 + 可选过滤参数）
export function listEquipment(params = {}) {
  return http({
    url: "/api/equipment",
    method: "GET",
    params
  });
}

export function getEquipmentById(id) {
  return http({
    url: `/api/equipment/${id}`,
    method: "GET"
  });
}

export function createEquipment(data) {
  return http({
    url: "/api/equipment",
    method: "POST",
    data
  });
}

export function updateEquipment(id, data) {
  return http({
    url: `/api/equipment/${id}`,
    method: "PUT",
    data
  });
}

export function deleteEquipment(id) {
  return http({
    url: `/api/equipment/${id}`,
    method: "DELETE"
  });
}
