package kr.hhplus.be.server.concert.interfaces.dto;

import kr.hhplus.be.server.model.Seat;
import kr.hhplus.be.server.model.SeatStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 좌석 정보 응답 DTO
 */
public class SeatDto {
    private final UUID id;
    private final UUID concertId;
    private final String seatNumber;
    private final BigDecimal price;
    private final SeatStatus status;
    private final UUID reservedBy;
    private final UUID reservationId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public SeatDto(Seat seat) {
        this.id = seat.getId();
        this.concertId = seat.getConcertId();
        this.seatNumber = seat.getSeatNumber();
        this.price = seat.getPrice();
        this.status = seat.getStatus();
        this.reservedBy = seat.getReservedBy();
        this.reservationId = seat.getReservationId();
        this.createdAt = seat.getCreatedAt();
        this.updatedAt = seat.getUpdatedAt();
    }

    public static SeatDto from(Seat seat) {
        if (seat == null) {
            return null;
        }
        return new SeatDto(seat);
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