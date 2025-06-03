package kr.hhplus.be.server.concert.domain.repository;

import kr.hhplus.be.server.concert.domain.model.Concert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ConcertRepository 인터페이스 테스트
 * - 실제 구현체가 아닌 인터페이스 스펙을 검증
 * - Mock을 사용해서 인터페이스 동작 확인
 */
@ExtendWith(MockitoExtension.class)
class ConcertRepositoryTest {

    @Mock
    private ConcertRepository concertRepository;

    @Test
    void 콘서트_저장_테스트() {
        // given
        Concert concert = new Concert("샤이니 월드 7th", "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0), 100);
        Concert savedConcert = new Concert("샤이니 월드 7th", "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0), 100);
        savedConcert.assignId(1L);

        when(concertRepository.save(concert)).thenReturn(savedConcert);

        // when
        Concert result = concertRepository.save(concert);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("샤이니 월드 7th");
        verify(concertRepository).save(concert);
    }

    @Test
    void ID로_콘서트_조회_성공_테스트() {
        // given
        Long id = 1L;
        Concert concert = new Concert("샤이니 월드 7th", "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0), 100);
        concert.assignId(id);

        when(concertRepository.findById(id)).thenReturn(Optional.of(concert));

        // when
        Optional<Concert> result = concertRepository.findById(id);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getTitle()).isEqualTo("샤이니 월드 7th");
        verify(concertRepository).findById(id);
    }

    @Test
    void 아티스트명으로_콘서트_조회_테스트() {
        // given
        String artist = "샤이니";
        Concert concert1 = new Concert("샤이니 월드 7th", artist,
            LocalDateTime.of(2025, 6, 25, 17, 25, 0), 100);
        Concert concert2 = new Concert("샤이니 온택트 콘서트", artist,
            LocalDateTime.of(2025, 6, 15, 19, 0, 0), 150);
        List<Concert> concerts = List.of(concert1, concert2);

        when(concertRepository.findByArtist(artist)).thenReturn(concerts);

        // when
        List<Concert> result = concertRepository.findByArtist(artist);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Concert::getArtist)
            .containsOnly("샤이니");
        verify(concertRepository).findByArtist(artist);
    }

    @Test
    void 날짜_범위로_콘서트_조회_테스트() {
        // given
        LocalDateTime startDate = LocalDateTime.of(2025, 6, 1, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 6, 30, 23, 59, 59);

        Concert concert = new Concert("샤이니 월드 7th", "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0), 100);
        List<Concert> concerts = List.of(concert);

        when(concertRepository.findByConcertDateBetween(startDate, endDate)).thenReturn(concerts);

        // when
        List<Concert> result = concertRepository.findByConcertDateBetween(startDate, endDate);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getConcertDate()).isBetween(startDate, endDate);
        verify(concertRepository).findByConcertDateBetween(startDate, endDate);
    }

    @Test
    void 예약_가능한_콘서트_조회_테스트() {
        // given
        Concert availableConcert = new Concert("샤이니 월드 7th", "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0), 100);
        List<Concert> concerts = List.of(availableConcert);

        when(concertRepository.findAvailableConcerts()).thenReturn(concerts);

        // when
        List<Concert> result = concertRepository.findAvailableConcerts();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).hasAvailableSeats()).isTrue();
        verify(concertRepository).findAvailableConcerts();
    }

    @Test
    void 다가오는_콘서트_조회_테스트() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Concert upcomingConcert = new Concert("샤이니 월드 7th", "샤이니",
            now.plusDays(30), 100);
        List<Concert> concerts = List.of(upcomingConcert);

        when(concertRepository.findUpcomingConcerts(now)).thenReturn(concerts);

        // when
        List<Concert> result = concertRepository.findUpcomingConcerts(now);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getConcertDate()).isAfter(now);
        verify(concertRepository).findUpcomingConcerts(now);
    }

    @Test
    void 제목으로_콘서트_검색_테스트() {
        // given
        String keyword = "샤이니";
        Concert concert = new Concert("샤이니 월드 7th", "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0), 100);
        List<Concert> concerts = List.of(concert);

        when(concertRepository.findByTitleContaining(keyword)).thenReturn(concerts);

        // when
        List<Concert> result = concertRepository.findByTitleContaining(keyword);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).contains(keyword);
        verify(concertRepository).findByTitleContaining(keyword);
    }

    @Test
    void 좌석_수_범위로_콘서트_조회_테스트() {
        // given
        int minSeats = 50;
        int maxSeats = 200;

        Concert concert = new Concert("샤이니 월드 7th", "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0), 100);
        List<Concert> concerts = List.of(concert);

        when(concertRepository.findByTotalSeatsBetween(minSeats, maxSeats)).thenReturn(concerts);

        // when
        List<Concert> result = concertRepository.findByTotalSeatsBetween(minSeats, maxSeats);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTotalSeats()).isBetween(minSeats, maxSeats);
        verify(concertRepository).findByTotalSeatsBetween(minSeats, maxSeats);
    }

    @Test
    void 콘서트_삭제_테스트() {
        // given
        Long id = 1L;

        // when
        concertRepository.deleteById(id);

        // then
        verify(concertRepository).deleteById(id);
    }

    @Test
    void 모든_콘서트_조회_테스트() {
        // given
        Concert concert1 = new Concert("샤이니 월드 7th", "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0), 100);
        Concert concert2 = new Concert("BTS 콘서트", "BTS",
            LocalDateTime.of(2025, 6, 15, 19, 0, 0), 200);
        List<Concert> concerts = List.of(concert1, concert2);

        when(concertRepository.findAll()).thenReturn(concerts);

        // when
        List<Concert> result = concertRepository.findAll();

        // then
        assertThat(result).hasSize(2);
        verify(concertRepository).findAll();
    }

    @Test
    void 콘서트_수_확인_테스트() {
        // given
        long expectedCount = 10L;

        when(concertRepository.count()).thenReturn(expectedCount);

        // when
        long result = concertRepository.count();

        // then
        assertThat(result).isEqualTo(expectedCount);
        verify(concertRepository).count();
    }
}