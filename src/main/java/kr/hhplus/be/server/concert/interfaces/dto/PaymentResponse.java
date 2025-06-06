package kr.hhplus.be.server.concert.interfaces.dto;

import kr.hhplus.be.server.concert.application.dto.PaymentDto;
import kr.hhplus.be.server.concert.domain.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 결제 결과 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private UUID id;
    private UUID userId;
    private UUID seatId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PaymentResponse from(PaymentDto dto) {
        return PaymentResponse.builder()
            .id(dto.getId())
            .userId(dto.getUserId())
            .seatId(dto.getSeatId())
            .amount(dto.getAmount())
            .status(dto.getStatus())
            .transactionId(dto.getTransactionId())
            .createdAt(dto.getCreatedAt())
            .updatedAt(dto.getUpdatedAt())
            .build();
    }

    public static List<PaymentResponse> from(List<PaymentDto> dtos) {
        return dtos.stream()
            .map(PaymentResponse::from)
            .collect(Collectors.toList());
    }
} 