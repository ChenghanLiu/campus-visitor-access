export function getRole() {
    return (localStorage.getItem("Role") || "").trim().toUpperCase();
}

export function isAdmin() {
    const role = getRole();
    return role === "ADMIN" || role === "ROLE_ADMIN";
}

export function isTeacher() {
    const role = getRole();
    return role === "TEACHER" || role === "ROLE_TEACHER";
}

export function isStudent() {
    const role = getRole();
    return role === "STUDENT" || role === "ROLE_STUDENT";
}
