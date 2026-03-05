package com.labsafety.system.reservation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // =========================
    // Conflict checks
    // =========================

    @Query("""
            select (count(r) > 0)
            from Reservation r
            where r.lab.id = :labId
              and r.status in :blockingStatuses
              and r.startTime < :endTime
              and r.endTime > :startTime
            """)
    boolean existsLabTimeConflict(
            @Param("labId") Long labId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("blockingStatuses") Collection<ReservationStatus> blockingStatuses
    );

    @Query("""
            select (count(r) > 0)
            from Reservation r
            where r.equipment.id = :equipmentId
              and r.status in :blockingStatuses
              and r.startTime < :endTime
              and r.endTime > :startTime
            """)
    boolean existsEquipmentTimeConflict(
            @Param("equipmentId") Long equipmentId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("blockingStatuses") Collection<ReservationStatus> blockingStatuses
    );

    @Query("""
            select (count(r) > 0)
            from Reservation r
            where r.lab.id = :labId
              and r.id <> :excludeId
              and r.status in :blockingStatuses
              and r.startTime < :endTime
              and r.endTime > :startTime
            """)
    boolean existsLabTimeConflictExcludingId(
            @Param("labId") Long labId,
            @Param("excludeId") Long excludeId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("blockingStatuses") Collection<ReservationStatus> blockingStatuses
    );

    @Query("""
            select (count(r) > 0)
            from Reservation r
            where r.equipment.id = :equipmentId
              and r.id <> :excludeId
              and r.status in :blockingStatuses
              and r.startTime < :endTime
              and r.endTime > :startTime
            """)
    boolean existsEquipmentTimeConflictExcludingId(
            @Param("equipmentId") Long equipmentId,
            @Param("excludeId") Long excludeId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("blockingStatuses") Collection<ReservationStatus> blockingStatuses
    );

    // =========================
    // List queries (Paging-safe)
    // =========================

    // ✅ 关键：分页查询别用 fetch join，用 EntityGraph 预加载关联
    @EntityGraph(attributePaths = {"lab", "equipment", "student", "approver"})
    @Query("""
            select r
            from Reservation r
            join r.student s
            where s.username = :username
            order by r.createdAt desc
            """)
    Page<Reservation> findMyReservations(
            @Param("username") String username,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"lab", "equipment", "student", "approver"})
    @Query("""
            select r
            from Reservation r
            where r.status = :status
            order by r.createdAt asc
            """)
    Page<Reservation> findByStatusOrderByCreatedAtAsc(
            @Param("status") ReservationStatus status,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"lab", "equipment", "student", "approver"})
    @Query("""
            select r
            from Reservation r
            join r.student s
            where (:status is null or r.status = :status)
              and (:labId is null or r.lab.id = :labId)
              and (:equipmentId is null or r.equipment.id = :equipmentId)
              and (:studentUsername is null or s.username = :studentUsername)
            order by r.createdAt desc
            """)
    Page<Reservation> adminSearch(
            @Param("status") ReservationStatus status,
            @Param("labId") Long labId,
            @Param("equipmentId") Long equipmentId,
            @Param("studentUsername") String studentUsername,
            Pageable pageable
    );

    // =========================
    // Stats
    // =========================

    long countByStatus(ReservationStatus status);

    @Modifying
    @Transactional
    @Query("delete from Reservation r where r.student.id = :studentId")
    int deleteAllByStudentId(@Param("studentId") Long studentId);

    @Query("""
    select r from Reservation r
    left join fetch r.lab
    left join fetch r.equipment
    left join fetch r.student
    left join fetch r.approver
    where r.id = :id
    """)
    Optional<Reservation> findDetailById(@Param("id") Long id);
}
