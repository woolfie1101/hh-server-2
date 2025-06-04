package kr.hhplus.be.server.concert.application.usecase;

import kr.hhplus.be.server.concert.application.dto.PaymentResult;
import kr.hhplus.be.server.concert.application.event.EventPublisher;
import kr.hhplus.be.server.concert.application.service.PaymentService;
import kr.hhplus.be.server.concert.domain.model.*;
import kr.hhplus.be.server.concert.domain.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ReservationUseCase 테스트 (클린 아키텍처)
 * - TDD Red-Green-Refactor 사이클 적용
 * - Mock을 활용한 순수 비즈니스 로직 검증
 * - 외부 의존성 완전 분리
 */
@ExtendWith(MockitoExtension.class)
class ReservationUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConcertRepository concertRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private ReservationUseCase reservationUseCase;

    @Test
    void 좌석_예약_성공_테스트() {
        // given
        String userId = "user-123";
        Long concertId = 1L;
        Long seatId = 10L;

        User user = new User(userId, "김철수", BigDecimal.valueOf(200000));
        user.assignId(1L);

        Concert concert = new Concert(
            "샤이니 월드 7th",
            "샤이니",
            LocalDateTime.of(2025, 6, 25, 17, 25, 0),
            100
        );
        concert.assignId(concertId);

        Seat seat = new Seat(concertId, "A-15", BigDecimal.valueOf(150000));
        seat.assignId(seatId);

        when(userRepository.findByUuid(userId)).thenReturn(Optional.of(user));
        when(concertRepository.findById(concertId)).thenReturn(Optional.of(concert));
        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation reservation = invocation.getArgument(0);
            reservation.assignId(1L);
            return reservation;
        });

        // when
        Reservation result = reservationUseCase.reserveSeat(userId, concertId, seatId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getConcertId()).isEqualTo(concertId);
        assertThat(result.getSeatId()).isEqualTo(seatId);
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.TEMPORARY);

        verify(userRepository).findByUuid(userId);
        verify(concertRepository).findById(concertId);
        verify(seatRepository).findById(seatId);
        verify(reservationRepository).save(any(Reservation.class));
        verify(eventPublisher).publishEvent(any()); // 예약 이벤트 발행
    }

    @Test
    void 이미_예약된_좌석_예약_실패_테스트() {
        // given
        String userId = "user-123";
        Long concertId = 1L;
        Long seatId = 10L;

        User user = new User(userId, "김철수", BigDecimal.valueOf(200000));
        Concert concert = new Concert("샤이니 월드 7th", "샤이니", LocalDateTime.of(2025, 6, 25, 17, 25, 0), 100);
        Seat seat = new Seat(concertId, "A-15", BigDecimal.valueOf(150000));
        seat.reserve("other-user"); // 다른 사용자가 이미 예약

        when(userRepository.findByUuid(userId)).thenReturn(Optional.of(user));
        when(concertRepository.findById(concertId)).thenReturn(Optional.of(concert));
        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));

        // when & then
        assertThatThrownBy(() -> reservationUseCase.reserveSeat(userId, concertId, seatId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("이미 예약된 좌석입니다.");

        verify(reservationRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void 결제_처리_성공_테스트() {
        // given
        Long reservationId = 1L;
        String paymentMethod = "CREDIT_CARD";

        User user = new User("user-123", "김철수", BigDecimal.valueOf(200000));
        user.assignId(1L);

        Reservation reservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        reservation.assignId(reservationId);

        Payment payment = new Payment("user-123", reservationId, BigDecimal.valueOf(150000), paymentMethod);
        payment.assignId(1L);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(userRepository.findByUuid("user-123")).thenReturn(Optional.of(user));
        when(paymentService.processPayment(any(Payment.class))).thenReturn("TXN-12345");
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        PaymentResult result = reservationUseCase.processPayment(reservationId, paymentMethod);

        // then
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getTransactionId()).isEqualTo("TXN-12345");

        verify(reservationRepository).findById(reservationId);
        verify(userRepository).findByUuid("user-123");
        verify(paymentService).processPayment(any(Payment.class));
        verify(paymentRepository).save(any(Payment.class));
        verify(reservationRepository).save(any(Reservation.class));
        verify(eventPublisher).publishEvent(any()); // 결제 완료 이벤트 발행
    }

    @Test
    void 잔액_부족으로_결제_실패_테스트() {
        // given
        Long reservationId = 1L;
        String paymentMethod = "POINT";

        User user = new User("user-123", "김철수", BigDecimal.valueOf(50000)); // 잔액 부족
        user.assignId(1L);

        Reservation reservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        reservation.assignId(reservationId);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(userRepository.findByUuid("user-123")).thenReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> reservationUseCase.processPayment(reservationId, paymentMethod))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("잔액이 부족합니다.");

        verify(paymentService, never()).processPayment(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void 예약_취소_성공_테스트() {
        // given
        Long reservationId = 1L;
        String userId = "user-123";

        Reservation reservation = new Reservation(userId, 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        reservation.assignId(reservationId);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        boolean result = reservationUseCase.cancelReservation(reservationId, userId);

        // then
        assertThat(result).isTrue();

        verify(reservationRepository).findById(reservationId);
        verify(reservationRepository).save(any(Reservation.class));
        verify(eventPublisher).publishEvent(any()); // 취소 이벤트 발행
    }

    @Test
    void 다른_사용자_예약_취소_실패_테스트() {
        // given
        Long reservationId = 1L;
        String userId = "user-123";
        String otherUserId = "user-456";

        Reservation reservation = new Reservation(otherUserId, 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        reservation.assignId(reservationId);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // when & then
        assertThatThrownBy(() -> reservationUseCase.cancelReservation(reservationId, userId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("본인의 예약만 취소할 수 있습니다.");

        verify(reservationRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }
}