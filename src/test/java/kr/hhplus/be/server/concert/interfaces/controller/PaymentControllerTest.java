package kr.hhplus.be.server.concert.interfaces.controller;

import kr.hhplus.be.server.concert.application.dto.PaymentDto;
import kr.hhplus.be.server.concert.application.usecase.PaymentUseCase;
import kr.hhplus.be.server.concert.domain.model.PaymentStatus;
import kr.hhplus.be.server.concert.interfaces.dto.PaymentRequest;
import kr.hhplus.be.server.concert.interfaces.dto.PaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentUseCase paymentUseCase;

    @InjectMocks
    private PaymentController paymentController;

    private UUID paymentId;
    private UUID userId;
    private PaymentDto paymentDto;
    private List<PaymentDto> paymentDtos;

    @BeforeEach
    void setUp() {
        paymentId = UUID.randomUUID();
        userId = UUID.randomUUID();
        paymentDto = PaymentDto.builder()
            .id(paymentId)
            .userId(userId)
            .amount(BigDecimal.valueOf(10000))
            .status(PaymentStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        paymentDtos = Arrays.asList(paymentDto);
    }

    @Test
    void createPayment_성공() {
        // given
        PaymentRequest request = PaymentRequest.builder()
            .userId(userId)
            .build();
        when(paymentUseCase.createPayment(any())).thenReturn(paymentDto);

        // when
        ResponseEntity<PaymentResponse> response = paymentController.createPayment(request);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().getId()).isEqualTo(paymentId);
        assertThat(response.getBody().getUserId()).isEqualTo(userId);
    }

    @Test
    void getPayment_성공() {
        // given
        when(paymentUseCase.getPayment(paymentId)).thenReturn(paymentDto);

        // when
        ResponseEntity<PaymentResponse> response = paymentController.getPayment(paymentId);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().getId()).isEqualTo(paymentId);
    }

    @Test
    void getUserPayments_성공() {
        // given
        when(paymentUseCase.getUserPayments(userId)).thenReturn(paymentDtos);

        // when
        ResponseEntity<List<PaymentResponse>> response = paymentController.getUserPayments(userId);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getId()).isEqualTo(paymentId);
        assertThat(response.getBody().get(0).getUserId()).isEqualTo(userId);
    }

    @Test
    void completePayment_성공() {
        // given
        String transactionId = "test-transaction-id";
        when(paymentUseCase.completePayment(paymentId)).thenReturn(paymentDto);

        // when
        ResponseEntity<PaymentResponse> response = paymentController.completePayment(paymentId, transactionId);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().getId()).isEqualTo(paymentId);
    }

    @Test
    void cancelPayment_성공() {
        // given
        String reason = "테스트 취소";
        when(paymentUseCase.cancelPayment(paymentId, reason)).thenReturn(paymentDto);

        // when
        ResponseEntity<PaymentResponse> response = paymentController.cancelPayment(paymentId, reason);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().getId()).isEqualTo(paymentId);
    }
} 