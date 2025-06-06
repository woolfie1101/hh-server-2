package kr.hhplus.be.server.model;

/**
 * 예약 상태 enum
 * - PENDING: 예약 대기
 * - COMPLETED: 예약 완료
 * - CANCELLED: 예약 취소
 * - EXPIRED: 예약 만료
 */
public enum ReservationStatus {
    PENDING,
    COMPLETED,
    CANCELLED,
    EXPIRED
} 