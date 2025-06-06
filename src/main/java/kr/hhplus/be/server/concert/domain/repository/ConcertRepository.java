package kr.hhplus.be.server.concert.domain.repository;

import kr.hhplus.be.server.concert.domain.model.Concert;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 콘서트 레포지토리 인터페이스
 */
public interface ConcertRepository {
    Concert save(Concert concert);
    Optional<Concert> findById(UUID id);
    List<Concert> findAll();
    List<Concert> findByArtist(String artist);
    List<Concert> findAvailableConcerts();
    List<Concert> findUpcomingConcerts();
    List<Concert> searchConcerts(String keyword);
} 