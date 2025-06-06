package kr.hhplus.be.server.concert.application.dto;

import kr.hhplus.be.server.concert.domain.model.Seat;
import kr.hhplus.be.server.concert.domain.model.ReservationStatus;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class SeatDto {
    private final UUID id;
    private final UUID concertId;
    private final String seatNumber;
    private final ReservationStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final UUID reservedBy;

    public SeatDto(Seat seat) {
        this.id = seat.getId();
        this.concertId = seat.getConcertId();
        this.seatNumber = seat.getSeatNumber();
        this.status = seat.getStatus();
        this.createdAt = seat.getCreatedAt();
        this.updatedAt = seat.getUpdatedAt();
        this.reservedBy = seat.getReservedBy();
    }
}