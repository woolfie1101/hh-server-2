package kr.hhplus.be.server.concert.infrastructure.persistence.repository;

import kr.hhplus.be.server.concert.domain.model.Concert;
import kr.hhplus.be.server.concert.domain.repository.ConcertRepository;
import kr.hhplus.be.server.concert.infrastructure.persistence.entity.ConcertEntity;
import kr.hhplus.be.server.concert.infrastructure.persistence.jpa.SpringConcertJpa;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Concert Repository 구현체
 * - 도메인 Repository 인터페이스 구현
 * - Spring Data JPA를 내부적으로 사용
 * - 도메인 ↔ JPA 엔티티 매핑 처리
 */
@Repository
public class ConcertRepositoryImpl implements ConcertRepository {

    private final SpringConcertJpa springConcertJpa;

    public ConcertRepositoryImpl(SpringConcertJpa springConcertJpa) {
        this.springConcertJpa = springConcertJpa;
    }

    @Override
    public Concert save(Concert concert) {
        ConcertEntity entity = ConcertEntity.fromDomain(concert);
        ConcertEntity savedEntity = springConcertJpa.save(entity);

        // 저장된 ID를 도메인 객체에 할당
        if (savedEntity.getId() != null) {
            concert.assignId(savedEntity.getId());
        }

        return concert;
    }

    @Override
    public Optional<Concert> findById(Long id) {
        return springConcertJpa.findById(id)
            .map(ConcertEntity::toDomain);
    }

    @Override
    public List<Concert> findByArtist(String artist) {
        return springConcertJpa.findByArtist(artist)
            .stream()
            .map(ConcertEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Concert> findByConcertDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return springConcertJpa.findByConcertDateBetween(startDate, endDate)
            .stream()
            .map(ConcertEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Concert> findAvailableConcerts() {
        return springConcertJpa.findAvailableConcerts()
            .stream()
            .map(ConcertEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Concert> findUpcomingConcerts(LocalDateTime currentTime) {
        return springConcertJpa.findUpcomingConcerts(currentTime)
            .stream()
            .map(ConcertEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Concert> findByTitleContaining(String keyword) {
        return springConcertJpa.findByTitleContaining(keyword)
            .stream()
            .map(ConcertEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Concert> findByTotalSeatsBetween(int minSeats, int maxSeats) {
        return springConcertJpa.findByTotalSeatsBetween(minSeats, maxSeats)
            .stream()
            .map(ConcertEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        springConcertJpa.deleteById(id);
    }

    @Override
    public List<Concert> findAll() {
        return springConcertJpa.findAll()
            .stream()
            .map(ConcertEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return springConcertJpa.count();
    }
}