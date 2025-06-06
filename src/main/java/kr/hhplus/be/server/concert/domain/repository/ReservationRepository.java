package kr.hhplus.be.server.concert.domain.repository;

import kr.hhplus.be.server.concert.domain.model.Reservation;
import kr.hhplus.be.server.concert.domain.model.ReservationStatus;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository {
    Reservation save(Reservation reservation);
    Optional<Reservation> findById(UUID id);
    List<Reservation> findByUserId(UUID userId);
    List<Reservation> findByConcertId(UUID concertId);
    List<Reservation> findBySeatId(UUID seatId);
    List<Reservation> findByStatus(ReservationStatus status);
    List<Reservation> findByUserIdAndStatus(UUID userId, ReservationStatus status);
    List<Reservation> findByConcertIdAndStatus(UUID concertId, ReservationStatus status);
    List<Reservation> findBySeatIdAndStatus(UUID seatId, ReservationStatus status);
    boolean existsBySeatIdAndStatus(UUID seatId, ReservationStatus status);
    long countByConcertId(UUID concertId);
    List<Reservation> findExpiredReservations(LocalDateTime cutoffTime);
    List<Reservation> findActiveReservationsByUserId(UUID userId);
    List<Reservation> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
} 