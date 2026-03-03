package com.labsafety.system.inspection.stats;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class InspectionStatisticsService {

    private final InspectionStatisticsRepository repository;

    public InspectionStatisticsService(InspectionStatisticsRepository repository) {
        this.repository = repository;
    }

    public InspectionStatisticsResponse.OverviewResponse overview() {

        long totalPlans = repository.countPlans();
        long completedPlans = repository.countCompletedPlans();
        long totalRecords = repository.countRecords();
        long unsafeCount = repository.countUnsafeRecords();

        long safeCount = totalRecords - unsafeCount;
        double safeRate = calcRate(safeCount, totalRecords);

        return new InspectionStatisticsResponse.OverviewResponse(
                totalPlans,
                completedPlans,
                totalRecords,
                unsafeCount,
                safeRate
        );
    }

    public Page<InspectionStatisticsResponse.LabStatResponse> labStats(Pageable pageable) {

        List<InspectionStatisticsResponse.LabStatRow> rows = repository.labStatRows();

        int total = rows.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);

        if (start >= total) {
            return new PageImpl<>(List.of(), pageable, total);
        }

        List<InspectionStatisticsResponse.LabStatResponse> content =
                rows.subList(start, end).stream()
                        .map(r -> new InspectionStatisticsResponse.LabStatResponse(
                                r.labId(),
                                r.labName(),
                                r.totalPlans(),
                                r.completedPlans(),
                                r.totalRecords(),
                                r.unsafeCount(),
                                calcRate(r.totalRecords() - r.unsafeCount(), r.totalRecords())
                        ))
                        .toList();

        return new PageImpl<>(content, pageable, total);
    }

    private double calcRate(long numerator, long denominator) {
        if (denominator <= 0) return 0.0;
        return BigDecimal.valueOf(numerator)
                .divide(BigDecimal.valueOf(denominator), 4, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
