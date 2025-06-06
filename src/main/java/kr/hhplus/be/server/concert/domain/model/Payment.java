package kr.hhplus.be.server.concert.domain.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Payment {
    private UUID id;
    private final UUID userId;
    private final UUID seatId;
    private UUID reservationId;
    private final BigDecimal amount;
    private PaymentStatus status;
    private String transactionId;
    private String paymentMethod;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Payment(UUID userId, UUID seatId, BigDecimal amount) {
        this.userId = userId;
        this.seatId = seatId;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void complete(String transactionId) {
        if (this.status != PaymentStatus.PENDING) {
            throw new IllegalStateException("결제가 대기 상태가 아닙니다.");
        }
        this.status = PaymentStatus.SUCCESS;
        this.transactionId = transactionId;
        this.updatedAt = LocalDateTime.now();
    }

    public void fail(String failureReason) {
        if (this.status != PaymentStatus.PENDING) {
            throw new IllegalStateException("결제가 대기 상태가 아닙니다.");
        }
        this.status = PaymentStatus.FAILED;
        this.failureReason = failureReason;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("결제가 완료 상태가 아닙니다.");
        }
        this.status = PaymentStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setReservationId(UUID reservationId) {
        this.reservationId = reservationId;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 