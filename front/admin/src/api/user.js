import http from "@/utils/http";

// pageable list
export function listUsers(params = {}) {
  return http({
    url: "/api/users",
    method: "GET",
    params
  });
}

export function getUserById(id) {
  return http({
    url: `/api/users/${id}`,
    method: "GET"
  });
}

export function createUser(data) {
  return http({
    url: "/api/users",
    method: "POST",
    data
  });
}

export function updateUser(id, data) {
  return http({
    url: `/api/users/${id}`,
    method: "PUT",
    data
  });
}

export function deleteUser(id) {
  return http({
    url: `/api/users/${id}`,
    method: "DELETE"
  });
}
