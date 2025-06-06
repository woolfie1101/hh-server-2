package kr.hhplus.be.server.concert.application.usecase;

import kr.hhplus.be.server.concert.application.event.EventPublisher;
import kr.hhplus.be.server.concert.application.exception.SeatExceptions;
import kr.hhplus.be.server.concert.domain.model.*;
import kr.hhplus.be.server.concert.domain.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * SeatUseCase 테스트 (클린 아키텍처)
 * - TDD Red-Green-Refactor 사이클 적용
 * - Mock을 활용한 순수 비즈니스 로직 검증
 * - 외부 의존성 완전 분리
 */
@ExtendWith(MockitoExtension.class)
class SeatUseCaseTest {

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private ConcertRepository concertRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private SeatUseCase seatUseCase;

    @Test
    void 좌석_조회_성공_테스트() {
        // given
        Long seatId = 1L;
        Seat seat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));
        seat.assignId(seatId);

        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));

        // when
        Seat result = seatUseCase.findSeat(seatId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(seatId);
        assertThat(result.getSeatNumber()).isEqualTo("A-15");
        verify(seatRepository).findById(seatId);
    }

    @Test
    void 콘서트_좌석_목록_조회_성공_테스트() {
        // given
        Long concertId = 1L;
        Concert concert = new Concert("샤이니 월드 7th", "샤이니", LocalDateTime.now().plusDays(30), 100);
        concert.assignId(concertId);

        List<Seat> seats = Arrays.asList(
            new Seat(concertId, "A-15", BigDecimal.valueOf(150000)),
            new Seat(concertId, "A-16", BigDecimal.valueOf(150000))
        );

        when(concertRepository.findById(concertId)).thenReturn(Optional.of(concert));
        when(seatRepository.findByConcertId(concertId)).thenReturn(seats);

        // when
        List<Seat> result = seatUseCase.findSeatsByConcert(concertId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSeatNumber()).isEqualTo("A-15");
        assertThat(result.get(1).getSeatNumber()).isEqualTo("A-16");
        verify(concertRepository).findById(concertId);
        verify(seatRepository).findByConcertId(concertId);
    }

    @Test
    void 좌석_예약_성공_테스트() {
        // given
        Long seatId = 1L;
        String userId = "user-123";
        Seat seat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));
        seat.assignId(seatId);

        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));
        when(seatRepository.save(any(Seat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Seat result = seatUseCase.reserveSeat(seatId, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(seatId);
        assertThat(result.getReservedBy()).isEqualTo(userId);
        verify(seatRepository).findById(seatId);
        verify(seatRepository).save(any(Seat.class));
        verify(eventPublisher).publishEvent(any()); // 좌석 예약 이벤트 발행
    }

    @Test
    void 좌석_예약_취소_성공_테스트() {
        // given
        Long seatId = 1L;
        String userId = "user-123";
        Seat seat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));
        seat.assignId(seatId);
        seat.reserve(userId);

        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));
        when(seatRepository.save(any(Seat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Seat result = seatUseCase.cancelSeatReservation(seatId, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(seatId);
        assertThat(result.isAvailable()).isTrue();
        verify(seatRepository).findById(seatId);
        verify(seatRepository).save(any(Seat.class));
        verify(eventPublisher).publishEvent(any()); // 좌석 취소 이벤트 발행
    }

    @Test
    void 좌석_예약_실패_이미예약_테스트() {
        // given
        Long seatId = 1L;
        String userId = "user-123";
        Seat seat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));
        seat.assignId(seatId);
        seat.reserve("other-user"); // 이미 다른 사용자가 예약

        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));

        // when & then
        assertThatThrownBy(() -> seatUseCase.reserveSeat(seatId, userId))
            .isInstanceOf(SeatExceptions.SeatAlreadyReservedException.class)
            .hasMessageContaining("이미 예약되었습니다");

        verify(seatRepository).findById(seatId);
        verify(seatRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void 좌석_예약_취소_실패_권한없음_테스트() {
        // given
        Long seatId = 1L;
        String userId = "user-123";
        String otherUserId = "user-456";
        Seat seat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));
        seat.assignId(seatId);
        seat.reserve(otherUserId); // 다른 사용자가 예약

        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));

        // when & then
        assertThatThrownBy(() -> seatUseCase.cancelSeatReservation(seatId, userId))
            .isInstanceOf(SeatExceptions.UnauthorizedAccessException.class)
            .hasMessageContaining("본인이 예약한 좌석만 취소할 수 있습니다");

        verify(seatRepository).findById(seatId);
        verify(seatRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }
} 