package kr.hhplus.be.server.concert.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.*;

class ConcertTest {

    @Test
    void 콘서트_생성_테스트() {
        // given
        String title = "샤이니 월드 7th";
        String artist = "샤이니";
        LocalDateTime concertDate = LocalDateTime.of(2025, 6, 25, 17, 25, 0);
        int totalSeats = 100;

        // when
        Concert concert = new Concert(title, artist, concertDate, totalSeats);

        // then
        assertThat(concert.getTitle()).isEqualTo(title);
        assertThat(concert.getArtist()).isEqualTo(artist);
        assertThat(concert.getConcertDate()).isEqualTo(concertDate);
        assertThat(concert.getTotalSeats()).isEqualTo(totalSeats);
        assertThat(concert.getAvailableSeats()).isEqualTo(totalSeats); // 처음엔 모든 좌석 이용 가능
    }

    @Test
    void 좌석_예약_테스트() {
        // given
        Concert concert = new Concert("샤이니 월드 7th", "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0), 100);

        // when
        boolean result = concert.reserveSeat();

        // then
        assertThat(result).isTrue();
        assertThat(concert.getAvailableSeats()).isEqualTo(99);
    }

    @Test
    void 모든_좌석_예약_완료시_예약_실패_테스트() {
        // given
        Concert concert = new Concert("샤이니 월드 7th", "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0), 1);

        // 1개 좌석 예약
        concert.reserveSeat();

        // when
        boolean result = concert.reserveSeat(); // 더 이상 예약 불가

        // then
        assertThat(result).isFalse();
        assertThat(concert.getAvailableSeats()).isEqualTo(0);
    }

    @Test
    void 좌석_예약_취소_테스트() {
        // given
        Concert concert = new Concert("샤이니 월드 7th", "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0), 100);
        concert.reserveSeat(); // 1개 예약

        // when
        concert.cancelReservation();

        // then
        assertThat(concert.getAvailableSeats()).isEqualTo(100);
    }

    @Test
    void 예약_가능_여부_확인_테스트() {
        // given
        Concert concert = new Concert("샤이니 월드 7th", "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0), 2);

        // when & then
        assertThat(concert.hasAvailableSeats()).isTrue();

        concert.reserveSeat();
        assertThat(concert.hasAvailableSeats()).isTrue();

        concert.reserveSeat(); // 마지막 좌석
        assertThat(concert.hasAvailableSeats()).isFalse();
    }

    @Test
    void 과거_날짜로_콘서트_생성시_예외_발생_테스트() {
        // given
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);

        // when & then
        assertThatThrownBy(() -> new Concert("샤이니 월드 7th", "샤이니", pastDate, 100))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("콘서트 날짜는 현재 시간 이후여야 합니다.");
    }

    @Test
    void 유효하지_않은_값으로_콘서트_생성시_예외_발생_테스트() {
        // given
        LocalDateTime futureDate = LocalDateTime.now().plusDays(30);

        // when & then
        assertThatThrownBy(() -> new Concert("", "샤이니", futureDate, 100))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("콘서트 제목은 필수입니다.");

        assertThatThrownBy(() -> new Concert("샤이니 월드 7th", "", futureDate, 100))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("아티스트명은 필수입니다.");

        assertThatThrownBy(() -> new Concert("샤이니 월드 7th", "샤이니", futureDate, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("총 좌석 수는 1 이상이어야 합니다.");
    }

    @Test
    void 과도한_예약_취소시_예외_발생_테스트() {
        // given
        Concert concert = new Concert("샤이니 월드 7th", "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0), 100);

        // when & then
        assertThatThrownBy(() -> concert.cancelReservation())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("취소할 예약이 없습니다.");
    }
}