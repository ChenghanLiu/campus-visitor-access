package com.labsafety.system.reservation;

import com.labsafety.system.reservation.dto.ReservationStatsResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReservationStatsService {

    private final ReservationRepository reservationRepository;

    public ReservationStatsService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public ReservationStatsResponse getStats() {
        long pending = reservationRepository.countByStatus(ReservationStatus.PENDING);
        long approved = reservationRepository.countByStatus(ReservationStatus.APPROVED);
        long rejected = reservationRepository.countByStatus(ReservationStatus.REJECTED);
        long cancelled = reservationRepository.countByStatus(ReservationStatus.CANCELLED);

        ReservationStatsResponse resp = new ReservationStatsResponse();
        resp.setPending(pending);
        resp.setApproved(approved);
        resp.setRejected(rejected);
        resp.setCancelled(cancelled);
        resp.setTotal(pending + approved + rejected + cancelled);

        return resp;
    }
}
