package kr.hhplus.be.server.concert.interfaces.controller;

import kr.hhplus.be.server.concert.application.dto.ConcertBookingStatus;
import kr.hhplus.be.server.concert.application.service.ConcertService;
import kr.hhplus.be.server.concert.domain.model.Concert;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/concerts")
public class ConcertController {

    private final ConcertService concertService;

    public ConcertController(ConcertService concertService) {
        this.concertService = concertService;
    }

    @GetMapping("/{concertId}")
    public ResponseEntity<Concert> getConcert(@PathVariable Long concertId) {
        Concert concert = concertService.getConcert(concertId);
        return ResponseEntity.ok(concert);
    }

    @GetMapping
    public ResponseEntity<List<Concert>> getAllConcerts() {
        List<Concert> concerts = concertService.getAllConcerts();
        return ResponseEntity.ok(concerts);
    }

    @GetMapping("/artist/{artist}")
    public ResponseEntity<List<Concert>> getConcertsByArtist(@PathVariable String artist) {
        List<Concert> concerts = concertService.getConcertsByArtist(artist);
        return ResponseEntity.ok(concerts);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Concert>> getAvailableConcerts() {
        List<Concert> concerts = concertService.getAvailableConcerts();
        return ResponseEntity.ok(concerts);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Concert>> getUpcomingConcerts() {
        List<Concert> concerts = concertService.getUpcomingConcerts();
        return ResponseEntity.ok(concerts);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Concert>> searchConcerts(@RequestParam String keyword) {
        List<Concert> concerts = concertService.searchConcertsByTitle(keyword);
        return ResponseEntity.ok(concerts);
    }

    @GetMapping("/{concertId}/status")
    public ResponseEntity<ConcertBookingStatus> getBookingStatus(@PathVariable Long concertId) {
        ConcertBookingStatus status = concertService.getBookingStatus(concertId);
        return ResponseEntity.ok(status);
    }
} 