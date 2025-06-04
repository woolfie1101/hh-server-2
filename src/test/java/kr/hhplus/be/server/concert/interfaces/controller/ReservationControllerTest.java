package kr.hhplus.be.server.concert.interfaces.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.concert.application.dto.PaymentResult;
import kr.hhplus.be.server.concert.application.exception.ReservationExceptions;
import kr.hhplus.be.server.concert.application.usecase.ReservationUseCase;
import kr.hhplus.be.server.concert.domain.model.Reservation;
import kr.hhplus.be.server.concert.domain.model.ReservationStatus;
import kr.hhplus.be.server.concert.interfaces.dto.ReservationDtos;
import kr.hhplus.be.server.concert.interfaces.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

/**
 * ReservationController 단위 테스트
 * - MockMvc를 사용한 Controller 계층 테스트
 * - UseCase Mock을 통한 독립적 테스트
 * - HTTP 요청/응답 검증
 */
@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Mock
    private ReservationUseCase reservationUseCase;

    @InjectMocks
    private ReservationController reservationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(reservationController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    void 좌석_예약_성공_테스트() throws Exception {
        // given
        ReservationDtos.SeatReservationRequest request = new ReservationDtos.SeatReservationRequest(
            "user-123", 1L, 10L
        );

        Reservation reservation = new Reservation("user-123", 1L, 10L, "A-15", BigDecimal.valueOf(150000));
        reservation.assignId(1L);

        when(reservationUseCase.reserveSeat("user-123", 1L, 10L)).thenReturn(reservation);

        // when & then
        mockMvc.perform(post("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("좌석이 성공적으로 예약되었습니다."))
            .andExpect(jsonPath("$.data.reservationId").value(1L))
            .andExpect(jsonPath("$.data.userId").value("user-123"))
            .andExpect(jsonPath("$.data.concertId").value(1L))
            .andExpect(jsonPath("$.data.seatId").value(10L))
            .andExpect(jsonPath("$.data.seatNumber").value("A-15"))
            .andExpect(jsonPath("$.data.price").value(150000))
            .andExpect(jsonPath("$.data.status").value("TEMPORARY"))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(reservationUseCase).reserveSeat("user-123", 1L, 10L);
    }

    @Test
    void 좌석_예약_검증_실패_테스트() throws Exception {
        // given - 잘못된 요청 (userId가 null)
        ReservationDtos.SeatReservationRequest request = new ReservationDtos.SeatReservationRequest(
            null, 1L, 10L
        );

        // Mock 설정: validation이 없으므로 UseCase가 호출될 수 있음
        // null userId로 인해 UseCase에서 예외 발생하도록 설정
        when(reservationUseCase.reserveSeat(isNull(), eq(1L), eq(10L)))
            .thenThrow(new IllegalArgumentException("사용자 ID는 필수입니다"));

        // when & then
        mockMvc.perform(post("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("사용자 ID는 필수입니다"));

        verify(reservationUseCase).reserveSeat(isNull(), eq(1L), eq(10L));
    }

    @Test
    void 이미_예약된_좌석_예약_실패_테스트() throws Exception {
        // given
        ReservationDtos.SeatReservationRequest request = new ReservationDtos.SeatReservationRequest(
            "user-123", 1L, 10L
        );

        when(reservationUseCase.reserveSeat("user-123", 1L, 10L))
            .thenThrow(new ReservationExceptions.SeatAlreadyReservedException(10L));

        // when & then
        mockMvc.perform(post("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(containsString("이미 예약되었습니다")));

        verify(reservationUseCase).reserveSeat("user-123", 1L, 10L);
    }

    @Test
    void 결제_처리_성공_테스트() throws Exception {
        // given
        Long reservationId = 1L;
        ReservationDtos.PaymentProcessRequest request = new ReservationDtos.PaymentProcessRequest(
            reservationId, "CREDIT_CARD"
        );

        PaymentResult paymentResult = PaymentResult.success("TXN-12345");

        when(reservationUseCase.processPayment(reservationId, "CREDIT_CARD")).thenReturn(paymentResult);

        // when & then
        mockMvc.perform(post("/api/v1/reservations/{reservationId}/payment", reservationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("결제가 성공적으로 처리되었습니다."))
            .andExpect(jsonPath("$.data.success").value(true))
            .andExpect(jsonPath("$.data.transactionId").value("TXN-12345"))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(reservationUseCase).processPayment(reservationId, "CREDIT_CARD");
    }

    @Test
    void 결제_처리_예약ID_불일치_테스트() throws Exception {
        // given
        Long pathReservationId = 1L;
        ReservationDtos.PaymentProcessRequest request = new ReservationDtos.PaymentProcessRequest(
            2L, "CREDIT_CARD"  // Body의 reservationId가 Path와 다름
        );

        // when & then
        mockMvc.perform(post("/api/v1/reservations/{reservationId}/payment", pathReservationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("예약 ID가 일치하지 않습니다."));

        verify(reservationUseCase, never()).processPayment(any(), any());
    }

    @Test
    void 잔액_부족으로_결제_실패_테스트() throws Exception {
        // given
        Long reservationId = 1L;
        ReservationDtos.PaymentProcessRequest request = new ReservationDtos.PaymentProcessRequest(
            reservationId, "POINT"
        );

        when(reservationUseCase.processPayment(reservationId, "POINT"))
            .thenThrow(new ReservationExceptions.InsufficientBalanceException("user-123"));

        // when & then
        mockMvc.perform(post("/api/v1/reservations/{reservationId}/payment", reservationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isPaymentRequired())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(containsString("잔액이 부족합니다")));

        verify(reservationUseCase).processPayment(reservationId, "POINT");
    }

    @Test
    void 예약_취소_성공_테스트() throws Exception {
        // given
        Long reservationId = 1L;
        ReservationDtos.ReservationCancelRequest request = new ReservationDtos.ReservationCancelRequest(
            reservationId, "user-123"
        );

        when(reservationUseCase.cancelReservation(reservationId, "user-123")).thenReturn(true);

        // when & then
        mockMvc.perform(delete("/api/v1/reservations/{reservationId}", reservationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("예약이 성공적으로 취소되었습니다."))
            .andExpect(jsonPath("$.data").value(true));

        verify(reservationUseCase).cancelReservation(reservationId, "user-123");
    }

    @Test
    void 권한_없는_예약_취소_실패_테스트() throws Exception {
        // given
        Long reservationId = 1L;
        ReservationDtos.ReservationCancelRequest request = new ReservationDtos.ReservationCancelRequest(
            reservationId, "user-456"  // 다른 사용자
        );

        when(reservationUseCase.cancelReservation(reservationId, "user-456"))
            .thenThrow(new ReservationExceptions.UnauthorizedAccessException("본인의 예약만 취소할 수 있습니다"));

        // when & then
        mockMvc.perform(delete("/api/v1/reservations/{reservationId}", reservationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(containsString("권한이 없습니다")));

        verify(reservationUseCase).cancelReservation(reservationId, "user-456");
    }

    @Test
    void 예약_조회_테스트() throws Exception {
        // given
        Long reservationId = 1L;
        String userId = "user-123";

        // when & then
        mockMvc.perform(get("/api/v1/reservations/{reservationId}", reservationId)
                .param("userId", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("예약 조회 기능은 추후 구현 예정입니다."));
    }

    @Test
    void 헬스_체크_테스트() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/reservations/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Reservation API is running"))
            .andExpect(jsonPath("$.data").value("OK"));
    }

    @Test
    void 잘못된_경로_변수_타입_테스트() throws Exception {
        // when & then - 문자열을 숫자 경로 변수에 전달
        mockMvc.perform(get("/api/v1/reservations/invalid-id")
                .param("userId", "user-123"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(containsString("올바르지 않습니다")));
    }

    @Test
    void 예상치_못한_예외_테스트() throws Exception {
        // given
        ReservationDtos.SeatReservationRequest request = new ReservationDtos.SeatReservationRequest(
            "user-123", 1L, 10L
        );

        when(reservationUseCase.reserveSeat("user-123", 1L, 10L))
            .thenThrow(new RuntimeException("Unexpected error"));

        // when & then
        mockMvc.perform(post("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(containsString("예상치 못한 오류가 발생했습니다")));

        verify(reservationUseCase).reserveSeat("user-123", 1L, 10L);
    }
}