-- Safety Training Module: courses + questions + enrollments + exam attempts + answers
-- Tables: training_courses, training_questions, training_enrollments, training_attempts, training_attempt_answers

CREATE TABLE training_courses (
                                  id BIGINT NOT NULL AUTO_INCREMENT,

                                  title VARCHAR(150) NOT NULL,
                                  content TEXT NOT NULL,

                                  start_time DATETIME NOT NULL,
                                  end_time DATETIME NOT NULL,

                                  mode VARCHAR(20) NOT NULL, -- ONLINE / OFFLINE / HYBRID

                                  passing_score INT NOT NULL DEFAULT 60, -- 0-100
                                  duration_minutes INT NOT NULL DEFAULT 30,

                                  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                  PRIMARY KEY (id),

                                  CONSTRAINT ck_training_courses_time
                                      CHECK (end_time > start_time),

                                  CONSTRAINT ck_training_courses_passing_score
                                      CHECK (passing_score >= 0 AND passing_score <= 100),

                                  CONSTRAINT ck_training_courses_duration
                                      CHECK (duration_minutes > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_training_courses_start_time
    ON training_courses(start_time);

CREATE INDEX idx_training_courses_title
    ON training_courses(title);


CREATE TABLE training_questions (
                                    id BIGINT NOT NULL AUTO_INCREMENT,

                                    course_id BIGINT NOT NULL,

                                    question_text TEXT NOT NULL,

                                    option_a VARCHAR(500) NOT NULL,
                                    option_b VARCHAR(500) NOT NULL,
                                    option_c VARCHAR(500) NOT NULL,
                                    option_d VARCHAR(500) NOT NULL,

                                    correct_option CHAR(1) NOT NULL, -- A/B/C/D
                                    explanation TEXT NULL,

                                    sort_order INT NOT NULL DEFAULT 0,

                                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                    PRIMARY KEY (id),

                                    CONSTRAINT fk_training_questions_course
                                        FOREIGN KEY (course_id) REFERENCES training_courses(id)
                                            ON DELETE CASCADE,

                                    CONSTRAINT ck_training_questions_correct_option
                                        CHECK (correct_option IN ('A','B','C','D'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_training_questions_course_id
    ON training_questions(course_id);

CREATE INDEX idx_training_questions_sort
    ON training_questions(course_id, sort_order);


CREATE TABLE training_enrollments (
                                      id BIGINT NOT NULL AUTO_INCREMENT,

                                      course_id BIGINT NOT NULL,
                                      student_id BIGINT NOT NULL, -- users.id

                                      status VARCHAR(20) NOT NULL, -- ENROLLED / COMPLETED
                                      completed_at DATETIME NULL,

                                      created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                      PRIMARY KEY (id),

                                      CONSTRAINT fk_training_enrollments_course
                                          FOREIGN KEY (course_id) REFERENCES training_courses(id)
                                              ON DELETE CASCADE,

                                      CONSTRAINT fk_training_enrollments_student
                                          FOREIGN KEY (student_id) REFERENCES users(id)
                                              ON DELETE CASCADE,

                                      CONSTRAINT uk_training_enrollments_course_student
                                          UNIQUE (course_id, student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_training_enrollments_student
    ON training_enrollments(student_id);

CREATE INDEX idx_training_enrollments_course
    ON training_enrollments(course_id);


CREATE TABLE training_attempts (
                                   id BIGINT NOT NULL AUTO_INCREMENT,

                                   course_id BIGINT NOT NULL,
                                   student_id BIGINT NOT NULL, -- users.id

                                   started_at DATETIME NOT NULL,
                                   submitted_at DATETIME NULL,

                                   score INT NULL, -- 0-100, can be null before submit
                                   passed TINYINT(1) NULL,

                                   created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                   PRIMARY KEY (id),

                                   CONSTRAINT fk_training_attempts_course
                                       FOREIGN KEY (course_id) REFERENCES training_courses(id)
                                           ON DELETE CASCADE,

                                   CONSTRAINT fk_training_attempts_student
                                       FOREIGN KEY (student_id) REFERENCES users(id)
                                           ON DELETE CASCADE,

                                   CONSTRAINT ck_training_attempts_score
                                       CHECK (score IS NULL OR (score >= 0 AND score <= 100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_training_attempts_course
    ON training_attempts(course_id);

CREATE INDEX idx_training_attempts_student
    ON training_attempts(student_id);

CREATE INDEX idx_training_attempts_course_student
    ON training_attempts(course_id, student_id);


CREATE TABLE training_attempt_answers (
                                          id BIGINT NOT NULL AUTO_INCREMENT,

                                          attempt_id BIGINT NOT NULL,
                                          question_id BIGINT NOT NULL,

                                          selected_option CHAR(1) NOT NULL, -- A/B/C/D
                                          is_correct TINYINT(1) NOT NULL DEFAULT 0,

                                          created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                          PRIMARY KEY (id),

                                          CONSTRAINT fk_training_attempt_answers_attempt
                                              FOREIGN KEY (attempt_id) REFERENCES training_attempts(id)
                                                  ON DELETE CASCADE,

                                          CONSTRAINT fk_training_attempt_answers_question
                                              FOREIGN KEY (question_id) REFERENCES training_questions(id)
                                                  ON DELETE CASCADE,

                                          CONSTRAINT uk_training_attempt_answers_attempt_question
                                              UNIQUE (attempt_id, question_id),

                                          CONSTRAINT ck_training_attempt_answers_selected_option
                                              CHECK (selected_option IN ('A','B','C','D'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_training_attempt_answers_attempt
    ON training_attempt_answers(attempt_id);

CREATE INDEX idx_training_attempt_answers_question
    ON training_attempt_answers(question_id);
