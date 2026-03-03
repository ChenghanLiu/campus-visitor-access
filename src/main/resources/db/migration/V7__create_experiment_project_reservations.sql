CREATE TABLE experiment_project_reservations (
                                                 id BIGINT NOT NULL AUTO_INCREMENT,

                                                 reservation_id BIGINT NOT NULL,
                                                 experiment_project_id BIGINT NOT NULL,

                                                 created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                                 PRIMARY KEY (id),

                                                 CONSTRAINT fk_epr_reservation
                                                     FOREIGN KEY (reservation_id)
                                                         REFERENCES reservations(id)
                                                         ON DELETE CASCADE,

                                                 CONSTRAINT fk_epr_experiment_project
                                                     FOREIGN KEY (experiment_project_id)
                                                         REFERENCES experiment_projects(id)
                                                         ON DELETE CASCADE,

                                                 CONSTRAINT uk_epr_reservation UNIQUE (reservation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_epr_experiment_project_id
    ON experiment_project_reservations(experiment_project_id);
