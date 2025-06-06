package kr.hhplus.be.server.concert.infrastructure.persistence.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.concert.domain.model.Reservation;
import kr.hhplus.be.server.concert.domain.model.ReservationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor
public class ReservationEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "concert_id", nullable = false)
    private UUID concertId;

    @Column(name = "seat_id", nullable = false)
    private UUID seatId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static ReservationEntity fromDomain(Reservation reservation) {
        ReservationEntity entity = new ReservationEntity();
        entity.id = reservation.getId();
        entity.userId = reservation.getUserId();
        entity.concertId = reservation.getConcertId();
        entity.seatId = reservation.getSeatId();
        entity.status = reservation.getStatus();
        entity.createdAt = reservation.getCreatedAt();
        entity.updatedAt = reservation.getUpdatedAt();
        return entity;
    }

    public Reservation toDomain() {
        return new Reservation(
            this.id,
            this.userId,
            this.concertId,
            this.seatId,
            this.status,
            this.createdAt,
            this.updatedAt
        );
    }
} 