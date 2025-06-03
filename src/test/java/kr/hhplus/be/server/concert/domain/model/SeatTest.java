package kr.hhplus.be.server.concert.domain.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.*;

class SeatTest {

    @Test
    void 좌석_생성_테스트() {
        // given
        Long concertId = 1L;
        String seatNumber = "A-15";
        BigDecimal price = BigDecimal.valueOf(150000);

        // when
        Seat seat = new Seat(concertId, seatNumber, price);

        // then
        assertThat(seat.getConcertId()).isEqualTo(concertId);
        assertThat(seat.getSeatNumber()).isEqualTo(seatNumber);
        assertThat(seat.getPrice()).isEqualTo(price);
        assertThat(seat.isAvailable()).isTrue(); // 초기 상태는 예약 가능
        assertThat(seat.getReservedBy()).isNull();
        assertThat(seat.getReservedAt()).isNull();
    }

    @Test
    void 좌석_예약_성공_테스트() {
        // given
        Seat seat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));
        String userId = "user-123";

        // when
        boolean result = seat.reserve(userId);

        // then
        assertThat(result).isTrue();
        assertThat(seat.isAvailable()).isFalse();
        assertThat(seat.getReservedBy()).isEqualTo(userId);
        assertThat(seat.getReservedAt()).isNotNull();
        assertThat(seat.getReservedAt()).isBefore(LocalDateTime.now().plusSeconds(1));
    }

    @Test
    void 이미_예약된_좌석_예약_실패_테스트() {
        // given
        Seat seat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));
        seat.reserve("user-123"); // 먼저 예약

        // when
        boolean result = seat.reserve("user-456"); // 다른 사용자가 예약 시도

        // then
        assertThat(result).isFalse();
        assertThat(seat.getReservedBy()).isEqualTo("user-123"); // 기존 예약 유지
    }

    @Test
    void 좌석_예약_취소_성공_테스트() {
        // given
        Seat seat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));
        String userId = "user-123";
        seat.reserve(userId);

        // when
        boolean result = seat.cancelReservation(userId);

        // then
        assertThat(result).isTrue();
        assertThat(seat.isAvailable()).isTrue();
        assertThat(seat.getReservedBy()).isNull();
        assertThat(seat.getReservedAt()).isNull();
    }

    @Test
    void 다른_사용자_좌석_취소_실패_테스트() {
        // given
        Seat seat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));
        seat.reserve("user-123");

        // when
        boolean result = seat.cancelReservation("user-456"); // 다른 사용자가 취소 시도

        // then
        assertThat(result).isFalse();
        assertThat(seat.getReservedBy()).isEqualTo("user-123"); // 기존 예약 유지
    }

    @Test
    void 예약되지_않은_좌석_취소_실패_테스트() {
        // given
        Seat seat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));

        // when
        boolean result = seat.cancelReservation("user-123");

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 특정_사용자_예약_여부_확인_테스트() {
        // given
        Seat seat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));
        String userId = "user-123";

        // when & then
        assertThat(seat.isReservedBy(userId)).isFalse();

        seat.reserve(userId);
        assertThat(seat.isReservedBy(userId)).isTrue();
        assertThat(seat.isReservedBy("user-456")).isFalse();
    }

    @Test
    void 예약_만료_시간_확인_테스트() {
        // given
        Seat seat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));
        seat.reserve("user-123");

        // when
        LocalDateTime expirationTime = seat.getReservationExpirationTime();

        // then
        assertThat(expirationTime).isNotNull();
        assertThat(expirationTime).isAfter(LocalDateTime.now());
        assertThat(expirationTime).isBefore(LocalDateTime.now().plusMinutes(6)); // 5분 + 여유시간
    }

    @Test
    void 예약_만료_여부_확인_테스트() {
        // given
        Seat seat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));
        seat.reserve("user-123");

        // when & then
        assertThat(seat.isReservationExpired()).isFalse(); // 방금 예약했으므로 만료 안됨
    }

    @Test
    void 유효하지_않은_값으로_좌석_생성시_예외_발생_테스트() {
        // when & then
        assertThatThrownBy(() -> new Seat(null, "A-15", BigDecimal.valueOf(150000)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("콘서트 ID는 필수입니다.");

        assertThatThrownBy(() -> new Seat(1L, "", BigDecimal.valueOf(150000)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("좌석 번호는 필수입니다.");

        assertThatThrownBy(() -> new Seat(1L, "A-15", BigDecimal.valueOf(-1000)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("좌석 가격은 0 이상이어야 합니다.");

        assertThatThrownBy(() -> new Seat(1L, "A-15", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("좌석 가격은 0 이상이어야 합니다.");
    }

    @Test
    void null_사용자로_예약_시_예외_발생_테스트() {
        // given
        Seat seat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));

        // when & then
        assertThatThrownBy(() -> seat.reserve(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("사용자 ID는 필수입니다.");

        assertThatThrownBy(() -> seat.reserve(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("사용자 ID는 필수입니다.");
    }
}