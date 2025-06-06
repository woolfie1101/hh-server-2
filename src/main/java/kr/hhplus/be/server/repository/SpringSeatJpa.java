package kr.hhplus.be.server.repository;

import kr.hhplus.be.server.entity.SeatEntity;
import kr.hhplus.be.server.model.Seat;
import kr.hhplus.be.server.model.SeatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
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
        return seatJpaRepository.save(entity).toDomain();
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
    public List<Seat> findByStatus(SeatStatus status) {
        return seatJpaRepository.findByStatus(status).stream()
            .map(SeatEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Seat> findByConcertIdAndStatus(UUID concertId, SeatStatus status) {
        return seatJpaRepository.findByConcertIdAndStatus(concertId, status).stream()
            .map(SeatEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Seat> findByReservedBy(UUID userId) {
        return seatJpaRepository.findByReservedBy(userId).stream()
            .map(SeatEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public long countByConcertId(UUID concertId) {
        return seatJpaRepository.countByConcertId(concertId);
    }

    @Override
    public long countByConcertIdAndStatus(UUID concertId, SeatStatus status) {
        return seatJpaRepository.countByConcertIdAndStatus(concertId, status);
    }
} 