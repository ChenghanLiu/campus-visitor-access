import http from "@/utils/http";

// pageable list
export function listTrainingCourses(params = {}) {
  return http({
    url: "/api/training/courses",
    method: "GET",
    params
  });
}

// detail
export function getTrainingCourse(courseId) {
  return http({
    url: `/api/training/courses/${courseId}`,
    method: "GET"
  });
}

// questions
export function getTrainingQuestions(courseId) {
  return http({
    url: `/api/training/courses/${courseId}/questions`,
    method: "GET"
  });
}

// admin create course
export function createTrainingCourse(data) {
  return http({
    url: "/api/training/courses",
    method: "POST",
    data
  });
}

// student enroll
export function enrollCourse(courseId) {
  return http({
    url: `/api/training/courses/${courseId}/enroll`,
    method: "POST"
  });
}

// student start attempt
export function startAttempt(courseId) {
  return http({
    url: `/api/training/courses/${courseId}/attempts/start`,
    method: "POST"
  });
}

// student submit attempt
// IMPORTANT: body must be a pure JSON object, e.g. {"1":"A","2":"C"} (keys are questionId)
export function submitAttempt(attemptId, payloadObj) {
  return http({
    url: `/api/training/attempts/${attemptId}/submit`,
    method: "POST",
    data: payloadObj, // ✅ no wrapping, pure json object
    headers: {
      "Content-Type": "application/json"
    }
  });
}

// student history
export function getAttemptHistory(courseId) {
  return http({
    url: `/api/training/courses/${courseId}/attempts`,
    method: "GET"
  });
}
