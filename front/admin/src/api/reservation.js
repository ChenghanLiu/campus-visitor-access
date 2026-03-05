import http from "@/utils/http";

export function listReservationsByRole(role, params = {}) {
  if (role === "STUDENT") {
    return http({ url: "/api/reservations/my", method: "GET", params });
  }
  if (role === "TEACHER") {
    // ✅ 关键：教师看全量（含 APPROVED），不再只看 pending
    return http({ url: "/api/reservations", method: "GET", params });
  }
  return http({ url: "/api/reservations", method: "GET", params });
}

export function createReservation(data) {
  return http({ url: "/api/reservations", method: "POST", data });
}

export function approveReservation(id, data = {}) {
  return http({ url: `/api/reservations/${id}/approve`, method: "PUT", data });
}

export function rejectReservation(id, data = {}) {
  return http({ url: `/api/reservations/${id}/reject`, method: "PUT", data });
}

export function cancelReservation(id, data = {}) {
  return http({ url: `/api/reservations/${id}/cancel`, method: "PUT", data });
}

// ✅ 核检 / 签退 / 登记（教师角色）
export function registerVisitor(id, data) {
  return http({ url: `/api/reservations/${id}/register`, method: "PUT", data });
}

export function checkInReservation(id) {
  return http({ url: `/api/reservations/${id}/check-in`, method: "PUT" });
}

export function checkOutReservation(id) {
  return http({ url: `/api/reservations/${id}/check-out`, method: "PUT" });
}