package kr.hhplus.be.server.concert.domain.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class ReservationStatusTest {

    @Test
    void 예약_상태_확인_테스트() {
        // when & then
        assertThat(ReservationStatus.TEMPORARY.getDescription()).isEqualTo("임시 예약");
        assertThat(ReservationStatus.CONFIRMED.getDescription()).isEqualTo("예약 확정");
        assertThat(ReservationStatus.CANCELLED.getDescription()).isEqualTo("예약 취소");
        assertThat(ReservationStatus.EXPIRED.getDescription()).isEqualTo("예약 만료");
    }

    @Test
    void 수정_가능_상태_확인_테스트() {
        // when & then
        assertThat(ReservationStatus.TEMPORARY.isModifiable()).isTrue();
        assertThat(ReservationStatus.CONFIRMED.isModifiable()).isFalse();
        assertThat(ReservationStatus.CANCELLED.isModifiable()).isFalse();
        assertThat(ReservationStatus.EXPIRED.isModifiable()).isFalse();
    }
}