package kr.hhplus.be.server.concert.domain.repository;

import kr.hhplus.be.server.concert.domain.model.Reservation;
import kr.hhplus.be.server.concert.domain.model.ReservationStatus;
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
 * ReservationRepository 인터페이스 테스트
 * - 실제 구현체가 아닌 인터페이스 스펙을 검증
 * - Mock을 사용해서 인터페이스 동작 확인
 */
@ExtendWith(MockitoExtension.class)
class ReservationRepositoryTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Test
    void 예약_저장_테스트() {
        // given
        Reservation reservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        Reservation savedReservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        savedReservation.assignId(1L);

        when(reservationRepository.save(reservation)).thenReturn(savedReservation);

        // when
        Reservation result = reservationRepository.save(reservation);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo("user-123");
        verify(reservationRepository).save(reservation);
    }

    @Test
    void ID로_예약_조회_성공_테스트() {
        // given
        Long id = 1L;
        Reservation reservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        reservation.assignId(id);

        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));

        // when
        Optional<Reservation> result = reservationRepository.findById(id);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        verify(reservationRepository).findById(id);
    }

    @Test
    void 사용자별_예약_조회_테스트() {
        // given
        String userId = "user-123";
        Reservation reservation1 = new Reservation(userId, 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        Reservation reservation2 = new Reservation(userId, 1L, 11L, "A-16", BigDecimal.valueOf(150000));
        List<Reservation> reservations = List.of(reservation1, reservation2);

        when(reservationRepository.findByUserId(userId)).thenReturn(reservations);

        // when
        List<Reservation> result = reservationRepository.findByUserId(userId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Reservation::getUserId)
            .containsOnly(userId);
        verify(reservationRepository).findByUserId(userId);
    }

    @Test
    void 콘서트별_예약_조회_테스트() {
        // given
        Long concertId = 1L;
        Reservation reservation1 = new Reservation("user-123", concertId, 10L, "A-15", BigDecimal.valueOf(150000));
        Reservation reservation2 = new Reservation("user-456", concertId, 11L, "A-16", BigDecimal.valueOf(150000));
        List<Reservation> reservations = List.of(reservation1, reservation2);

        when(reservationRepository.findByConcertId(concertId)).thenReturn(reservations);

        // when
        List<Reservation> result = reservationRepository.findByConcertId(concertId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Reservation::getConcertId)
            .containsOnly(concertId);
        verify(reservationRepository).findByConcertId(concertId);
    }

    @Test
    void 좌석별_예약_조회_테스트() {
        // given
        Long seatId = 10L;
        Reservation reservation = new Reservation("user-123", 1L, seatId, "A-15", BigDecimal.valueOf(150000));

        when(reservationRepository.findBySeatId(seatId)).thenReturn(Optional.of(reservation));

        // when
        Optional<Reservation> result = reservationRepository.findBySeatId(seatId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getSeatId()).isEqualTo(seatId);
        verify(reservationRepository).findBySeatId(seatId);
    }

    @Test
    void 상태별_예약_조회_테스트() {
        // given
        ReservationStatus status = ReservationStatus.TEMPORARY;
        Reservation reservation1 = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        Reservation reservation2 = new Reservation("user-456", 1L, 11L, "A-16", BigDecimal.valueOf(150000));
        List<Reservation> reservations = List.of(reservation1, reservation2);

        when(reservationRepository.findByStatus(status)).thenReturn(reservations);

        // when
        List<Reservation> result = reservationRepository.findByStatus(status);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Reservation::getStatus)
            .containsOnly(status);
        verify(reservationRepository).findByStatus(status);
    }

    @Test
    void 만료된_예약_조회_테스트() {
        // given
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(5);
        Reservation expiredReservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        List<Reservation> reservations = List.of(expiredReservation);

        when(reservationRepository.findExpiredReservations(cutoffTime)).thenReturn(reservations);

        // when
        List<Reservation> result = reservationRepository.findExpiredReservations(cutoffTime);

        // then
        assertThat(result).hasSize(1);
        verify(reservationRepository).findExpiredReservations(cutoffTime);
    }

    @Test
    void 사용자와_콘서트로_예약_조회_테스트() {
        // given
        String userId = "user-123";
        Long concertId = 1L;
        Reservation reservation = new Reservation(userId, concertId, 10L, "A-15", BigDecimal.valueOf(150000));
        List<Reservation> reservations = List.of(reservation);

        when(reservationRepository.findByUserIdAndConcertId(userId, concertId)).thenReturn(reservations);

        // when
        List<Reservation> result = reservationRepository.findByUserIdAndConcertId(userId, concertId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
        assertThat(result.get(0).getConcertId()).isEqualTo(concertId);
        verify(reservationRepository).findByUserIdAndConcertId(userId, concertId);
    }

    @Test
    void 날짜_범위별_예약_조회_테스트() {
        // given
        LocalDateTime startDate = LocalDateTime.of(2025, 5, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 5, 31, 23, 59);

        Reservation reservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        List<Reservation> reservations = List.of(reservation);

        when(reservationRepository.findByReservedAtBetween(startDate, endDate)).thenReturn(reservations);

        // when
        List<Reservation> result = reservationRepository.findByReservedAtBetween(startDate, endDate);

        // then
        assertThat(result).hasSize(1);
        verify(reservationRepository).findByReservedAtBetween(startDate, endDate);
    }

    @Test
    void 사용자별_활성_예약_조회_테스트() {
        // given
        String userId = "user-123";
        Reservation activeReservation = new Reservation(userId, 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        List<Reservation> reservations = List.of(activeReservation);

        when(reservationRepository.findActiveReservationsByUserId(userId)).thenReturn(reservations);

        // when
        List<Reservation> result = reservationRepository.findActiveReservationsByUserId(userId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
        verify(reservationRepository).findActiveReservationsByUserId(userId);
    }

    @Test
    void 콘서트별_예약_수_조회_테스트() {
        // given
        Long concertId = 1L;
        long expectedCount = 25L;

        when(reservationRepository.countByConcertId(concertId)).thenReturn(expectedCount);

        // when
        long result = reservationRepository.countByConcertId(concertId);

        // then
        assertThat(result).isEqualTo(expectedCount);
        verify(reservationRepository).countByConcertId(concertId);
    }

    @Test
    void 예약_삭제_테스트() {
        // given
        Long id = 1L;

        // when
        reservationRepository.deleteById(id);

        // then
        verify(reservationRepository).deleteById(id);
    }

    @Test
    void 모든_예약_조회_테스트() {
        // given
        Reservation reservation1 = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        Reservation reservation2 = new Reservation("user-456", 1L, 11L, "A-16", BigDecimal.valueOf(150000));
        List<Reservation> reservations = List.of(reservation1, reservation2);

        when(reservationRepository.findAll()).thenReturn(reservations);

        // when
        List<Reservation> result = reservationRepository.findAll();

        // then
        assertThat(result).hasSize(2);
        verify(reservationRepository).findAll();
    }

    @Test
    void 예약_수_확인_테스트() {
        // given
        long expectedCount = 100L;

        when(reservationRepository.count()).thenReturn(expectedCount);

        // when
        long result = reservationRepository.count();

        // then
        assertThat(result).isEqualTo(expectedCount);
        verify(reservationRepository).count();
    }
}