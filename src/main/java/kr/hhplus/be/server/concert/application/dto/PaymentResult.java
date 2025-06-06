package kr.hhplus.be.server.concert.application.dto;

/**
 * 결제 처리 결과 DTO
 * - 결제 처리의 성공/실패 결과
 * - UseCase에서 사용하는 응답 객체
 */
public class PaymentResult {

    private final boolean success;
    private final String transactionId;
    private final String message;

    public PaymentResult(boolean success, String transactionId, String message) {
        this.success = success;
        this.transactionId = transactionId;
        this.message = message;
    }

    /**
     * 성공 결과 생성
     */
    public static PaymentResult success(String transactionId) {
        return new PaymentResult(true, transactionId, "결제가 완료되었습니다.");
    }

    /**
     * 실패 결과 생성
     */
    public static PaymentResult failure(String message) {
        return new PaymentResult(false, null, message);
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "PaymentResult{" +
            "success=" + success +
            ", transactionId='" + transactionId + '\'' +
            ", message='" + message + '\'' +
            '}';
    }
}