package kr.hhplus.be.server.concert.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 좌석 도메인 모델 (순수 POJO)
 * - 좌석 정보와 예약 상태 관리
 */
public class Seat {

    private static final int RESERVATION_TIMEOUT_MINUTES = 5; // 예약 만료 시간 (5분)

    private Long id;
    private Long concertId;
    private String seatNumber;
    private BigDecimal price;
    private String reservedBy;      // 예약한 사용자 ID
    private LocalDateTime reservedAt; // 예약 시간

    // 기본 생성자 (JPA용)
    protected Seat() {}

    // 비즈니스 생성자
    public Seat(Long concertId, String seatNumber, BigDecimal price) {
        validateConcertId(concertId);
        validateSeatNumber(seatNumber);
        validatePrice(price);

        this.concertId = concertId;
        this.seatNumber = seatNumber;
        this.price = price;
        this.reservedBy = null;
        this.reservedAt = null;
    }

    /**
     * 좌석 예약
     * @param userId 예약할 사용자 ID
     * @return 예약 성공 여부
     */
    public boolean reserve(String userId) {
        validateUserId(userId);

        if (!isAvailable()) {
            return false;
        }

        this.reservedBy = userId;
        this.reservedAt = LocalDateTime.now();
        return true;
    }

    /**
     * 예약 취소
     * @param userId 취소를 요청한 사용자 ID
     * @return 취소 성공 여부
     */
    public boolean cancelReservation(String userId) {
        if (!isReservedBy(userId)) {
            return false;
        }

        this.reservedBy = null;
        this.reservedAt = null;
        return true;
    }

    /**
     * 좌석이 예약 가능한지 확인
     */
    public boolean isAvailable() {
        return reservedBy == null;
    }

    /**
     * 특정 사용자가 예약한 좌석인지 확인
     */
    public boolean isReservedBy(String userId) {
        if (userId == null || isAvailable()) {
            return false;
        }
        return reservedBy.equals(userId);
    }

    /**
     * 예약 만료 시간 계산
     */
    public LocalDateTime getReservationExpirationTime() {
        if (reservedAt == null) {
            return null;
        }
        return reservedAt.plusMinutes(RESERVATION_TIMEOUT_MINUTES);
    }

    /**
     * 예약이 만료되었는지 확인
     */
    public boolean isReservationExpired() {
        if (reservedAt == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(getReservationExpirationTime());
    }

    /**
     * 만료된 예약 자동 해제
     */
    public void releaseExpiredReservation() {
        if (isReservationExpired()) {
            this.reservedBy = null;
            this.reservedAt = null;
        }
    }

    // ID 할당 (Repository에서 사용)
    public void assignId(Long id) {
        this.id = id;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getConcertId() {
        return concertId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getReservedBy() {
        return reservedBy;
    }

    public LocalDateTime getReservedAt() {
        return reservedAt;
    }

    // 검증 메서드들
    private void validateConcertId(Long concertId) {
        if (concertId == null) {
            throw new IllegalArgumentException("콘서트 ID는 필수입니다.");
        }
    }

    private void validateSeatNumber(String seatNumber) {
        if (seatNumber == null || seatNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("좌석 번호는 필수입니다.");
        }
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("좌석 가격은 0 이상이어야 합니다.");
        }
    }

    private void validateUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Seat seat = (Seat) obj;
        return id != null ? id.equals(seat.id) : seat.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Seat{" +
            "id=" + id +
            ", concertId=" + concertId +
            ", seatNumber='" + seatNumber + '\'' +
            ", price=" + price +
            ", reservedBy='" + reservedBy + '\'' +
            ", reservedAt=" + reservedAt +
            ", available=" + isAvailable() +
            '}';
    }
}