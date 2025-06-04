package kr.hhplus.be.server.concert.infrastructure.persistence.entity;

import kr.hhplus.be.server.concert.domain.model.Payment;
import kr.hhplus.be.server.concert.domain.model.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment JPA 엔티티
 * - 데이터베이스 테이블 매핑 전용 객체
 * - 도메인 모델과 분리된 순수한 데이터 구조
 * - JPA 어노테이션과 DB 제약사항 포함
 */
@Entity
@Table(name = "payments")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "refund_reason", length = 500)
    private String refundReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 기본 생성자 (JPA 필수)
    protected PaymentEntity() {}

    // 비즈니스 생성자
    public PaymentEntity(String userId, Long reservationId, BigDecimal amount, String paymentMethod) {
        this.userId = userId;
        this.reservationId = reservationId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.PENDING;
        this.transactionId = null;
        this.failureReason = null;
        this.refundReason = null;
        this.createdAt = LocalDateTime.now();
        this.completedAt = null;
        this.refundedAt = null;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 도메인 모델로 변환
     */
    public Payment toDomain() {
        Payment payment = new Payment(this.userId, this.reservationId, this.amount, this.paymentMethod);
        if (this.id != null) {
            payment.assignId(this.id);
        }

        // 상태별로 도메인 객체의 상태 복원
        restoreDomainState(payment);

        return payment;
    }

    /**
     * 도메인 모델에서 엔티티 생성
     */
    public static PaymentEntity fromDomain(Payment payment) {
        PaymentEntity entity = new PaymentEntity(
            payment.getUserId(),
            payment.getReservationId(),
            payment.getAmount(),
            payment.getPaymentMethod()
        );

        if (payment.getId() != null) {
            entity.setId(payment.getId());
        }

        // 도메인 상태를 엔티티에 반영
        entity.setStatus(payment.getStatus());
        entity.setTransactionId(payment.getTransactionId());
        entity.setFailureReason(payment.getFailureReason());
        entity.setRefundReason(payment.getRefundReason());
        entity.setCreatedAt(payment.getCreatedAt());
        entity.setCompletedAt(payment.getCompletedAt());
        entity.setRefundedAt(payment.getRefundedAt());

        return entity;
    }

    /**
     * 도메인 모델의 변경사항을 엔티티에 반영
     */
    public void updateFromDomain(Payment payment) {
        this.userId = payment.getUserId();
        this.reservationId = payment.getReservationId();
        this.amount = payment.getAmount();
        this.paymentMethod = payment.getPaymentMethod();
        this.status = payment.getStatus();
        this.transactionId = payment.getTransactionId();
        this.failureReason = payment.getFailureReason();
        this.refundReason = payment.getRefundReason();
        this.completedAt = payment.getCompletedAt();
        this.refundedAt = payment.getRefundedAt();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 도메인 객체의 상태를 복원하는 헬퍼 메서드
     */
    private void restoreDomainState(Payment payment) {
        // 상태에 따라 도메인 객체를 적절한 상태로 복원
        switch (this.status) {
            case COMPLETED:
                if (this.transactionId != null) {
                    payment.complete(this.transactionId);
                }
                break;
            case FAILED:
                if (this.failureReason != null) {
                    payment.fail(this.failureReason);
                }
                break;
            case CANCELLED:
                payment.cancel();
                break;
            case REFUNDED:
                // 먼저 완료 상태로 만든 후 환불 처리
                if (this.transactionId != null) {
                    payment.complete(this.transactionId);
                }
                if (this.refundReason != null) {
                    payment.refund(this.refundReason);
                }
                break;
            case PENDING:
            default:
                // PENDING은 기본 상태이므로 추가 처리 불필요
                break;
        }
    }

    // JPA 생명주기 콜백
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getRefundedAt() {
        return refundedAt;
    }

    public void setRefundedAt(LocalDateTime refundedAt) {
        this.refundedAt = refundedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        PaymentEntity that = (PaymentEntity) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PaymentEntity{" +
            "id=" + id +
            ", userId='" + userId + '\'' +
            ", reservationId=" + reservationId +
            ", amount=" + amount +
            ", paymentMethod='" + paymentMethod + '\'' +
            ", status=" + status +
            ", transactionId='" + transactionId + '\'' +
            ", createdAt=" + createdAt +
            ", completedAt=" + completedAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}