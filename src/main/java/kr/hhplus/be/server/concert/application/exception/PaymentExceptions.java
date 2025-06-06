package kr.hhplus.be.server.concert.application.exception;

/**
 * 결제 관련 예외 클래스들
 */
public class PaymentExceptions {

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String resourceType, Object id) {
            super(String.format("%s(ID: %s)를 찾을 수 없습니다.", resourceType, id));
        }
    }

    public static class InsufficientBalanceException extends RuntimeException {
        public InsufficientBalanceException(String userId) {
            super(String.format("사용자(ID: %s)의 잔액이 부족합니다.", userId));
        }
    }

    public static class AlreadyPaidException extends RuntimeException {
        public AlreadyPaidException(Long reservationId) {
            super(String.format("예약(ID: %d)은 이미 결제가 완료되었습니다.", reservationId));
        }
    }

    public static class PaymentProcessingException extends RuntimeException {
        public PaymentProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class UnauthorizedAccessException extends RuntimeException {
        public UnauthorizedAccessException(String message) {
            super(message);
        }
    }

    public static class InvalidPaymentStatusException extends RuntimeException {
        public InvalidPaymentStatusException(String message) {
            super(message);
        }
    }
} 