package kr.hhplus.be.server.concert.application.event;

import kr.hhplus.be.server.concert.domain.model.Payment;
import kr.hhplus.be.server.concert.domain.model.Reservation;
import java.time.LocalDateTime;

/**
 * 예약 관련 도메인 이벤트들
 * - 예약 생성, 결제 완료, 예약 취소 이벤트
 * - 이벤트 기반 아키텍처를 위한 도메인 이벤트
 */
public class ReservationEvents {

    /**
     * 예약 생성 이벤트
     */
    public static class ReservationCreated {
        private final Reservation reservation;
        private final LocalDateTime occurredAt;

        public ReservationCreated(Reservation reservation) {
            this.reservation = reservation;
            this.occurredAt = LocalDateTime.now();
        }

        public Reservation getReservation() {
            return reservation;
        }

        public LocalDateTime getOccurredAt() {
            return occurredAt;
        }

        @Override
        public String toString() {
            return "ReservationCreated{" +
                "reservationId=" + reservation.getId() +
                ", userId='" + reservation.getUserId() + '\'' +
                ", occurredAt=" + occurredAt +
                '}';
        }
    }

    /**
     * 결제 완료 이벤트
     */
    public static class PaymentCompleted {
        private final Payment payment;
        private final Reservation reservation;
        private final LocalDateTime occurredAt;

        public PaymentCompleted(Payment payment, Reservation reservation) {
            this.payment = payment;
            this.reservation = reservation;
            this.occurredAt = LocalDateTime.now();
        }

        public Payment getPayment() {
            return payment;
        }

        public Reservation getReservation() {
            return reservation;
        }

        public LocalDateTime getOccurredAt() {
            return occurredAt;
        }

        @Override
        public String toString() {
            return "PaymentCompleted{" +
                "paymentId=" + payment.getId() +
                ", reservationId=" + reservation.getId() +
                ", amount=" + payment.getAmount() +
                ", occurredAt=" + occurredAt +
                '}';
        }
    }

    /**
     * 예약 취소 이벤트
     */
    public static class ReservationCancelled {
        private final Reservation reservation;
        private final String reason;
        private final LocalDateTime occurredAt;

        public ReservationCancelled(Reservation reservation) {
            this(reservation, "사용자 요청");
        }

        public ReservationCancelled(Reservation reservation, String reason) {
            this.reservation = reservation;
            this.reason = reason;
            this.occurredAt = LocalDateTime.now();
        }

        public Reservation getReservation() {
            return reservation;
        }

        public String getReason() {
            return reason;
        }

        public LocalDateTime getOccurredAt() {
            return occurredAt;
        }

        @Override
        public String toString() {
            return "ReservationCancelled{" +
                "reservationId=" + reservation.getId() +
                ", userId='" + reservation.getUserId() + '\'' +
                ", reason='" + reason + '\'' +
                ", occurredAt=" + occurredAt +
                '}';
        }
    }
}