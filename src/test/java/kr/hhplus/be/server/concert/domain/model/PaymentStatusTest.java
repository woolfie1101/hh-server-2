package kr.hhplus.be.server.concert.domain.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class PaymentStatusTest {

    @Test
    void 결제_상태_확인_테스트() {
        // when & then
        assertThat(PaymentStatus.PENDING.getDescription()).isEqualTo("결제 대기");
        assertThat(PaymentStatus.COMPLETED.getDescription()).isEqualTo("결제 완료");
        assertThat(PaymentStatus.FAILED.getDescription()).isEqualTo("결제 실패");
        assertThat(PaymentStatus.CANCELLED.getDescription()).isEqualTo("결제 취소");
        assertThat(PaymentStatus.REFUNDED.getDescription()).isEqualTo("결제 환불");
    }

    @Test
    void 최종_상태_확인_테스트() {
        // when & then
        assertThat(PaymentStatus.PENDING.isFinal()).isFalse();
        assertThat(PaymentStatus.COMPLETED.isFinal()).isTrue();
        assertThat(PaymentStatus.FAILED.isFinal()).isTrue();
        assertThat(PaymentStatus.CANCELLED.isFinal()).isTrue();
        assertThat(PaymentStatus.REFUNDED.isFinal()).isTrue();
    }

    @Test
    void 성공_상태_확인_테스트() {
        // when & then
        assertThat(PaymentStatus.PENDING.isSuccessful()).isFalse();
        assertThat(PaymentStatus.COMPLETED.isSuccessful()).isTrue();
        assertThat(PaymentStatus.FAILED.isSuccessful()).isFalse();
        assertThat(PaymentStatus.CANCELLED.isSuccessful()).isFalse();
        assertThat(PaymentStatus.REFUNDED.isSuccessful()).isFalse();
    }
}