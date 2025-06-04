package kr.hhplus.be.server.concert.interfaces.controller;

import kr.hhplus.be.server.concert.application.dto.PaymentResult;
import kr.hhplus.be.server.concert.application.usecase.ReservationUseCase;
import kr.hhplus.be.server.concert.domain.model.Reservation;
import kr.hhplus.be.server.concert.interfaces.dto.ReservationDtos;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ReservationController - 예약/결제 API 엔드포인트
 * - 클린 아키텍처의 Interface Adapters 레이어
 * - UseCase를 호출하고 HTTP 응답으로 변환
 */
@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationUseCase reservationUseCase;

    public ReservationController(ReservationUseCase reservationUseCase) {
        this.reservationUseCase = reservationUseCase;
    }

    /**
     * 좌석 예약
     * POST /api/v1/reservations
     */
    @PostMapping
    public ResponseEntity<ReservationDtos.ApiResponse<ReservationDtos.ReservationResponse>> reserveSeat(
        @RequestBody ReservationDtos.SeatReservationRequest request) {

        // UseCase 호출
        Reservation reservation = reservationUseCase.reserveSeat(
            request.getUserId(),
            request.getConcertId(),
            request.getSeatId()
        );

        // 응답 DTO 변환
        ReservationDtos.ReservationResponse response = ReservationDtos.ReservationResponse.from(reservation);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ReservationDtos.ApiResponse.success("좌석이 성공적으로 예약되었습니다.", response));
    }

    /**
     * 결제 처리
     * POST /api/v1/reservations/{reservationId}/payment
     */
    @PostMapping("/{reservationId}/payment")
    public ResponseEntity<ReservationDtos.ApiResponse<ReservationDtos.PaymentResponse>> processPayment(
        @PathVariable Long reservationId,
        @RequestBody ReservationDtos.PaymentProcessRequest request) {

        // 요청 검증 (Path Variable과 Body의 일관성)
        if (!reservationId.equals(request.getReservationId())) {
            return ResponseEntity.badRequest()
                .body(ReservationDtos.ApiResponse.failure("예약 ID가 일치하지 않습니다."));
        }

        // UseCase 호출
        PaymentResult paymentResult = reservationUseCase.processPayment(
            request.getReservationId(),
            request.getPaymentMethod()
        );

        // 응답 DTO 생성
        ReservationDtos.PaymentResponse response = new ReservationDtos.PaymentResponse(
            paymentResult.isSuccess(),
            paymentResult.getTransactionId(),
            paymentResult.getMessage(),
            null // 예약 정보는 별도 조회로 분리
        );

        return ResponseEntity.ok(
            ReservationDtos.ApiResponse.success("결제가 성공적으로 처리되었습니다.", response)
        );
    }

    /**
     * 예약 취소
     * DELETE /api/v1/reservations/{reservationId}
     */
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ReservationDtos.ApiResponse<Boolean>> cancelReservation(
        @PathVariable Long reservationId,
        @RequestBody ReservationDtos.ReservationCancelRequest request) {

        // 요청 검증 (Path Variable과 Body의 일관성)
        if (!reservationId.equals(request.getReservationId())) {
            return ResponseEntity.badRequest()
                .body(ReservationDtos.ApiResponse.failure("예약 ID가 일치하지 않습니다."));
        }

        // UseCase 호출
        boolean cancelled = reservationUseCase.cancelReservation(
            request.getReservationId(),
            request.getUserId()
        );

        return ResponseEntity.ok(
            ReservationDtos.ApiResponse.success("예약이 성공적으로 취소되었습니다.", cancelled)
        );
    }

    /**
     * 예약 조회 (단일)
     * GET /api/v1/reservations/{reservationId}
     */
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDtos.ApiResponse<ReservationDtos.ReservationResponse>> getReservation(
        @PathVariable Long reservationId,
        @RequestParam String userId) {

        // TODO: 예약 조회 UseCase 구현 필요
        // 현재는 단순 응답 반환
        return ResponseEntity.ok(
            ReservationDtos.ApiResponse.success("예약 조회 기능은 추후 구현 예정입니다.", null)
        );
    }

    /**
     * 헬스 체크
     * GET /api/v1/reservations/health
     */
    @GetMapping("/health")
    public ResponseEntity<ReservationDtos.ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(
            ReservationDtos.ApiResponse.success("Reservation API is running", "OK")
        );
    }
}