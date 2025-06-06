package kr.hhplus.be.server.concert.infrastructure.persistence.jpa;

import kr.hhplus.be.server.concert.domain.model.ReservationStatus;
import kr.hhplus.be.server.concert.infrastructure.persistence.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Reservation Spring Data JPA Repository
 * - JPA 전용 데이터 접근 인터페이스
 * - ReservationRepositoryImpl에서 내부적으로 사용
 */
public interface SpringReservationJpa extends JpaRepository<ReservationEntity, Long> {

    /**
     * 사용자별 예약 조회
     */
    List<ReservationEntity> findByUserId(String userId);

    /**
     * 콘서트별 예약 조회
     */
    List<ReservationEntity> findByConcertId(Long concertId);

    /**
     * 좌석별 예약 조회 (좌석당 최대 1개 예약)
     */
    Optional<ReservationEntity> findBySeatId(Long seatId);

    /**
     * 상태별 예약 조회
     */
    List<ReservationEntity> findByStatus(ReservationStatus status);

    /**
     * 만료된 예약 조회 (자동 처리 대상)
     * TEMPORARY 상태이면서 만료 시간이 지난 예약들
     */
    @Query("SELECT r FROM ReservationEntity r WHERE r.status = 'TEMPORARY' AND r.expiresAt < :cutoffTime")
    List<ReservationEntity> findExpiredReservations(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * 사용자와 콘서트로 예약 조회
     */
    List<ReservationEntity> findByUserIdAndConcertId(String userId, Long concertId);

    /**
     * 날짜 범위별 예약 조회
     */
    List<ReservationEntity> findByReservedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 사용자의 활성 예약 조회 (TEMPORARY, CONFIRMED 상태)
     */
    @Query("SELECT r FROM ReservationEntity r WHERE r.userId = :userId AND r.status IN ('TEMPORARY', 'CONFIRMED')")
    List<ReservationEntity> findActiveReservationsByUserId(@Param("userId") String userId);

    /**
     * 콘서트별 예약 수 조회
     */
    long countByConcertId(Long concertId);

    /**
     * 콘서트별 상태별 예약 수 조회
     */
    long countByConcertIdAndStatus(Long concertId, ReservationStatus status);

    /**
     * 특정 시간 이후의 예약 조회
     */
    List<ReservationEntity> findByReservedAtAfter(LocalDateTime dateTime);

    /**
     * 확정된 예약 조회 (CONFIRMED 상태)
     */
    @Query("SELECT r FROM ReservationEntity r WHERE r.status = 'CONFIRMED' ORDER BY r.confirmedAt DESC")
    List<ReservationEntity> findConfirmedReservations();

    /**
     * 사용자별 확정된 예약 조회
     */
    List<ReservationEntity> findByUserIdAndStatus(String userId, ReservationStatus status);

    /**
     * 콘서트별 확정된 예약 조회
     */
    List<ReservationEntity> findByConcertIdAndStatus(Long concertId, ReservationStatus status);

    /**
     * 좌석 번호로 예약 조회
     */
    Optional<ReservationEntity> findByConcertIdAndSeatNumber(Long concertId, String seatNumber);

    /**
     * 만료 시간이 임박한 예약 조회 (알림용)
     */
    @Query("SELECT r FROM ReservationEntity r WHERE r.status = 'TEMPORARY' " +
        "AND r.expiresAt BETWEEN :now AND :nearFuture")
    List<ReservationEntity> findReservationsExpiringSoon(@Param("now") LocalDateTime now,
        @Param("nearFuture") LocalDateTime nearFuture);

    /**
     * 특정 날짜의 예약 통계
     */
    @Query("SELECT r.status, COUNT(r) FROM ReservationEntity r " +
        "WHERE DATE(r.reservedAt) = DATE(:date) " +
        "GROUP BY r.status")
    List<Object[]> findReservationStatsByDate(@Param("date") LocalDateTime date);
}