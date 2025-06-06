package kr.hhplus.be.server.dto;

import lombok.Getter;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * 포인트 충전 요청 DTO
 */
@Getter
public class PointChargeRequest {
    private UUID userId;
    private BigDecimal amount;

    public PointChargeRequest(UUID userId, BigDecimal amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public UUID getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
} 