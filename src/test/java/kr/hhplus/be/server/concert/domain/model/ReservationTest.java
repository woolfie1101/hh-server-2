package kr.hhplus.be.server.concert.domain.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.*;

class ReservationTest {

    @Test
    void 예약_생성_테스트() {
        // given
        String userId = "user-123";
        Long concertId = 1L;
        Long seatId = 10L;
        String seatNumber = "A-15";
        BigDecimal price = BigDecimal.valueOf(150000);

        // when
        Reservation reservation = new Reservation(userId, concertId, seatId, seatNumber, price);

        // then
        assertThat(reservation.getUserId()).isEqualTo(userId);
        assertThat(reservation.getConcertId()).isEqualTo(concertId);
        assertThat(reservation.getSeatId()).isEqualTo(seatId);
        assertThat(reservation.getSeatNumber()).isEqualTo(seatNumber);
        assertThat(reservation.getPrice()).isEqualTo(price);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.TEMPORARY);
        assertThat(reservation.getReservedAt()).isNotNull();
        assertThat(reservation.getExpiresAt()).isNotNull();
        assertThat(reservation.getConfirmedAt()).isNull();
    }

    @Test
    void 예약_확정_성공_테스트() {
        // given
        Reservation reservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));

        // when
        boolean result = reservation.confirm();

        // then
        assertThat(result).isTrue();
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(reservation.getConfirmedAt()).isNotNull();
        assertThat(reservation.getConfirmedAt()).isBefore(LocalDateTime.now().plusSeconds(1));
    }

    @Test
    void 이미_확정된_예약_재확정_실패_테스트() {
        // given
        Reservation reservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        reservation.confirm(); // 먼저 확정

        // when
        boolean result = reservation.confirm(); // 재확정 시도

        // then
        assertThat(result).isFalse();
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED); // 상태 변화 없음
    }

    @Test
    void 예약_취소_성공_테스트() {
        // given
        Reservation reservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));

        // when
        boolean result = reservation.cancel();

        // then
        assertThat(result).isTrue();
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    @Test
    void 확정된_예약_취소_실패_테스트() {
        // given
        Reservation reservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        reservation.confirm(); // 먼저 확정

        // when
        boolean result = reservation.cancel(); // 취소 시도

        // then
        assertThat(result).isFalse();
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED); // 상태 변화 없음
    }

    @Test
    void 예약_만료_여부_확인_테스트() {
        // given
        Reservation reservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));

        // when & then
        assertThat(reservation.isExpired()).isFalse(); // 방금 생성했으므로 만료 안됨
    }

    @Test
    void 예약_만료_처리_테스트() {
        // given
        Reservation reservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));

        // when
        reservation.markAsExpired();

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
    }

    @Test
    void 확정된_예약_만료_처리_실패_테스트() {
        // given
        Reservation reservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        reservation.confirm(); // 먼저 확정

        // when
        reservation.markAsExpired();

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED); // 상태 변화 없음
    }

    @Test
    void 예약_가능_상태_확인_테스트() {
        // given
        Reservation reservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));

        // when & then
        assertThat(reservation.isModifiable()).isTrue(); // TEMPORARY 상태는 수정 가능

        reservation.confirm();
        assertThat(reservation.isModifiable()).isFalse(); // CONFIRMED 상태는 수정 불가

        Reservation cancelledReservation = new Reservation("user-456", 2L, 20L, "B-10", BigDecimal.valueOf(120000));
        cancelledReservation.cancel();
        assertThat(cancelledReservation.isModifiable()).isFalse(); // CANCELLED 상태는 수정 불가
    }

    @Test
    void 남은_시간_확인_테스트() {
        // given
        Reservation reservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));

        // when
        long remainingMinutes = reservation.getRemainingMinutes();

        // then
        assertThat(remainingMinutes).isGreaterThanOrEqualTo(4);
        assertThat(remainingMinutes).isLessThanOrEqualTo(5);
    }

    @Test
    void 유효하지_않은_값으로_예약_생성시_예외_발생_테스트() {
        // when & then
        assertThatThrownBy(() -> new Reservation(null, 1L, 10L, "A-15", BigDecimal.valueOf(150000)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("사용자 ID는 필수입니다.");

        assertThatThrownBy(() -> new Reservation("", 1L, 10L, "A-15", BigDecimal.valueOf(150000)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("사용자 ID는 필수입니다.");

        assertThatThrownBy(() -> new Reservation("user-123", null, 10L, "A-15", BigDecimal.valueOf(150000)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("콘서트 ID는 필수입니다.");

        assertThatThrownBy(() -> new Reservation("user-123", 1L, null, "A-15", BigDecimal.valueOf(150000)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("좌석 ID는 필수입니다.");

        assertThatThrownBy(() -> new Reservation("user-123", 1L, 10L, "", BigDecimal.valueOf(150000)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("좌석 번호는 필수입니다.");

        assertThatThrownBy(() -> new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(-1000)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("가격은 0 이상이어야 합니다.");
    }

    @Test
    void 예약_정보_유효성_검증_테스트() {
        // given
        Reservation validReservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));

        // when & then
        assertThat(validReservation.isValid()).isTrue();
    }

    @Test
    void 결제_가능_상태_확인_테스트() {
        // given
        Reservation reservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));

        // when & then
        assertThat(reservation.isPayable()).isTrue(); // TEMPORARY 상태는 결제 가능

        reservation.confirm();
        assertThat(reservation.isPayable()).isFalse(); // CONFIRMED 상태는 결제 불가

        Reservation cancelledReservation = new Reservation("user-456", 2L, 20L, "B-10", BigDecimal.valueOf(120000));
        cancelledReservation.cancel();
        assertThat(cancelledReservation.isPayable()).isFalse(); // CANCELLED 상태는 결제 불가
    }

    @Test
    void 자동_만료_처리_테스트() {
        // given
        Reservation reservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));

        // when
        reservation.processExpiredReservation(); // 아직 만료되지 않았으므로 상태 변화 없음

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.TEMPORARY);
    }

    @Test
    void 결제와_함께_예약_확정_성공_테스트() {
        // given
        Reservation reservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        reservation.assignId(1L); // ID 할당 (Repository에서 할당하는 것 시뮬레이션)

        Payment payment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        payment.complete("TXN-12345"); // 결제 완료

        // when
        boolean result = reservation.confirmWithPayment(payment);

        // then
        assertThat(result).isTrue();
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    void 결제_실패시_예약_확정_실패_테스트() {
        // given
        Reservation reservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        reservation.assignId(1L);

        Payment payment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        payment.fail("잔액 부족"); // 결제 실패

        // when
        boolean result = reservation.confirmWithPayment(payment);

        // then
        assertThat(result).isFalse();
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.TEMPORARY); // 상태 변화 없음
    }

    @Test
    void 결제와_함께_예약_취소_및_환불_테스트() {
        // given
        Reservation reservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        reservation.assignId(1L);

        Payment payment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        payment.complete("TXN-12345"); // 결제 완료

        // when
        boolean result = reservation.cancelWithRefund(payment);

        // then
        assertThat(result).isTrue();
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
        assertThat(payment.getRefundReason()).isEqualTo("예약 취소");
    }

    @Test
    void 대기중인_결제와_함께_예약_취소_테스트() {
        // given
        Reservation reservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        reservation.assignId(1L);

        Payment payment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        // 결제는 PENDING 상태

        // when
        boolean result = reservation.cancelWithRefund(payment);

        // then
        assertThat(result).isTrue();
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
    }

    @Test
    void 예약_만료시_결제_취소_테스트() {
        // given
        Reservation reservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        Payment payment = new Payment("user-123", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");

        // when
        reservation.expireWithPaymentCancellation(payment);

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
    }

    @Test
    void 잘못된_결제_정보로_예약_확정_실패_테스트() {
        // given
        Reservation reservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        reservation.assignId(1L);

        // 다른 사용자의 결제
        Payment wrongUserPayment = new Payment("user-456", 1L, BigDecimal.valueOf(150000), "CREDIT_CARD");
        wrongUserPayment.complete("TXN-12345");

        // when
        boolean result = reservation.confirmWithPayment(wrongUserPayment);

        // then
        assertThat(result).isFalse();
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.TEMPORARY);
    }

    @Test
    void 예약_결제_상태_확인_테스트() {
        // given
        Reservation reservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));

        // when & then
        assertThat(reservation.isPaid()).isFalse(); // 초기 상태는 결제되지 않음

        // 결제 완료 상태로 변경
        reservation.confirm();
        assertThat(reservation.isPaid()).isTrue(); // 결제 완료 상태 확인

        // 취소 상태로 변경
        reservation.cancel();
        assertThat(reservation.isPaid()).isFalse(); // 취소 상태는 결제되지 않음
    }
}