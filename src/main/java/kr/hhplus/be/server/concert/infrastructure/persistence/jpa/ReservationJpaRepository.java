package kr.hhplus.be.server.concert.infrastructure.persistence.jpa;

import kr.hhplus.be.server.concert.infrastructure.persistence.entity.ReservationEntity;
import kr.hhplus.be.server.concert.domain.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, UUID> {
    List<ReservationEntity> findByUserId(UUID userId);
    List<ReservationEntity> findByConcertId(UUID concertId);
    List<ReservationEntity> findBySeatId(UUID seatId);
    List<ReservationEntity> findByStatus(ReservationStatus status);
    List<ReservationEntity> findByStatusAndCreatedAtBefore(ReservationStatus status, LocalDateTime cutoffTime);
    List<ReservationEntity> findByUserIdAndStatus(UUID userId, ReservationStatus status);
    List<ReservationEntity> findByConcertIdAndStatus(UUID concertId, ReservationStatus status);
    List<ReservationEntity> findBySeatIdAndStatus(UUID seatId, ReservationStatus status);
    List<ReservationEntity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    long countByConcertId(UUID concertId);
    long countByConcertIdAndStatus(UUID concertId, ReservationStatus status);
    boolean existsBySeatIdAndStatus(UUID seatId, ReservationStatus status);
} 