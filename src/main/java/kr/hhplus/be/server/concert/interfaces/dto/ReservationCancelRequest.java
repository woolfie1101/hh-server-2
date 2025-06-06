package kr.hhplus.be.server.concert.interfaces.dto;

import java.util.UUID;

/**
 * 예약 취소 요청 DTO
 */
public class ReservationCancelRequest {
    private UUID reservationId;
    private UUID userId;

    public ReservationCancelRequest() {}

    public ReservationCancelRequest(UUID reservationId, UUID userId) {
        this.reservationId = reservationId;
        this.userId = userId;
    }

    public UUID getReservationId() {
        return reservationId;
    }

    public void setReservationId(UUID reservationId) {
        this.reservationId = reservationId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
} 