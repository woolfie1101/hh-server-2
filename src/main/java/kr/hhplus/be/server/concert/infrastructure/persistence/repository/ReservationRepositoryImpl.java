package kr.hhplus.be.server.concert.infrastructure.persistence.repository;

import kr.hhplus.be.server.concert.domain.model.Reservation;
import kr.hhplus.be.server.concert.domain.model.ReservationStatus;
import kr.hhplus.be.server.concert.domain.repository.ReservationRepository;
import kr.hhplus.be.server.concert.infrastructure.persistence.entity.ReservationEntity;
import kr.hhplus.be.server.concert.infrastructure.persistence.jpa.SpringReservationJpa;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Reservation Repository 구현체
 * - 도메인 Repository 인터페이스 구현
 * - Spring Data JPA를 내부적으로 사용
 * - 도메인 ↔ JPA 엔티티 매핑 처리
 */
@Repository
public class ReservationRepositoryImpl implements ReservationRepository {

    private final SpringReservationJpa springReservationJpa;

    public ReservationRepositoryImpl(SpringReservationJpa springReservationJpa) {
        this.springReservationJpa = springReservationJpa;
    }

    @Override
    public Reservation save(Reservation reservation) {
        ReservationEntity entity = ReservationEntity.fromDomain(reservation);
        ReservationEntity savedEntity = springReservationJpa.save(entity);

        // 저장된 ID를 도메인 객체에 할당
        if (savedEntity.getId() != null) {
            reservation.assignId(savedEntity.getId());
        }

        return reservation;
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return springReservationJpa.findById(id)
            .map(ReservationEntity::toDomain);
    }

    @Override
    public List<Reservation> findByUserId(String userId) {
        return springReservationJpa.findByUserId(userId)
            .stream()
            .map(ReservationEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByConcertId(Long concertId) {
        return springReservationJpa.findByConcertId(concertId)
            .stream()
            .map(ReservationEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Reservation> findBySeatId(Long seatId) {
        return springReservationJpa.findBySeatId(seatId)
            .map(ReservationEntity::toDomain);
    }

    @Override
    public List<Reservation> findByStatus(ReservationStatus status) {
        return springReservationJpa.findByStatus(status)
            .stream()
            .map(ReservationEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findExpiredReservations(LocalDateTime cutoffTime) {
        return springReservationJpa.findExpiredReservations(cutoffTime)
            .stream()
            .map(ReservationEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByUserIdAndConcertId(String userId, Long concertId) {
        return springReservationJpa.findByUserIdAndConcertId(userId, concertId)
            .stream()
            .map(ReservationEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByReservedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return springReservationJpa.findByReservedAtBetween(startDate, endDate)
            .stream()
            .map(ReservationEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findActiveReservationsByUserId(String userId) {
        return springReservationJpa.findActiveReservationsByUserId(userId)
            .stream()
            .map(ReservationEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public long countByConcertId(Long concertId) {
        return springReservationJpa.countByConcertId(concertId);
    }

    @Override
    public void deleteById(Long id) {
        springReservationJpa.deleteById(id);
    }

    @Override
    public List<Reservation> findAll() {
        return springReservationJpa.findAll()
            .stream()
            .map(ReservationEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return springReservationJpa.count();
    }
}