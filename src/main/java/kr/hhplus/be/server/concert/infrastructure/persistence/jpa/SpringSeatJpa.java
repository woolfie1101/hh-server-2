package kr.hhplus.be.server.concert.infrastructure.persistence.jpa;

import kr.hhplus.be.server.concert.infrastructure.persistence.entity.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Seat Spring Data JPA Repository
 * - JPA 전용 데이터 접근 인터페이스
 * - SeatRepositoryImpl에서 내부적으로 사용
 */
public interface SpringSeatJpa extends JpaRepository<SeatEntity, Long> {

    /**
     * 콘서트별 모든 좌석 조회
     */
    List<SeatEntity> findByConcertId(Long concertId);

    /**
     * 콘서트별 예약 가능한 좌석 조회
     */
    @Query("SELECT s FROM SeatEntity s WHERE s.concertId = :concertId AND s.reservedBy IS NULL")
    List<SeatEntity> findAvailableSeatsByConcertId(@Param("concertId") Long concertId);

    /**
     * 특정 사용자가 예약한 좌석 조회
     */
    List<SeatEntity> findByReservedBy(String userId);

    /**
     * 만료된 예약 좌석 조회 (자동 해제 대상)
     * 예약 시간으로부터 5분이 지난 좌석들
     */
    @Query("SELECT s FROM SeatEntity s WHERE s.reservedBy IS NOT NULL " +
        "AND s.reservedAt < :cutoffTime")
    List<SeatEntity> findExpiredReservations(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * 콘서트와 좌석번호로 특정 좌석 조회
     */
    Optional<SeatEntity> findByConcertIdAndSeatNumber(Long concertId, String seatNumber);

    /**
     * 가격 범위로 좌석 조회
     */
    List<SeatEntity> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * 콘서트별 예약 가능한 좌석 수 조회
     */
    @Query("SELECT COUNT(s) FROM SeatEntity s WHERE s.concertId = :concertId AND s.reservedBy IS NULL")
    long countAvailableSeatsByConcertId(@Param("concertId") Long concertId);

    /**
     * 콘서트별 예약된 좌석 수 조회
     */
    @Query("SELECT COUNT(s) FROM SeatEntity s WHERE s.concertId = :concertId AND s.reservedBy IS NOT NULL")
    long countReservedSeatsByConcertId(@Param("concertId") Long concertId);

    /**
     * 콘서트별 가격대별 좌석 조회
     */
    List<SeatEntity> findByConcertIdAndPrice(Long concertId, BigDecimal price);

    /**
     * 콘서트별 가격 범위로 좌석 조회
     */
    List<SeatEntity> findByConcertIdAndPriceBetween(Long concertId, BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * 좌석 번호 리스트로 좌석 조회
     */
    List<SeatEntity> findByConcertIdAndSeatNumberIn(Long concertId, List<String> seatNumbers);

    /**
     * 특정 시간 이후에 예약된 좌석 조회
     */
    List<SeatEntity> findByReservedAtAfter(LocalDateTime dateTime);

    /**
     * 콘서트별 좌석 가격 통계
     */
    @Query("SELECT MIN(s.price), MAX(s.price), AVG(s.price) FROM SeatEntity s WHERE s.concertId = :concertId")
    List<Object[]> findPriceStatsByConcertId(@Param("concertId") Long concertId);

    /**
     * 콘서트별 좌석 번호 패턴으로 조회 (예: A로 시작하는 좌석)
     */
    @Query("SELECT s FROM SeatEntity s WHERE s.concertId = :concertId AND s.seatNumber LIKE :pattern")
    List<SeatEntity> findByConcertIdAndSeatNumberPattern(@Param("concertId") Long concertId,
        @Param("pattern") String pattern);

    /**
     * 여러 콘서트의 좌석 조회
     */
    List<SeatEntity> findByConcertIdIn(List<Long> concertIds);

    /**
     * 콘서트별 좌석 예약 상태 통계
     */
    @Query("SELECT s.concertId, COUNT(s), COUNT(CASE WHEN s.reservedBy IS NOT NULL THEN 1 END) " +
        "FROM SeatEntity s WHERE s.concertId IN :concertIds " +
        "GROUP BY s.concertId")
    List<Object[]> findReservationStatsByConcertIds(@Param("concertIds") List<Long> concertIds);
}