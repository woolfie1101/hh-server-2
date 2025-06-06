package kr.hhplus.be.server.concert.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private UUID userId;
    private UUID seatId;
    private BigDecimal amount;
} 