package kr.hhplus.be.server.concert.infrastructure.persistence.entity;

import kr.hhplus.be.server.concert.domain.model.Concert;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.*;

/**
 * ConcertEntity JPA 엔티티 테스트
 * - JPA 매핑 및 변환 로직 검증
 * - 도메인 모델과 엔티티 간 변환 테스트
 */
class ConcertEntityTest {

    @Test
    void 엔티티_생성_테스트() {
        // given
        String title = "샤이니 월드 7th";
        String artist = "샤이니";
        LocalDateTime concertDate = LocalDateTime.of(2025, 6, 25, 17, 25, 0);
        int totalSeats = 100;

        // when
        ConcertEntity entity = new ConcertEntity(title, artist, concertDate, totalSeats);

        // then
        assertThat(entity.getTitle()).isEqualTo(title);
        assertThat(entity.getArtist()).isEqualTo(artist);
        assertThat(entity.getConcertDate()).isEqualTo(concertDate);
        assertThat(entity.getTotalSeats()).isEqualTo(totalSeats);
        assertThat(entity.getReservedSeats()).isEqualTo(0); // 초기값 확인
        assertThat(entity.getId()).isNull(); // 아직 저장 전이므로 ID 없음
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
    }

    @Test
    void 도메인_모델로_변환_테스트() {
        // given
        ConcertEntity entity = new ConcertEntity(
            "샤이니 월드 7th",
            "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0),
            100
        );
        entity.setId(1L); // Repository에서 저장 후 ID 할당 시뮬레이션

        // when
        Concert concert = entity.toDomain();

        // then
        assertThat(concert.getId()).isEqualTo(1L);
        assertThat(concert.getTitle()).isEqualTo("샤이니 월드 7th");
        assertThat(concert.getArtist()).isEqualTo("샤이니");
        assertThat(concert.getConcertDate()).isEqualTo(LocalDateTime.of(2025, 6, 25, 17, 25, 0));
        assertThat(concert.getTotalSeats()).isEqualTo(100);
        assertThat(concert.getReservedSeats()).isEqualTo(0); // 초기값 확인
    }

    @Test
    void 도메인_모델에서_엔티티_생성_테스트() {
        // given
        Concert concert = new Concert(
            "샤이니 월드 7th",
            "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0),
            100
        );
        concert.assignId(1L);

        // when
        ConcertEntity entity = ConcertEntity.fromDomain(concert);

        // then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getTitle()).isEqualTo("샤이니 월드 7th");
        assertThat(entity.getArtist()).isEqualTo("샤이니");
        assertThat(entity.getConcertDate()).isEqualTo(LocalDateTime.of(2025, 6, 25, 17, 25, 0));
        assertThat(entity.getTotalSeats()).isEqualTo(100);
        assertThat(entity.getReservedSeats()).isEqualTo(0);
    }

    @Test
    void 도메인_모델_업데이트_테스트() {
        // given
        ConcertEntity entity = new ConcertEntity(
            "샤이니 월드 7th",
            "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0),
            100
        );
        entity.setId(1L);

        Concert updatedConcert = new Concert(
            "샤이니 월드 7th",
            "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0),
            100
        );
        updatedConcert.assignId(1L);
        updatedConcert.reserveSeat(); // 좌석 예약 (reservedSeats = 1)

        // when
        entity.updateFromDomain(updatedConcert);

        // then
        assertThat(entity.getReservedSeats()).isEqualTo(1);
        assertThat(entity.getUpdatedAt()).isNotNull();
    }

    @Test
    void 엔티티_동등성_비교_테스트() {
        // given
        ConcertEntity entity1 = new ConcertEntity(
            "샤이니 월드 7th",
            "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0),
            100
        );
        entity1.setId(1L);

        ConcertEntity entity2 = new ConcertEntity(
            "아이유 콘서트",
            "아이유",
            LocalDateTime.of(2025, 7, 15, 19, 0, 0),
            200
        );
        entity2.setId(1L);

        ConcertEntity entity3 = new ConcertEntity(
            "BTS 월드투어",
            "BTS",
            LocalDateTime.of(2025, 8, 20, 18, 0, 0),
            500
        );
        entity3.setId(2L);

        // when & then
        assertThat(entity1).isEqualTo(entity2); // 같은 ID면 동등
        assertThat(entity1).isNotEqualTo(entity3); // 다른 ID면 다름
        assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
    }

    @Test
    void 기본_생성자_테스트() {
        // when
        ConcertEntity entity = new ConcertEntity();

        // then
        assertThat(entity.getId()).isNull();
        assertThat(entity.getTitle()).isNull();
        assertThat(entity.getArtist()).isNull();
        assertThat(entity.getConcertDate()).isNull();
        assertThat(entity.getTotalSeats()).isEqualTo(0);
        assertThat(entity.getReservedSeats()).isEqualTo(0);
    }

    @Test
    void toString_테스트() {
        // given
        ConcertEntity entity = new ConcertEntity(
            "샤이니 월드 7th",
            "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0),
            100
        );
        entity.setId(1L);

        // when
        String result = entity.toString();

        // then
        assertThat(result).contains("ConcertEntity");
        assertThat(result).contains("id=1");
        assertThat(result).contains("title='샤이니 월드 7th'");
        assertThat(result).contains("artist='샤이니'");
        assertThat(result).contains("totalSeats=100");
        assertThat(result).contains("reservedSeats=0");
    }
}