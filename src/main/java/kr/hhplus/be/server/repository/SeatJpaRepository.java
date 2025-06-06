package kr.hhplus.be.server.repository;

import kr.hhplus.be.server.entity.SeatEntity;
import kr.hhplus.be.server.model.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SeatJpaRepository extends JpaRepository<SeatEntity, UUID> {
    List<SeatEntity> findByConcertId(UUID concertId);
    
    List<SeatEntity> findByStatus(SeatStatus status);
    
    List<SeatEntity> findByConcertIdAndStatus(UUID concertId, SeatStatus status);
    
    List<SeatEntity> findByReservedBy(UUID userId);
    
    long countByConcertId(UUID concertId);
    
    long countByConcertIdAndStatus(UUID concertId, SeatStatus status);
} 