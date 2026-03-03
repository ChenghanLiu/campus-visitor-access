// AUTO-GENERATED from Postman collection. Edit if needed.
import http from "@/utils/http";

// auth APIs

export function postAuthLogin(data = {}, config = {}) {
  return http({ url: "/api/auth/login", method: "POST", data, ...config });
}

export function postAuthRegister(data = {}, config = {}) {
  return http({ url: "/api/auth/register", method: "POST", data, ...config });
}
