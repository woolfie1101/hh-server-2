package kr.hhplus.be.server.concert.domain.repository;

import kr.hhplus.be.server.concert.domain.model.Reservation;
import kr.hhplus.be.server.concert.domain.model.ReservationStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Reservation 도메인 Repository 인터페이스
 * - 순수한 도메인 레이어의 데이터 접근 추상화
 * - 프레임워크에 독립적인 인터페이스
 * - 예약 비즈니스 로직 중심의 메서드 정의
 */
public interface ReservationRepository {

    /**
     * 예약 저장
     * @param reservation 저장할 예약
     * @return 저장된 예약 (ID가 할당됨)
     */
    Reservation save(Reservation reservation);

    /**
     * ID로 예약 조회
     * @param id 예약 ID
     * @return 조회된 예약
     */
    Optional<Reservation> findById(Long id);

    /**
     * 사용자별 예약 조회
     * @param userId 사용자 ID
     * @return 해당 사용자의 모든 예약 목록
     */
    List<Reservation> findByUserId(String userId);

    /**
     * 콘서트별 예약 조회
     * @param concertId 콘서트 ID
     * @return 해당 콘서트의 모든 예약 목록
     */
    List<Reservation> findByConcertId(Long concertId);

    /**
     * 좌석별 예약 조회 (좌석당 최대 1개 예약)
     * @param seatId 좌석 ID
     * @return 해당 좌석의 예약
     */
    Optional<Reservation> findBySeatId(Long seatId);

    /**
     * 상태별 예약 조회
     * @param status 예약 상태
     * @return 해당 상태의 예약 목록
     */
    List<Reservation> findByStatus(ReservationStatus status);

    /**
     * 만료된 예약 조회 (자동 처리 대상)
     * @param cutoffTime 만료 기준 시간
     * @return 만료된 예약 목록
     */
    List<Reservation> findExpiredReservations(LocalDateTime cutoffTime);

    /**
     * 사용자와 콘서트로 예약 조회
     * @param userId 사용자 ID
     * @param concertId 콘서트 ID
     * @return 해당 사용자의 특정 콘서트 예약 목록
     */
    List<Reservation> findByUserIdAndConcertId(String userId, Long concertId);

    /**
     * 날짜 범위별 예약 조회
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 기간 내 예약 목록
     */
    List<Reservation> findByReservedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 사용자의 활성 예약 조회 (TEMPORARY, CONFIRMED 상태)
     * @param userId 사용자 ID
     * @return 활성 상태의 예약 목록
     */
    List<Reservation> findActiveReservationsByUserId(String userId);

    /**
     * 콘서트별 예약 수 조회
     * @param concertId 콘서트 ID
     * @return 해당 콘서트의 총 예약 수
     */
    long countByConcertId(Long concertId);

    /**
     * 예약 삭제
     * @param id 삭제할 예약 ID
     */
    void deleteById(Long id);

    /**
     * 모든 예약 조회
     * @return 모든 예약 목록
     */
    List<Reservation> findAll();

    /**
     * 예약 수 조회
     * @return 총 예약 수
     */
    long count();
}