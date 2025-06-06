package kr.hhplus.be.server.repository;

import kr.hhplus.be.server.entity.ReservationEntity;
import kr.hhplus.be.server.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 예약 JPA 레포지토리 인터페이스
 */
@Repository
public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, UUID> {
    List<ReservationEntity> findByUserId(UUID userId);
    
    List<ReservationEntity> findByConcertId(UUID concertId);
    
    List<ReservationEntity> findBySeatId(UUID seatId);
    
    List<ReservationEntity> findByStatus(ReservationStatus status);
    
    List<ReservationEntity> findByStatusAndExpiresAtBefore(ReservationStatus status, LocalDateTime dateTime);
    
    List<ReservationEntity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    long countByConcertId(UUID concertId);
    
    boolean existsBySeatIdAndStatus(UUID seatId, ReservationStatus status);
} 