package kr.hhplus.be.server.concert.interfaces.controller;

import kr.hhplus.be.server.concert.application.usecase.SeatUseCase;
import kr.hhplus.be.server.concert.domain.model.Seat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    private final SeatUseCase seatUseCase;

    public SeatController(SeatUseCase seatUseCase) {
        this.seatUseCase = seatUseCase;
    }

    @GetMapping("/{seatId}")
    public ResponseEntity<Seat> getSeat(@PathVariable Long seatId) {
        Seat seat = seatUseCase.findSeat(seatId);
        return ResponseEntity.ok(seat);
    }

    @GetMapping("/concert/{concertId}")
    public ResponseEntity<List<Seat>> getSeatsByConcert(@PathVariable Long concertId) {
        List<Seat> seats = seatUseCase.findSeatsByConcert(concertId);
        return ResponseEntity.ok(seats);
    }

    @PostMapping("/{seatId}/reserve")
    public ResponseEntity<Seat> reserveSeat(
        @PathVariable Long seatId,
        @RequestParam String userId
    ) {
        Seat seat = seatUseCase.reserveSeat(seatId, userId);
        return ResponseEntity.ok(seat);
    }

    @PostMapping("/{seatId}/cancel")
    public ResponseEntity<Seat> cancelSeatReservation(
        @PathVariable Long seatId,
        @RequestParam String userId
    ) {
        Seat seat = seatUseCase.cancelSeatReservation(seatId, userId);
        return ResponseEntity.ok(seat);
    }
} 