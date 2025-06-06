package kr.hhplus.be.server.concert.domain.model;

/**
 * 결제 수단
 */
public enum PaymentMethod {
    CARD("신용카드"),
    BANK_TRANSFER("계좌이체"),
    POINT("포인트");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 