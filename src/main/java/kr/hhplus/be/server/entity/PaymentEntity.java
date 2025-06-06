package kr.hhplus.be.server.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.concert.domain.model.Payment;
import kr.hhplus.be.server.concert.domain.model.PaymentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 결제 JPA 엔티티
 */
@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "seat_id", nullable = false)
    private UUID seatId;

    @Column(name = "reservation_id")
    private UUID reservationId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static PaymentEntity fromDomain(Payment payment) {
        PaymentEntity entity = new PaymentEntity();
        entity.id = payment.getId();
        entity.userId = payment.getUserId();
        entity.seatId = payment.getSeatId();
        entity.reservationId = payment.getReservationId();
        entity.amount = payment.getAmount();
        entity.status = payment.getStatus();
        entity.transactionId = payment.getTransactionId();
        entity.paymentMethod = payment.getPaymentMethod();
        entity.failureReason = payment.getFailureReason();
        entity.createdAt = payment.getCreatedAt();
        entity.updatedAt = payment.getUpdatedAt();
        return entity;
    }

    public Payment toDomain() {
        Payment payment = new Payment(
            this.userId,
            this.seatId,
            this.amount
        );
        payment.setId(this.id);
        payment.setReservationId(this.reservationId);
        payment.setPaymentMethod(this.paymentMethod);
        payment.setCreatedAt(this.createdAt);
        payment.setUpdatedAt(this.updatedAt);
        return payment;
    }

    public void complete() {
        this.status = PaymentStatus.SUCCESS;
        this.updatedAt = LocalDateTime.now();
    }

    public void fail(String failureReason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = failureReason;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = PaymentStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public UUID getReservationId() {
        return reservationId;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getSeatId() {
        return seatId;
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

    public String getFailureReason() {
        return failureReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
} 