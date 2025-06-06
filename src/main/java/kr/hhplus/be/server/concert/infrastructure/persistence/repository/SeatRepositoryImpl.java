package kr.hhplus.be.server.concert.infrastructure.persistence.repository;

import kr.hhplus.be.server.concert.domain.model.Seat;
import kr.hhplus.be.server.concert.domain.repository.SeatRepository;
import kr.hhplus.be.server.concert.infrastructure.persistence.entity.SeatEntity;
import kr.hhplus.be.server.concert.infrastructure.persistence.jpa.SpringSeatJpa;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Seat Repository 구현체
 * - 도메인 Repository 인터페이스 구현
 * - Spring Data JPA를 내부적으로 사용
 * - 도메인 ↔ JPA 엔티티 매핑 처리
 */
@Repository
public class SeatRepositoryImpl implements SeatRepository {

    private final SpringSeatJpa springSeatJpa;

    public SeatRepositoryImpl(SpringSeatJpa springSeatJpa) {
        this.springSeatJpa = springSeatJpa;
    }

    @Override
    public Seat save(Seat seat) {
        SeatEntity entity = SeatEntity.fromDomain(seat);
        SeatEntity savedEntity = springSeatJpa.save(entity);

        // 저장된 ID를 도메인 객체에 할당
        if (savedEntity.getId() != null) {
            seat.assignId(savedEntity.getId());
        }

        return seat;
    }

    @Override
    public Optional<Seat> findById(Long id) {
        return springSeatJpa.findById(id)
            .map(SeatEntity::toDomain);
    }

    @Override
    public List<Seat> findByConcertId(Long concertId) {
        return springSeatJpa.findByConcertId(concertId)
            .stream()
            .map(SeatEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Seat> findAvailableSeatsByConcertId(Long concertId) {
        return springSeatJpa.findAvailableSeatsByConcertId(concertId)
            .stream()
            .map(SeatEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Seat> findByReservedBy(String userId) {
        return springSeatJpa.findByReservedBy(userId)
            .stream()
            .map(SeatEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Seat> findExpiredReservations(LocalDateTime cutoffTime) {
        return springSeatJpa.findExpiredReservations(cutoffTime)
            .stream()
            .map(SeatEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Seat> findByConcertIdAndSeatNumber(Long concertId, String seatNumber) {
        return springSeatJpa.findByConcertIdAndSeatNumber(concertId, seatNumber)
            .map(SeatEntity::toDomain);
    }

    @Override
    public List<Seat> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return springSeatJpa.findByPriceBetween(minPrice, maxPrice)
            .stream()
            .map(SeatEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public long countAvailableSeatsByConcertId(Long concertId) {
        return springSeatJpa.countAvailableSeatsByConcertId(concertId);
    }

    @Override
    public void deleteById(Long id) {
        springSeatJpa.deleteById(id);
    }

    @Override
    public List<Seat> findAll() {
        return springSeatJpa.findAll()
            .stream()
            .map(SeatEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return springSeatJpa.count();
    }
}