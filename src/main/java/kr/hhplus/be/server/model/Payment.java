package kr.hhplus.be.server.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 결제 도메인 모델
 * - 결제의 핵심 비즈니스 로직을 포함
 * - 결제 상태 관리 및 변경
 * - 결제 관련 유효성 검사
 */
public class Payment {

    private final UUID id;
    private final UUID userId;
    private final UUID reservationId;
    private final BigDecimal amount;
    private final String paymentMethod;
    private PaymentStatus status;
    private String transactionId;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Payment(UUID id, UUID userId, UUID reservationId, BigDecimal amount, String paymentMethod, PaymentStatus status) {
        this.id = id;
        this.userId = userId;
        this.reservationId = reservationId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Payment create(UUID userId, UUID reservationId, BigDecimal amount, String paymentMethod) {
        return new Payment(
            UUID.randomUUID(),
            userId,
            reservationId,
            amount,
            paymentMethod,
            PaymentStatus.PENDING
        );
    }

    public void complete(String transactionId) {
        if (this.status != PaymentStatus.PENDING) {
            throw new IllegalStateException("완료할 수 없는 결제 상태입니다.");
        }
        this.status = PaymentStatus.SUCCESS;
        this.transactionId = transactionId;
        this.updatedAt = LocalDateTime.now();
    }

    public void fail() {
        if (this.status != PaymentStatus.PENDING) {
            throw new IllegalStateException("실패 처리할 수 없는 결제 상태입니다.");
        }
        this.status = PaymentStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("취소할 수 없는 결제 상태입니다.");
        }
        this.status = PaymentStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getReservationId() {
        return reservationId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
} 
