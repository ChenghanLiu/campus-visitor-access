-- =========================================
-- Safety Inspection Module
-- Tables:
--   inspection_plans
--   inspection_records
-- =========================================

CREATE TABLE inspection_plans (
                                  id BIGINT NOT NULL AUTO_INCREMENT,

                                  lab_id BIGINT NOT NULL,
                                  inspector_id BIGINT NOT NULL,

                                  inspection_time DATETIME NOT NULL,

                                  content TEXT NOT NULL,

                                  status VARCHAR(20) NOT NULL, -- SCHEDULED / COMPLETED

                                  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                  PRIMARY KEY (id),

                                  CONSTRAINT fk_inspection_plans_lab
                                      FOREIGN KEY (lab_id)
                                          REFERENCES labs(id)
                                          ON DELETE CASCADE,

                                  CONSTRAINT fk_inspection_plans_inspector
                                      FOREIGN KEY (inspector_id)
                                          REFERENCES users(id)
                                          ON DELETE CASCADE,

                                  CONSTRAINT ck_inspection_plans_status
                                      CHECK (status IN ('SCHEDULED', 'COMPLETED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_inspection_plans_lab
    ON inspection_plans(lab_id);

CREATE INDEX idx_inspection_plans_inspector
    ON inspection_plans(inspector_id);

CREATE INDEX idx_inspection_plans_time
    ON inspection_plans(inspection_time);



CREATE TABLE inspection_records (
                                    id BIGINT NOT NULL AUTO_INCREMENT,

                                    plan_id BIGINT NOT NULL,

                                    is_safe TINYINT(1) NOT NULL,

                                    problem_description TEXT NULL,

                                    suggestion TEXT NULL,

                                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                    PRIMARY KEY (id),

                                    CONSTRAINT fk_inspection_records_plan
                                        FOREIGN KEY (plan_id)
                                            REFERENCES inspection_plans(id)
                                            ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_inspection_records_plan
    ON inspection_records(plan_id);
