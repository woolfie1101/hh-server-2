package kr.hhplus.be.server.concert.infrastructure.persistence.repository;

import kr.hhplus.be.server.concert.domain.model.Seat;
import kr.hhplus.be.server.concert.infrastructure.persistence.entity.SeatEntity;
import kr.hhplus.be.server.concert.infrastructure.persistence.jpa.SpringSeatJpa;
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
 * SeatRepositoryImpl 구현체 테스트
 * - 도메인 모델과 JPA 엔티티 간 변환 테스트
 * - Spring Data JPA와의 연동 테스트
 * - Mock을 사용한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class SeatRepositoryImplTest {

    @Mock
    private SpringSeatJpa springSeatJpa;

    @InjectMocks
    private SeatRepositoryImpl seatRepositoryImpl;

    @Test
    void 좌석_저장_테스트() {
        // given
        Seat seat = new Seat(
            1L,
            "A-1",
            new BigDecimal("50000")
        );

        SeatEntity savedEntity = new SeatEntity(
            1L,
            "A-1",
            new BigDecimal("50000")
        );
        savedEntity.setId(1L);

        when(springSeatJpa.save(any(SeatEntity.class))).thenReturn(savedEntity);

        // when
        Seat result = seatRepositoryImpl.save(seat);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSeatNumber()).isEqualTo("A-1");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("50000"));
        verify(springSeatJpa).save(any(SeatEntity.class));
    }

    @Test
    void ID로_좌석_조회_성공_테스트() {
        // given
        Long id = 1L;
        SeatEntity entity = new SeatEntity(
            1L,
            "A-1",
            new BigDecimal("50000")
        );
        entity.setId(id);

        when(springSeatJpa.findById(id)).thenReturn(Optional.of(entity));

        // when
        Optional<Seat> result = seatRepositoryImpl.findById(id);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getSeatNumber()).isEqualTo("A-1");
        verify(springSeatJpa).findById(id);
    }

    @Test
    void ID로_좌석_조회_실패_테스트() {
        // given
        Long id = 999L;
        when(springSeatJpa.findById(id)).thenReturn(Optional.empty());

        // when
        Optional<Seat> result = seatRepositoryImpl.findById(id);

        // then
        assertThat(result).isEmpty();
        verify(springSeatJpa).findById(id);
    }

    @Test
    void 콘서트별_모든_좌석_조회_테스트() {
        // given
        Long concertId = 1L;
        SeatEntity entity1 = new SeatEntity(concertId, "A-1", new BigDecimal("50000"));
        SeatEntity entity2 = new SeatEntity(concertId, "A-2", new BigDecimal("50000"));
        entity1.setId(1L);
        entity2.setId(2L);

        when(springSeatJpa.findByConcertId(concertId)).thenReturn(List.of(entity1, entity2));

        // when
        List<Seat> result = seatRepositoryImpl.findByConcertId(concertId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Seat::getConcertId).containsOnly(concertId);
        assertThat(result).extracting(Seat::getSeatNumber)
            .containsExactlyInAnyOrder("A-1", "A-2");
        verify(springSeatJpa).findByConcertId(concertId);
    }

    @Test
    void 콘서트별_예약가능한_좌석_조회_테스트() {
        // given
        Long concertId = 1L;
        SeatEntity entity1 = new SeatEntity(concertId, "A-1", new BigDecimal("50000"));
        SeatEntity entity2 = new SeatEntity(concertId, "A-2", new BigDecimal("50000"));
        entity1.setId(1L);
        entity2.setId(2L);
        // reservedBy가 null이므로 예약 가능

        when(springSeatJpa.findAvailableSeatsByConcertId(concertId))
            .thenReturn(List.of(entity1, entity2));

        // when
        List<Seat> result = seatRepositoryImpl.findAvailableSeatsByConcertId(concertId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(Seat::isAvailable);
        verify(springSeatJpa).findAvailableSeatsByConcertId(concertId);
    }

    @Test
    void 사용자가_예약한_좌석_조회_테스트() {
        // given
        String userId = "user123";
        SeatEntity entity1 = new SeatEntity(1L, "A-1", new BigDecimal("50000"));
        SeatEntity entity2 = new SeatEntity(2L, "B-1", new BigDecimal("70000"));
        entity1.setId(1L);
        entity1.setReservedBy(userId);
        entity1.setReservedAt(LocalDateTime.now());
        entity2.setId(2L);
        entity2.setReservedBy(userId);
        entity2.setReservedAt(LocalDateTime.now());

        when(springSeatJpa.findByReservedBy(userId)).thenReturn(List.of(entity1, entity2));

        // when
        List<Seat> result = seatRepositoryImpl.findByReservedBy(userId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Seat::getReservedBy).containsOnly(userId);
        verify(springSeatJpa).findByReservedBy(userId);
    }

    @Test
    void 만료된_예약_좌석_조회_테스트() {
        // given
        LocalDateTime cutoffTime = LocalDateTime.now();
        LocalDateTime reservedTime = cutoffTime.minusMinutes(10); // 10분 전 예약

        SeatEntity entity = new SeatEntity(1L, "A-1", new BigDecimal("50000"));
        entity.setId(1L);
        entity.setReservedBy("user123");
        entity.setReservedAt(reservedTime);

        when(springSeatJpa.findExpiredReservations(cutoffTime)).thenReturn(List.of(entity));

        // when
        List<Seat> result = seatRepositoryImpl.findExpiredReservations(cutoffTime);

        // then
        assertThat(result).hasSize(1);
        // 쿼리에서 이미 만료된 좌석만 조회했으므로, 결과가 있다는 것 자체가 만료되었음을 의미
        assertThat(result.get(0).getReservedBy()).isEqualTo("user123");
        // 예약 시간이 null이 아님을 확인
        assertThat(result.get(0).getReservedAt()).isNotNull();
        verify(springSeatJpa).findExpiredReservations(cutoffTime);
    }

    @Test
    void 콘서트와_좌석번호로_좌석_조회_테스트() {
        // given
        Long concertId = 1L;
        String seatNumber = "A-1";
        SeatEntity entity = new SeatEntity(concertId, seatNumber, new BigDecimal("50000"));
        entity.setId(1L);

        when(springSeatJpa.findByConcertIdAndSeatNumber(concertId, seatNumber))
            .thenReturn(Optional.of(entity));

        // when
        Optional<Seat> result = seatRepositoryImpl.findByConcertIdAndSeatNumber(concertId, seatNumber);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getConcertId()).isEqualTo(concertId);
        assertThat(result.get().getSeatNumber()).isEqualTo(seatNumber);
        verify(springSeatJpa).findByConcertIdAndSeatNumber(concertId, seatNumber);
    }

    @Test
    void 가격_범위로_좌석_조회_테스트() {
        // given
        BigDecimal minPrice = new BigDecimal("30000");
        BigDecimal maxPrice = new BigDecimal("70000");

        SeatEntity entity1 = new SeatEntity(1L, "A-1", new BigDecimal("50000"));
        SeatEntity entity2 = new SeatEntity(2L, "B-1", new BigDecimal("60000"));
        entity1.setId(1L);
        entity2.setId(2L);

        when(springSeatJpa.findByPriceBetween(minPrice, maxPrice))
            .thenReturn(List.of(entity1, entity2));

        // when
        List<Seat> result = seatRepositoryImpl.findByPriceBetween(minPrice, maxPrice);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(seat ->
            seat.getPrice().compareTo(minPrice) >= 0 &&
                seat.getPrice().compareTo(maxPrice) <= 0
        );
        verify(springSeatJpa).findByPriceBetween(minPrice, maxPrice);
    }

    @Test
    void 콘서트별_예약가능한_좌석수_조회_테스트() {
        // given
        Long concertId = 1L;
        long count = 50L;

        when(springSeatJpa.countAvailableSeatsByConcertId(concertId)).thenReturn(count);

        // when
        long result = seatRepositoryImpl.countAvailableSeatsByConcertId(concertId);

        // then
        assertThat(result).isEqualTo(count);
        verify(springSeatJpa).countAvailableSeatsByConcertId(concertId);
    }

    @Test
    void 좌석_예약_상태_변경_테스트() {
        // given
        Seat seat = new Seat(1L, "A-1", new BigDecimal("50000"));
        seat.assignId(1L);

        // 예약 처리
        String userId = "user123";
        seat.reserve(userId);

        SeatEntity savedEntity = new SeatEntity(1L, "A-1", new BigDecimal("50000"));
        savedEntity.setId(1L);
        savedEntity.setReservedBy(userId);
        savedEntity.setReservedAt(LocalDateTime.now());

        when(springSeatJpa.save(any(SeatEntity.class))).thenReturn(savedEntity);

        // when
        Seat result = seatRepositoryImpl.save(seat);

        // then
        assertThat(result.getReservedBy()).isEqualTo(userId);
        assertThat(result.isAvailable()).isFalse();
        verify(springSeatJpa).save(any(SeatEntity.class));
    }

    @Test
    void 좌석_예약_취소_테스트() {
        // given
        Seat seat = new Seat(1L, "A-1", new BigDecimal("50000"));
        seat.assignId(1L);
        seat.reserve("user123");

        // 예약 취소
        seat.cancelReservation("user123");

        SeatEntity savedEntity = new SeatEntity(1L, "A-1", new BigDecimal("50000"));
        savedEntity.setId(1L);
        // reservedBy가 null이므로 예약 취소됨

        when(springSeatJpa.save(any(SeatEntity.class))).thenReturn(savedEntity);

        // when
        Seat result = seatRepositoryImpl.save(seat);

        // then
        assertThat(result.getReservedBy()).isNull();
        assertThat(result.isAvailable()).isTrue();
        verify(springSeatJpa).save(any(SeatEntity.class));
    }

    @Test
    void 좌석_삭제_테스트() {
        // given
        Long id = 1L;

        // when
        seatRepositoryImpl.deleteById(id);

        // then
        verify(springSeatJpa).deleteById(id);
    }

    @Test
    void 모든_좌석_조회_테스트() {
        // given
        SeatEntity entity1 = new SeatEntity(1L, "A-1", new BigDecimal("50000"));
        SeatEntity entity2 = new SeatEntity(2L, "B-1", new BigDecimal("70000"));
        entity1.setId(1L);
        entity2.setId(2L);

        when(springSeatJpa.findAll()).thenReturn(List.of(entity1, entity2));

        // when
        List<Seat> result = seatRepositoryImpl.findAll();

        // then
        assertThat(result).hasSize(2);
        verify(springSeatJpa).findAll();
    }

    @Test
    void 좌석_수_조회_테스트() {
        // given
        long count = 100L;
        when(springSeatJpa.count()).thenReturn(count);

        // when
        long result = seatRepositoryImpl.count();

        // then
        assertThat(result).isEqualTo(count);
        verify(springSeatJpa).count();
    }
}