package kr.hhplus.be.server.concert.interfaces.dto;

import java.util.UUID;

/**
 * 좌석 예약 요청 DTO
 * - 사용자 ID
 * - 콘서트 ID
 * - 좌석 ID
 */
public class SeatReservationRequest {
    private UUID userId;
    private UUID concertId;
    private UUID seatId;

    public SeatReservationRequest() {
    }

    public SeatReservationRequest(UUID userId, UUID concertId, UUID seatId) {
        this.userId = userId;
        this.concertId = concertId;
        this.seatId = seatId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getConcertId() {
        return concertId;
    }

    public void setConcertId(UUID concertId) {
        this.concertId = concertId;
    }

    public UUID getSeatId() {
        return seatId;
    }

    public void setSeatId(UUID seatId) {
        this.seatId = seatId;
    }
} 