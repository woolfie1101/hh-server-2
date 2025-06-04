package kr.hhplus.be.server.concert.application.service;

import kr.hhplus.be.server.concert.application.dto.ConcertBookingStatus;
import kr.hhplus.be.server.concert.domain.model.Concert;
import kr.hhplus.be.server.concert.domain.repository.ConcertRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ConcertService 테스트
 * - 레이어드 아키텍처 서비스 계층 테스트
 * - Mock을 사용한 단위 테스트
 * - 비즈니스 로직 및 검증 로직 테스트
 */
@ExtendWith(MockitoExtension.class)
class ConcertServiceTest {

    @Mock
    private ConcertRepository concertRepository;

    @InjectMocks
    private ConcertService concertService;

    @Test
    void 콘서트_조회_성공_테스트() {
        // given
        Long concertId = 1L;
        Concert concert = new Concert(
            "샤이니 월드 7th",
            "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0),
            100
        );
        concert.assignId(concertId);

        when(concertRepository.findById(concertId)).thenReturn(Optional.of(concert));

        // when
        Concert result = concertService.getConcert(concertId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(concertId);
        assertThat(result.getTitle()).isEqualTo("샤이니 월드 7th");
        assertThat(result.getArtist()).isEqualTo("샤이니");
        verify(concertRepository).findById(concertId);
    }

    @Test
    void 콘서트_조회_실패_테스트() {
        // given
        Long concertId = 999L;
        when(concertRepository.findById(concertId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> concertService.getConcert(concertId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("콘서트를 찾을 수 없습니다. ID: " + concertId);

        verify(concertRepository).findById(concertId);
    }

    @Test
    void null_콘서트_ID로_조회시_예외_발생_테스트() {
        // when & then
        assertThatThrownBy(() -> concertService.getConcert(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("콘서트 ID는 필수입니다.");

        verify(concertRepository, never()).findById(any());
    }

    @Test
    void 모든_콘서트_조회_테스트() {
        // given
        Concert concert1 = new Concert(
            "샤이니 월드 7th",
            "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0),
            100
        );
        Concert concert2 = new Concert(
            "아이유 콘서트",
            "아이유",
            LocalDateTime.of(2025, 7, 15, 19, 0, 0),
            150
        );
        List<Concert> concerts = List.of(concert1, concert2);

        when(concertRepository.findAll()).thenReturn(concerts);

        // when
        List<Concert> result = concertService.getAllConcerts();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Concert::getTitle)
            .containsExactlyInAnyOrder("샤이니 월드 7th", "아이유 콘서트");
        verify(concertRepository).findAll();
    }

    @Test
    void 아티스트별_콘서트_조회_테스트() {
        // given
        String artist = "샤이니";
        Concert concert1 = new Concert(
            "샤이니 월드 7th",
            artist,
            LocalDateTime.of(2025, 6, 25, 17, 25, 0),
            100
        );
        Concert concert2 = new Concert(
            "샤이니 콘서트 투어",
            artist,
            LocalDateTime.of(2025, 7, 15, 19, 0, 0),
            150
        );
        List<Concert> concerts = List.of(concert1, concert2);

        when(concertRepository.findByArtist(artist)).thenReturn(concerts);

        // when
        List<Concert> result = concertService.getConcertsByArtist(artist);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Concert::getArtist).containsOnly(artist);
        verify(concertRepository).findByArtist(artist);
    }

    @Test
    void null_아티스트로_조회시_예외_발생_테스트() {
        // when & then
        assertThatThrownBy(() -> concertService.getConcertsByArtist(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("아티스트명은 필수입니다.");

        assertThatThrownBy(() -> concertService.getConcertsByArtist(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("아티스트명은 필수입니다.");

        verify(concertRepository, never()).findByArtist(any());
    }

    @Test
    void 예약_가능한_콘서트_조회_테스트() {
        // given
        Concert availableConcert = new Concert(
            "예약 가능 콘서트",
            "아티스트A",
            LocalDateTime.of(2025, 6, 25, 19, 0, 0),
            100
        );
        availableConcert.reserveSeat(); // 일부 좌석 예약
        List<Concert> concerts = List.of(availableConcert);

        when(concertRepository.findAvailableConcerts()).thenReturn(concerts);

        // when
        List<Concert> result = concertService.getAvailableConcerts();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).hasAvailableSeats()).isTrue();
        verify(concertRepository).findAvailableConcerts();
    }

    @Test
    void 다가오는_콘서트_조회_테스트() {
        // given
        Concert upcomingConcert = new Concert(
            "다가오는 콘서트",
            "아티스트A",
            LocalDateTime.now().plusDays(7),
            100
        );
        List<Concert> concerts = List.of(upcomingConcert);

        when(concertRepository.findUpcomingConcerts(any(LocalDateTime.class))).thenReturn(concerts);

        // when
        List<Concert> result = concertService.getUpcomingConcerts();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("다가오는 콘서트");
        verify(concertRepository).findUpcomingConcerts(any(LocalDateTime.class));
    }

    @Test
    void 날짜_범위별_콘서트_조회_테스트() {
        // given
        LocalDateTime startDate = LocalDateTime.of(2025, 6, 1, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 7, 31, 23, 59, 59);

        Concert concert = new Concert(
            "6월 콘서트",
            "아티스트A",
            LocalDateTime.of(2025, 6, 15, 19, 0, 0),
            100
        );
        List<Concert> concerts = List.of(concert);

        when(concertRepository.findByConcertDateBetween(startDate, endDate)).thenReturn(concerts);

        // when
        List<Concert> result = concertService.getConcertsByDateRange(startDate, endDate);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("6월 콘서트");
        verify(concertRepository).findByConcertDateBetween(startDate, endDate);
    }

    @Test
    void 잘못된_날짜_범위로_조회시_예외_발생_테스트() {
        // given
        LocalDateTime startDate = LocalDateTime.of(2025, 7, 1, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 6, 1, 0, 0, 0); // 시작일이 종료일보다 늦음

        // when & then
        assertThatThrownBy(() -> concertService.getConcertsByDateRange(startDate, endDate))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("시작 날짜는 종료 날짜보다 이전이어야 합니다.");

        assertThatThrownBy(() -> concertService.getConcertsByDateRange(null, endDate))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("시작 날짜와 종료 날짜는 필수입니다.");

        verify(concertRepository, never()).findByConcertDateBetween(any(), any());
    }

    @Test
    void 콘서트_제목_검색_테스트() {
        // given
        String keyword = "샤이니";
        Concert concert = new Concert(
            "샤이니 월드 7th",
            "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0),
            100
        );
        List<Concert> concerts = List.of(concert);

        when(concertRepository.findByTitleContaining(keyword)).thenReturn(concerts);

        // when
        List<Concert> result = concertService.searchConcertsByTitle(keyword);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).contains(keyword);
        verify(concertRepository).findByTitleContaining(keyword);
    }

    @Test
    void null_키워드로_검색시_예외_발생_테스트() {
        // when & then
        assertThatThrownBy(() -> concertService.searchConcertsByTitle(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("검색 키워드는 필수입니다.");

        assertThatThrownBy(() -> concertService.searchConcertsByTitle(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("검색 키워드는 필수입니다.");

        verify(concertRepository, never()).findByTitleContaining(any());
    }

    @Test
    void 좌석_수_범위별_콘서트_조회_테스트() {
        // given
        int minSeats = 100;
        int maxSeats = 300;

        Concert concert = new Concert(
            "중간 규모 콘서트",
            "아티스트A",
            LocalDateTime.of(2025, 6, 25, 19, 0, 0),
            150
        );
        List<Concert> concerts = List.of(concert);

        when(concertRepository.findByTotalSeatsBetween(minSeats, maxSeats)).thenReturn(concerts);

        // when
        List<Concert> result = concertService.getConcertsBySeatRange(minSeats, maxSeats);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTotalSeats()).isBetween(minSeats, maxSeats);
        verify(concertRepository).findByTotalSeatsBetween(minSeats, maxSeats);
    }

    @Test
    void 잘못된_좌석_범위로_조회시_예외_발생_테스트() {
        // when & then
        assertThatThrownBy(() -> concertService.getConcertsBySeatRange(-1, 100))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("좌석 수는 0 이상이어야 합니다.");

        assertThatThrownBy(() -> concertService.getConcertsBySeatRange(200, 100))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("최소 좌석 수는 최대 좌석 수보다 작거나 같아야 합니다.");

        verify(concertRepository, never()).findByTotalSeatsBetween(anyInt(), anyInt());
    }

    @Test
    void 콘서트_예약_현황_조회_테스트() {
        // given
        Long concertId = 1L;
        Concert concert = new Concert(
            "샤이니 월드 7th",
            "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0),
            100
        );
        concert.assignId(concertId);
        concert.reserveSeat(); // 1석 예약

        when(concertRepository.findById(concertId)).thenReturn(Optional.of(concert));

        // when
        ConcertBookingStatus result = concertService.getBookingStatus(concertId);

        // then
        assertThat(result.getConcertId()).isEqualTo(concertId);
        assertThat(result.getTitle()).isEqualTo("샤이니 월드 7th");
        assertThat(result.getTotalSeats()).isEqualTo(100);
        assertThat(result.getReservedSeats()).isEqualTo(1);
        assertThat(result.getAvailableSeats()).isEqualTo(99);
        assertThat(result.isSoldOut()).isFalse();
        assertThat(result.isBookingAvailable()).isTrue();
        verify(concertRepository).findById(concertId);
    }

    @Test
    void 콘서트_수_조회_테스트() {
        // given
        long expectedCount = 50L;
        when(concertRepository.count()).thenReturn(expectedCount);

        // when
        long result = concertService.getConcertCount();

        // then
        assertThat(result).isEqualTo(expectedCount);
        verify(concertRepository).count();
    }
}