package kr.hhplus.be.server.concert.application.service;

import kr.hhplus.be.server.concert.application.dto.ConcertBookingStatus;
import kr.hhplus.be.server.concert.domain.model.Concert;
import kr.hhplus.be.server.concert.domain.repository.ConcertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ConcertService - 콘서트 조회 서비스 (레이어드 아키텍처)
 * - 콘서트 관련 비즈니스 로직 처리
 * - 단순한 CRUD 및 조회 기능 중심
 * - 검증은 도메인 모델에서 담당
 */
@Service
@Transactional(readOnly = true)
public class ConcertService {

    private final ConcertRepository concertRepository;

    public ConcertService(ConcertRepository concertRepository) {
        this.concertRepository = concertRepository;
    }

    /**
     * 콘서트 조회 (ID)
     * @param concertId 콘서트 ID
     * @return 조회된 콘서트
     * @throws IllegalArgumentException 콘서트가 존재하지 않는 경우
     */
    public Concert getConcert(Long concertId) {
        if (concertId == null) {
            throw new IllegalArgumentException("콘서트 ID는 필수입니다.");
        }

        return concertRepository.findById(concertId)
            .orElseThrow(() -> new IllegalArgumentException("콘서트를 찾을 수 없습니다. ID: " + concertId));
    }

    /**
     * 모든 콘서트 조회
     * @return 전체 콘서트 목록
     */
    public List<Concert> getAllConcerts() {
        return concertRepository.findAll();
    }

    /**
     * 아티스트별 콘서트 조회
     * @param artist 아티스트명
     * @return 해당 아티스트의 콘서트 목록
     */
    public List<Concert> getConcertsByArtist(String artist) {
        if (artist == null || artist.trim().isEmpty()) {
            throw new IllegalArgumentException("아티스트명은 필수입니다.");
        }

        return concertRepository.findByArtist(artist);
    }

    /**
     * 예약 가능한 콘서트 조회
     * - 좌석이 남아있는 콘서트만 조회
     * @return 예약 가능한 콘서트 목록
     */
    public List<Concert> getAvailableConcerts() {
        return concertRepository.findAvailableConcerts();
    }

    /**
     * 다가오는 콘서트 조회
     * - 현재 시간 이후의 콘서트만 조회
     * @return 다가오는 콘서트 목록 (날짜순 정렬)
     */
    public List<Concert> getUpcomingConcerts() {
        LocalDateTime currentTime = LocalDateTime.now();
        return concertRepository.findUpcomingConcerts(currentTime);
    }

    /**
     * 날짜 범위로 콘서트 조회
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 기간 내 콘서트 목록
     */
    public List<Concert> getConcertsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("시작 날짜와 종료 날짜는 필수입니다.");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작 날짜는 종료 날짜보다 이전이어야 합니다.");
        }

        return concertRepository.findByConcertDateBetween(startDate, endDate);
    }

    /**
     * 콘서트 제목 검색
     * @param keyword 검색 키워드
     * @return 제목에 키워드가 포함된 콘서트 목록
     */
    public List<Concert> searchConcertsByTitle(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("검색 키워드는 필수입니다.");
        }

        return concertRepository.findByTitleContaining(keyword);
    }

    /**
     * 좌석 수 범위로 콘서트 조회
     * @param minSeats 최소 좌석 수
     * @param maxSeats 최대 좌석 수
     * @return 조건에 맞는 콘서트 목록
     */
    public List<Concert> getConcertsBySeatRange(int minSeats, int maxSeats) {
        if (minSeats < 0 || maxSeats < 0) {
            throw new IllegalArgumentException("좌석 수는 0 이상이어야 합니다.");
        }
        if (minSeats > maxSeats) {
            throw new IllegalArgumentException("최소 좌석 수는 최대 좌석 수보다 작거나 같아야 합니다.");
        }

        return concertRepository.findByTotalSeatsBetween(minSeats, maxSeats);
    }

    /**
     * 콘서트 예약 현황 확인
     * @param concertId 콘서트 ID
     * @return 예약 현황 정보
     */
    public ConcertBookingStatus getBookingStatus(Long concertId) {
        Concert concert = getConcert(concertId);
        return ConcertBookingStatus.from(concert);
    }

    /**
     * 콘서트 수 조회
     * @return 총 콘서트 수
     */
    public long getConcertCount() {
        return concertRepository.count();
    }
}