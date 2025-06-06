package kr.hhplus.be.server.concert.application.usecase;

import kr.hhplus.be.server.concert.application.dto.PaymentResult;
import kr.hhplus.be.server.concert.application.event.EventPublisher;
import kr.hhplus.be.server.concert.application.exception.PaymentExceptions;
import kr.hhplus.be.server.concert.application.service.PaymentService;
import kr.hhplus.be.server.concert.domain.model.*;
import kr.hhplus.be.server.concert.domain.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * PaymentUseCase 테스트 (클린 아키텍처)
 * - TDD Red-Green-Refactor 사이클 적용
 * - Mock을 활용한 순수 비즈니스 로직 검증
 * - 외부 의존성 완전 분리
 */
@ExtendWith(MockitoExtension.class)
class PaymentUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private PaymentUseCase paymentUseCase;

    @Test
    void 결제_처리_성공_테스트() {
        // given
        Long reservationId = 1L;
        String paymentMethod = "CREDIT_CARD";
        String userId = "user-123";

        User user = new User(userId, "김철수", BigDecimal.valueOf(200000));
        user.assignId(1L);

        Reservation reservation = new Reservation(userId, 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        reservation.assignId(reservationId);

        Payment payment = new Payment(userId, reservationId, BigDecimal.valueOf(150000), paymentMethod);
        payment.assignId(1L);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(userRepository.findByUuid(userId)).thenReturn(Optional.of(user));
        when(paymentService.processPayment(any(Payment.class))).thenReturn("TXN-12345");
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        PaymentResult result = paymentUseCase.processPayment(reservationId, paymentMethod);

        // then
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getTransactionId()).isEqualTo("TXN-12345");

        verify(reservationRepository).findById(reservationId);
        verify(userRepository).findByUuid(userId);
        verify(paymentService).processPayment(any(Payment.class));
        verify(paymentRepository).save(any(Payment.class));
        verify(reservationRepository).save(any(Reservation.class));
        verify(eventPublisher).publishEvent(any()); // 결제 완료 이벤트 발행
    }

    @Test
    void 결제_취소_성공_테스트() {
        // given
        Long paymentId = 1L;
        String userId = "user-123";

        Payment payment = new Payment(userId, 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        payment.assignId(paymentId);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        boolean result = paymentUseCase.cancelPayment(paymentId, userId);

        // then
        assertThat(result).isTrue();
        verify(paymentRepository).findById(paymentId);
        verify(paymentRepository).save(any(Payment.class));
        verify(eventPublisher).publishEvent(any()); // 결제 취소 이벤트 발행
    }

    @Test
    void 결제_환불_성공_테스트() {
        // given
        Long paymentId = 1L;
        String userId = "user-123";
        String refundReason = "고객 요청";

        Payment payment = new Payment(userId, 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        payment.assignId(paymentId);
        payment.complete("TXN-12345"); // 결제 완료 상태로 설정

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        boolean result = paymentUseCase.refundPayment(paymentId, userId, refundReason);

        // then
        assertThat(result).isTrue();
        verify(paymentRepository).findById(paymentId);
        verify(paymentRepository).save(any(Payment.class));
        verify(eventPublisher).publishEvent(any()); // 결제 환불 이벤트 발행
    }

    @Test
    void 결제_처리_실패_잔액부족_테스트() {
        // given
        Long reservationId = 1L;
        String paymentMethod = "POINT";
        String userId = "user-123";

        User user = new User(userId, "김철수", BigDecimal.valueOf(100000)); // 잔액 부족
        user.assignId(1L);

        Reservation reservation = new Reservation(userId, 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        reservation.assignId(reservationId);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(userRepository.findByUuid(userId)).thenReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> paymentUseCase.processPayment(reservationId, paymentMethod))
            .isInstanceOf(PaymentExceptions.InsufficientBalanceException.class)
            .hasMessageContaining("잔액이 부족합니다");

        verify(reservationRepository).findById(reservationId);
        verify(userRepository).findByUuid(userId);
        verify(paymentService, never()).processPayment(any());
        verify(paymentRepository, never()).save(any());
        verify(reservationRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void 결제_취소_실패_권한없음_테스트() {
        // given
        Long paymentId = 1L;
        String userId = "user-123";
        String otherUserId = "user-456";

        Payment payment = new Payment(otherUserId, 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        payment.assignId(paymentId);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        // when & then
        assertThatThrownBy(() -> paymentUseCase.cancelPayment(paymentId, userId))
            .isInstanceOf(PaymentExceptions.UnauthorizedAccessException.class)
            .hasMessageContaining("본인의 결제만 취소/환불할 수 있습니다");

        verify(paymentRepository).findById(paymentId);
        verify(paymentRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void 결제_환불_실패_상태오류_테스트() {
        // given
        Long paymentId = 1L;
        String userId = "user-123";
        String refundReason = "고객 요청";

        Payment payment = new Payment(userId, 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        payment.assignId(paymentId);
        // 결제 완료 상태가 아님

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        // when & then
        assertThatThrownBy(() -> paymentUseCase.refundPayment(paymentId, userId, refundReason))
            .isInstanceOf(PaymentExceptions.InvalidPaymentStatusException.class)
            .hasMessageContaining("환불할 수 없는 결제 상태입니다");

        verify(paymentRepository).findById(paymentId);
        verify(paymentRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }
} 