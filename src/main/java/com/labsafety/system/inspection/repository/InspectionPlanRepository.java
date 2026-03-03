package com.labsafety.system.inspection.repository;

import com.labsafety.system.inspection.InspectionPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionPlanRepository
        extends JpaRepository<InspectionPlan, Long> {

    Page<InspectionPlan> findByLab_Id(Long labId, Pageable pageable);

    Page<InspectionPlan> findByInspector_Id(Long inspectorId, Pageable pageable);

    Page<InspectionPlan> findByStatus(InspectionPlan.InspectionStatus status, Pageable pageable);
}
