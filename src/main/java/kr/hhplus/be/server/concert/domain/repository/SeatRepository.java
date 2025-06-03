package kr.hhplus.be.server.concert.domain.repository;

import kr.hhplus.be.server.concert.domain.model.Seat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Seat 도메인 Repository 인터페이스
 * - 순수한 도메인 레이어의 데이터 접근 추상화
 * - 프레임워크에 독립적인 인터페이스
 * - 좌석 예약 비즈니스 로직 중심의 메서드 정의
 */
public interface SeatRepository {

    /**
     * 좌석 저장
     * @param seat 저장할 좌석
     * @return 저장된 좌석 (ID가 할당됨)
     */
    Seat save(Seat seat);

    /**
     * ID로 좌석 조회
     * @param id 좌석 ID
     * @return 조회된 좌석
     */
    Optional<Seat> findById(Long id);

    /**
     * 콘서트별 모든 좌석 조회
     * @param concertId 콘서트 ID
     * @return 해당 콘서트의 모든 좌석 목록
     */
    List<Seat> findByConcertId(Long concertId);

    /**
     * 콘서트별 예약 가능한 좌석 조회
     * @param concertId 콘서트 ID
     * @return 해당 콘서트의 예약 가능한 좌석 목록
     */
    List<Seat> findAvailableSeatsByConcertId(Long concertId);

    /**
     * 특정 사용자가 예약한 좌석 조회
     * @param userId 사용자 ID
     * @return 해당 사용자가 예약한 좌석 목록
     */
    List<Seat> findByReservedBy(String userId);

    /**
     * 만료된 예약 좌석 조회 (자동 해제 대상)
     * @param cutoffTime 만료 기준 시간
     * @return 만료된 예약 좌석 목록
     */
    List<Seat> findExpiredReservations(LocalDateTime cutoffTime);

    /**
     * 콘서트와 좌석번호로 특정 좌석 조회
     * @param concertId 콘서트 ID
     * @param seatNumber 좌석 번호
     * @return 조회된 좌석
     */
    Optional<Seat> findByConcertIdAndSeatNumber(Long concertId, String seatNumber);

    /**
     * 가격 범위로 좌석 조회
     * @param minPrice 최소 가격
     * @param maxPrice 최대 가격
     * @return 가격 범위에 맞는 좌석 목록
     */
    List<Seat> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * 콘서트별 예약 가능한 좌석 수 조회
     * @param concertId 콘서트 ID
     * @return 예약 가능한 좌석 수
     */
    long countAvailableSeatsByConcertId(Long concertId);

    /**
     * 좌석 삭제
     * @param id 삭제할 좌석 ID
     */
    void deleteById(Long id);

    /**
     * 모든 좌석 조회
     * @return 모든 좌석 목록
     */
    List<Seat> findAll();

    /**
     * 좌석 수 조회
     * @return 총 좌석 수
     */
    long count();
}