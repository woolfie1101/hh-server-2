package kr.hhplus.be.server.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.model.Reservation;
import kr.hhplus.be.server.model.ReservationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 예약 JPA 엔티티
 */
@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID concertId;

    @Column(nullable = false)
    private UUID seatId;

    @Column(nullable = false)
    private String seatNumber;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime expiresAt;

    public static ReservationEntity fromDomain(Reservation reservation) {
        ReservationEntity entity = new ReservationEntity();
        entity.id = reservation.getId();
        entity.userId = reservation.getUserId();
        entity.concertId = reservation.getConcertId();
        entity.seatId = reservation.getSeatId();
        entity.seatNumber = reservation.getSeatNumber();
        entity.price = reservation.getPrice();
        entity.status = reservation.getStatus();
        entity.createdAt = reservation.getCreatedAt();
        entity.updatedAt = reservation.getUpdatedAt();
        entity.expiresAt = reservation.getExpiresAt();
        return entity;
    }

    public Reservation toDomain() {
        return new Reservation(
            this.id,
            this.userId,
            this.concertId,
            this.seatId,
            this.seatNumber,
            this.price,
            this.status,
            this.createdAt,
            this.updatedAt,
            this.expiresAt
        );
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void complete() {
        this.status = ReservationStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateFromDomain(Reservation reservation) {
        this.status = reservation.getStatus();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
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

    // Setters
    public void setStatus(ReservationStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
} 