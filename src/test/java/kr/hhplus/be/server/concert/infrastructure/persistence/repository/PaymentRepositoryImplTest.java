package kr.hhplus.be.server.concert.infrastructure.persistence.repository;

import kr.hhplus.be.server.concert.domain.model.Payment;
import kr.hhplus.be.server.concert.domain.model.PaymentStatus;
import kr.hhplus.be.server.concert.infrastructure.persistence.entity.PaymentEntity;
import kr.hhplus.be.server.concert.infrastructure.persistence.jpa.SpringPaymentJpa;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * PaymentRepositoryImpl 구현체 테스트
 * - 도메인 모델과 JPA 엔티티 간 변환 테스트
 * - Spring Data JPA와의 연동 테스트
 * - Mock을 사용한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class PaymentRepositoryImplTest {

    @Mock
    private SpringPaymentJpa springPaymentJpa;

    @InjectMocks
    private PaymentRepositoryImpl paymentRepositoryImpl;

    @Test
    void 결제_저장_테스트() {
        // given
        Payment payment = new Payment(
            "user123",
            1L,
            new BigDecimal("50000"),
            "CREDIT_CARD"
        );

        PaymentEntity savedEntity = new PaymentEntity(
            "user123",
            1L,
            new BigDecimal("50000"),
            "CREDIT_CARD"
        );
        savedEntity.setId(1L);

        when(springPaymentJpa.save(any(PaymentEntity.class))).thenReturn(savedEntity);

        // when
        Payment result = paymentRepositoryImpl.save(payment);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo("user123");
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("50000"));
        verify(springPaymentJpa).save(any(PaymentEntity.class));
    }

    @Test
    void ID로_결제_조회_성공_테스트() {
        // given
        Long id = 1L;
        PaymentEntity entity = new PaymentEntity(
            "user123",
            1L,
            new BigDecimal("50000"),
            "CREDIT_CARD"
        );
        entity.setId(id);

        when(springPaymentJpa.findById(id)).thenReturn(Optional.of(entity));

        // when
        Optional<Payment> result = paymentRepositoryImpl.findById(id);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getUserId()).isEqualTo("user123");
        verify(springPaymentJpa).findById(id);
    }

    @Test
    void ID로_결제_조회_실패_테스트() {
        // given
        Long id = 999L;
        when(springPaymentJpa.findById(id)).thenReturn(Optional.empty());

        // when
        Optional<Payment> result = paymentRepositoryImpl.findById(id);

        // then
        assertThat(result).isEmpty();
        verify(springPaymentJpa).findById(id);
    }

    @Test
    void 예약ID로_결제_조회_테스트() {
        // given
        Long reservationId = 1L;
        PaymentEntity entity = new PaymentEntity(
            "user123",
            reservationId,
            new BigDecimal("50000"),
            "CREDIT_CARD"
        );
        entity.setId(1L);

        when(springPaymentJpa.findByReservationId(reservationId)).thenReturn(Optional.of(entity));

        // when
        Optional<Payment> result = paymentRepositoryImpl.findByReservationId(reservationId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getReservationId()).isEqualTo(reservationId);
        verify(springPaymentJpa).findByReservationId(reservationId);
    }

    @Test
    void 사용자별_결제_조회_테스트() {
        // given
        String userId = "user123";
        PaymentEntity entity1 = new PaymentEntity(userId, 1L, new BigDecimal("30000"), "CREDIT_CARD");
        PaymentEntity entity2 = new PaymentEntity(userId, 2L, new BigDecimal("50000"), "BANK_TRANSFER");
        entity1.setId(1L);
        entity2.setId(2L);

        when(springPaymentJpa.findByUserId(userId)).thenReturn(List.of(entity1, entity2));

        // when
        List<Payment> result = paymentRepositoryImpl.findByUserId(userId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Payment::getUserId).containsOnly(userId);
        assertThat(result).extracting(Payment::getAmount)
            .containsExactlyInAnyOrder(new BigDecimal("30000"), new BigDecimal("50000"));
        verify(springPaymentJpa).findByUserId(userId);
    }

    @Test
    void 상태별_결제_조회_테스트() {
        // given
        PaymentStatus status = PaymentStatus.COMPLETED;
        PaymentEntity entity1 = new PaymentEntity("user1", 1L, new BigDecimal("30000"), "CREDIT_CARD");
        PaymentEntity entity2 = new PaymentEntity("user2", 2L, new BigDecimal("50000"), "BANK_TRANSFER");
        entity1.setId(1L);
        entity1.setStatus(status);
        entity1.setTransactionId("TXN001");
        entity1.setCompletedAt(LocalDateTime.now());
        entity2.setId(2L);
        entity2.setStatus(status);
        entity2.setTransactionId("TXN002");
        entity2.setCompletedAt(LocalDateTime.now());

        when(springPaymentJpa.findByStatus(status)).thenReturn(List.of(entity1, entity2));

        // when
        List<Payment> result = paymentRepositoryImpl.findByStatus(status);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Payment::getStatus).containsOnly(status);
        verify(springPaymentJpa).findByStatus(status);
    }

    @Test
    void 거래ID로_결제_조회_테스트() {
        // given
        String transactionId = "TXN123456";
        PaymentEntity entity = new PaymentEntity("user123", 1L, new BigDecimal("50000"), "CREDIT_CARD");
        entity.setId(1L);
        entity.setTransactionId(transactionId);
        entity.setStatus(PaymentStatus.COMPLETED);

        when(springPaymentJpa.findByTransactionId(transactionId)).thenReturn(Optional.of(entity));

        // when
        Optional<Payment> result = paymentRepositoryImpl.findByTransactionId(transactionId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getTransactionId()).isEqualTo(transactionId);
        verify(springPaymentJpa).findByTransactionId(transactionId);
    }

    @Test
    void 결제방법별_결제_조회_테스트() {
        // given
        String paymentMethod = "CREDIT_CARD";
        PaymentEntity entity = new PaymentEntity("user123", 1L, new BigDecimal("50000"), paymentMethod);
        entity.setId(1L);

        when(springPaymentJpa.findByPaymentMethod(paymentMethod)).thenReturn(List.of(entity));

        // when
        List<Payment> result = paymentRepositoryImpl.findByPaymentMethod(paymentMethod);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPaymentMethod()).isEqualTo(paymentMethod);
        verify(springPaymentJpa).findByPaymentMethod(paymentMethod);
    }

    @Test
    void 날짜_범위별_결제_조회_테스트() {
        // given
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 6, 30, 23, 59, 59);

        PaymentEntity entity = new PaymentEntity("user123", 1L, new BigDecimal("50000"), "CREDIT_CARD");
        entity.setId(1L);
        entity.setCreatedAt(LocalDateTime.of(2025, 1, 15, 10, 0, 0));

        when(springPaymentJpa.findByCreatedAtBetween(startDate, endDate)).thenReturn(List.of(entity));

        // when
        List<Payment> result = paymentRepositoryImpl.findByCreatedAtBetween(startDate, endDate);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCreatedAt()).isBetween(startDate, endDate);
        verify(springPaymentJpa).findByCreatedAtBetween(startDate, endDate);
    }

    @Test
    void 금액_범위별_결제_조회_테스트() {
        // given
        BigDecimal minAmount = new BigDecimal("10000");
        BigDecimal maxAmount = new BigDecimal("100000");

        PaymentEntity entity = new PaymentEntity("user123", 1L, new BigDecimal("50000"), "CREDIT_CARD");
        entity.setId(1L);

        when(springPaymentJpa.findByAmountBetween(minAmount, maxAmount)).thenReturn(List.of(entity));

        // when
        List<Payment> result = paymentRepositoryImpl.findByAmountBetween(minAmount, maxAmount);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAmount()).isBetween(minAmount, maxAmount);
        verify(springPaymentJpa).findByAmountBetween(minAmount, maxAmount);
    }

    @Test
    void 대기중인_결제_조회_테스트() {
        // given
        PaymentEntity entity = new PaymentEntity("user123", 1L, new BigDecimal("50000"), "CREDIT_CARD");
        entity.setId(1L);
        entity.setStatus(PaymentStatus.PENDING);

        when(springPaymentJpa.findPendingPayments()).thenReturn(List.of(entity));

        // when
        List<Payment> result = paymentRepositoryImpl.findPendingPayments();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(PaymentStatus.PENDING);
        verify(springPaymentJpa).findPendingPayments();
    }

    @Test
    void 실패한_결제_조회_테스트() {
        // given
        PaymentEntity entity = new PaymentEntity("user123", 1L, new BigDecimal("50000"), "CREDIT_CARD");
        entity.setId(1L);
        entity.setStatus(PaymentStatus.FAILED);
        entity.setFailureReason("잔액 부족");

        when(springPaymentJpa.findFailedPayments()).thenReturn(List.of(entity));

        // when
        List<Payment> result = paymentRepositoryImpl.findFailedPayments();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(PaymentStatus.FAILED);
        verify(springPaymentJpa).findFailedPayments();
    }

    @Test
    void 사용자별_총_결제금액_계산_테스트() {
        // given
        String userId = "user123";
        BigDecimal totalAmount = new BigDecimal("150000");

        when(springPaymentJpa.calculateTotalAmountByUserId(userId)).thenReturn(totalAmount);

        // when
        BigDecimal result = paymentRepositoryImpl.calculateTotalAmountByUserId(userId);

        // then
        assertThat(result).isEqualTo(totalAmount);
        verify(springPaymentJpa).calculateTotalAmountByUserId(userId);
    }

    @Test
    void 사용자별_총_결제금액_계산_결과없음_테스트() {
        // given
        String userId = "user999";
        when(springPaymentJpa.calculateTotalAmountByUserId(userId)).thenReturn(null);

        // when
        BigDecimal result = paymentRepositoryImpl.calculateTotalAmountByUserId(userId);

        // then
        assertThat(result).isEqualTo(BigDecimal.ZERO);
        verify(springPaymentJpa).calculateTotalAmountByUserId(userId);
    }

    @Test
    void 결제_삭제_테스트() {
        // given
        Long id = 1L;

        // when
        paymentRepositoryImpl.deleteById(id);

        // then
        verify(springPaymentJpa).deleteById(id);
    }

    @Test
    void 모든_결제_조회_테스트() {
        // given
        PaymentEntity entity1 = new PaymentEntity("user1", 1L, new BigDecimal("30000"), "CREDIT_CARD");
        PaymentEntity entity2 = new PaymentEntity("user2", 2L, new BigDecimal("50000"), "BANK_TRANSFER");
        entity1.setId(1L);
        entity2.setId(2L);

        when(springPaymentJpa.findAll()).thenReturn(List.of(entity1, entity2));

        // when
        List<Payment> result = paymentRepositoryImpl.findAll();

        // then
        assertThat(result).hasSize(2);
        verify(springPaymentJpa).findAll();
    }

    @Test
    void 결제_수_조회_테스트() {
        // given
        long count = 100L;
        when(springPaymentJpa.count()).thenReturn(count);

        // when
        long result = paymentRepositoryImpl.count();

        // then
        assertThat(result).isEqualTo(count);
        verify(springPaymentJpa).count();
    }
}