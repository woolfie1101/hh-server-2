package kr.hhplus.be.server.concert.interfaces.dto;

import kr.hhplus.be.server.model.Reservation;
import kr.hhplus.be.server.model.ReservationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 예약 응답 DTO
 * - 예약 ID
 * - 사용자 ID
 * - 콘서트 ID
 * - 좌석 ID
 * - 좌석 번호
 * - 가격
 * - 예약 상태
 * - 생성 시간
 * - 만료 시간
 * - 수정 시간
 */
public class ReservationResponse {
    private final UUID id;
    private final UUID userId;
    private final UUID concertId;
    private final UUID seatId;
    private final String seatNumber;
    private final Long price;
    private final ReservationStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiresAt;
    private final LocalDateTime updatedAt;

    public ReservationResponse(
            UUID id,
            UUID userId,
            UUID concertId,
            UUID seatId,
            String seatNumber,
            Long price,
            ReservationStatus status,
            LocalDateTime createdAt,
            LocalDateTime expiresAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.concertId = concertId;
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.price = price;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.updatedAt = updatedAt;
    }

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getUserId(),
                reservation.getConcertId(),
                reservation.getSeatId(),
                null, // seatNumber는 현재 Reservation 모델에 없음
                null, // price는 현재 Reservation 모델에 없음
                reservation.getStatus(),
                reservation.getCreatedAt(),
                reservation.getExpiresAt(),
                reservation.getUpdatedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getConcertId() {
        return concertId;
    }

    public UUID getSeatId() {
        return seatId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public Long getPrice() {
        return price;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
} 