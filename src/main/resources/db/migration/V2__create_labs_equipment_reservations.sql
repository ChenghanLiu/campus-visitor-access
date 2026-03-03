CREATE TABLE labs (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,

                      name VARCHAR(150) NOT NULL UNIQUE,
                      type VARCHAR(100) NOT NULL,
                      location VARCHAR(200) NOT NULL,

                      capacity INT NOT NULL,

                      open_time TIME NOT NULL,
                      close_time TIME NOT NULL,

                      manager_name VARCHAR(100) NULL,
                      manager_phone VARCHAR(50) NULL,

                      details TEXT NULL,

                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                      CONSTRAINT chk_labs_time CHECK (close_time > open_time)
);

CREATE INDEX idx_labs_type ON labs(type);
CREATE INDEX idx_labs_location ON labs(location);

-- =========================================================
-- EQUIPMENT
-- =========================================================
CREATE TABLE equipment (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,

                           lab_id BIGINT NOT NULL,

                           category VARCHAR(100) NOT NULL,
                           name VARCHAR(150) NOT NULL,

                           model VARCHAR(150) NULL,
                           specification VARCHAR(255) NULL,

                           quantity INT NOT NULL DEFAULT 1,
                           purchase_date DATE NULL,

                           storage_location VARCHAR(200) NULL,

                           status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',
                           description TEXT NULL,

                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                           CONSTRAINT fk_equipment_lab
                               FOREIGN KEY (lab_id) REFERENCES labs(id)
                                   ON DELETE RESTRICT
                                   ON UPDATE CASCADE,

                           CONSTRAINT chk_equipment_status
                               CHECK (status IN ('AVAILABLE', 'MAINTENANCE', 'BROKEN', 'RETIRED'))
);

CREATE INDEX idx_equipment_lab_id ON equipment(lab_id);
CREATE INDEX idx_equipment_category ON equipment(category);
CREATE INDEX idx_equipment_status ON equipment(status);

-- Optional: help avoid duplicates within a lab (not globally unique)
-- If you don't want this constraint, remove it.
CREATE UNIQUE INDEX uk_equipment_lab_name_model
    ON equipment(lab_id, name, model);

-- =========================================================
-- RESERVATIONS
-- =========================================================
CREATE TABLE reservations (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,

                              lab_id BIGINT NOT NULL,
                              equipment_id BIGINT NOT NULL,

                              student_id BIGINT NOT NULL,

                              status VARCHAR(20) NOT NULL DEFAULT 'PENDING',

                              start_time DATETIME NOT NULL,
                              end_time DATETIME NOT NULL,

                              purpose VARCHAR(255) NULL,
                              remark VARCHAR(500) NULL,

                              approver_id BIGINT NULL,
                              decided_at DATETIME NULL,
                              decision_note VARCHAR(500) NULL,

                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                              CONSTRAINT fk_reservations_lab
                                  FOREIGN KEY (lab_id) REFERENCES labs(id)
                                      ON DELETE RESTRICT
                                      ON UPDATE CASCADE,

                              CONSTRAINT fk_reservations_equipment
                                  FOREIGN KEY (equipment_id) REFERENCES equipment(id)
                                      ON DELETE RESTRICT
                                      ON UPDATE CASCADE,

                              CONSTRAINT fk_reservations_student
                                  FOREIGN KEY (student_id) REFERENCES users(id)
                                      ON DELETE RESTRICT
                                      ON UPDATE CASCADE,

                              CONSTRAINT fk_reservations_approver
                                  FOREIGN KEY (approver_id) REFERENCES users(id)
                                      ON DELETE SET NULL
                                      ON UPDATE CASCADE,

                              CONSTRAINT chk_reservations_status
                                  CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),

                              CONSTRAINT chk_reservations_time
                                  CHECK (end_time > start_time)
);

-- For conflict checks & common queries
CREATE INDEX idx_reservations_lab_status_time
    ON reservations(lab_id, status, start_time, end_time);

CREATE INDEX idx_reservations_equipment_status_time
    ON reservations(equipment_id, status, start_time, end_time);

CREATE INDEX idx_reservations_student_time
    ON reservations(student_id, start_time, end_time);

CREATE INDEX idx_reservations_approver
    ON reservations(approver_id);
