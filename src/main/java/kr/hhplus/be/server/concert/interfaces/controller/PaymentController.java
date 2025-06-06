package kr.hhplus.be.server.concert.interfaces.controller;

import kr.hhplus.be.server.concert.application.dto.PaymentDto;
import kr.hhplus.be.server.concert.application.usecase.PaymentUseCase;
import kr.hhplus.be.server.concert.interfaces.dto.PaymentRequest;
import kr.hhplus.be.server.concert.interfaces.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentUseCase paymentUseCase;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest request) {
        PaymentDto paymentDto = paymentUseCase.createPayment(
            request.getUserId()
        );
        return ResponseEntity.ok(PaymentResponse.from(paymentDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable UUID id) {
        PaymentDto paymentDto = paymentUseCase.getPayment(id);
        return ResponseEntity.ok(PaymentResponse.from(paymentDto));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponse>> getUserPayments(@PathVariable UUID userId) {
        List<PaymentDto> paymentDtos = paymentUseCase.getUserPayments(userId);
        return ResponseEntity.ok(PaymentResponse.from(paymentDtos));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<PaymentResponse> completePayment(
        @PathVariable UUID id,
        @RequestParam String transactionId
    ) {
        PaymentDto paymentDto = paymentUseCase.completePayment(id);
        return ResponseEntity.ok(PaymentResponse.from(paymentDto));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(@PathVariable UUID id, @RequestParam String reason) {
        PaymentDto paymentDto = paymentUseCase.cancelPayment(id, reason);
        return ResponseEntity.ok(PaymentResponse.from(paymentDto));
    }
} 