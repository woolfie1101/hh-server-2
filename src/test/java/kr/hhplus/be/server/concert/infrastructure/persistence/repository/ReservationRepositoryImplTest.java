package kr.hhplus.be.server.concert.infrastructure.persistence.repository;

import kr.hhplus.be.server.concert.domain.model.Reservation;
import kr.hhplus.be.server.concert.domain.model.ReservationStatus;
import kr.hhplus.be.server.concert.infrastructure.persistence.entity.ReservationEntity;
import kr.hhplus.be.server.concert.infrastructure.persistence.jpa.SpringReservationJpa;
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
 * ReservationRepositoryImpl 구현체 테스트
 * - 도메인 모델과 JPA 엔티티 간 변환 테스트
 * - Spring Data JPA와의 연동 테스트
 * - Mock을 사용한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class ReservationRepositoryImplTest {

    @Mock
    private SpringReservationJpa springReservationJpa;

    @InjectMocks
    private ReservationRepositoryImpl reservationRepositoryImpl;

    @Test
    void 예약_저장_테스트() {
        // given
        Reservation reservation = new Reservation(
            "user123",
            1L,
            1L,
            "A-1",
            new BigDecimal("50000")
        );

        ReservationEntity savedEntity = new ReservationEntity(
            "user123",
            1L,
            1L,
            "A-1",
            new BigDecimal("50000")
        );
        savedEntity.setId(1L);

        when(springReservationJpa.save(any(ReservationEntity.class))).thenReturn(savedEntity);

        // when
        Reservation result = reservationRepositoryImpl.save(reservation);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo("user123");
        assertThat(result.getSeatNumber()).isEqualTo("A-1");
        verify(springReservationJpa).save(any(ReservationEntity.class));
    }

    @Test
    void ID로_예약_조회_성공_테스트() {
        // given
        Long id = 1L;
        ReservationEntity entity = new ReservationEntity(
            "user123",
            1L,
            1L,
            "A-1",
            new BigDecimal("50000")
        );
        entity.setId(id);

        when(springReservationJpa.findById(id)).thenReturn(Optional.of(entity));

        // when
        Optional<Reservation> result = reservationRepositoryImpl.findById(id);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getUserId()).isEqualTo("user123");
        verify(springReservationJpa).findById(id);
    }

    @Test
    void ID로_예약_조회_실패_테스트() {
        // given
        Long id = 999L;
        when(springReservationJpa.findById(id)).thenReturn(Optional.empty());

        // when
        Optional<Reservation> result = reservationRepositoryImpl.findById(id);

        // then
        assertThat(result).isEmpty();
        verify(springReservationJpa).findById(id);
    }

    @Test
    void 사용자별_예약_조회_테스트() {
        // given
        String userId = "user123";
        ReservationEntity entity1 = new ReservationEntity(userId, 1L, 1L, "A-1", new BigDecimal("30000"));
        ReservationEntity entity2 = new ReservationEntity(userId, 2L, 2L, "B-2", new BigDecimal("50000"));
        entity1.setId(1L);
        entity2.setId(2L);

        when(springReservationJpa.findByUserId(userId)).thenReturn(List.of(entity1, entity2));

        // when
        List<Reservation> result = reservationRepositoryImpl.findByUserId(userId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Reservation::getUserId).containsOnly(userId);
        assertThat(result).extracting(Reservation::getSeatNumber)
            .containsExactlyInAnyOrder("A-1", "B-2");
        verify(springReservationJpa).findByUserId(userId);
    }

    @Test
    void 콘서트별_예약_조회_테스트() {
        // given
        Long concertId = 1L;
        ReservationEntity entity1 = new ReservationEntity("user1", concertId, 1L, "A-1", new BigDecimal("50000"));
        ReservationEntity entity2 = new ReservationEntity("user2", concertId, 2L, "A-2", new BigDecimal("50000"));
        entity1.setId(1L);
        entity2.setId(2L);

        when(springReservationJpa.findByConcertId(concertId)).thenReturn(List.of(entity1, entity2));

        // when
        List<Reservation> result = reservationRepositoryImpl.findByConcertId(concertId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Reservation::getConcertId).containsOnly(concertId);
        verify(springReservationJpa).findByConcertId(concertId);
    }

    @Test
    void 좌석별_예약_조회_테스트() {
        // given
        Long seatId = 1L;
        ReservationEntity entity = new ReservationEntity("user123", 1L, seatId, "A-1", new BigDecimal("50000"));
        entity.setId(1L);

        when(springReservationJpa.findBySeatId(seatId)).thenReturn(Optional.of(entity));

        // when
        Optional<Reservation> result = reservationRepositoryImpl.findBySeatId(seatId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getSeatId()).isEqualTo(seatId);
        verify(springReservationJpa).findBySeatId(seatId);
    }

    @Test
    void 상태별_예약_조회_테스트() {
        // given
        ReservationStatus status = ReservationStatus.CONFIRMED;
        ReservationEntity entity1 = new ReservationEntity("user1", 1L, 1L, "A-1", new BigDecimal("50000"));
        ReservationEntity entity2 = new ReservationEntity("user2", 1L, 2L, "A-2", new BigDecimal("50000"));
        entity1.setId(1L);
        entity1.setStatus(status);
        entity2.setId(2L);
        entity2.setStatus(status);

        when(springReservationJpa.findByStatus(status)).thenReturn(List.of(entity1, entity2));

        // when
        List<Reservation> result = reservationRepositoryImpl.findByStatus(status);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Reservation::getStatus).containsOnly(status);
        verify(springReservationJpa).findByStatus(status);
    }

    @Test
    void 만료된_예약_조회_테스트() {
        // given
        LocalDateTime cutoffTime = LocalDateTime.now();
        LocalDateTime reservedTime = cutoffTime.minusMinutes(10);
        LocalDateTime expiresTime = cutoffTime.minusMinutes(5); // 5분 전에 만료됨

        ReservationEntity entity = new ReservationEntity("user123", 1L, 1L, "A-1", new BigDecimal("50000"));
        entity.setId(1L);
        entity.setStatus(ReservationStatus.TEMPORARY);
        entity.setReservedAt(reservedTime);
        entity.setExpiresAt(expiresTime);

        when(springReservationJpa.findExpiredReservations(cutoffTime)).thenReturn(List.of(entity));

        // when
        List<Reservation> result = reservationRepositoryImpl.findExpiredReservations(cutoffTime);

        // then
        assertThat(result).hasSize(1);
        // 쿼리에서 이미 만료된 예약만 조회했으므로, 결과가 있다는 것 자체가 만료되었음을 의미
        assertThat(result.get(0).getStatus()).isEqualTo(ReservationStatus.TEMPORARY);
        assertThat(result.get(0).getUserId()).isEqualTo("user123");
        verify(springReservationJpa).findExpiredReservations(cutoffTime);
    }

    @Test
    void 사용자와_콘서트별_예약_조회_테스트() {
        // given
        String userId = "user123";
        Long concertId = 1L;
        ReservationEntity entity = new ReservationEntity(userId, concertId, 1L, "A-1", new BigDecimal("50000"));
        entity.setId(1L);

        when(springReservationJpa.findByUserIdAndConcertId(userId, concertId))
            .thenReturn(List.of(entity));

        // when
        List<Reservation> result = reservationRepositoryImpl.findByUserIdAndConcertId(userId, concertId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
        assertThat(result.get(0).getConcertId()).isEqualTo(concertId);
        verify(springReservationJpa).findByUserIdAndConcertId(userId, concertId);
    }

    @Test
    void 날짜_범위별_예약_조회_테스트() {
        // given
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 6, 30, 23, 59, 59);

        ReservationEntity entity = new ReservationEntity("user123", 1L, 1L, "A-1", new BigDecimal("50000"));
        entity.setId(1L);
        entity.setReservedAt(LocalDateTime.of(2025, 1, 15, 10, 0, 0));

        when(springReservationJpa.findByReservedAtBetween(startDate, endDate))
            .thenReturn(List.of(entity));

        // when
        List<Reservation> result = reservationRepositoryImpl.findByReservedAtBetween(startDate, endDate);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReservedAt()).isBetween(startDate, endDate);
        verify(springReservationJpa).findByReservedAtBetween(startDate, endDate);
    }

    @Test
    void 사용자의_활성_예약_조회_테스트() {
        // given
        String userId = "user123";
        ReservationEntity entity1 = new ReservationEntity(userId, 1L, 1L, "A-1", new BigDecimal("50000"));
        ReservationEntity entity2 = new ReservationEntity(userId, 2L, 2L, "B-2", new BigDecimal("50000"));
        entity1.setId(1L);
        entity1.setStatus(ReservationStatus.TEMPORARY);
        entity2.setId(2L);
        entity2.setStatus(ReservationStatus.CONFIRMED);

        when(springReservationJpa.findActiveReservationsByUserId(userId))
            .thenReturn(List.of(entity1, entity2));

        // when
        List<Reservation> result = reservationRepositoryImpl.findActiveReservationsByUserId(userId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Reservation::getStatus)
            .containsExactlyInAnyOrder(ReservationStatus.TEMPORARY, ReservationStatus.CONFIRMED);
        verify(springReservationJpa).findActiveReservationsByUserId(userId);
    }

    @Test
    void 콘서트별_예약_수_조회_테스트() {
        // given
        Long concertId = 1L;
        long count = 50L;

        when(springReservationJpa.countByConcertId(concertId)).thenReturn(count);

        // when
        long result = reservationRepositoryImpl.countByConcertId(concertId);

        // then
        assertThat(result).isEqualTo(count);
        verify(springReservationJpa).countByConcertId(concertId);
    }

    @Test
    void 예약_삭제_테스트() {
        // given
        Long id = 1L;

        // when
        reservationRepositoryImpl.deleteById(id);

        // then
        verify(springReservationJpa).deleteById(id);
    }

    @Test
    void 모든_예약_조회_테스트() {
        // given
        ReservationEntity entity1 = new ReservationEntity("user1", 1L, 1L, "A-1", new BigDecimal("50000"));
        ReservationEntity entity2 = new ReservationEntity("user2", 2L, 2L, "B-2", new BigDecimal("50000"));
        entity1.setId(1L);
        entity2.setId(2L);

        when(springReservationJpa.findAll()).thenReturn(List.of(entity1, entity2));

        // when
        List<Reservation> result = reservationRepositoryImpl.findAll();

        // then
        assertThat(result).hasSize(2);
        verify(springReservationJpa).findAll();
    }

    @Test
    void 예약_수_조회_테스트() {
        // given
        long count = 100L;
        when(springReservationJpa.count()).thenReturn(count);

        // when
        long result = reservationRepositoryImpl.count();

        // then
        assertThat(result).isEqualTo(count);
        verify(springReservationJpa).count();
    }
}