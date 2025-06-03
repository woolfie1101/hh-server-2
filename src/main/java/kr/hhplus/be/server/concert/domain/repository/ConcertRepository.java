package kr.hhplus.be.server.concert.domain.repository;

import kr.hhplus.be.server.concert.domain.model.Concert;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Concert 도메인 Repository 인터페이스
 * - 순수한 도메인 레이어의 데이터 접근 추상화
 * - 프레임워크에 독립적인 인터페이스
 * - 콘서트 예약 비즈니스 로직 중심의 메서드 정의
 */
public interface ConcertRepository {

    /**
     * 콘서트 저장
     * @param concert 저장할 콘서트
     * @return 저장된 콘서트 (ID가 할당됨)
     */
    Concert save(Concert concert);

    /**
     * ID로 콘서트 조회
     * @param id 콘서트 ID
     * @return 조회된 콘서트
     */
    Optional<Concert> findById(Long id);

    /**
     * 아티스트명으로 콘서트 조회
     * @param artist 아티스트명
     * @return 해당 아티스트의 콘서트 목록
     */
    List<Concert> findByArtist(String artist);

    /**
     * 날짜 범위로 콘서트 조회
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 기간 내 콘서트 목록
     */
    List<Concert> findByConcertDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 예약 가능한 콘서트 조회 (좌석이 남아있는 콘서트)
     * @return 예약 가능한 콘서트 목록
     */
    List<Concert> findAvailableConcerts();

    /**
     * 다가오는 콘서트 조회 (현재 시간 이후)
     * @param currentTime 기준 시간
     * @return 다가오는 콘서트 목록
     */
    List<Concert> findUpcomingConcerts(LocalDateTime currentTime);

    /**
     * 제목으로 콘서트 검색
     * @param keyword 검색 키워드
     * @return 제목에 키워드가 포함된 콘서트 목록
     */
    List<Concert> findByTitleContaining(String keyword);

    /**
     * 좌석 수 범위로 콘서트 조회
     * @param minSeats 최소 좌석 수
     * @param maxSeats 최대 좌석 수
     * @return 조건에 맞는 콘서트 목록
     */
    List<Concert> findByTotalSeatsBetween(int minSeats, int maxSeats);

    /**
     * 콘서트 삭제
     * @param id 삭제할 콘서트 ID
     */
    void deleteById(Long id);

    /**
     * 모든 콘서트 조회
     * @return 모든 콘서트 목록
     */
    List<Concert> findAll();

    /**
     * 콘서트 수 조회
     * @return 총 콘서트 수
     */
    long count();
}