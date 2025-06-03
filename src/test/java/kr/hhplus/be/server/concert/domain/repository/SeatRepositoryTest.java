package kr.hhplus.be.server.concert.domain.repository;

import kr.hhplus.be.server.concert.domain.model.Seat;
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
 * SeatRepository 인터페이스 테스트
 * - 실제 구현체가 아닌 인터페이스 스펙을 검증
 * - Mock을 사용해서 인터페이스 동작 확인
 */
@ExtendWith(MockitoExtension.class)
class SeatRepositoryTest {

    @Mock
    private SeatRepository seatRepository;

    @Test
    void 좌석_저장_테스트() {
        // given
        Seat seat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));
        Seat savedSeat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));
        savedSeat.assignId(1L);

        when(seatRepository.save(seat)).thenReturn(savedSeat);

        // when
        Seat result = seatRepository.save(seat);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSeatNumber()).isEqualTo("A-15");
        verify(seatRepository).save(seat);
    }

    @Test
    void ID로_좌석_조회_성공_테스트() {
        // given
        Long id = 1L;
        Seat seat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));
        seat.assignId(id);

        when(seatRepository.findById(id)).thenReturn(Optional.of(seat));

        // when
        Optional<Seat> result = seatRepository.findById(id);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getSeatNumber()).isEqualTo("A-15");
        verify(seatRepository).findById(id);
    }

    @Test
    void 콘서트별_좌석_조회_테스트() {
        // given
        Long concertId = 1L;
        Seat seat1 = new Seat(concertId, "A-15", BigDecimal.valueOf(150000));
        Seat seat2 = new Seat(concertId, "A-16", BigDecimal.valueOf(150000));
        List<Seat> seats = List.of(seat1, seat2);

        when(seatRepository.findByConcertId(concertId)).thenReturn(seats);

        // when
        List<Seat> result = seatRepository.findByConcertId(concertId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Seat::getConcertId)
            .containsOnly(concertId);
        verify(seatRepository).findByConcertId(concertId);
    }

    @Test
    void 예약_가능한_좌석_조회_테스트() {
        // given
        Long concertId = 1L;
        Seat availableSeat = new Seat(concertId, "A-15", BigDecimal.valueOf(150000));
        List<Seat> seats = List.of(availableSeat);

        when(seatRepository.findAvailableSeatsByConcertId(concertId)).thenReturn(seats);

        // when
        List<Seat> result = seatRepository.findAvailableSeatsByConcertId(concertId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).isAvailable()).isTrue();
        verify(seatRepository).findAvailableSeatsByConcertId(concertId);
    }

    @Test
    void 특정_사용자_예약_좌석_조회_테스트() {
        // given
        String userId = "user-123";
        Seat reservedSeat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));
        reservedSeat.reserve(userId);
        List<Seat> seats = List.of(reservedSeat);

        when(seatRepository.findByReservedBy(userId)).thenReturn(seats);

        // when
        List<Seat> result = seatRepository.findByReservedBy(userId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReservedBy()).isEqualTo(userId);
        verify(seatRepository).findByReservedBy(userId);
    }

    @Test
    void 만료된_예약_좌석_조회_테스트() {
        // given
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(5);
        Seat expiredSeat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));
        List<Seat> seats = List.of(expiredSeat);

        when(seatRepository.findExpiredReservations(cutoffTime)).thenReturn(seats);

        // when
        List<Seat> result = seatRepository.findExpiredReservations(cutoffTime);

        // then
        assertThat(result).hasSize(1);
        verify(seatRepository).findExpiredReservations(cutoffTime);
    }

    @Test
    void 콘서트와_좌석번호로_좌석_조회_테스트() {
        // given
        Long concertId = 1L;
        String seatNumber = "A-15";
        Seat seat = new Seat(concertId, seatNumber, BigDecimal.valueOf(150000));

        when(seatRepository.findByConcertIdAndSeatNumber(concertId, seatNumber))
            .thenReturn(Optional.of(seat));

        // when
        Optional<Seat> result = seatRepository.findByConcertIdAndSeatNumber(concertId, seatNumber);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getConcertId()).isEqualTo(concertId);
        assertThat(result.get().getSeatNumber()).isEqualTo(seatNumber);
        verify(seatRepository).findByConcertIdAndSeatNumber(concertId, seatNumber);
    }

    @Test
    void 가격_범위로_좌석_조회_테스트() {
        // given
        BigDecimal minPrice = BigDecimal.valueOf(100000);
        BigDecimal maxPrice = BigDecimal.valueOf(200000);

        Seat seat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));
        List<Seat> seats = List.of(seat);

        when(seatRepository.findByPriceBetween(minPrice, maxPrice)).thenReturn(seats);

        // when
        List<Seat> result = seatRepository.findByPriceBetween(minPrice, maxPrice);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPrice()).isBetween(minPrice, maxPrice);
        verify(seatRepository).findByPriceBetween(minPrice, maxPrice);
    }

    @Test
    void 콘서트별_예약_가능_좌석_수_조회_테스트() {
        // given
        Long concertId = 1L;
        long expectedCount = 50L;

        when(seatRepository.countAvailableSeatsByConcertId(concertId)).thenReturn(expectedCount);

        // when
        long result = seatRepository.countAvailableSeatsByConcertId(concertId);

        // then
        assertThat(result).isEqualTo(expectedCount);
        verify(seatRepository).countAvailableSeatsByConcertId(concertId);
    }

    @Test
    void 좌석_삭제_테스트() {
        // given
        Long id = 1L;

        // when
        seatRepository.deleteById(id);

        // then
        verify(seatRepository).deleteById(id);
    }

    @Test
    void 모든_좌석_조회_테스트() {
        // given
        Seat seat1 = new Seat(1L, "A-15", BigDecimal.valueOf(150000));
        Seat seat2 = new Seat(1L, "A-16", BigDecimal.valueOf(150000));
        List<Seat> seats = List.of(seat1, seat2);

        when(seatRepository.findAll()).thenReturn(seats);

        // when
        List<Seat> result = seatRepository.findAll();

        // then
        assertThat(result).hasSize(2);
        verify(seatRepository).findAll();
    }

    @Test
    void 좌석_수_확인_테스트() {
        // given
        long expectedCount = 100L;

        when(seatRepository.count()).thenReturn(expectedCount);

        // when
        long result = seatRepository.count();

        // then
        assertThat(result).isEqualTo(expectedCount);
        verify(seatRepository).count();
    }
}