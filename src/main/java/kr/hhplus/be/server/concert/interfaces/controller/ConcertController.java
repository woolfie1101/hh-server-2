package kr.hhplus.be.server.concert.interfaces.controller;

import kr.hhplus.be.server.concert.application.usecase.ConcertUseCase;
import kr.hhplus.be.server.concert.application.dto.ConcertDto;
import kr.hhplus.be.server.concert.interfaces.dto.ConcertResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/concerts")
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertUseCase concertUseCase;

    @GetMapping
    public ResponseEntity<List<ConcertResponse>> getAllConcerts() {
        List<ConcertDto> concerts = concertUseCase.getAllConcerts();
        return ResponseEntity.ok(ConcertResponse.from(concerts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConcertResponse> getConcert(@PathVariable UUID id) {
        ConcertDto concert = concertUseCase.getConcert(id);
        return ResponseEntity.ok(ConcertResponse.from(concert));
    }

    @GetMapping("/artist/{artist}")
    public ResponseEntity<List<ConcertResponse>> getConcertsByArtist(@PathVariable String artist) {
        List<ConcertDto> concerts = concertUseCase.getConcertsByArtist(artist);
        return ResponseEntity.ok(ConcertResponse.from(concerts));
    }

    @GetMapping("/available")
    public ResponseEntity<List<ConcertResponse>> getAvailableConcerts() {
        List<ConcertDto> concerts = concertUseCase.getAvailableConcerts();
        return ResponseEntity.ok(ConcertResponse.from(concerts));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<ConcertResponse>> getUpcomingConcerts() {
        List<ConcertDto> concerts = concertUseCase.getUpcomingConcerts();
        return ResponseEntity.ok(ConcertResponse.from(concerts));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ConcertResponse>> searchConcerts(@RequestParam String keyword) {
        List<ConcertDto> concerts = concertUseCase.searchConcerts(keyword);
        return ResponseEntity.ok(ConcertResponse.from(concerts));
    }
} 