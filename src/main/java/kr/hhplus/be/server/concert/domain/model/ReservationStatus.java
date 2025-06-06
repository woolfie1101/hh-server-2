package kr.hhplus.be.server.concert.domain.model;

public enum ReservationStatus {
    PENDING,    // 예약 대기 중
    COMPLETED,  // 예약 완료
    CANCELLED,  // 예약 취소
    EXPIRED,      // 예약 만료
    AVAILABLE // 사용 가능한 좌석
} 