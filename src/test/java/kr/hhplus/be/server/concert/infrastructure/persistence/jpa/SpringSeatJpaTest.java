package kr.hhplus.be.server.concert.infrastructure.persistence.jpa;

import kr.hhplus.be.server.concert.infrastructure.persistence.entity.SeatEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=true"
})
class SpringSeatJpaTest {

    @Autowired
    private SpringSeatJpa seatJpa;

    @Test
    void 좌석_저장_및_조회_테스트() {
        // given
        SeatEntity seat = new SeatEntity(1L, "A-15", BigDecimal.valueOf(150000));

        // when
        SeatEntity savedSeat = seatJpa.save(seat);

        // then
        assertThat(savedSeat.getId()).isNotNull();
        assertThat(savedSeat.getConcertId()).isEqualTo(1L);
        assertThat(savedSeat.getSeatNumber()).isEqualTo("A-15");
        assertThat(savedSeat.getPrice()).isEqualTo(BigDecimal.valueOf(150000));
        assertThat(savedSeat.getReservedBy()).isNull();
        assertThat(savedSeat.getReservedAt()).isNull();
    }

    @Test
    void 콘서트별_좌석_조회_테스트() {
        // given
        Long concertId = 1L;
        SeatEntity seat1 = new SeatEntity(concertId, "A-15", BigDecimal.valueOf(150000));
        SeatEntity seat2 = new SeatEntity(concertId, "A-16", BigDecimal.valueOf(150000));
        seatJpa.save(seat1);
        seatJpa.save(seat2);

        // when
        List<SeatEntity> seats = seatJpa.findByConcertId(concertId);

        // then
        assertThat(seats).hasSize(2);
        assertThat(seats).extracting(SeatEntity::getSeatNumber)
            .containsExactlyInAnyOrder("A-15", "A-16");
    }

    @Test
    void 예약된_좌석_조회_테스트() {
        // given
        String userId = "user-123";
        SeatEntity reservedSeat = new SeatEntity(1L, "A-15", BigDecimal.valueOf(150000));
        reservedSeat.setReservedBy(userId);
        SeatEntity availableSeat = new SeatEntity(1L, "A-16", BigDecimal.valueOf(150000));
        seatJpa.save(reservedSeat);
        seatJpa.save(availableSeat);

        // when
        List<SeatEntity> reservedSeats = seatJpa.findByReservedBy(userId);

        // then
        assertThat(reservedSeats).hasSize(1);
        assertThat(reservedSeats.get(0).getSeatNumber()).isEqualTo("A-15");
    }

    @Test
    void 좌석_번호로_조회_테스트() {
        // given
        Long concertId = 1L;
        String seatNumber = "A-15";
        SeatEntity seat = new SeatEntity(concertId, seatNumber, BigDecimal.valueOf(150000));
        seatJpa.save(seat);

        // when
        Optional<SeatEntity> foundSeatOpt = seatJpa.findByConcertIdAndSeatNumber(concertId, seatNumber);

        // then
        assertThat(foundSeatOpt).isPresent();
        SeatEntity foundSeat = foundSeatOpt.get();
        assertThat(foundSeat.getConcertId()).isEqualTo(concertId);
        assertThat(foundSeat.getSeatNumber()).isEqualTo(seatNumber);
    }

    @Test
    void 좌석_예약_상태_업데이트_테스트() {
        // given
        String userId = "user-123";
        LocalDateTime now = LocalDateTime.now();
        SeatEntity seat = new SeatEntity(1L, "A-15", BigDecimal.valueOf(150000));
        seat.setCreatedAt(now);
        seat.setUpdatedAt(now);
        SeatEntity savedSeat = seatJpa.save(seat);

        // when
        savedSeat.setReservedBy(userId);
        savedSeat.setReservedAt(now);
        savedSeat.setUpdatedAt(now);
        SeatEntity updatedSeat = seatJpa.save(savedSeat);

        // then
        assertThat(updatedSeat).isNotNull();
        assertThat(updatedSeat.getReservedBy()).isEqualTo(userId);
        assertThat(updatedSeat.getReservedAt()).isNotNull();
        assertThat(updatedSeat.getUpdatedAt()).isNotNull();
    }
} 