package com.labsafety.system.inspection.stats;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InspectionStatisticsRepository {

    @PersistenceContext
    private EntityManager em;

    public long countPlans() {
        return em.createQuery(
                "select count(p) from InspectionPlan p",
                Long.class
        ).getSingleResult();
    }

    public long countCompletedPlans() {
        return em.createQuery(
                "select count(p) from InspectionPlan p where p.status = 'COMPLETED'",
                Long.class
        ).getSingleResult();
    }

    public long countRecords() {
        return em.createQuery(
                "select count(r) from InspectionRecord r",
                Long.class
        ).getSingleResult();
    }

    public long countUnsafeRecords() {
        return em.createQuery(
                "select count(r) from InspectionRecord r where r.isSafe = false",
                Long.class
        ).getSingleResult();
    }

    public List<InspectionStatisticsResponse.LabStatRow> labStatRows() {
        return em.createQuery(
                "select new com.labsafety.system.inspection.stats.InspectionStatisticsResponse$LabStatRow(" +
                        "l.id, l.name, " +
                        "count(distinct p.id), " +
                        "sum(case when p.status = 'COMPLETED' then 1 else 0 end), " +
                        "count(r.id), " +
                        "sum(case when r.isSafe = false then 1 else 0 end)" +
                        ") " +
                        "from InspectionPlan p " +
                        "left join p.lab l " +
                        "left join InspectionRecord r on r.plan = p " +
                        "group by l.id, l.name " +
                        "order by l.id desc",
                InspectionStatisticsResponse.LabStatRow.class
        ).getResultList();
    }
}
