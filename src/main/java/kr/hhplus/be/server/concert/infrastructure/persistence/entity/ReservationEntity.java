package kr.hhplus.be.server.concert.infrastructure.persistence.entity;

import kr.hhplus.be.server.concert.domain.model.Reservation;
import kr.hhplus.be.server.concert.domain.model.ReservationStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Reservation JPA 엔티티
 * - 데이터베이스 테이블 매핑 전용 객체
 * - 도메인 모델과 분리된 순수한 데이터 구조
 * - JPA 어노테이션과 DB 제약사항 포함
 */
@Entity
@Table(name = "reservations")
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "concert_id", nullable = false)
    private Long concertId;

    @Column(name = "seat_id", nullable = false)
    private Long seatId;

    @Column(name = "seat_number", nullable = false, length = 10)
    private String seatNumber;

    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ReservationStatus status;

    @Column(name = "reserved_at", nullable = false, updatable = false)
    private LocalDateTime reservedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 기본 생성자 (JPA 필수)
    protected ReservationEntity() {}

    // 비즈니스 생성자
    public ReservationEntity(String userId, Long concertId, Long seatId, String seatNumber, BigDecimal price) {
        this.userId = userId;
        this.concertId = concertId;
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.price = price;
        this.status = ReservationStatus.TEMPORARY;
        this.reservedAt = LocalDateTime.now();
        this.expiresAt = this.reservedAt.plusMinutes(5); // 5분 후 만료
        this.confirmedAt = null;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 도메인 모델로 변환
     */
    public Reservation toDomain() {
        Reservation reservation = new Reservation(
            this.userId,
            this.concertId,
            this.seatId,
            this.seatNumber,
            this.price
        );

        if (this.id != null) {
            reservation.assignId(this.id);
        }

        // 상태별로 도메인 객체의 상태 복원
        restoreDomainState(reservation);

        return reservation;
    }

    /**
     * 도메인 모델에서 엔티티 생성
     */
    public static ReservationEntity fromDomain(Reservation reservation) {
        ReservationEntity entity = new ReservationEntity(
            reservation.getUserId(),
            reservation.getConcertId(),
            reservation.getSeatId(),
            reservation.getSeatNumber(),
            reservation.getPrice()
        );

        if (reservation.getId() != null) {
            entity.setId(reservation.getId());
        }

        // 도메인 상태를 엔티티에 반영
        entity.setStatus(reservation.getStatus());
        entity.setReservedAt(reservation.getReservedAt());
        entity.setExpiresAt(reservation.getExpiresAt());
        entity.setConfirmedAt(reservation.getConfirmedAt());

        return entity;
    }

    /**
     * 도메인 모델의 변경사항을 엔티티에 반영
     */
    public void updateFromDomain(Reservation reservation) {
        this.userId = reservation.getUserId();
        this.concertId = reservation.getConcertId();
        this.seatId = reservation.getSeatId();
        this.seatNumber = reservation.getSeatNumber();
        this.price = reservation.getPrice();
        this.status = reservation.getStatus();
        this.reservedAt = reservation.getReservedAt();
        this.expiresAt = reservation.getExpiresAt();
        this.confirmedAt = reservation.getConfirmedAt();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 도메인 객체의 상태를 복원하는 헬퍼 메서드
     */
    private void restoreDomainState(Reservation reservation) {
        // 도메인 객체는 생성자에서 TEMPORARY 상태로 생성되므로,
        // 다른 상태로 변경이 필요한 경우에만 처리
        switch (this.status) {
            case CONFIRMED:
                reservation.confirm();
                break;
            case CANCELLED:
                reservation.cancel();
                break;
            case EXPIRED:
                reservation.markAsExpired();
                break;
            case TEMPORARY:
            default:
                // TEMPORARY는 기본 상태이므로 추가 처리 불필요
                // 단, 만료 시간이 지났다면 만료 처리
                if (LocalDateTime.now().isAfter(this.expiresAt)) {
                    reservation.processExpiredReservation();
                }
                break;
        }
    }

    // JPA 생명주기 콜백
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (reservedAt == null) {
            reservedAt = LocalDateTime.now();
        }
        if (expiresAt == null) {
            expiresAt = reservedAt.plusMinutes(5);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getConcertId() {
        return concertId;
    }

    public void setConcertId(Long concertId) {
        this.concertId = concertId;
    }

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public LocalDateTime getReservedAt() {
        return reservedAt;
    }

    public void setReservedAt(LocalDateTime reservedAt) {
        this.reservedAt = reservedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ReservationEntity that = (ReservationEntity) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ReservationEntity{" +
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
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}