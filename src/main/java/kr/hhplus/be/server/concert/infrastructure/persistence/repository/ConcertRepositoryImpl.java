package kr.hhplus.be.server.concert.infrastructure.persistence.repository;

import kr.hhplus.be.server.concert.domain.model.Concert;
import kr.hhplus.be.server.concert.domain.repository.ConcertRepository;
import kr.hhplus.be.server.concert.infrastructure.persistence.entity.ConcertEntity;
import kr.hhplus.be.server.concert.infrastructure.persistence.jpa.SpringConcertJpa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {

    private final SpringConcertJpa springConcertJpa;

    @Override
    public Concert save(Concert concert) {
        ConcertEntity entity = ConcertEntity.from(concert);
        ConcertEntity savedEntity = springConcertJpa.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Concert> findById(UUID id) {
        return springConcertJpa.findById(id)
            .map(ConcertEntity::toDomain);
    }

    @Override
    public List<Concert> findAll() {
        return springConcertJpa.findAll().stream()
            .map(ConcertEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Concert> findByArtist(String artist) {
        return springConcertJpa.findByArtist(artist).stream()
            .map(ConcertEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Concert> findAvailableConcerts() {
        return springConcertJpa.findByConcertDateAfter(LocalDateTime.now()).stream()
            .map(ConcertEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Concert> findUpcomingConcerts() {
        LocalDateTime now = LocalDateTime.now();
        return springConcertJpa.findByConcertDateAfter(now).stream()
            .map(ConcertEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Concert> searchConcerts(String keyword) {
        return springConcertJpa.findByTitleContainingOrArtistContaining(keyword, keyword).stream()
            .map(ConcertEntity::toDomain)
            .collect(Collectors.toList());
    }
} 