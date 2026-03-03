package com.labsafety.system.reservation;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Centralized conflict rules for reservation time ranges.
 *
 * Uses half-open intervals: [start, end)
 * - Two reservations overlap if: startA < endB AND startB < endA
 * - Adjacent ranges are allowed: [09:00,10:00) and [10:00,11:00) do NOT overlap.
 */
public final class ReservationConflictPolicy {

    private ReservationConflictPolicy() {
    }

    /**
     * Validates that start and end are non-null and start is strictly before end.
     */
    public static void validateRange(LocalDateTime startAt, LocalDateTime endAt) {
        Objects.requireNonNull(startAt, "startAt cannot be null");
        Objects.requireNonNull(endAt, "endAt cannot be null");

        if (!startAt.isBefore(endAt)) {
            throw new IllegalArgumentException("Invalid time range: startAt must be before endAt");
        }
    }

    /**
     * Returns true if two time ranges overlap using [start, end) semantics.
     */
    public static boolean overlaps(LocalDateTime startA, LocalDateTime endA,
                                   LocalDateTime startB, LocalDateTime endB) {
        validateRange(startA, endA);
        validateRange(startB, endB);

        // overlap condition for half-open intervals
        return startA.isBefore(endB) && startB.isBefore(endA);
    }

    /**
     * Convenience: returns true if the candidate range conflicts with an existing range.
     * This is the same as overlaps() but reads nicer at call sites.
     */
    public static boolean conflicts(LocalDateTime candidateStart, LocalDateTime candidateEnd,
                                    LocalDateTime existingStart, LocalDateTime existingEnd) {
        return overlaps(candidateStart, candidateEnd, existingStart, existingEnd);
    }
}
