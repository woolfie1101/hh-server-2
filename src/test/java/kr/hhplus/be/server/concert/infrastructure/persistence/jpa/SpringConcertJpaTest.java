package kr.hhplus.be.server.concert.infrastructure.persistence.jpa;

import kr.hhplus.be.server.concert.infrastructure.persistence.entity.ConcertEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

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
class SpringConcertJpaTest {

    @Autowired
    private SpringConcertJpa springConcertJpa;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void 아티스트별_콘서트_조회_테스트() {
        // given
        ConcertEntity concert1 = new ConcertEntity(
            "샤이니 월드 7th",
            "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0),
            100
        );
        ConcertEntity concert2 = new ConcertEntity(
            "샤이니 콘서트 투어",
            "샤이니",
            LocalDateTime.of(2025, 7, 15, 19, 0, 0),
            150
        );
        ConcertEntity concert3 = new ConcertEntity(
            "아이유 콘서트",
            "아이유",
            LocalDateTime.of(2025, 8, 20, 18, 0, 0),
            200
        );

        entityManager.persist(concert1);
        entityManager.persist(concert2);
        entityManager.persist(concert3);
        entityManager.flush();

        // when
        List<ConcertEntity> result = springConcertJpa.findByArtist("샤이니");

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ConcertEntity::getArtist)
            .containsOnly("샤이니");
        assertThat(result).extracting(ConcertEntity::getTitle)
            .containsExactlyInAnyOrder("샤이니 월드 7th", "샤이니 콘서트 투어");
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
        List<ConcertEntity> result = springConcertJpa.findByConcertDateBetween(startDate, endDate);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ConcertEntity::getTitle)
            .containsExactlyInAnyOrder("6월 콘서트", "7월 콘서트");
    }

    @Test
    void 예약_가능한_콘서트_조회_테스트() {
        // given
        ConcertEntity availableConcert = new ConcertEntity(
            "예약 가능 콘서트",
            "아티스트A",
            LocalDateTime.of(2025, 6, 25, 19, 0, 0),
            100
        );
        availableConcert.setReservedSeats(50); // 50석 예약됨

        ConcertEntity soldOutConcert = new ConcertEntity(
            "매진 콘서트",
            "아티스트B",
            LocalDateTime.of(2025, 7, 15, 19, 0, 0),
            100
        );
        soldOutConcert.setReservedSeats(100); // 전석 예약됨

        entityManager.persist(availableConcert);
        entityManager.persist(soldOutConcert);
        entityManager.flush();

        // when
        List<ConcertEntity> result = springConcertJpa.findAvailableConcerts();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("예약 가능 콘서트");
        assertThat(result.get(0).getTotalSeats()).isGreaterThan(result.get(0).getReservedSeats());
    }

    @Test
    void 다가오는_콘서트_조회_테스트() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime currentTime = now.minusHours(1); // 1시간 전을 기준으로 설정

        ConcertEntity pastConcert = new ConcertEntity(
            "지난 콘서트",
            "아티스트A",
            now.minusDays(1), // 1일 전
            100
        );
        ConcertEntity upcomingConcert1 = new ConcertEntity(
            "다가오는 콘서트1",
            "아티스트B",
            now.plusDays(1), // 1일 후
            150
        );
        ConcertEntity upcomingConcert2 = new ConcertEntity(
            "다가오는 콘서트2",
            "아티스트C",
            now.plusDays(7), // 7일 후
            200
        );

        entityManager.persist(pastConcert);
        entityManager.persist(upcomingConcert1);
        entityManager.persist(upcomingConcert2);
        entityManager.flush();

        // when
        List<ConcertEntity> result = springConcertJpa.findUpcomingConcerts(currentTime);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ConcertEntity::getTitle)
            .containsExactlyInAnyOrder("다가오는 콘서트1", "다가오는 콘서트2");

        // 날짜순으로 정렬되는지 확인
        assertThat(result.get(0).getConcertDate()).isBefore(result.get(1).getConcertDate());
    }

    @Test
    void 제목_검색_콘서트_조회_테스트() {
        // given
        ConcertEntity concert1 = new ConcertEntity(
            "샤이니 월드 7th",
            "샤이니",
            LocalDateTime.of(2025, 6, 25, 19, 0, 0),
            100
        );
        ConcertEntity concert2 = new ConcertEntity(
            "샤이니 콘서트 투어",
            "샤이니",
            LocalDateTime.of(2025, 7, 15, 19, 0, 0),
            150
        );
        ConcertEntity concert3 = new ConcertEntity(
            "아이유 콘서트",
            "아이유",
            LocalDateTime.of(2025, 8, 20, 19, 0, 0),
            200
        );

        entityManager.persist(concert1);
        entityManager.persist(concert2);
        entityManager.persist(concert3);
        entityManager.flush();

        // when
        List<ConcertEntity> result = springConcertJpa.findByTitleContaining("샤이니");

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ConcertEntity::getTitle)
            .allMatch(title -> title.contains("샤이니"));
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
        List<ConcertEntity> result = springConcertJpa.findByTotalSeatsBetween(100, 300);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("중간 규모 콘서트");
        assertThat(result.get(0).getTotalSeats()).isBetween(100, 300);
    }

    @Test
    void 콘서트_저장_테스트() {
        // given
        ConcertEntity concert = new ConcertEntity(
            "샤이니 월드 7th",
            "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0),
            100
        );

        // when
        ConcertEntity savedConcert = springConcertJpa.save(concert);

        // then
        assertThat(savedConcert.getId()).isNotNull();
        assertThat(savedConcert.getTitle()).isEqualTo("샤이니 월드 7th");
        assertThat(savedConcert.getArtist()).isEqualTo("샤이니");
        assertThat(savedConcert.getTotalSeats()).isEqualTo(100);
        assertThat(savedConcert.getReservedSeats()).isEqualTo(0);
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
        springConcertJpa.deleteById(concertId);
        entityManager.flush();

        // then
        assertThat(springConcertJpa.findById(concertId)).isEmpty();
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
        long count = springConcertJpa.count();

        // then
        assertThat(count).isEqualTo(2);
    }
}