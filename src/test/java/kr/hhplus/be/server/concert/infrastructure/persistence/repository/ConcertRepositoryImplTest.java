package kr.hhplus.be.server.concert.infrastructure.persistence.repository;

import kr.hhplus.be.server.concert.domain.model.Concert;
import kr.hhplus.be.server.concert.infrastructure.persistence.entity.ConcertEntity;
import kr.hhplus.be.server.concert.infrastructure.persistence.jpa.SpringConcertJpa;
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
 * ConcertRepositoryImpl 구현체 테스트
 * - 도메인 모델과 JPA 엔티티 간 변환 테스트
 * - Spring Data JPA와의 연동 테스트
 * - Mock을 사용한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class ConcertRepositoryImplTest {

    @Mock
    private SpringConcertJpa springConcertJpa;

    @InjectMocks
    private ConcertRepositoryImpl concertRepositoryImpl;

    @Test
    void 콘서트_저장_테스트() {
        // given
        Concert concert = new Concert(
            "샤이니 월드 7th",
            "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0),
            100
        );

        ConcertEntity savedEntity = new ConcertEntity(
            "샤이니 월드 7th",
            "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0),
            100
        );
        savedEntity.setId(1L);

        when(springConcertJpa.save(any(ConcertEntity.class))).thenReturn(savedEntity);

        // when
        Concert result = concertRepositoryImpl.save(concert);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("샤이니 월드 7th");
        assertThat(result.getArtist()).isEqualTo("샤이니");
        verify(springConcertJpa).save(any(ConcertEntity.class));
    }

    @Test
    void ID로_콘서트_조회_성공_테스트() {
        // given
        Long id = 1L;
        ConcertEntity entity = new ConcertEntity(
            "샤이니 월드 7th",
            "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0),
            100
        );
        entity.setId(id);

        when(springConcertJpa.findById(id)).thenReturn(Optional.of(entity));

        // when
        Optional<Concert> result = concertRepositoryImpl.findById(id);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getTitle()).isEqualTo("샤이니 월드 7th");
        verify(springConcertJpa).findById(id);
    }

    @Test
    void ID로_콘서트_조회_실패_테스트() {
        // given
        Long id = 999L;
        when(springConcertJpa.findById(id)).thenReturn(Optional.empty());

        // when
        Optional<Concert> result = concertRepositoryImpl.findById(id);

        // then
        assertThat(result).isEmpty();
        verify(springConcertJpa).findById(id);
    }

    @Test
    void 아티스트별_콘서트_조회_테스트() {
        // given
        String artist = "샤이니";
        ConcertEntity entity1 = new ConcertEntity(
            "샤이니 월드 7th",
            artist,
            LocalDateTime.of(2025, 6, 25, 17, 25, 0),
            100
        );
        ConcertEntity entity2 = new ConcertEntity(
            "샤이니 콘서트 투어",
            artist,
            LocalDateTime.of(2025, 7, 15, 19, 0, 0),
            150
        );
        entity1.setId(1L);
        entity2.setId(2L);

        when(springConcertJpa.findByArtist(artist)).thenReturn(List.of(entity1, entity2));

        // when
        List<Concert> result = concertRepositoryImpl.findByArtist(artist);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Concert::getArtist).containsOnly(artist);
        assertThat(result).extracting(Concert::getTitle)
            .containsExactlyInAnyOrder("샤이니 월드 7th", "샤이니 콘서트 투어");
        verify(springConcertJpa).findByArtist(artist);
    }

    @Test
    void 날짜_범위별_콘서트_조회_테스트() {
        // given
        LocalDateTime startDate = LocalDateTime.of(2025, 6, 1, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 7, 31, 23, 59, 59);

        ConcertEntity entity1 = new ConcertEntity(
            "6월 콘서트",
            "아티스트A",
            LocalDateTime.of(2025, 6, 15, 19, 0, 0),
            100
        );
        ConcertEntity entity2 = new ConcertEntity(
            "7월 콘서트",
            "아티스트B",
            LocalDateTime.of(2025, 7, 20, 19, 0, 0),
            150
        );
        entity1.setId(1L);
        entity2.setId(2L);

        when(springConcertJpa.findByConcertDateBetween(startDate, endDate))
            .thenReturn(List.of(entity1, entity2));

        // when
        List<Concert> result = concertRepositoryImpl.findByConcertDateBetween(startDate, endDate);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Concert::getTitle)
            .containsExactlyInAnyOrder("6월 콘서트", "7월 콘서트");
        verify(springConcertJpa).findByConcertDateBetween(startDate, endDate);
    }

    @Test
    void 예약_가능한_콘서트_조회_테스트() {
        // given
        ConcertEntity availableEntity = new ConcertEntity(
            "예약 가능 콘서트",
            "아티스트A",
            LocalDateTime.of(2025, 6, 25, 19, 0, 0),
            100
        );
        availableEntity.setId(1L);
        availableEntity.setReservedSeats(50);

        when(springConcertJpa.findAvailableConcerts()).thenReturn(List.of(availableEntity));

        // when
        List<Concert> result = concertRepositoryImpl.findAvailableConcerts();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("예약 가능 콘서트");
        assertThat(result.get(0).hasAvailableSeats()).isTrue();
        verify(springConcertJpa).findAvailableConcerts();
    }

    @Test
    void 다가오는_콘서트_조회_테스트() {
        // given
        LocalDateTime currentTime = LocalDateTime.now();
        ConcertEntity upcomingEntity = new ConcertEntity(
            "다가오는 콘서트",
            "아티스트A",
            currentTime.plusDays(7),
            100
        );
        upcomingEntity.setId(1L);

        when(springConcertJpa.findUpcomingConcerts(currentTime)).thenReturn(List.of(upcomingEntity));

        // when
        List<Concert> result = concertRepositoryImpl.findUpcomingConcerts(currentTime);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("다가오는 콘서트");
        assertThat(result.get(0).getConcertDate()).isAfter(currentTime);
        verify(springConcertJpa).findUpcomingConcerts(currentTime);
    }

    @Test
    void 제목_검색_콘서트_조회_테스트() {
        // given
        String keyword = "샤이니";
        ConcertEntity entity = new ConcertEntity(
            "샤이니 월드 7th",
            "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0),
            100
        );
        entity.setId(1L);

        when(springConcertJpa.findByTitleContaining(keyword)).thenReturn(List.of(entity));

        // when
        List<Concert> result = concertRepositoryImpl.findByTitleContaining(keyword);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).contains(keyword);
        verify(springConcertJpa).findByTitleContaining(keyword);
    }

    @Test
    void 좌석_수_범위별_콘서트_조회_테스트() {
        // given
        int minSeats = 100;
        int maxSeats = 300;

        ConcertEntity entity = new ConcertEntity(
            "중간 규모 콘서트",
            "아티스트A",
            LocalDateTime.of(2025, 6, 25, 19, 0, 0),
            150
        );
        entity.setId(1L);

        when(springConcertJpa.findByTotalSeatsBetween(minSeats, maxSeats))
            .thenReturn(List.of(entity));

        // when
        List<Concert> result = concertRepositoryImpl.findByTotalSeatsBetween(minSeats, maxSeats);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTotalSeats()).isBetween(minSeats, maxSeats);
        verify(springConcertJpa).findByTotalSeatsBetween(minSeats, maxSeats);
    }

    @Test
    void 콘서트_삭제_테스트() {
        // given
        Long id = 1L;

        // when
        concertRepositoryImpl.deleteById(id);

        // then
        verify(springConcertJpa).deleteById(id);
    }

    @Test
    void 모든_콘서트_조회_테스트() {
        // given
        ConcertEntity entity1 = new ConcertEntity(
            "콘서트1",
            "아티스트A",
            LocalDateTime.of(2025, 6, 25, 19, 0, 0),
            100
        );
        ConcertEntity entity2 = new ConcertEntity(
            "콘서트2",
            "아티스트B",
            LocalDateTime.of(2025, 7, 15, 19, 0, 0),
            150
        );
        entity1.setId(1L);
        entity2.setId(2L);

        when(springConcertJpa.findAll()).thenReturn(List.of(entity1, entity2));

        // when
        List<Concert> result = concertRepositoryImpl.findAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Concert::getTitle)
            .containsExactlyInAnyOrder("콘서트1", "콘서트2");
        verify(springConcertJpa).findAll();
    }

    @Test
    void 콘서트_수_조회_테스트() {
        // given
        long count = 50L;
        when(springConcertJpa.count()).thenReturn(count);

        // when
        long result = concertRepositoryImpl.count();

        // then
        assertThat(result).isEqualTo(count);
        verify(springConcertJpa).count();
    }
}