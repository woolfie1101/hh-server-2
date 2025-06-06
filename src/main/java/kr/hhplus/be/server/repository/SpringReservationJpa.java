package kr.hhplus.be.server.repository;

import kr.hhplus.be.server.entity.ReservationEntity;
import kr.hhplus.be.server.model.Reservation;
import kr.hhplus.be.server.model.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SpringReservationJpa implements ReservationRepository {
    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public Reservation save(Reservation reservation) {
        ReservationEntity entity = ReservationEntity.fromDomain(reservation);
        return reservationJpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<Reservation> findById(UUID id) {
        return reservationJpaRepository.findById(id)
            .map(ReservationEntity::toDomain);
    }

    @Override
    public List<Reservation> findByUserId(UUID userId) {
        return reservationJpaRepository.findByUserId(userId).stream()
            .map(ReservationEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByConcertId(UUID concertId) {
        return reservationJpaRepository.findByConcertId(concertId).stream()
            .map(ReservationEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findBySeatId(UUID seatId) {
        return reservationJpaRepository.findBySeatId(seatId).stream()
            .map(ReservationEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByStatus(ReservationStatus status) {
        return reservationJpaRepository.findByStatus(status).stream()
            .map(ReservationEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        return reservationJpaRepository.findByStatusAndExpiresAtBefore(ReservationStatus.PENDING, now).stream()
            .map(ReservationEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return reservationJpaRepository.findByCreatedAtBetween(startDate, endDate).stream()
            .map(ReservationEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public long countByConcertId(UUID concertId) {
        return reservationJpaRepository.countByConcertId(concertId);
    }

    @Override
    public boolean existsBySeatIdAndStatus(UUID seatId, ReservationStatus status) {
        return reservationJpaRepository.existsBySeatIdAndStatus(seatId, status);
    }
} 