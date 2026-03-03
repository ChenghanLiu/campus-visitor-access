package com.labsafety.system.lab.stats;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LabStatisticsService {

    private final LabStatisticsRepository repository;

    public LabStatisticsService(LabStatisticsRepository repository) {
        this.repository = repository;
    }

    public LabStatisticsResponse.OverviewResponse overview() {

        long totalLabs = repository.countLabs();
        long totalCapacity = repository.sumCapacity();
        long totalEquipment = repository.countEquipment();
        long totalReservations = repository.countReservations();
        long activeReservations = repository.countActiveReservations(); // PENDING + APPROVED
        long totalInspectionPlans = repository.countInspectionPlans();
        long unsafeInspectionCount = repository.countUnsafeInspectionRecords();

        return new LabStatisticsResponse.OverviewResponse(
                totalLabs,
                totalCapacity,
                totalEquipment,
                totalReservations,
                activeReservations,
                totalInspectionPlans,
                unsafeInspectionCount
        );
    }

    public Page<LabStatisticsResponse.LabStatResponse> labStats(Pageable pageable) {

        // Same approach as your other stats: query rows once, then page in memory.
        List<LabStatisticsResponse.LabStatRow> rows = repository.labStatRows();

        int total = rows.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        if (start >= total) {
            return new PageImpl<>(List.of(), pageable, total);
        }

        List<LabStatisticsResponse.LabStatResponse> content = rows.subList(start, end).stream()
                .map(r -> new LabStatisticsResponse.LabStatResponse(
                        r.labId(),
                        r.labName(),
                        r.capacity(),
                        r.equipmentCount(),
                        r.totalReservations(),
                        r.activeReservations(),
                        r.inspectionPlans(),
                        r.unsafeInspections()
                ))
                .toList();

        return new PageImpl<>(content, pageable, total);
    }
}
