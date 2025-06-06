package kr.hhplus.be.server.concert.application.exception;

import java.util.UUID;

/**
 * 결제 관련 커스텀 예외들
 */
public class PaymentExceptions {

    /**
     * 결제를 찾을 수 없는 경우
     */
    public static class PaymentNotFoundException extends RuntimeException {
        public PaymentNotFoundException(UUID paymentId) {
            super(String.format("결제를 찾을 수 없습니다: %s", paymentId));
        }
    }

    /**
     * 결제 작업에 대한 권한이 없는 경우
     */
    public static class UnauthorizedPaymentOperationException extends RuntimeException {
        public UnauthorizedPaymentOperationException(UUID paymentId) {
            super(String.format("결제 %s에 대한 작업 권한이 없습니다.", paymentId));
        }
    }

    /**
     * 결제 처리 실패 예외
     */
    public static class PaymentProcessingException extends RuntimeException {
        public PaymentProcessingException(String message) {
            super(message);
        }

        public PaymentProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }

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