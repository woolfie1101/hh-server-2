package kr.hhplus.be.server.concert.infrastructure.persistence.jpa;

import kr.hhplus.be.server.concert.infrastructure.persistence.entity.ConcertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 콘서트 Spring Data JPA Repository
 */
@Repository
public interface SpringConcertJpa extends JpaRepository<ConcertEntity, UUID> {

    /**
     * 아티스트명으로 콘서트 조회
     */
    List<ConcertEntity> findByArtist(String artist);

    /**
     * 날짜 범위로 콘서트 조회
     */
    List<ConcertEntity> findByConcertDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 예약 가능한 콘서트 조회 (좌석이 남아있는 콘서트)
     */
    @Query("SELECT c FROM ConcertEntity c WHERE c.totalSeats > c.reservedSeats")
    List<ConcertEntity> findAvailableConcerts();

    /**
     * 다가오는 콘서트 조회 (현재 시간 이후)
     */
    @Query("SELECT c FROM ConcertEntity c WHERE c.concertDate > :currentTime ORDER BY c.concertDate ASC")
    List<ConcertEntity> findUpcomingConcerts(@Param("currentTime") LocalDateTime currentTime);

    /**
     * 제목으로 콘서트 검색
     */
    List<ConcertEntity> findByTitleContaining(String keyword);

    /**
     * 좌석 수 범위로 콘서트 조회
     */
    List<ConcertEntity> findByTotalSeatsBetween(int minSeats, int maxSeats);

    @Query("SELECT c FROM ConcertEntity c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ConcertEntity> searchByTitle(@Param("keyword") String keyword);

    @Query("SELECT c FROM ConcertEntity c WHERE c.price BETWEEN :minPrice AND :maxPrice")
    List<ConcertEntity> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    List<ConcertEntity> findByConcertDateAfter(LocalDateTime date);

    List<ConcertEntity> findByTitleContainingOrArtistContaining(String title, String artist);
}