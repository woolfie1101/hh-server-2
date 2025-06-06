package kr.hhplus.be.server.concert.application.dto;

import kr.hhplus.be.server.concert.domain.model.Payment;
import kr.hhplus.be.server.concert.domain.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private UUID id;
    private UUID userId;
    private UUID seatId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PaymentDto from(Payment payment) {
        return PaymentDto.builder()
            .id(payment.getId())
            .userId(payment.getUserId())
            .seatId(payment.getSeatId())
            .amount(payment.getAmount())
            .status(payment.getStatus())
            .transactionId(payment.getTransactionId())
            .createdAt(payment.getCreatedAt())
            .updatedAt(payment.getUpdatedAt())
            .build();
    }
} 