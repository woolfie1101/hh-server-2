package kr.hhplus.be.server.concert.interfaces.controller;

import kr.hhplus.be.server.concert.application.dto.SeatDto;
import kr.hhplus.be.server.concert.application.usecase.SeatUseCase;
import kr.hhplus.be.server.concert.interfaces.dto.SeatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/concerts/{concertId}/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatUseCase seatUseCase;

    @GetMapping
    public ResponseEntity<List<SeatResponse>> getSeats(@PathVariable UUID concertId) {
        List<SeatDto> seatDtos = seatUseCase.getSeats(concertId);
        return ResponseEntity.ok(SeatResponse.from(seatDtos));
    }

    @GetMapping("/available")
    public ResponseEntity<List<SeatResponse>> getAvailableSeats(@PathVariable UUID concertId) {
        List<SeatDto> seatDtos = seatUseCase.getAvailableSeats(concertId);
        return ResponseEntity.ok(SeatResponse.from(seatDtos));
    }

    @GetMapping("/reserved")
    public ResponseEntity<List<SeatResponse>> getReservedSeats(@PathVariable UUID concertId) {
        List<SeatDto> seatDtos = seatUseCase.getReservedSeats(concertId);
        return ResponseEntity.ok(SeatResponse.from(seatDtos));
    }

    @GetMapping("/{seatId}")
    public ResponseEntity<SeatResponse> getSeat(
        @PathVariable UUID seatId
    ) {
        SeatDto seatDto = seatUseCase.getSeat(seatId);
        return ResponseEntity.ok(SeatResponse.from(seatDto));
    }

    @PostMapping("/{seatId}/reserve")
    public ResponseEntity<SeatResponse> reserveSeat(
        @PathVariable UUID concertId,
        @PathVariable UUID seatId,
        @RequestParam UUID userId
    ) {
        SeatDto seatDto = seatUseCase.reserveSeat(seatId, userId);
        return ResponseEntity.ok(SeatResponse.from(seatDto));
    }

    @PostMapping("/{seatId}/cancel")
    public ResponseEntity<SeatResponse> cancelReservation(
        @PathVariable UUID concertId,
        @PathVariable UUID seatId,
        @RequestParam UUID userId
    ) {
        SeatDto seatDto = seatUseCase.cancelReservation(seatId, userId);
        return ResponseEntity.ok(SeatResponse.from(seatDto));
    }
} 