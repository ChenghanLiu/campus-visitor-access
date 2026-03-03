package com.labsafety.system.inspection.repository;

import com.labsafety.system.inspection.InspectionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InspectionRecordRepository
        extends JpaRepository<InspectionRecord, Long> {

    List<InspectionRecord> findByPlan_Id(Long planId);

    Optional<InspectionRecord> findByPlan_IdAndPlan_Status(
            Long planId,
            com.labsafety.system.inspection.InspectionPlan.InspectionStatus status
    );

    boolean existsByPlan_Id(Long planId);
}
