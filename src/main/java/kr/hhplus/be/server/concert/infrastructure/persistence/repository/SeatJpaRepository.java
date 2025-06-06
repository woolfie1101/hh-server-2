package kr.hhplus.be.server.concert.infrastructure.persistence.repository;

import kr.hhplus.be.server.concert.infrastructure.persistence.entity.SeatEntity;
import kr.hhplus.be.server.concert.domain.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface SeatJpaRepository extends JpaRepository<SeatEntity, UUID> {
    List<SeatEntity> findByConcertId(UUID concertId);
    List<SeatEntity> findByStatus(ReservationStatus status);
    List<SeatEntity> findByConcertIdAndStatus(UUID concertId, ReservationStatus status);
    boolean existsByIdAndStatus(UUID id, ReservationStatus status);
    long countByConcertId(UUID concertId);
    long countByConcertIdAndStatus(UUID concertId, ReservationStatus status);
} 