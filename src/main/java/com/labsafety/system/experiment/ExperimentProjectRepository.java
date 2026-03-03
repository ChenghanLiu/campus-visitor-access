package com.labsafety.system.experiment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperimentProjectRepository extends JpaRepository<ExperimentProject, Long> {

    Page<ExperimentProject> findByLab_Id(Long labId, Pageable pageable);

    Page<ExperimentProject> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    Page<ExperimentProject> findByLab_IdAndNameContainingIgnoreCase(Long labId, String keyword, Pageable pageable);
}
