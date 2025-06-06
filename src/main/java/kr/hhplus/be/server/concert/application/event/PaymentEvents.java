package kr.hhplus.be.server.concert.application.event;

import kr.hhplus.be.server.model.Payment;
import kr.hhplus.be.server.model.Reservation;

/**
 * 결제 관련 이벤트 클래스들
 */
public class PaymentEvents {

    public static class PaymentCompleted {
        private final Payment payment;
        private final Reservation reservation;

        public PaymentCompleted(Payment payment, Reservation reservation) {
            this.payment = payment;
            this.reservation = reservation;
        }

        public Payment getPayment() {
            return payment;
        }

        public Reservation getReservation() {
            return reservation;
        }
    }

    public static class PaymentCancelled {
        private final Payment payment;

        public PaymentCancelled(Payment payment) {
            this.payment = payment;
        }

        public Payment getPayment() {
            return payment;
        }
    }

    public static class PaymentRefunded {
        private final Payment payment;

        public PaymentRefunded(Payment payment) {
            this.payment = payment;
        }

        public Payment getPayment() {
            return payment;
        }
    }
} 