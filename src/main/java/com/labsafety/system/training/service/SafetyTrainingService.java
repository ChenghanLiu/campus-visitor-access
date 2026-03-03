package com.labsafety.system.training.service;

import com.labsafety.system.training.*;
import com.labsafety.system.training.repository.*;
import com.labsafety.system.user.User;
import com.labsafety.system.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class SafetyTrainingService {

    private final SafetyTrainingCourseRepository courseRepository;
    private final SafetyTrainingQuestionRepository questionRepository;
    private final SafetyTrainingEnrollmentRepository enrollmentRepository;
    private final SafetyTrainingAttemptRepository attemptRepository;
    private final SafetyTrainingAttemptAnswerRepository answerRepository;
    private final UserRepository userRepository;

    public SafetyTrainingService(
            SafetyTrainingCourseRepository courseRepository,
            SafetyTrainingQuestionRepository questionRepository,
            SafetyTrainingEnrollmentRepository enrollmentRepository,
            SafetyTrainingAttemptRepository attemptRepository,
            SafetyTrainingAttemptAnswerRepository answerRepository,
            UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.questionRepository = questionRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.attemptRepository = attemptRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
    }

    // ===============================
    // 1️⃣ 报名课程
    // ===============================
    public void enroll(Long courseId, String username) {

        SafetyTrainingCourse course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (enrollmentRepository.existsByCourse_IdAndStudent_Id(courseId, student.getId())) {
            return;
        }

        SafetyTrainingEnrollment enrollment = new SafetyTrainingEnrollment();
        enrollment.setCourse(course);
        enrollment.setStudent(student);
        enrollment.setStatus(SafetyTrainingEnrollment.EnrollmentStatus.ENROLLED);

        enrollmentRepository.save(enrollment);
    }

    // ===============================
    // 2️⃣ 开始考试
    // ===============================
    public SafetyTrainingAttempt startAttempt(Long courseId, String username) {

        SafetyTrainingCourse course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SafetyTrainingAttempt attempt = new SafetyTrainingAttempt();
        attempt.setCourse(course);
        attempt.setStudent(student);
        attempt.setStartedAt(LocalDateTime.now());

        return attemptRepository.save(attempt);
    }

    // ===============================
    // 3️⃣ 提交考试（自动评分）
    // ===============================
    public SafetyTrainingAttempt submitAttempt(
            Long attemptId,
            Map<Long, SafetyTrainingAttemptAnswer.SelectedOption> answersMap) {

        SafetyTrainingAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        if (attempt.getSubmittedAt() != null) {
            throw new RuntimeException("Attempt already submitted");
        }

        List<SafetyTrainingQuestion> questions =
                questionRepository.findByCourse_IdOrderBySortOrderAsc(
                        attempt.getCourse().getId());

        int totalQuestions = questions.size();
        int correctCount = 0;

        for (SafetyTrainingQuestion question : questions) {

            SafetyTrainingAttemptAnswer.SelectedOption selected =
                    answersMap.get(question.getId());

            if (selected == null) {
                continue;
            }

            boolean isCorrect =
                    selected.name().equals(question.getCorrectOption().name());

            if (isCorrect) {
                correctCount++;
            }

            SafetyTrainingAttemptAnswer answer = new SafetyTrainingAttemptAnswer();
            answer.setAttempt(attempt);
            answer.setQuestion(question);
            answer.setSelectedOption(selected);
            answer.setIsCorrect(isCorrect);

            answerRepository.save(answer);
        }

        int score = totalQuestions == 0 ? 0 :
                (int) ((correctCount * 100.0) / totalQuestions);

        boolean passed = score >= attempt.getCourse().getPassingScore();

        attempt.setScore(score);
        attempt.setPassed(passed);
        attempt.setSubmittedAt(LocalDateTime.now());

        return attemptRepository.save(attempt);
    }

    // ===============================
    // 4️⃣ 查看成绩
    // ===============================
    @Transactional(readOnly = true)
    public List<SafetyTrainingAttempt> getStudentAttempts(Long courseId, String username) {

        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return attemptRepository
                .findByCourseIdAndStudentIdWithCourse(courseId, student.getId());

    }
    public SafetyTrainingCourse createCourse(SafetyTrainingCourse course) {

        if (course.getStartTime() == null || course.getEndTime() == null) {
            throw new RuntimeException("Start time and end time required");
        }

        if (!course.getStartTime().isBefore(course.getEndTime())) {
            throw new RuntimeException("Invalid time range");
        }

        return courseRepository.save(course);
    }

}
