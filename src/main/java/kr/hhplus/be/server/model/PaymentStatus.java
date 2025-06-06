package kr.hhplus.be.server.model;

/**
 * 결제 상태 enum
 * - PENDING: 결제 대기 중
 * - SUCCESS: 결제 완료
 * - FAILED: 결제 실패
 * - CANCELLED: 결제 취소
 */
public enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    CANCELLED
} 