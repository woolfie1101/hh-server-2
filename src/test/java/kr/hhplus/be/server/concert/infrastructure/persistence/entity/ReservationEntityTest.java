package kr.hhplus.be.server.concert.infrastructure.persistence.entity;

import kr.hhplus.be.server.concert.domain.model.Reservation;
import kr.hhplus.be.server.concert.domain.model.ReservationStatus;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.*;

/**
 * ReservationEntity JPA 엔티티 테스트
 * - JPA 매핑 및 변환 로직 검증
 * - 도메인 모델과 엔티티 간 변환 테스트
 */
class ReservationEntityTest {

    @Test
    void 엔티티_생성_테스트() {
        // given
        String userId = "user-123";
        Long concertId = 1L;
        Long seatId = 10L;
        String seatNumber = "A-15";
        BigDecimal price = BigDecimal.valueOf(150000);

        // when
        ReservationEntity entity = new ReservationEntity(userId, concertId, seatId, seatNumber, price);

        // then
        assertThat(entity.getUserId()).isEqualTo(userId);
        assertThat(entity.getConcertId()).isEqualTo(concertId);
        assertThat(entity.getSeatId()).isEqualTo(seatId);
        assertThat(entity.getSeatNumber()).isEqualTo(seatNumber);
        assertThat(entity.getPrice()).isEqualTo(price);
        assertThat(entity.getStatus()).isEqualTo(ReservationStatus.TEMPORARY);
        assertThat(entity.getId()).isNull(); // 아직 저장 전이므로 ID 없음
        assertThat(entity.getReservedAt()).isNotNull();
        assertThat(entity.getExpiresAt()).isNotNull();
        assertThat(entity.getConfirmedAt()).isNull();
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
    }

    @Test
    void 도메인_모델로_변환_테스트() {
        // given
        ReservationEntity entity = new ReservationEntity(
            "user-123",
            1L,
            10L,
            "A-15",
            BigDecimal.valueOf(150000)
        );
        entity.setId(1L); // Repository에서 저장 후 ID 할당 시뮬레이션

        // when
        Reservation reservation = entity.toDomain();

        // then
        assertThat(reservation.getId()).isEqualTo(1L);
        assertThat(reservation.getUserId()).isEqualTo("user-123");
        assertThat(reservation.getConcertId()).isEqualTo(1L);
        assertThat(reservation.getSeatId()).isEqualTo(10L);
        assertThat(reservation.getSeatNumber()).isEqualTo("A-15");
        assertThat(reservation.getPrice()).isEqualTo(BigDecimal.valueOf(150000));
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.TEMPORARY);
    }

    @Test
    void 확정된_예약_도메인_모델로_변환_테스트() {
        // given
        ReservationEntity entity = new ReservationEntity(
            "user-123",
            1L,
            10L,
            "A-15",
            BigDecimal.valueOf(150000)
        );
        entity.setId(1L);
        entity.setStatus(ReservationStatus.CONFIRMED);

        // when
        Reservation reservation = entity.toDomain();

        // then
        assertThat(reservation.getId()).isEqualTo(1L);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(reservation.getConfirmedAt()).isNotNull();
    }

    @Test
    void 도메인_모델에서_엔티티_생성_테스트() {
        // given
        Reservation reservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        reservation.assignId(1L);

        // when
        ReservationEntity entity = ReservationEntity.fromDomain(reservation);

        // then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getUserId()).isEqualTo("user-123");
        assertThat(entity.getConcertId()).isEqualTo(1L);
        assertThat(entity.getSeatId()).isEqualTo(10L);
        assertThat(entity.getSeatNumber()).isEqualTo("A-15");
        assertThat(entity.getPrice()).isEqualTo(BigDecimal.valueOf(150000));
        assertThat(entity.getStatus()).isEqualTo(ReservationStatus.TEMPORARY);
    }

    @Test
    void 확정된_도메인_모델에서_엔티티_생성_테스트() {
        // given
        Reservation reservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        reservation.assignId(1L);
        reservation.confirm();

        // when
        ReservationEntity entity = ReservationEntity.fromDomain(reservation);

        // then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(entity.getConfirmedAt()).isNotNull();
    }

    @Test
    void 도메인_모델_업데이트_테스트() {
        // given
        ReservationEntity entity = new ReservationEntity(
            "user-123",
            1L,
            10L,
            "A-15",
            BigDecimal.valueOf(150000)
        );
        entity.setId(1L);

        Reservation updatedReservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        updatedReservation.assignId(1L);
        updatedReservation.confirm(); // 예약 확정

        // when
        entity.updateFromDomain(updatedReservation);

        // then
        assertThat(entity.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(entity.getConfirmedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
    }

    @Test
    void 취소된_예약_도메인_업데이트_테스트() {
        // given
        ReservationEntity entity = new ReservationEntity(
            "user-123",
            1L,
            10L,
            "A-15",
            BigDecimal.valueOf(150000)
        );
        entity.setId(1L);

        Reservation cancelledReservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        cancelledReservation.assignId(1L);
        cancelledReservation.cancel();

        // when
        entity.updateFromDomain(cancelledReservation);

        // then
        assertThat(entity.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(entity.getConfirmedAt()).isNull();
    }

    @Test
    void 만료된_예약_도메인_변환_테스트() {
        // given
        ReservationEntity entity = new ReservationEntity(
            "user-123",
            1L,
            10L,
            "A-15",
            BigDecimal.valueOf(150000)
        );
        entity.setId(1L);
        entity.setStatus(ReservationStatus.EXPIRED);

        // when
        Reservation reservation = entity.toDomain();

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
        assertThat(reservation.isExpired()).isTrue();
    }

    @Test
    void 엔티티_동등성_비교_테스트() {
        // given
        ReservationEntity entity1 = new ReservationEntity(
            "user-123",
            1L,
            10L,
            "A-15",
            BigDecimal.valueOf(150000)
        );
        entity1.setId(1L);

        ReservationEntity entity2 = new ReservationEntity(
            "user-456",
            2L,
            20L,
            "B-10",
            BigDecimal.valueOf(120000)
        );
        entity2.setId(1L);

        ReservationEntity entity3 = new ReservationEntity(
            "user-789",
            3L,
            30L,
            "C-5",
            BigDecimal.valueOf(100000)
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
        ReservationEntity entity = new ReservationEntity();

        // then
        assertThat(entity.getId()).isNull();
        assertThat(entity.getUserId()).isNull();
        assertThat(entity.getConcertId()).isNull();
        assertThat(entity.getSeatId()).isNull();
        assertThat(entity.getSeatNumber()).isNull();
        assertThat(entity.getPrice()).isNull();
        assertThat(entity.getStatus()).isNull();
    }

    @Test
    void toString_테스트() {
        // given
        ReservationEntity entity = new ReservationEntity(
            "user-123",
            1L,
            10L,
            "A-15",
            BigDecimal.valueOf(150000)
        );
        entity.setId(1L);

        // when
        String result = entity.toString();

        // then
        assertThat(result).contains("ReservationEntity");
        assertThat(result).contains("id=1");
        assertThat(result).contains("userId='user-123'");
        assertThat(result).contains("concertId=1");
        assertThat(result).contains("seatId=10");
        assertThat(result).contains("seatNumber='A-15'");
        assertThat(result).contains("price=150000");
        assertThat(result).contains("status=TEMPORARY");
    }

    @Test
    void 만료_시간_자동_설정_테스트() {
        // given & when
        ReservationEntity entity = new ReservationEntity(
            "user-123",
            1L,
            10L,
            "A-15",
            BigDecimal.valueOf(150000)
        );

        // then
        assertThat(entity.getExpiresAt()).isAfter(entity.getReservedAt());
        assertThat(entity.getExpiresAt()).isEqualTo(entity.getReservedAt().plusMinutes(5));
    }
}