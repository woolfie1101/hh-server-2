package kr.hhplus.be.server.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 예약 도메인 모델
 * - 예약의 핵심 비즈니스 로직을 포함
 * - 예약 상태 관리 및 변경
 * - 예약 관련 유효성 검사
 */
public class Reservation {

    private final UUID id;
    private final UUID userId;
    private final UUID concertId;
    private final UUID seatId;
    private final String seatNumber;
    private final BigDecimal price;
    private ReservationStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final LocalDateTime expiresAt;

    public Reservation(UUID id, UUID userId, UUID concertId, UUID seatId, String seatNumber, BigDecimal price, ReservationStatus status,
        LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime expiresAt) {
        this.id = id;
        this.userId = userId;
        this.concertId = concertId;
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.price = price;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.expiresAt = expiresAt;
    }

    public static Reservation create(UUID userId, UUID concertId, UUID seatId) {
        return new Reservation(
            UUID.randomUUID(),
            userId,
            concertId,
            seatId,
            null,
            BigDecimal.ZERO,
            ReservationStatus.PENDING,
            LocalDateTime.now(),
            LocalDateTime.now(),
            LocalDateTime.now().plusMinutes(15) // 15분 후 만료
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

    public BigDecimal getPrice() {
        return price;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
} 