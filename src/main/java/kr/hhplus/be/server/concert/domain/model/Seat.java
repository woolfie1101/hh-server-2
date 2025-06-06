package kr.hhplus.be.server.concert.domain.model;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 좌석 도메인 모델
 */
@Getter
public class Seat {
    private final UUID id;
    private final UUID concertId;
    private final String seatNumber;
    private final int price;
    private ReservationStatus status;
    private UUID reservedBy;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Seat(UUID id, UUID concertId, String seatNumber, int price, ReservationStatus status, UUID reservedBy,
        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.concertId = concertId;
        this.seatNumber = seatNumber;
        this.price = price;
        this.status = status;
        this.reservedBy = reservedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Seat create(UUID concertId, String seatNumber, int price) {
        return new Seat(
            UUID.randomUUID(),
            concertId,
            seatNumber,
            price,
            ReservationStatus.AVAILABLE,
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    public void reserve(UUID userId) {
        if (status != ReservationStatus.AVAILABLE) {
            throw new IllegalStateException("이미 예약된 좌석입니다.");
        }
        this.status = ReservationStatus.PENDING;
        this.reservedBy = userId;
        this.updatedAt = LocalDateTime.now();
    }

    public void complete() {
        if (status != ReservationStatus.PENDING) {
            throw new IllegalStateException("완료할 수 없는 좌석 상태입니다.");
        }
        this.status = ReservationStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (status != ReservationStatus.PENDING) {
            throw new IllegalStateException("취소할 수 없는 좌석 상태입니다.");
        }
        this.status = ReservationStatus.AVAILABLE;
        this.reservedBy = null;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isReservedBy(UUID userId) {
        return status == ReservationStatus.PENDING && userId.equals(reservedBy);
    }

    public void expire() {
        if (status != ReservationStatus.PENDING) {
            throw new IllegalStateException("예약된 좌석이 아닙니다.");
        }
        this.status = ReservationStatus.AVAILABLE;
        this.reservedBy = null;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isAvailable() {
        return status == ReservationStatus.AVAILABLE;
    }

    public boolean isReserved() {
        return status == ReservationStatus.PENDING;
    }
} 