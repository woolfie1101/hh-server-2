package kr.hhplus.be.server.concert.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Reservation {
    private final UUID id;
    private final UUID userId;
    private final UUID concertId;
    private final UUID seatId;
    private ReservationStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Reservation(UUID id, UUID userId, UUID concertId, UUID seatId, ReservationStatus status,
        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.concertId = concertId;
        this.seatId = seatId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Reservation create(UUID userId, UUID concertId, UUID seatId) {
        return new Reservation(
            UUID.randomUUID(),
            userId,
            concertId,
            seatId,
            ReservationStatus.PENDING,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    public void complete() {
        if (status != ReservationStatus.PENDING) {
            throw new IllegalStateException("완료할 수 없는 예약 상태입니다.");
        }
        this.status = ReservationStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (status != ReservationStatus.PENDING) {
            throw new IllegalStateException("취소할 수 없는 예약 상태입니다.");
        }
        this.status = ReservationStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void expire() {
        if (status != ReservationStatus.PENDING) {
            throw new IllegalStateException("만료할 수 없는 예약 상태입니다.");
        }
        this.status = ReservationStatus.EXPIRED;
        this.updatedAt = LocalDateTime.now();
    }
} 