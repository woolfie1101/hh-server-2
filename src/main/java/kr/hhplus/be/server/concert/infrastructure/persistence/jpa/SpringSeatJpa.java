package kr.hhplus.be.server.concert.infrastructure.persistence.jpa;

import kr.hhplus.be.server.concert.domain.model.Seat;
import kr.hhplus.be.server.concert.domain.model.ReservationStatus;
import kr.hhplus.be.server.concert.domain.repository.SeatRepository;
import kr.hhplus.be.server.concert.infrastructure.persistence.entity.SeatEntity;
import kr.hhplus.be.server.concert.infrastructure.persistence.repository.SeatJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SpringSeatJpa implements SeatRepository {

    private final SeatJpaRepository seatJpaRepository;

    @Override
    public Seat save(Seat seat) {
        SeatEntity entity = SeatEntity.fromDomain(seat);
        SeatEntity savedEntity = seatJpaRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Seat> findById(UUID id) {
        return seatJpaRepository.findById(id)
            .map(SeatEntity::toDomain);
    }

    @Override
    public List<Seat> findByConcertId(UUID concertId) {
        return seatJpaRepository.findByConcertId(concertId).stream()
            .map(SeatEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Seat> findByStatus(ReservationStatus status) {
        return seatJpaRepository.findByStatus(status).stream()
            .map(SeatEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Seat> findByConcertIdAndStatus(UUID concertId, ReservationStatus status) {
        return seatJpaRepository.findByConcertIdAndStatus(concertId, status).stream()
            .map(SeatEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Seat> findReservedSeats(UUID concertId) {
        return seatJpaRepository.findByConcertIdAndStatus(concertId, ReservationStatus.PENDING).stream()
            .map(SeatEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Seat> findByReservedBy(UUID userId) {
        return List.of();
    }

    @Override
    public List<Seat> findAvailableSeats(UUID concertId) {
        return seatJpaRepository.findByConcertIdAndStatus(concertId, ReservationStatus.AVAILABLE).stream()
            .map(SeatEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public boolean existsBySeatIdAndStatus(UUID seatId, ReservationStatus status) {
        return seatJpaRepository.existsByIdAndStatus(seatId, status);
    }

    @Override
    public long countByConcertId(UUID concertId) {
        return seatJpaRepository.countByConcertId(concertId);
    }

    @Override
    public long countByConcertIdAndStatus(UUID concertId, ReservationStatus status) {
        return seatJpaRepository.countByConcertIdAndStatus(concertId, status);
    }

    @Override
    public boolean existsByConcertIdAndSeatNumber(UUID concertId, String seatNumber) {
        return false;
    }

    @Override
    public void deleteById(UUID id) {
        seatJpaRepository.deleteById(id);
    }
} 