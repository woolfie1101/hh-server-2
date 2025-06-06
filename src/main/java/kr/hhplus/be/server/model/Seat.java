package kr.hhplus.be.server.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 좌석 도메인 모델
 * - 좌석의 핵심 비즈니스 로직을 포함
 * - 좌석 상태 관리 및 변경
 * - 좌석 관련 유효성 검사
 */
public class Seat {

    private final UUID id;
    private final UUID concertId;
    private final String seatNumber;
    private final BigDecimal price;
    private SeatStatus status;
    private UUID reservedBy;
    private UUID reservationId;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Seat(UUID id, UUID concertId, String seatNumber, BigDecimal price, SeatStatus status,
        UUID reservedBy, UUID reservationId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.concertId = concertId;
        this.seatNumber = seatNumber;
        this.price = price;
        this.status = status;
        this.reservedBy = reservedBy;
        this.reservationId = reservationId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Seat create(UUID concertId, String seatNumber, BigDecimal price) {
        return new Seat(
            UUID.randomUUID(),
            concertId,
            seatNumber,
            price,
            SeatStatus.AVAILABLE,
            null,
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    public void reserve(UUID userId, UUID reservationId) {
        if (status != SeatStatus.AVAILABLE) {
            throw new IllegalStateException("이미 예약된 좌석입니다.");
        }
        this.status = SeatStatus.RESERVED;
        this.reservedBy = userId;
        this.reservationId = reservationId;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (status != SeatStatus.RESERVED) {
            throw new IllegalStateException("예약된 좌석이 아닙니다.");
        }
        this.status = SeatStatus.AVAILABLE;
        this.reservedBy = null;
        this.reservationId = null;
        this.updatedAt = LocalDateTime.now();
    }

    public void expire() {
        if (status == SeatStatus.RESERVED) {
            this.status = SeatStatus.AVAILABLE;
            this.reservedBy = null;
            this.reservationId = null;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getConcertId() {
        return concertId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public UUID getReservedBy() {
        return reservedBy;
    }

    public UUID getReservationId() {
        return reservationId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
} 