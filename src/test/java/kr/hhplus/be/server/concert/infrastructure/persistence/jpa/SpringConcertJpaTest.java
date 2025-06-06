package kr.hhplus.be.server.concert.infrastructure.persistence.jpa;

import kr.hhplus.be.server.concert.infrastructure.persistence.entity.ConcertEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

/**
 * SpringConcertJpa Spring Data JPA 인터페이스 테스트
 * - @DataJpaTest를 사용한 슬라이스 테스트
 * - 실제 H2 인메모리 DB와 연동 테스트
 * - JPA 쿼리 메서드 동작 검증
 */
@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=true"
})
@ActiveProfiles("test")
class SpringConcertJpaTest {

    @Autowired
    private SpringConcertJpa concertJpa;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void 콘서트_저장_및_조회_테스트() {
        // given
        ConcertEntity concert = new ConcertEntity(
            "샤이니 월드 7th",
            "샤이니",
            LocalDateTime.now().plusDays(30),
            100
        );

        // when
        ConcertEntity savedConcert = concertJpa.save(concert);

        // then
        assertThat(savedConcert.getId()).isNotNull();
        assertThat(savedConcert.getTitle()).isEqualTo("샤이니 월드 7th");
        assertThat(savedConcert.getArtist()).isEqualTo("샤이니");
        assertThat(savedConcert.getTotalSeats()).isEqualTo(100);
        assertThat(savedConcert.getReservedSeats()).isEqualTo(0);
    }

    @Test
    void 아티스트별_콘서트_조회_테스트() {
        // given
        ConcertEntity concert1 = new ConcertEntity(
            "샤이니 월드 7th",
            "샤이니",
            LocalDateTime.now().plusDays(30),
            100
        );
        ConcertEntity concert2 = new ConcertEntity(
            "아이유 콘서트",
            "아이유",
            LocalDateTime.now().plusDays(60),
            200
        );
        concertJpa.save(concert1);
        concertJpa.save(concert2);

        // when
        List<ConcertEntity> shineeConcerts = concertJpa.findByArtist("샤이니");
        List<ConcertEntity> iuConcerts = concertJpa.findByArtist("아이유");

        // then
        assertThat(shineeConcerts).hasSize(1);
        assertThat(shineeConcerts.get(0).getTitle()).isEqualTo("샤이니 월드 7th");
        assertThat(iuConcerts).hasSize(1);
        assertThat(iuConcerts.get(0).getTitle()).isEqualTo("아이유 콘서트");
    }

    @Test
    void 예약_가능한_콘서트_조회_테스트() {
        // given
        ConcertEntity availableConcert = new ConcertEntity(
            "샤이니 월드 7th",
            "샤이니",
            LocalDateTime.now().plusDays(30),
            100
        );
        ConcertEntity soldOutConcert = new ConcertEntity(
            "아이유 콘서트",
            "아이유",
            LocalDateTime.now().plusDays(60),
            1
        );
        soldOutConcert.setReservedSeats(1); // 매진 상태
        concertJpa.save(availableConcert);
        concertJpa.save(soldOutConcert);

        // when
        List<ConcertEntity> availableConcerts = concertJpa.findAvailableConcerts();

        // then
        assertThat(availableConcerts).hasSize(1);
        assertThat(availableConcerts.get(0).getTitle()).isEqualTo("샤이니 월드 7th");
    }

    @Test
    void 다가오는_콘서트_조회_테스트() {
        // given
        LocalDateTime now = LocalDateTime.now();
        ConcertEntity pastConcert = new ConcertEntity(
            "과거 콘서트",
            "아티스트1",
            now.minusDays(1),
            100
        );
        ConcertEntity futureConcert = new ConcertEntity(
            "미래 콘서트",
            "아티스트2",
            now.plusDays(30),
            100
        );
        concertJpa.save(pastConcert);
        concertJpa.save(futureConcert);

        // when
        List<ConcertEntity> upcomingConcerts = concertJpa.findUpcomingConcerts(now);

        // then
        assertThat(upcomingConcerts).hasSize(1);
        assertThat(upcomingConcerts.get(0).getTitle()).isEqualTo("미래 콘서트");
    }

    @Test
    void 제목으로_콘서트_검색_테스트() {
        // given
        ConcertEntity concert1 = new ConcertEntity(
            "샤이니 월드 7th",
            "샤이니",
            LocalDateTime.now().plusDays(30),
            100
        );
        ConcertEntity concert2 = new ConcertEntity(
            "아이유 콘서트",
            "아이유",
            LocalDateTime.now().plusDays(60),
            200
        );
        concertJpa.save(concert1);
        concertJpa.save(concert2);

        // when
        List<ConcertEntity> searchResults = concertJpa.findByTitleContaining("콘서트");

        // then
        assertThat(searchResults).hasSize(1);
        assertThat(searchResults.get(0).getTitle()).isEqualTo("아이유 콘서트");
    }

    @Test
    void 날짜_범위별_콘서트_조회_테스트() {
        // given
        LocalDateTime startDate = LocalDateTime.of(2025, 6, 1, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 7, 31, 23, 59, 59);

        ConcertEntity concert1 = new ConcertEntity(
            "6월 콘서트",
            "아티스트A",
            LocalDateTime.of(2025, 6, 15, 19, 0, 0),
            100
        );
        ConcertEntity concert2 = new ConcertEntity(
            "7월 콘서트",
            "아티스트B",
            LocalDateTime.of(2025, 7, 20, 19, 0, 0),
            150
        );
        ConcertEntity concert3 = new ConcertEntity(
            "8월 콘서트",
            "아티스트C",
            LocalDateTime.of(2025, 8, 10, 19, 0, 0),
            200
        );

        entityManager.persist(concert1);
        entityManager.persist(concert2);
        entityManager.persist(concert3);
        entityManager.flush();

        // when
        List<ConcertEntity> result = concertJpa.findByConcertDateBetween(startDate, endDate);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ConcertEntity::getTitle)
            .containsExactlyInAnyOrder("6월 콘서트", "7월 콘서트");
    }

    @Test
    void 좌석_수_범위별_콘서트_조회_테스트() {
        // given
        ConcertEntity smallConcert = new ConcertEntity(
            "소규모 콘서트",
            "아티스트A",
            LocalDateTime.of(2025, 6, 25, 19, 0, 0),
            50
        );
        ConcertEntity mediumConcert = new ConcertEntity(
            "중간 규모 콘서트",
            "아티스트B",
            LocalDateTime.of(2025, 7, 15, 19, 0, 0),
            150
        );
        ConcertEntity largeConcert = new ConcertEntity(
            "대규모 콘서트",
            "아티스트C",
            LocalDateTime.of(2025, 8, 20, 19, 0, 0),
            500
        );

        entityManager.persist(smallConcert);
        entityManager.persist(mediumConcert);
        entityManager.persist(largeConcert);
        entityManager.flush();

        // when
        List<ConcertEntity> result = concertJpa.findByTotalSeatsBetween(100, 300);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("중간 규모 콘서트");
        assertThat(result.get(0).getTotalSeats()).isBetween(100, 300);
    }

    @Test
    void 콘서트_삭제_테스트() {
        // given
        ConcertEntity concert = new ConcertEntity(
            "삭제될 콘서트",
            "아티스트",
            LocalDateTime.of(2025, 6, 25, 19, 0, 0),
            100
        );
        ConcertEntity savedConcert = entityManager.persistAndFlush(concert);
        Long concertId = savedConcert.getId();

        // when
        concertJpa.deleteById(concertId);
        entityManager.flush();

        // then
        assertThat(concertJpa.findById(concertId)).isEmpty();
    }

    @Test
    void 전체_콘서트_수_조회_테스트() {
        // given
        ConcertEntity concert1 = new ConcertEntity(
            "콘서트1",
            "아티스트A",
            LocalDateTime.of(2025, 6, 25, 19, 0, 0),
            100
        );
        ConcertEntity concert2 = new ConcertEntity(
            "콘서트2",
            "아티스트B",
            LocalDateTime.of(2025, 7, 15, 19, 0, 0),
            150
        );

        entityManager.persist(concert1);
        entityManager.persist(concert2);
        entityManager.flush();

        // when
        long count = concertJpa.count();

        // then
        assertThat(count).isEqualTo(2);
    }
}