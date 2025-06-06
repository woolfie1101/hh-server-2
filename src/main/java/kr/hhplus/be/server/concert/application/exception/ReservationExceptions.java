package kr.hhplus.be.server.concert.application.exception;

import java.util.UUID;

/**
 * 예약 관련 커스텀 예외들
 * - 비즈니스 규칙 위반에 대한 명확한 예외 정의
 * - 예외 메시지와 처리 방식 표준화
 */
public class ReservationExceptions {

    /**
     * 리소스를 찾을 수 없는 경우
     */
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String resourceType, Object id) {
            super(String.format("%s을(를) 찾을 수 없습니다: %s", resourceType, id));
        }
    }

    /**
     * 이미 예약된 좌석에 대한 예외
     */
    public static class SeatAlreadyReservedException extends RuntimeException {
        public SeatAlreadyReservedException(UUID seatId) {
            super(String.format("좌석 %s번은 이미 예약되었습니다.", seatId));
        }
    }

    /**
     * 잔액 부족 예외
     */
    public static class InsufficientBalanceException extends RuntimeException {
        public InsufficientBalanceException(UUID userId) {
            super(String.format("사용자 %s의 잔액이 부족합니다.", userId));
        }
    }

    /**
     * 권한 없음 예외
     */
    public static class UnauthorizedAccessException extends RuntimeException {
        public UnauthorizedAccessException(String action) {
            super(String.format("%s에 대한 권한이 없습니다.", action));
        }
    }

    /**
     * 예약 상태 오류 예외
     */
    public static class InvalidReservationStateException extends RuntimeException {
        public InvalidReservationStateException(String message) {
            super(message);
        }
    }

    /**
     * 결제 처리 실패 예외
     */
    public static class PaymentProcessingException extends RuntimeException {
        public PaymentProcessingException(String message, Throwable cause) {
            super(message, cause);
        }

        public PaymentProcessingException(String message) {
            super(message);
        }
    }
}