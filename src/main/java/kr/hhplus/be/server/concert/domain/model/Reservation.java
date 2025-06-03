package kr.hhplus.be.server.concert.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 예약 도메인 모델 (순수 POJO)
 * - 사용자의 콘서트 좌석 예약 정보와 상태 관리
 */
public class Reservation {

    private static final int RESERVATION_TIMEOUT_MINUTES = 5; // 예약 만료 시간 (5분)

    private Long id;
    private String userId;
    private Long concertId;
    private Long seatId;
    private String seatNumber;
    private BigDecimal price;
    private ReservationStatus status;
    private LocalDateTime reservedAt;   // 예약 생성 시간
    private LocalDateTime expiresAt;    // 예약 만료 시간
    private LocalDateTime confirmedAt;  // 예약 확정 시간

    // 기본 생성자 (JPA용)
    protected Reservation() {}

    // 비즈니스 생성자
    public Reservation(String userId, Long concertId, Long seatId, String seatNumber, BigDecimal price) {
        validateUserId(userId);
        validateConcertId(concertId);
        validateSeatId(seatId);
        validateSeatNumber(seatNumber);
        validatePrice(price);

        this.userId = userId;
        this.concertId = concertId;
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.price = price;
        this.status = ReservationStatus.TEMPORARY;
        this.reservedAt = LocalDateTime.now();
        this.expiresAt = this.reservedAt.plusMinutes(RESERVATION_TIMEOUT_MINUTES);
        this.confirmedAt = null;
    }

    /**
     * 예약 확정 (결제 완료 시)
     * @return 확정 성공 여부
     */
    public boolean confirm() {
        // 만료된 예약이 있다면 자동 처리
        processExpiredReservation();

        if (!status.isModifiable()) {
            return false;
        }

        this.status = ReservationStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
        return true;
    }

    /**
     * 예약 취소
     * @return 취소 성공 여부
     */
    public boolean cancel() {
        // 만료된 예약이 있다면 자동 처리
        processExpiredReservation();

        if (!status.isModifiable()) {
            return false;
        }

        this.status = ReservationStatus.CANCELLED;
        return true;
    }

    /**
     * 예약 만료 처리
     */
    public void markAsExpired() {
        if (status.isModifiable()) {
            this.status = ReservationStatus.EXPIRED;
        }
    }

    /**
     * 예약이 만료되었는지 확인
     */
    public boolean isExpired() {
        if (status == ReservationStatus.EXPIRED) {
            return true;
        }

        // TEMPORARY 상태에서 시간이 지났는지 확인
        if (status == ReservationStatus.TEMPORARY) {
            return LocalDateTime.now().isAfter(expiresAt);
        }

        return false;
    }

    /**
     * 만료된 예약 자동 처리
     */
    public void processExpiredReservation() {
        if (status == ReservationStatus.TEMPORARY && isExpired()) {
            markAsExpired();
        }
    }

    /**
     * 수정 가능한 상태인지 확인
     */
    public boolean isModifiable() {
        // 만료된 예약이 있다면 자동 처리
        processExpiredReservation();
        return status.isModifiable() && !isExpired();
    }

    /**
     * 남은 시간 (분 단위)
     */
    public long getRemainingMinutes() {
        if (status != ReservationStatus.TEMPORARY) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(expiresAt)) {
            return 0;
        }

        return java.time.Duration.between(now, expiresAt).toMinutes();
    }

    /**
     * 예약 상태 요약 정보
     */
    public String getStatusSummary() {
        // 만료된 예약이 있다면 자동 처리
        processExpiredReservation();

        switch (status) {
            case TEMPORARY:
                if (isExpired()) {
                    return "만료됨";
                }
                return String.format("임시 예약 (남은 시간: %d분)", getRemainingMinutes());
            case CONFIRMED:
                return "예약 확정";
            case CANCELLED:
                return "예약 취소";
            case EXPIRED:
                return "예약 만료";
            default:
                return status.getDescription();
        }
    }

    /**
     * 결제 가능한 상태인지 확인
     */
    public boolean isPayable() {
        processExpiredReservation();
        return status == ReservationStatus.TEMPORARY && !isExpired();
    }

    /**
     * 예약 정보가 유효한지 전체 검증
     */
    public boolean isValid() {
        try {
            validateUserId(this.userId);
            validateConcertId(this.concertId);
            validateSeatId(this.seatId);
            validateSeatNumber(this.seatNumber);
            validatePrice(this.price);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 결제와 함께 예약 확정
     * @param payment 결제 정보
     * @return 확정 성공 여부
     */
    public boolean confirmWithPayment(Payment payment) {
        if (!isPaymentValid(payment)) {
            return false;
        }

        if (!payment.isSuccessful()) {
            return false;
        }

        return confirm();
    }

    /**
     * 결제와 함께 예약 취소 (환불 처리)
     * @param payment 결제 정보
     * @return 취소 성공 여부
     */
    public boolean cancelWithRefund(Payment payment) {
        if (!isPaymentValid(payment)) {
            return false;
        }

        // 예약 취소 먼저 시도
        if (!cancel()) {
            return false;
        }

        // 완료된 결제가 있다면 환불 처리
        if (payment.canBeRefunded()) {
            payment.refund("예약 취소");
        } else if (payment.canBeProcessed()) {
            payment.cancel();
        }

        return true;
    }

    /**
     * 만료 시 결제 취소 처리
     * @param payment 결제 정보
     */
    public void expireWithPaymentCancellation(Payment payment) {
        markAsExpired();

        if (payment != null && payment.canBeProcessed()) {
            payment.cancel();
        }
    }

    /**
     * 결제 정보가 이 예약과 일치하는지 검증
     */
    private boolean isPaymentValid(Payment payment) {
        if (payment == null) {
            return false;
        }

        return this.id != null &&
            this.id.equals(payment.getReservationId()) &&
            this.userId.equals(payment.getUserId()) &&
            this.price.equals(payment.getAmount());
    }

    // ID 할당 (Repository에서 사용)
    public void assignId(Long id) {
        this.id = id;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public Long getConcertId() {
        return concertId;
    }

    public Long getSeatId() {
        return seatId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public LocalDateTime getReservedAt() {
        return reservedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    // 검증 메서드들
    private void validateUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }
    }

    private void validateConcertId(Long concertId) {
        if (concertId == null) {
            throw new IllegalArgumentException("콘서트 ID는 필수입니다.");
        }
    }

    private void validateSeatId(Long seatId) {
        if (seatId == null) {
            throw new IllegalArgumentException("좌석 ID는 필수입니다.");
        }
    }

    private void validateSeatNumber(String seatNumber) {
        if (seatNumber == null || seatNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("좌석 번호는 필수입니다.");
        }
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Reservation that = (Reservation) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Reservation{" +
            "id=" + id +
            ", userId='" + userId + '\'' +
            ", concertId=" + concertId +
            ", seatId=" + seatId +
            ", seatNumber='" + seatNumber + '\'' +
            ", price=" + price +
            ", status=" + status +
            ", reservedAt=" + reservedAt +
            ", expiresAt=" + expiresAt +
            ", confirmedAt=" + confirmedAt +
            '}';
    }
}