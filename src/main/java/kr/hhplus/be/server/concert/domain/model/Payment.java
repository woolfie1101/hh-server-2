package kr.hhplus.be.server.concert.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 결제 도메인 모델 (순수 POJO)
 * - 예약에 대한 결제 정보와 상태 관리
 */
public class Payment {

    private Long id;
    private String userId;
    private Long reservationId;
    private BigDecimal amount;
    private String paymentMethod;
    private PaymentStatus status;
    private String transactionId;       // 외부 결제 시스템의 거래 ID
    private String failureReason;       // 결제 실패 사유
    private String refundReason;        // 환불 사유
    private LocalDateTime createdAt;    // 결제 요청 시간
    private LocalDateTime completedAt;  // 결제 완료 시간
    private LocalDateTime refundedAt;   // 환불 처리 시간

    // 기본 생성자 (JPA용)
    protected Payment() {}

    // 비즈니스 생성자
    public Payment(String userId, Long reservationId, BigDecimal amount, String paymentMethod) {
        validateUserId(userId);
        validateReservationId(reservationId);
        validateAmount(amount);
        validatePaymentMethod(paymentMethod);

        this.userId = userId;
        this.reservationId = reservationId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.transactionId = null;
        this.failureReason = null;
        this.refundReason = null;
        this.completedAt = null;
        this.refundedAt = null;
    }

    /**
     * 결제 완료 처리
     * @param transactionId 외부 결제 시스템의 거래 ID
     * @return 완료 처리 성공 여부
     */
    public boolean complete(String transactionId) {
        validateTransactionId(transactionId);

        if (!canBeProcessed()) {
            return false;
        }

        this.status = PaymentStatus.COMPLETED;
        this.transactionId = transactionId;
        this.completedAt = LocalDateTime.now();
        return true;
    }

    /**
     * 결제 실패 처리
     * @param failureReason 실패 사유
     * @return 실패 처리 성공 여부
     */
    public boolean fail(String failureReason) {
        validateFailureReason(failureReason);

        if (!canBeProcessed()) {
            return false;
        }

        this.status = PaymentStatus.FAILED;
        this.failureReason = failureReason;
        return true;
    }

    /**
     * 결제 취소 처리 (예약 취소 시)
     * @return 취소 처리 성공 여부
     */
    public boolean cancel() {
        if (!canBeProcessed()) {
            return false;
        }

        this.status = PaymentStatus.CANCELLED;
        return true;
    }

    /**
     * 결제 환불 처리 (이미 완료된 결제)
     * @param refundReason 환불 사유
     * @return 환불 처리 성공 여부
     */
    public boolean refund(String refundReason) {
        validateRefundReason(refundReason);

        // 완료된 결제만 환불 가능
        if (status != PaymentStatus.COMPLETED) {
            return false;
        }

        this.status = PaymentStatus.REFUNDED;
        this.refundReason = refundReason;
        this.refundedAt = LocalDateTime.now();
        return true;
    }

    /**
     * 결제가 성공했는지 확인
     */
    public boolean isSuccessful() {
        return status.isSuccessful();
    }

    /**
     * 결제 처리가 가능한 상태인지 확인
     */
    public boolean canBeProcessed() {
        return status == PaymentStatus.PENDING;
    }

    /**
     * 환불 가능한 상태인지 확인
     */
    public boolean canBeRefunded() {
        return status == PaymentStatus.COMPLETED;
    }

    /**
     * 결제 상태 요약 정보
     */
    public String getStatusSummary() {
        switch (status) {
            case PENDING:
                return "결제 처리 중";
            case COMPLETED:
                return String.format("결제 완료 (거래ID: %s)", transactionId);
            case FAILED:
                return String.format("결제 실패 (%s)", failureReason);
            case CANCELLED:
                return "결제 취소됨";
            case REFUNDED:
                return String.format("환불 완료 (%s)", refundReason);
            default:
                return status.getDescription();
        }
    }

    // ID 할당 (Repository에서 사용)
    public void assignId(Long id) {
        this.id = id;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public Long getReservationId() {
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

    public String getFailureReason() {
        return failureReason;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public LocalDateTime getRefundedAt() {
        return refundedAt;
    }

    // 검증 메서드들
    private void validateUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }
    }

    private void validateReservationId(Long reservationId) {
        if (reservationId == null) {
            throw new IllegalArgumentException("예약 ID는 필수입니다.");
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("결제 금액은 0보다 커야 합니다.");
        }
    }

    private void validatePaymentMethod(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("결제 방법은 필수입니다.");
        }
    }

    private void validateTransactionId(String transactionId) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("거래 ID는 필수입니다.");
        }
    }

    private void validateFailureReason(String failureReason) {
        if (failureReason == null || failureReason.trim().isEmpty()) {
            throw new IllegalArgumentException("실패 사유는 필수입니다.");
        }
    }

    private void validateRefundReason(String refundReason) {
        if (refundReason == null || refundReason.trim().isEmpty()) {
            throw new IllegalArgumentException("환불 사유는 필수입니다.");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Payment payment = (Payment) obj;
        return id != null ? id.equals(payment.id) : payment.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Payment{" +
            "id=" + id +
            ", userId='" + userId + '\'' +
            ", reservationId=" + reservationId +
            ", amount=" + amount +
            ", paymentMethod='" + paymentMethod + '\'' +
            ", status=" + status +
            ", transactionId='" + transactionId + '\'' +
            ", createdAt=" + createdAt +
            ", completedAt=" + completedAt +
            '}';
    }
}