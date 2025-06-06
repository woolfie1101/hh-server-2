package kr.hhplus.be.server.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 포인트 충전 응답 DTO
 */
public record PointChargeResponse(
    UUID userId,
    BigDecimal amount,
    BigDecimal balance,
    LocalDateTime updatedAt
) {} 