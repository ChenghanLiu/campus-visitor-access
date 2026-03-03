import http from "@/utils/http";

// Reservation
export function getReservationStats() {
    return http({ url: "/api/reservations/stats", method: "GET" });
}

// Experiment
export function getExperimentStats() {
    return http({ url: "/api/experiment-projects/statistics", method: "GET" });
}

// Training
export function getTrainingOverviewStats() {
    return http({ url: "/api/training/statistics/overview", method: "GET" });
}

export function getTrainingCourseStats(params = {}) {
    return http({ url: "/api/training/statistics/courses", method: "GET", params });
}

// Lab
export function getLabOverviewStats() {
    return http({ url: "/api/labs/statistics/overview", method: "GET" });
}

export function getLabStatsByLab(params = {}) {
    return http({ url: "/api/labs/statistics/labs", method: "GET", params });
}

// Inspection
export function getInspectionOverviewStats() {
    return http({ url: "/api/inspection/statistics/overview", method: "GET" });
}

export function getInspectionStatsByLab(params = {}) {
    return http({ url: "/api/inspection/statistics/labs", method: "GET", params });
}
