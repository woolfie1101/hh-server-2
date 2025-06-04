package kr.hhplus.be.server.concert.infrastructure.persistence.entity;

import kr.hhplus.be.server.concert.domain.model.Payment;
import kr.hhplus.be.server.concert.domain.model.PaymentStatus;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.*;

/**
 * PaymentEntity JPA 엔티티 테스트
 * - JPA 매핑 및 변환 로직 검증
 * - 도메인 모델과 엔티티 간 변환 테스트
 */
class PaymentEntityTest {

    @Test
    void 엔티티_생성_테스트() {
        // given
        String userId = "user-123";
        Long reservationId = 1L;
        BigDecimal amount = BigDecimal.valueOf(150000);
        String paymentMethod = "CREDIT_CARD";

        // when
        PaymentEntity entity = new PaymentEntity(userId, reservationId, amount, paymentMethod);

        // then
        assertThat(entity.getUserId()).isEqualTo(userId);
        assertThat(entity.getReservationId()).isEqualTo(reservationId);
        assertThat(entity.getAmount()).isEqualTo(amount);
        assertThat(entity.getPaymentMethod()).isEqualTo(paymentMethod);
        assertThat(entity.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(entity.getId()).isNull(); // 아직 저장 전이므로 ID 없음
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
        assertThat(entity.getTransactionId()).isNull();
        assertThat(entity.getFailureReason()).isNull();
        assertThat(entity.getRefundReason()).isNull();
        assertThat(entity.getCompletedAt()).isNull();
        assertThat(entity.getRefundedAt()).isNull();
    }

    @Test
    void 도메인_모델로_변환_테스트() {
        // given
        PaymentEntity entity = new PaymentEntity(
            "user-123",
            1L,
            BigDecimal.valueOf(150000),
            "CREDIT_CARD"
        );
        entity.setId(1L); // Repository에서 저장 후 ID 할당 시뮬레이션

        // when
        Payment payment = entity.toDomain();

        // then
        assertThat(payment.getId()).isEqualTo(1L);
        assertThat(payment.getUserId()).isEqualTo("user-123");
        assertThat(payment.getReservationId()).isEqualTo(1L);
        assertThat(payment.getAmount()).isEqualTo(BigDecimal.valueOf(150000));
        assertThat(payment.getPaymentMethod()).isEqualTo("CREDIT_CARD");
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    void 완료된_결제_도메인_모델로_변환_테스트() {
        // given
        PaymentEntity entity = new PaymentEntity(
            "user-123",
            1L,
            BigDecimal.valueOf(150000),
            "CREDIT_CARD"
        );
        entity.setId(1L);
        entity.setStatus(PaymentStatus.COMPLETED);
        entity.setTransactionId("TXN-12345");

        // when
        Payment payment = entity.toDomain();

        // then
        assertThat(payment.getId()).isEqualTo(1L);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(payment.getTransactionId()).isEqualTo("TXN-12345");
        assertThat(payment.isSuccessful()).isTrue();
    }

    @Test
    void 도메인_모델에서_엔티티_생성_테스트() {
        // given
        Payment payment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        payment.assignId(1L);

        // when
        PaymentEntity entity = PaymentEntity.fromDomain(payment);

        // then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getUserId()).isEqualTo("user-123");
        assertThat(entity.getReservationId()).isEqualTo(1L);
        assertThat(entity.getAmount()).isEqualTo(BigDecimal.valueOf(150000));
        assertThat(entity.getPaymentMethod()).isEqualTo("CREDIT_CARD");
        assertThat(entity.getStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    void 완료된_도메인_모델에서_엔티티_생성_테스트() {
        // given
        Payment payment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        payment.assignId(1L);
        payment.complete("TXN-12345");

        // when
        PaymentEntity entity = PaymentEntity.fromDomain(payment);

        // then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(entity.getTransactionId()).isEqualTo("TXN-12345");
        assertThat(entity.getCompletedAt()).isNotNull();
    }

    @Test
    void 도메인_모델_업데이트_테스트() {
        // given
        PaymentEntity entity = new PaymentEntity(
            "user-123",
            1L,
            BigDecimal.valueOf(150000),
            "CREDIT_CARD"
        );
        entity.setId(1L);

        Payment updatedPayment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        updatedPayment.assignId(1L);
        updatedPayment.complete("TXN-12345"); // 결제 완료

        // when
        entity.updateFromDomain(updatedPayment);

        // then
        assertThat(entity.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(entity.getTransactionId()).isEqualTo("TXN-12345");
        assertThat(entity.getCompletedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
    }

    @Test
    void 실패한_결제_도메인_업데이트_테스트() {
        // given
        PaymentEntity entity = new PaymentEntity(
            "user-123",
            1L,
            BigDecimal.valueOf(150000),
            "CREDIT_CARD"
        );
        entity.setId(1L);

        Payment failedPayment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        failedPayment.assignId(1L);
        failedPayment.fail("잔액 부족");

        // when
        entity.updateFromDomain(failedPayment);

        // then
        assertThat(entity.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(entity.getFailureReason()).isEqualTo("잔액 부족");
        assertThat(entity.getCompletedAt()).isNull();
    }

    @Test
    void 환불된_결제_도메인_변환_테스트() {
        // given
        PaymentEntity entity = new PaymentEntity(
            "user-123",
            1L,
            BigDecimal.valueOf(150000),
            "CREDIT_CARD"
        );
        entity.setId(1L);
        entity.setStatus(PaymentStatus.REFUNDED);
        entity.setTransactionId("TXN-12345");
        entity.setRefundReason("사용자 요청");

        // when
        Payment payment = entity.toDomain();

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
        assertThat(payment.getTransactionId()).isEqualTo("TXN-12345");
        assertThat(payment.getRefundReason()).isEqualTo("사용자 요청");
    }

    @Test
    void 엔티티_동등성_비교_테스트() {
        // given
        PaymentEntity entity1 = new PaymentEntity(
            "user-123",
            1L,
            BigDecimal.valueOf(150000),
            "CREDIT_CARD"
        );
        entity1.setId(1L);

        PaymentEntity entity2 = new PaymentEntity(
            "user-456",
            2L,
            BigDecimal.valueOf(120000),
            "BANK_TRANSFER"
        );
        entity2.setId(1L);

        PaymentEntity entity3 = new PaymentEntity(
            "user-789",
            3L,
            BigDecimal.valueOf(100000),
            "MOBILE_PAYMENT"
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
        PaymentEntity entity = new PaymentEntity();

        // then
        assertThat(entity.getId()).isNull();
        assertThat(entity.getUserId()).isNull();
        assertThat(entity.getReservationId()).isNull();
        assertThat(entity.getAmount()).isNull();
        assertThat(entity.getPaymentMethod()).isNull();
        assertThat(entity.getStatus()).isNull();
    }

    @Test
    void toString_테스트() {
        // given
        PaymentEntity entity = new PaymentEntity(
            "user-123",
            1L,
            BigDecimal.valueOf(150000),
            "CREDIT_CARD"
        );
        entity.setId(1L);

        // when
        String result = entity.toString();

        // then
        assertThat(result).contains("PaymentEntity");
        assertThat(result).contains("id=1");
        assertThat(result).contains("userId='user-123'");
        assertThat(result).contains("reservationId=1");
        assertThat(result).contains("amount=150000");
        assertThat(result).contains("paymentMethod='CREDIT_CARD'");
        assertThat(result).contains("status=PENDING");
    }
}