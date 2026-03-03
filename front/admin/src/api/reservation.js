import http from "@/utils/http";

export function listReservationsByRole(role, params = {}) {
  if (role === "STUDENT") {
    return http({ url: "/api/reservations/my", method: "GET", params });
  }
  if (role === "TEACHER") {
    return http({ url: "/api/reservations/pending", method: "GET", params });
  }
  return http({ url: "/api/reservations", method: "GET", params });
}

export function createReservation(data) {
  return http({ url: "/api/reservations", method: "POST", data });
}

// ✅ 允许传 decisionNote（后端如果不需要也不会坏）
export function approveReservation(id, data = {}) {
  return http({
    url: `/api/reservations/${id}/approve`,
    method: "PUT",
    data
  });
}

export function rejectReservation(id, data = {}) {
  return http({
    url: `/api/reservations/${id}/reject`,
    method: "PUT",
    data
  });
}

export function cancelReservation(id, data = {}) {
  return http({
    url: `/api/reservations/${id}/cancel`,
    method: "PUT",
    data
  });
}

