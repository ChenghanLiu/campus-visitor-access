package com.labsafety.system.lab.stats;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LabStatisticsRepository {

    @PersistenceContext
    private EntityManager em;

    // ---------- Overview ----------
    public long countLabs() {
        Long v = em.createQuery("select count(l) from Lab l", Long.class)
                .getSingleResult();
        return v == null ? 0L : v;
    }

    public long sumCapacity() {
        Long v = em.createQuery("select coalesce(sum(l.capacity), 0) from Lab l", Long.class)
                .getSingleResult();
        return v == null ? 0L : v;
    }

    public long countEquipment() {
        Long v = em.createQuery("select count(e) from Equipment e", Long.class)
                .getSingleResult();
        return v == null ? 0L : v;
    }

    public long countReservations() {
        Long v = em.createQuery("select count(r) from Reservation r", Long.class)
                .getSingleResult();
        return v == null ? 0L : v;
    }

    /**
     * Active reservations = PENDING + APPROVED (these block time in your reservation logic).
     */
    public long countActiveReservations() {
        Long v = em.createQuery(
                        "select count(r) from Reservation r " +
                                "where r.status in ('PENDING', 'APPROVED')",
                        Long.class
                )
                .getSingleResult();
        return v == null ? 0L : v;
    }

    public long countInspectionPlans() {
        Long v = em.createQuery("select count(p) from InspectionPlan p", Long.class)
                .getSingleResult();
        return v == null ? 0L : v;
    }

    public long countUnsafeInspectionRecords() {
        Long v = em.createQuery(
                        "select count(ir) from InspectionRecord ir where ir.isSafe = false",
                        Long.class
                )
                .getSingleResult();
        return v == null ? 0L : v;
    }

    // ---------- Per-lab ----------
    public List<LabStatisticsResponse.LabStatRow> labStatRows() {
        /**
         * Notes:
         * - We anchor from Lab so labs with 0 activity still show up.
         * - equipmentCount counts all equipment linked to the lab.
         * - reservations count includes all statuses; activeReservations counts PENDING + APPROVED.
         * - inspectionPlans counts plans; unsafeInspections counts unsafe records for plans under this lab.
         */
        return em.createQuery(
                        "select new com.labsafety.system.lab.stats.LabStatisticsResponse$LabStatRow(" +
                                "l.id, l.name, l.capacity, " +
                                "count(distinct e.id), " +
                                "count(distinct r.id), " +
                                "sum(case when r.status in ('PENDING','APPROVED') then 1 else 0 end), " +
                                "count(distinct p.id), " +
                                "sum(case when ir.isSafe = false then 1 else 0 end)" +
                                ") " +
                                "from Lab l " +
                                "left join Equipment e on e.lab = l " +
                                "left join Reservation r on r.lab = l " +
                                "left join InspectionPlan p on p.lab = l " +
                                "left join InspectionRecord ir on ir.plan = p " +
                                "group by l.id, l.name, l.capacity " +
                                "order by l.id desc",
                        LabStatisticsResponse.LabStatRow.class
                )
                .getResultList();
    }
}
