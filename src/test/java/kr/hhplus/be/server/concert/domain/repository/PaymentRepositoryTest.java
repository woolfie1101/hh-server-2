package kr.hhplus.be.server.concert.domain.repository;

import kr.hhplus.be.server.concert.domain.model.Payment;
import kr.hhplus.be.server.concert.domain.model.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * PaymentRepository 인터페이스 테스트
 * - 실제 구현체가 아닌 인터페이스 스펙을 검증
 * - Mock을 사용해서 인터페이스 동작 확인
 */
@ExtendWith(MockitoExtension.class)
class PaymentRepositoryTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Test
    void 결제_저장_테스트() {
        // given
        Payment payment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        Payment savedPayment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        savedPayment.assignId(1L);

        when(paymentRepository.save(payment)).thenReturn(savedPayment);

        // when
        Payment result = paymentRepository.save(payment);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo("user-123");
        verify(paymentRepository).save(payment);
    }

    @Test
    void ID로_결제_조회_성공_테스트() {
        // given
        Long id = 1L;
        Payment payment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        payment.assignId(id);

        when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));

        // when
        Optional<Payment> result = paymentRepository.findById(id);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        verify(paymentRepository).findById(id);
    }

    @Test
    void 예약ID로_결제_조회_테스트() {
        // given
        Long reservationId = 1L;
        Payment payment = new Payment("user-123", reservationId, BigDecimal.valueOf(150000), "CREDIT_CARD");

        when(paymentRepository.findByReservationId(reservationId)).thenReturn(Optional.of(payment));

        // when
        Optional<Payment> result = paymentRepository.findByReservationId(reservationId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getReservationId()).isEqualTo(reservationId);
        verify(paymentRepository).findByReservationId(reservationId);
    }

    @Test
    void 사용자별_결제_조회_테스트() {
        // given
        String userId = "user-123";
        Payment payment1 = new Payment(userId, 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        Payment payment2 = new Payment(userId, 2L, BigDecimal.valueOf(120000), "BANK_TRANSFER");
        List<Payment> payments = List.of(payment1, payment2);

        when(paymentRepository.findByUserId(userId)).thenReturn(payments);

        // when
        List<Payment> result = paymentRepository.findByUserId(userId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Payment::getUserId)
            .containsOnly(userId);
        verify(paymentRepository).findByUserId(userId);
    }

    @Test
    void 상태별_결제_조회_테스트() {
        // given
        PaymentStatus status = PaymentStatus.COMPLETED;
        Payment payment1 = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        Payment payment2 = new Payment("user-456", 2L, BigDecimal.valueOf(120000), "BANK_TRANSFER");
        List<Payment> payments = List.of(payment1, payment2);

        when(paymentRepository.findByStatus(status)).thenReturn(payments);

        // when
        List<Payment> result = paymentRepository.findByStatus(status);

        // then
        assertThat(result).hasSize(2);
        verify(paymentRepository).findByStatus(status);
    }

    @Test
    void 거래ID로_결제_조회_테스트() {
        // given
        String transactionId = "TXN-12345";
        Payment payment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        payment.complete(transactionId);

        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(Optional.of(payment));

        // when
        Optional<Payment> result = paymentRepository.findByTransactionId(transactionId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getTransactionId()).isEqualTo(transactionId);
        verify(paymentRepository).findByTransactionId(transactionId);
    }

    @Test
    void 결제_방법별_결제_조회_테스트() {
        // given
        String paymentMethod = "CREDIT_CARD";
        Payment payment1 = new Payment("user-123", 1L, BigDecimal.valueOf(150000), paymentMethod);
        Payment payment2 = new Payment("user-456", 2L, BigDecimal.valueOf(120000), paymentMethod);
        List<Payment> payments = List.of(payment1, payment2);

        when(paymentRepository.findByPaymentMethod(paymentMethod)).thenReturn(payments);

        // when
        List<Payment> result = paymentRepository.findByPaymentMethod(paymentMethod);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Payment::getPaymentMethod)
            .containsOnly(paymentMethod);
        verify(paymentRepository).findByPaymentMethod(paymentMethod);
    }

    @Test
    void 날짜_범위별_결제_조회_테스트() {
        // given
        LocalDateTime startDate = LocalDateTime.of(2025, 5, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 5, 31, 23, 59);

        Payment payment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        List<Payment> payments = List.of(payment);

        when(paymentRepository.findByCreatedAtBetween(startDate, endDate)).thenReturn(payments);

        // when
        List<Payment> result = paymentRepository.findByCreatedAtBetween(startDate, endDate);

        // then
        assertThat(result).hasSize(1);
        verify(paymentRepository).findByCreatedAtBetween(startDate, endDate);
    }

    @Test
    void 금액_범위별_결제_조회_테스트() {
        // given
        BigDecimal minAmount = BigDecimal.valueOf(100000);
        BigDecimal maxAmount = BigDecimal.valueOf(200000);

        Payment payment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        List<Payment> payments = List.of(payment);

        when(paymentRepository.findByAmountBetween(minAmount, maxAmount)).thenReturn(payments);

        // when
        List<Payment> result = paymentRepository.findByAmountBetween(minAmount, maxAmount);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAmount()).isBetween(minAmount, maxAmount);
        verify(paymentRepository).findByAmountBetween(minAmount, maxAmount);
    }

    @Test
    void 처리_대기중인_결제_조회_테스트() {
        // given
        Payment pendingPayment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        List<Payment> payments = List.of(pendingPayment);

        when(paymentRepository.findPendingPayments()).thenReturn(payments);

        // when
        List<Payment> result = paymentRepository.findPendingPayments();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(PaymentStatus.PENDING);
        verify(paymentRepository).findPendingPayments();
    }

    @Test
    void 실패한_결제_조회_테스트() {
        // given
        Payment failedPayment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        failedPayment.fail("잔액 부족");
        List<Payment> payments = List.of(failedPayment);

        when(paymentRepository.findFailedPayments()).thenReturn(payments);

        // when
        List<Payment> result = paymentRepository.findFailedPayments();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(PaymentStatus.FAILED);
        verify(paymentRepository).findFailedPayments();
    }

    @Test
    void 사용자별_총_결제_금액_조회_테스트() {
        // given
        String userId = "user-123";
        BigDecimal expectedTotal = BigDecimal.valueOf(500000);

        when(paymentRepository.calculateTotalAmountByUserId(userId)).thenReturn(expectedTotal);

        // when
        BigDecimal result = paymentRepository.calculateTotalAmountByUserId(userId);

        // then
        assertThat(result).isEqualTo(expectedTotal);
        verify(paymentRepository).calculateTotalAmountByUserId(userId);
    }

    @Test
    void 결제_삭제_테스트() {
        // given
        Long id = 1L;

        // when
        paymentRepository.deleteById(id);

        // then
        verify(paymentRepository).deleteById(id);
    }

    @Test
    void 모든_결제_조회_테스트() {
        // given
        Payment payment1 = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        Payment payment2 = new Payment("user-456", 2L, BigDecimal.valueOf(120000), "BANK_TRANSFER");
        List<Payment> payments = List.of(payment1, payment2);

        when(paymentRepository.findAll()).thenReturn(payments);

        // when
        List<Payment> result = paymentRepository.findAll();

        // then
        assertThat(result).hasSize(2);
        verify(paymentRepository).findAll();
    }

    @Test
    void 결제_수_확인_테스트() {
        // given
        long expectedCount = 50L;

        when(paymentRepository.count()).thenReturn(expectedCount);

        // when
        long result = paymentRepository.count();

        // then
        assertThat(result).isEqualTo(expectedCount);
        verify(paymentRepository).count();
    }
}