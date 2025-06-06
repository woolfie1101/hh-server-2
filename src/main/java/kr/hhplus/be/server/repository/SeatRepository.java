package kr.hhplus.be.server.repository;

import kr.hhplus.be.server.model.Seat;
import kr.hhplus.be.server.model.SeatStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeatRepository {
    Seat save(Seat seat);
    
    Optional<Seat> findById(UUID id);
    
    List<Seat> findByConcertId(UUID concertId);
    
    List<Seat> findByStatus(SeatStatus status);
    
    List<Seat> findByConcertIdAndStatus(UUID concertId, SeatStatus status);
    
    List<Seat> findByReservedBy(UUID userId);
    
    long countByConcertId(UUID concertId);
    
    long countByConcertIdAndStatus(UUID concertId, SeatStatus status);
} 