package kr.hhplus.be.server.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.model.Seat;
import kr.hhplus.be.server.model.SeatStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 좌석 JPA 엔티티
 */
@Entity
@Table(name = "seats")
@Getter
@NoArgsConstructor
public class SeatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID concertId;

    @Column(nullable = false)
    private String seatNumber;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status;

    @Column
    private UUID reservedBy;

    @Column
    private UUID reservationId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public static SeatEntity fromDomain(Seat seat) {
        SeatEntity entity = new SeatEntity();
        entity.id = seat.getId();
        entity.concertId = seat.getConcertId();
        entity.seatNumber = seat.getSeatNumber();
        entity.price = seat.getPrice();
        entity.status = seat.getStatus();
        entity.reservedBy = seat.getReservedBy();
        entity.reservationId = seat.getReservationId();
        entity.createdAt = seat.getCreatedAt();
        entity.updatedAt = seat.getUpdatedAt();
        return entity;
    }

    public Seat toDomain() {
        return new Seat(
            this.id,
            this.concertId,
            this.seatNumber,
            this.price,
            this.status,
            this.reservedBy,
            this.reservationId,
            this.createdAt,
            this.updatedAt
        );
    }

    public void updateFromDomain(Seat seat) {
        this.status = seat.getStatus();
        this.reservedBy = seat.getReservedBy();
        this.updatedAt = LocalDateTime.now();
    }

    public void reserve(UUID userId) {
        if (this.status != SeatStatus.AVAILABLE) {
            throw new IllegalStateException("이미 예약된 좌석입니다.");
        }
        this.status = SeatStatus.RESERVED;
        this.reservedBy = userId;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status != SeatStatus.RESERVED) {
            throw new IllegalStateException("예약된 좌석이 아닙니다.");
        }
        this.status = SeatStatus.AVAILABLE;
        this.reservedBy = null;
        this.updatedAt = LocalDateTime.now();
    }

    public void expire() {
        if (this.status != SeatStatus.RESERVED) {
            throw new IllegalStateException("만료할 수 없는 좌석 상태입니다.");
        }
        this.status = SeatStatus.AVAILABLE;
        this.reservedBy = null;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
} 