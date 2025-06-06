package kr.hhplus.be.server.concert.interfaces.dto;

import java.util.UUID;

/**
 * 결제 처리 요청 DTO
 */
public class PaymentProcessRequest {
    private UUID reservationId;
    private String paymentMethod;

    public PaymentProcessRequest() {}

    public PaymentProcessRequest(UUID reservationId, String paymentMethod) {
        this.reservationId = reservationId;
        this.paymentMethod = paymentMethod;
    }

    public UUID getReservationId() {
        return reservationId;
    }

    public void setReservationId(UUID reservationId) {
        this.reservationId = reservationId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
} 