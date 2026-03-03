CREATE TABLE experiment_projects (
                                     id BIGINT NOT NULL AUTO_INCREMENT,
                                     lab_id BIGINT NOT NULL,

                                     name VARCHAR(150) NOT NULL,
                                     objective TEXT NOT NULL,
                                     content TEXT NOT NULL,
                                     requirements TEXT NOT NULL,

                                     start_time DATETIME NOT NULL,
                                     end_time DATETIME NOT NULL,

                                     created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                     PRIMARY KEY (id),

                                     CONSTRAINT fk_experiment_projects_lab
                                         FOREIGN KEY (lab_id) REFERENCES labs(id),

                                     CONSTRAINT ck_experiment_projects_time
                                         CHECK (end_time > start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_experiment_projects_lab_id
    ON experiment_projects(lab_id);

CREATE INDEX idx_experiment_projects_start_time
    ON experiment_projects(start_time);

CREATE INDEX idx_experiment_projects_name
    ON experiment_projects(name);
