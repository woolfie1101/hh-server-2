package kr.hhplus.be.server.concert.domain.model;

/**
 * 예약 상태 열거형
 */
public enum ReservationStatus {

    TEMPORARY("임시 예약"),      // 5분간 임시 예약 상태
    CONFIRMED("예약 확정"),      // 결제 완료된 확정 상태
    CANCELLED("예약 취소"),      // 사용자가 직접 취소
    EXPIRED("예약 만료");        // 5분 경과로 자동 만료

    private final String description;

    ReservationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 수정 가능한 상태인지 확인
     * TEMPORARY 상태만 수정 가능
     */
    public boolean isModifiable() {
        return this == TEMPORARY;
    }

    /**
     * 완료된 상태인지 확인 (확정 또는 취소)
     */
    public boolean isFinalized() {
        return this == CONFIRMED || this == CANCELLED || this == EXPIRED;
    }
}