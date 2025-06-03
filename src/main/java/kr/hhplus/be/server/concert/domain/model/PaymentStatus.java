package kr.hhplus.be.server.concert.domain.model;

/**
 * 결제 상태 열거형
 */
public enum PaymentStatus {

    PENDING("결제 대기"),        // 결제 요청 중
    COMPLETED("결제 완료"),      // 결제 성공 완료
    FAILED("결제 실패"),         // 결제 실패
    CANCELLED("결제 취소"),      // 결제 취소 (예약 취소 시)
    REFUNDED("결제 환불");       // 결제 환불 (예약 취소 후)

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 최종 상태인지 확인 (더 이상 변경되지 않는 상태)
     */
    public boolean isFinal() {
        return this != PENDING;
    }

    /**
     * 성공한 결제인지 확인
     */
    public boolean isSuccessful() {
        return this == COMPLETED;
    }

    /**
     * 실패한 결제인지 확인 (실패, 취소, 환불 포함)
     */
    public boolean isUnsuccessful() {
        return this == FAILED || this == CANCELLED || this == REFUNDED;
    }
}