package com.labsafety.system.lab;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LabRepository extends JpaRepository<Lab, Long> {

    Optional<Lab> findByName(String name);

    boolean existsByName(String name);

}
