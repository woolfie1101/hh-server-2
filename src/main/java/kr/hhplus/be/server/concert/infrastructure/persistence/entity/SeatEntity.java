package kr.hhplus.be.server.concert.infrastructure.persistence.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.concert.domain.model.Seat;
import kr.hhplus.be.server.concert.domain.model.ReservationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "seats")
@Getter
@NoArgsConstructor
public class SeatEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "concert_id", nullable = false)
    private UUID concertId;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @Column(name = "price", nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    @Column(name = "reserved_by")
    private UUID reservedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static SeatEntity fromDomain(Seat seat) {
        SeatEntity entity = new SeatEntity();
        entity.id = seat.getId();
        entity.concertId = seat.getConcertId();
        entity.seatNumber = seat.getSeatNumber();
        entity.price = seat.getPrice();
        entity.status = seat.getStatus();
        entity.reservedBy = seat.getReservedBy();
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
            this.createdAt,
            this.updatedAt
        );
    }
} 