package kr.hhplus.be.server.concert.domain.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.*;

class PaymentTest {

    @Test
    void 결제_생성_테스트() {
        // given
        String userId = "user-123";
        Long reservationId = 1L;
        BigDecimal amount = BigDecimal.valueOf(150000);
        String paymentMethod = "CREDIT_CARD";

        // when
        Payment payment = new Payment(userId, reservationId, amount, paymentMethod);

        // then
        assertThat(payment.getUserId()).isEqualTo(userId);
        assertThat(payment.getReservationId()).isEqualTo(reservationId);
        assertThat(payment.getAmount()).isEqualTo(amount);
        assertThat(payment.getPaymentMethod()).isEqualTo(paymentMethod);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getCreatedAt()).isNotNull();
        assertThat(payment.getCompletedAt()).isNull();
        assertThat(payment.getFailureReason()).isNull();
    }

    @Test
    void 결제_성공_처리_테스트() {
        // given
        Payment payment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        String transactionId = "TXN-12345";

        // when
        boolean result = payment.complete(transactionId);

        // then
        assertThat(result).isTrue();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(payment.getTransactionId()).isEqualTo(transactionId);
        assertThat(payment.getCompletedAt()).isNotNull();
        assertThat(payment.getCompletedAt()).isBefore(LocalDateTime.now().plusSeconds(1));
    }

    @Test
    void 이미_완료된_결제_재처리_실패_테스트() {
        // given
        Payment payment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        payment.complete("TXN-12345"); // 먼저 완료

        // when
        boolean result = payment.complete("TXN-67890"); // 재완료 시도

        // then
        assertThat(result).isFalse();
        assertThat(payment.getTransactionId()).isEqualTo("TXN-12345"); // 기존 거래 ID 유지
    }

    @Test
    void 결제_실패_처리_테스트() {
        // given
        Payment payment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        String failureReason = "잔액 부족";

        // when
        boolean result = payment.fail(failureReason);

        // then
        assertThat(result).isTrue();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(payment.getFailureReason()).isEqualTo(failureReason);
        assertThat(payment.getCompletedAt()).isNull(); // 완료 시간은 여전히 null
    }

    @Test
    void 이미_완료된_결제_실패_처리_불가_테스트() {
        // given
        Payment payment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        payment.complete("TXN-12345"); // 먼저 완료

        // when
        boolean result = payment.fail("네트워크 오류"); // 실패 처리 시도

        // then
        assertThat(result).isFalse();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED); // 상태 변화 없음
    }

    @Test
    void 결제_취소_처리_테스트() {
        // given
        Payment payment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");

        // when
        boolean result = payment.cancel();

        // then
        assertThat(result).isTrue();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
    }

    @Test
    void 완료된_결제_취소_실패_테스트() {
        // given
        Payment payment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        payment.complete("TXN-12345"); // 먼저 완료

        // when
        boolean result = payment.cancel(); // 취소 시도

        // then
        assertThat(result).isFalse();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED); // 상태 변화 없음
    }

    @Test
    void 결제_환불_처리_테스트() {
        // given
        Payment payment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        payment.complete("TXN-12345"); // 먼저 완료
        String refundReason = "사용자 요청";

        // when
        boolean result = payment.refund(refundReason);

        // then
        assertThat(result).isTrue();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
        assertThat(payment.getRefundReason()).isEqualTo(refundReason);
        assertThat(payment.getRefundedAt()).isNotNull();
    }

    @Test
    void 미완료_결제_환불_실패_테스트() {
        // given
        Payment payment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");

        // when
        boolean result = payment.refund("사용자 요청"); // 미완료 상태에서 환불 시도

        // then
        assertThat(result).isFalse();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING); // 상태 변화 없음
    }

    @Test
    void 결제_성공_여부_확인_테스트() {
        // given
        Payment pendingPayment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        Payment completedPayment = new Payment("user-456", 2L, BigDecimal.valueOf(120000), "BANK_TRANSFER");
        completedPayment.complete("TXN-67890");

        // when & then
        assertThat(pendingPayment.isSuccessful()).isFalse();
        assertThat(completedPayment.isSuccessful()).isTrue();
    }

    @Test
    void 결제_처리_가능_여부_확인_테스트() {
        // given
        Payment payment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");

        // when & then
        assertThat(payment.canBeProcessed()).isTrue(); // PENDING 상태는 처리 가능

        payment.complete("TXN-12345");
        assertThat(payment.canBeProcessed()).isFalse(); // COMPLETED 상태는 처리 불가

        Payment failedPayment = new Payment("user-456", 2L, BigDecimal.valueOf(120000), "BANK_TRANSFER");
        failedPayment.fail("네트워크 오류");
        assertThat(failedPayment.canBeProcessed()).isFalse(); // FAILED 상태는 처리 불가
    }

    @Test
    void 유효하지_않은_값으로_결제_생성시_예외_발생_테스트() {
        // when & then
        assertThatThrownBy(() -> new Payment(null, 1L, BigDecimal.valueOf(150000), "CREDIT_CARD"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("사용자 ID는 필수입니다.");

        assertThatThrownBy(() -> new Payment("", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("사용자 ID는 필수입니다.");

        assertThatThrownBy(() -> new Payment("user-123", null, BigDecimal.valueOf(150000), "CREDIT_CARD"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("예약 ID는 필수입니다.");

        assertThatThrownBy(() -> new Payment("user-123", 1L, BigDecimal.valueOf(-1000), "CREDIT_CARD"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("결제 금액은 0보다 커야 합니다.");

        assertThatThrownBy(() -> new Payment("user-123", 1L, BigDecimal.valueOf(150000), ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("결제 방법은 필수입니다.");
    }

    @Test
    void null_값으로_처리시_예외_발생_테스트() {
        // given
        Payment payment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");

        // when & then
        assertThatThrownBy(() -> payment.complete(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("거래 ID는 필수입니다.");

        assertThatThrownBy(() -> payment.complete(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("거래 ID는 필수입니다.");

        assertThatThrownBy(() -> payment.fail(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("실패 사유는 필수입니다.");

        assertThatThrownBy(() -> payment.refund(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("환불 사유는 필수입니다.");
    }

    @Test
    void 결제_처리_시간_확인_테스트() {
        // given
        Payment payment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");

        // when & then
        assertThat(payment.getProcessingTimeInSeconds()).isGreaterThanOrEqualTo(0);

        payment.complete("TXN-12345");
        assertThat(payment.getProcessingTimeInSeconds()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void 결제_정보_유효성_검증_테스트() {
        // given
        Payment validPayment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");

        // when & then
        assertThat(validPayment.isValid()).isTrue();
    }

    @Test
    void 재시도_가능_여부_확인_테스트() {
        // given
        Payment payment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");

        // when & then
        assertThat(payment.canRetry()).isFalse(); // PENDING 상태는 재시도 불가

        payment.fail("네트워크 오류");
        assertThat(payment.canRetry()).isTrue(); // FAILED 상태는 재시도 가능

        Payment completedPayment = new Payment("user-456", 2L, BigDecimal.valueOf(120000), "BANK_TRANSFER");
        completedPayment.complete("TXN-67890");
        assertThat(completedPayment.canRetry()).isFalse(); // COMPLETED 상태는 재시도 불가
    }

    @Test
    void 결제_방법_유효성_확인_테스트() {
        // given & when & then
        Payment creditCardPayment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        assertThat(creditCardPayment.isValidPaymentMethod()).isTrue();

        Payment bankTransferPayment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "BANK_TRANSFER");
        assertThat(bankTransferPayment.isValidPaymentMethod()).isTrue();

        Payment mobilePayment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "MOBILE_PAYMENT");
        assertThat(mobilePayment.isValidPaymentMethod()).isTrue();

        Payment pointPayment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "POINT");
        assertThat(pointPayment.isValidPaymentMethod()).isTrue();
    }

    @Test
    void 지원하지_않는_결제_방법으로_생성시_예외_발생_테스트() {
        // when & then
        assertThatThrownBy(() -> new Payment("user-123", 1L, BigDecimal.valueOf(150000), "UNKNOWN_METHOD"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("지원하지 않는 결제 방법입니다: UNKNOWN_METHOD");
    }
}