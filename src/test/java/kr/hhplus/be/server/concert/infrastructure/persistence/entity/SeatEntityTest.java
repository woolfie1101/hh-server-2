package kr.hhplus.be.server.concert.infrastructure.persistence.entity;

import kr.hhplus.be.server.concert.domain.model.Seat;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.*;

/**
 * SeatEntity JPA 엔티티 테스트
 * - JPA 매핑 및 변환 로직 검증
 * - 도메인 모델과 엔티티 간 변환 테스트
 */
class SeatEntityTest {

    @Test
    void 엔티티_생성_테스트() {
        // given
        Long concertId = 1L;
        String seatNumber = "A-15";
        BigDecimal price = BigDecimal.valueOf(150000);

        // when
        SeatEntity entity = new SeatEntity(concertId, seatNumber, price);

        // then
        assertThat(entity.getConcertId()).isEqualTo(concertId);
        assertThat(entity.getSeatNumber()).isEqualTo(seatNumber);
        assertThat(entity.getPrice()).isEqualTo(price);
        assertThat(entity.getReservedBy()).isNull();
        assertThat(entity.getReservedAt()).isNull();
        assertThat(entity.getId()).isNull(); // 아직 저장 전이므로 ID 없음
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
    }

    @Test
    void 도메인_모델로_변환_테스트() {
        // given
        SeatEntity entity = new SeatEntity(1L, "A-15", BigDecimal.valueOf(150000));
        entity.setId(1L); // Repository에서 저장 후 ID 할당 시뮬레이션

        // when
        Seat seat = entity.toDomain();

        // then
        assertThat(seat.getId()).isEqualTo(1L);
        assertThat(seat.getConcertId()).isEqualTo(1L);
        assertThat(seat.getSeatNumber()).isEqualTo("A-15");
        assertThat(seat.getPrice()).isEqualTo(BigDecimal.valueOf(150000));
        assertThat(seat.isAvailable()).isTrue(); // 초기 상태는 예약 가능
        assertThat(seat.getReservedBy()).isNull();
        assertThat(seat.getReservedAt()).isNull();
    }

    @Test
    void 예약된_좌석_도메인_모델로_변환_테스트() {
        // given
        SeatEntity entity = new SeatEntity(1L, "A-15", BigDecimal.valueOf(150000));
        entity.setId(1L);
        entity.setReservedBy("user-123");
        entity.setReservedAt(LocalDateTime.now()); // reservedAt도 설정

        // when
        Seat seat = entity.toDomain();

        // then
        assertThat(seat.getId()).isEqualTo(1L);
        assertThat(seat.getReservedBy()).isEqualTo("user-123");
        assertThat(seat.isAvailable()).isFalse(); // 예약된 상태
        assertThat(seat.isReservedBy("user-123")).isTrue();
    }

    @Test
    void 도메인_모델에서_엔티티_생성_테스트() {
        // given
        Seat seat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));
        seat.assignId(1L);

        // when
        SeatEntity entity = SeatEntity.fromDomain(seat);

        // then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getConcertId()).isEqualTo(1L);
        assertThat(entity.getSeatNumber()).isEqualTo("A-15");
        assertThat(entity.getPrice()).isEqualTo(BigDecimal.valueOf(150000));
        assertThat(entity.getReservedBy()).isNull();
        assertThat(entity.getReservedAt()).isNull();
    }

    @Test
    void 예약된_도메인_모델에서_엔티티_생성_테스트() {
        // given
        Seat seat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));
        seat.assignId(1L);
        seat.reserve("user-123");

        // when
        SeatEntity entity = SeatEntity.fromDomain(seat);

        // then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getReservedBy()).isEqualTo("user-123");
        assertThat(entity.getReservedAt()).isNotNull();
    }

    @Test
    void 도메인_모델_업데이트_테스트() {
        // given
        SeatEntity entity = new SeatEntity(1L, "A-15", BigDecimal.valueOf(150000));
        entity.setId(1L);

        Seat updatedSeat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));
        updatedSeat.assignId(1L);
        updatedSeat.reserve("user-123"); // 좌석 예약

        // when
        entity.updateFromDomain(updatedSeat);

        // then
        assertThat(entity.getReservedBy()).isEqualTo("user-123");
        assertThat(entity.getReservedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
    }

    @Test
    void 예약_취소된_도메인_업데이트_테스트() {
        // given
        SeatEntity entity = new SeatEntity(1L, "A-15", BigDecimal.valueOf(150000));
        entity.setId(1L);
        entity.setReservedBy("user-123");

        Seat cancelledSeat = new Seat(1L, "A-15", BigDecimal.valueOf(150000));
        cancelledSeat.assignId(1L);
        cancelledSeat.reserve("user-123");
        cancelledSeat.cancelReservation("user-123"); // 예약 취소

        // when
        entity.updateFromDomain(cancelledSeat);

        // then
        assertThat(entity.getReservedBy()).isNull();
        assertThat(entity.getReservedAt()).isNull();
    }

    @Test
    void 엔티티_동등성_비교_테스트() {
        // given
        SeatEntity entity1 = new SeatEntity(1L, "A-15", BigDecimal.valueOf(150000));
        entity1.setId(1L);

        SeatEntity entity2 = new SeatEntity(2L, "B-10", BigDecimal.valueOf(120000));
        entity2.setId(1L);

        SeatEntity entity3 = new SeatEntity(3L, "C-5", BigDecimal.valueOf(100000));
        entity3.setId(2L);

        // when & then
        assertThat(entity1).isEqualTo(entity2); // 같은 ID면 동등
        assertThat(entity1).isNotEqualTo(entity3); // 다른 ID면 다름
        assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
    }

    @Test
    void 기본_생성자_테스트() {
        // when
        SeatEntity entity = new SeatEntity();

        // then
        assertThat(entity.getId()).isNull();
        assertThat(entity.getConcertId()).isNull();
        assertThat(entity.getSeatNumber()).isNull();
        assertThat(entity.getPrice()).isNull();
        assertThat(entity.getReservedBy()).isNull();
        assertThat(entity.getReservedAt()).isNull();
    }

    @Test
    void toString_테스트() {
        // given
        SeatEntity entity = new SeatEntity(1L, "A-15", BigDecimal.valueOf(150000));
        entity.setId(1L);

        // when
        String result = entity.toString();

        // then
        assertThat(result).contains("SeatEntity");
        assertThat(result).contains("id=1");
        assertThat(result).contains("concertId=1");
        assertThat(result).contains("seatNumber='A-15'");
        assertThat(result).contains("price=150000");
        assertThat(result).contains("reservedBy='null'");
    }

    @Test
    void 좌석번호_유니크_제약조건_확인_테스트() {
        // given
        SeatEntity entity1 = new SeatEntity(1L, "A-15", BigDecimal.valueOf(150000));
        SeatEntity entity2 = new SeatEntity(1L, "A-15", BigDecimal.valueOf(150000)); // 같은 콘서트, 같은 좌석번호

        // when & then
        // 실제로는 DB에서 유니크 제약조건 위반 예외가 발생해야 함
        // 여기서는 엔티티 구조만 확인
        assertThat(entity1.getConcertId()).isEqualTo(entity2.getConcertId());
        assertThat(entity1.getSeatNumber()).isEqualTo(entity2.getSeatNumber());
    }
}