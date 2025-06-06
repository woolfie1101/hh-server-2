package kr.hhplus.be.server.concert.domain.repository;

import kr.hhplus.be.server.concert.domain.model.Seat;
import kr.hhplus.be.server.concert.domain.model.ReservationStatus;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 좌석 레포지토리 인터페이스
 */
@Repository
public interface SeatRepository {
    Seat save(Seat seat);
    Optional<Seat> findById(UUID id);
    List<Seat> findByConcertId(UUID concertId);
    List<Seat> findByStatus(ReservationStatus status);
    List<Seat> findByConcertIdAndStatus(UUID concertId, ReservationStatus status);
    List<Seat> findAvailableSeats(UUID concertId);
    List<Seat> findReservedSeats(UUID concertId);
    List<Seat> findByReservedBy(UUID userId);
    long countByConcertId(UUID concertId);
    long countByConcertIdAndStatus(UUID concertId, ReservationStatus status);
    boolean existsByConcertIdAndSeatNumber(UUID concertId, String seatNumber);
    boolean existsBySeatIdAndStatus(UUID seatId, ReservationStatus status);
    void deleteById(UUID id);
} 