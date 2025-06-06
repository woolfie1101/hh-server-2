package kr.hhplus.be.server.repository;

import kr.hhplus.be.server.model.Reservation;
import kr.hhplus.be.server.model.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository {
    Reservation save(Reservation reservation);
    
    Optional<Reservation> findById(UUID id);
    
    List<Reservation> findByUserId(UUID userId);
    
    List<Reservation> findByConcertId(UUID concertId);
    
    List<Reservation> findBySeatId(UUID seatId);
    
    List<Reservation> findByStatus(ReservationStatus status);
    
    List<Reservation> findExpiredReservations();
    
    List<Reservation> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    long countByConcertId(UUID concertId);
    
    boolean existsBySeatIdAndStatus(UUID seatId, ReservationStatus status);
} 