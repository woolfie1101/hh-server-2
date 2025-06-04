package kr.hhplus.be.server.concert.interfaces.dto;

import kr.hhplus.be.server.concert.domain.model.Reservation;
import kr.hhplus.be.server.concert.domain.model.ReservationStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 예약 관련 요청/응답 DTO들
 * - API 레이어에서 사용하는 데이터 전송 객체
 * - 검증 어노테이션 포함
 */
public class ReservationDtos {

    /**
     * 좌석 예약 요청 DTO
     */
    public static class SeatReservationRequest {

        private String userId;
        private Long concertId;
        private Long seatId;

        // 기본 생성자
        public SeatReservationRequest() {}

        // 생성자
        public SeatReservationRequest(String userId, Long concertId, Long seatId) {
            this.userId = userId;
            this.concertId = concertId;
            this.seatId = seatId;
        }

        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public Long getConcertId() { return concertId; }
        public void setConcertId(Long concertId) { this.concertId = concertId; }

        public Long getSeatId() { return seatId; }
        public void setSeatId(Long seatId) { this.seatId = seatId; }
    }

    /**
     * 결제 처리 요청 DTO
     */
    public static class PaymentProcessRequest {

        private Long reservationId;
        private String paymentMethod;

        // 기본 생성자
        public PaymentProcessRequest() {}

        // 생성자
        public PaymentProcessRequest(Long reservationId, String paymentMethod) {
            this.reservationId = reservationId;
            this.paymentMethod = paymentMethod;
        }

        // Getters and Setters
        public Long getReservationId() { return reservationId; }
        public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }

    /**
     * 예약 취소 요청 DTO
     */
    public static class ReservationCancelRequest {

        private Long reservationId;
        private String userId;

        // 기본 생성자
        public ReservationCancelRequest() {}

        // 생성자
        public ReservationCancelRequest(Long reservationId, String userId) {
            this.reservationId = reservationId;
            this.userId = userId;
        }

        // Getters and Setters
        public Long getReservationId() { return reservationId; }
        public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
    }

    /**
     * 예약 정보 응답 DTO
     */
    public static class ReservationResponse {

        private Long reservationId;
        private String userId;
        private Long concertId;
        private Long seatId;
        private String seatNumber;
        private BigDecimal price;
        private ReservationStatus status;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime reservedAt;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime expiresAt;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime confirmedAt;

        // 기본 생성자
        public ReservationResponse() {}

        // 생성자
        public ReservationResponse(Long reservationId, String userId, Long concertId, Long seatId,
            String seatNumber, BigDecimal price, ReservationStatus status,
            LocalDateTime reservedAt, LocalDateTime expiresAt, LocalDateTime confirmedAt) {
            this.reservationId = reservationId;
            this.userId = userId;
            this.concertId = concertId;
            this.seatId = seatId;
            this.seatNumber = seatNumber;
            this.price = price;
            this.status = status;
            this.reservedAt = reservedAt;
            this.expiresAt = expiresAt;
            this.confirmedAt = confirmedAt;
        }

        /**
         * Reservation 도메인 모델로부터 응답 DTO 생성
         */
        public static ReservationResponse from(Reservation reservation) {
            if (reservation == null) {
                return null;
            }

            return new ReservationResponse(
                reservation.getId(),
                reservation.getUserId(),
                reservation.getConcertId(),
                reservation.getSeatId(),
                reservation.getSeatNumber(),
                reservation.getPrice(),
                reservation.getStatus(),
                reservation.getReservedAt(),
                reservation.getExpiresAt(),
                reservation.getConfirmedAt()
            );
        }

        // Getters and Setters
        public Long getReservationId() { return reservationId; }
        public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public Long getConcertId() { return concertId; }
        public void setConcertId(Long concertId) { this.concertId = concertId; }

        public Long getSeatId() { return seatId; }
        public void setSeatId(Long seatId) { this.seatId = seatId; }

        public String getSeatNumber() { return seatNumber; }
        public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }

        public ReservationStatus getStatus() { return status; }
        public void setStatus(ReservationStatus status) { this.status = status; }

        public LocalDateTime getReservedAt() { return reservedAt; }
        public void setReservedAt(LocalDateTime reservedAt) { this.reservedAt = reservedAt; }

        public LocalDateTime getExpiresAt() { return expiresAt; }
        public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

        public LocalDateTime getConfirmedAt() { return confirmedAt; }
        public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }
    }

    /**
     * 결제 결과 응답 DTO
     */
    public static class PaymentResponse {

        private boolean success;
        private String transactionId;
        private String message;
        private ReservationResponse reservation;

        // 기본 생성자
        public PaymentResponse() {}

        // 생성자
        public PaymentResponse(boolean success, String transactionId, String message, ReservationResponse reservation) {
            this.success = success;
            this.transactionId = transactionId;
            this.message = message;
            this.reservation = reservation;
        }

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public ReservationResponse getReservation() { return reservation; }
        public void setReservation(ReservationResponse reservation) { this.reservation = reservation; }
    }

    /**
     * 공통 API 응답 포맷
     */
    public static class ApiResponse<T> {

        private boolean success;
        private String message;
        private T data;
        private String timestamp;

        // 기본 생성자
        public ApiResponse() {
            this.timestamp = LocalDateTime.now().toString();
        }

        // 생성자
        public ApiResponse(boolean success, String message, T data) {
            this();
            this.success = success;
            this.message = message;
            this.data = data;
        }

        /**
         * 성공 응답 생성
         */
        public static <T> ApiResponse<T> success(T data) {
            return new ApiResponse<>(true, "요청이 성공적으로 처리되었습니다.", data);
        }

        /**
         * 성공 응답 생성 (메시지 포함)
         */
        public static <T> ApiResponse<T> success(String message, T data) {
            return new ApiResponse<>(true, message, data);
        }

        /**
         * 실패 응답 생성
         */
        public static <T> ApiResponse<T> failure(String message) {
            return new ApiResponse<>(false, message, null);
        }

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public T getData() { return data; }
        public void setData(T data) { this.data = data; }

        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }
}