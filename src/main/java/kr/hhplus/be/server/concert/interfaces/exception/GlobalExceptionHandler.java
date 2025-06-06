package kr.hhplus.be.server.concert.interfaces.exception;

import kr.hhplus.be.server.concert.application.exception.ReservationExceptions;
import kr.hhplus.be.server.concert.interfaces.dto.ReservationDtos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler - 전역 예외 처리
 * - 애플리케이션 전체의 예외를 일관된 형태로 처리
 * - 클라이언트에게 적절한 HTTP 상태코드와 메시지 반환
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 리소스를 찾을 수 없는 경우
     */
    @ExceptionHandler(ReservationExceptions.ResourceNotFoundException.class)
    public ResponseEntity<ReservationDtos.ApiResponse<Object>> handleResourceNotFound(
        ReservationExceptions.ResourceNotFoundException ex) {

        logger.warn("Resource not found: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ReservationDtos.ApiResponse.failure(ex.getMessage()));
    }

    /**
     * 이미 예약된 좌석 예외
     */
    @ExceptionHandler(ReservationExceptions.SeatAlreadyReservedException.class)
    public ResponseEntity<ReservationDtos.ApiResponse<Object>> handleSeatAlreadyReserved(
        ReservationExceptions.SeatAlreadyReservedException ex) {

        logger.warn("Seat already reserved: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ReservationDtos.ApiResponse.failure(ex.getMessage()));
    }

    /**
     * 잔액 부족 예외
     */
    @ExceptionHandler(ReservationExceptions.InsufficientBalanceException.class)
    public ResponseEntity<ReservationDtos.ApiResponse<Object>> handleInsufficientBalance(
        ReservationExceptions.InsufficientBalanceException ex) {

        logger.warn("Insufficient balance: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
            .body(ReservationDtos.ApiResponse.failure(ex.getMessage()));
    }

    /**
     * 권한 없음 예외
     */
    @ExceptionHandler(ReservationExceptions.UnauthorizedAccessException.class)
    public ResponseEntity<ReservationDtos.ApiResponse<Object>> handleUnauthorizedAccess(
        ReservationExceptions.UnauthorizedAccessException ex) {

        logger.warn("Unauthorized access: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ReservationDtos.ApiResponse.failure(ex.getMessage()));
    }

    /**
     * 예약 상태 오류 예외
     */
    @ExceptionHandler(ReservationExceptions.InvalidReservationStateException.class)
    public ResponseEntity<ReservationDtos.ApiResponse<Object>> handleInvalidReservationState(
        ReservationExceptions.InvalidReservationStateException ex) {

        logger.warn("Invalid reservation state: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ReservationDtos.ApiResponse.failure(ex.getMessage()));
    }

    /**
     * 결제 처리 실패 예외
     */
    @ExceptionHandler(ReservationExceptions.PaymentProcessingException.class)
    public ResponseEntity<ReservationDtos.ApiResponse<Object>> handlePaymentProcessing(
        ReservationExceptions.PaymentProcessingException ex) {

        logger.error("Payment processing failed: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ReservationDtos.ApiResponse.failure("결제 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
    }

    /**
     * 요청 데이터 검증 실패 (Bean Validation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ReservationDtos.ApiResponse<Object>> handleValidationExceptions(
        MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        logger.warn("Validation failed: {}", errors);

        // 첫 번째 에러 메시지를 메인 메시지로 사용
        String firstErrorMessage = errors.values().iterator().next();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ReservationDtos.ApiResponse.failure("입력값 검증에 실패했습니다: " + firstErrorMessage));
    }

    /**
     * 타입 변환 오류 (PathVariable, RequestParam 등)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ReservationDtos.ApiResponse<Object>> handleTypeMismatch(
        MethodArgumentTypeMismatchException ex) {

        logger.warn("Type mismatch: {} for parameter {}", ex.getValue(), ex.getName());

        String message = String.format("'%s' 파라미터의 값 '%s'이(가) 올바르지 않습니다.",
            ex.getName(), ex.getValue());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ReservationDtos.ApiResponse.failure(message));
    }

    /**
     * 일반적인 IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ReservationDtos.ApiResponse<Object>> handleIllegalArgument(
        IllegalArgumentException ex) {

        logger.warn("Invalid argument: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ReservationDtos.ApiResponse.failure(ex.getMessage()));
    }

    /**
     * 일반적인 IllegalStateException
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ReservationDtos.ApiResponse<Object>> handleIllegalState(
        IllegalStateException ex) {

        logger.warn("Invalid state: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ReservationDtos.ApiResponse.failure(ex.getMessage()));
    }

    /**
     * 예상하지 못한 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ReservationDtos.ApiResponse<Object>> handleAllUncaughtException(
        Exception ex) {

        logger.error("Unexpected error occurred", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ReservationDtos.ApiResponse.failure("서버에서 예상치 못한 오류가 발생했습니다. 관리자에게 문의해주세요."));
    }
}