package kr.hhplus.be.server.exception;

import java.util.UUID;
import java.math.BigDecimal;

public class PaymentExceptions {
    public static class PaymentNotFoundException extends RuntimeException {
        public PaymentNotFoundException(UUID paymentId) {
            super(String.format("결제 정보를 찾을 수 없습니다. (결제 ID: %s)", paymentId));
        }
    }

    public static class UnauthorizedPaymentOperationException extends RuntimeException {
        public UnauthorizedPaymentOperationException(UUID paymentId, UUID userId) {
            super(String.format("해당 결제에 대한 권한이 없습니다. (결제 ID: %s, 사용자 ID: %s)", paymentId, userId));
        }
    }

    public static class PaymentAlreadyCompletedException extends RuntimeException {
        public PaymentAlreadyCompletedException(UUID paymentId) {
            super(String.format("이미 완료된 결제입니다. (결제 ID: %s)", paymentId));
        }
    }

    public static class PaymentAlreadyCancelledException extends RuntimeException {
        public PaymentAlreadyCancelledException(UUID paymentId) {
            super(String.format("이미 취소된 결제입니다. (결제 ID: %s)", paymentId));
        }
    }

    public static class PaymentAmountMismatchException extends RuntimeException {
        public PaymentAmountMismatchException(UUID paymentId, BigDecimal expectedAmount, BigDecimal actualAmount) {
            super(String.format("결제 금액이 일치하지 않습니다. (결제 ID: %s, 예상 금액: %s, 실제 금액: %s)", 
                paymentId, expectedAmount, actualAmount));
        }
    }
} 