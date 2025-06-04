package kr.hhplus.be.server.concert.infrastructure.persistence.entity;

import kr.hhplus.be.server.concert.domain.model.Seat;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Seat JPA 엔티티
 * - 데이터베이스 테이블 매핑 전용 객체
 * - 도메인 모델과 분리된 순수한 데이터 구조
 * - JPA 어노테이션과 DB 제약사항 포함
 */
@Entity
@Table(name = "seats",
    uniqueConstraints = @UniqueConstraint(columnNames = {"concert_id", "seat_number"}))
public class SeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "concert_id", nullable = false)
    private Long concertId;

    @Column(name = "seat_number", nullable = false, length = 10)
    private String seatNumber;

    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(name = "reserved_by", length = 50)
    private String reservedBy;

    @Column(name = "reserved_at")
    private LocalDateTime reservedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 기본 생성자 (JPA 필수)
    protected SeatEntity() {}

    // 비즈니스 생성자
    public SeatEntity(Long concertId, String seatNumber, BigDecimal price) {
        this.concertId = concertId;
        this.seatNumber = seatNumber;
        this.price = price;
        this.reservedBy = null;
        this.reservedAt = null;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 도메인 모델로 변환
     */
    public Seat toDomain() {
        Seat seat = new Seat(this.concertId, this.seatNumber, this.price);

        if (this.id != null) {
            seat.assignId(this.id);
        }

        // 예약 상태 복원
        restoreDomainState(seat);

        return seat;
    }

    /**
     * 도메인 모델에서 엔티티 생성
     */
    public static SeatEntity fromDomain(Seat seat) {
        SeatEntity entity = new SeatEntity(
            seat.getConcertId(),
            seat.getSeatNumber(),
            seat.getPrice()
        );

        if (seat.getId() != null) {
            entity.setId(seat.getId());
        }

        // 도메인 상태를 엔티티에 반영
        entity.setReservedBy(seat.getReservedBy());
        entity.setReservedAt(seat.getReservedAt());

        return entity;
    }

    /**
     * 도메인 모델의 변경사항을 엔티티에 반영
     */
    public void updateFromDomain(Seat seat) {
        this.concertId = seat.getConcertId();
        this.seatNumber = seat.getSeatNumber();
        this.price = seat.getPrice();
        this.reservedBy = seat.getReservedBy();
        this.reservedAt = seat.getReservedAt();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 도메인 객체의 상태를 복원하는 헬퍼 메서드
     */
    private void restoreDomainState(Seat seat) {
        // 예약 상태가 있다면 복원
        if (this.reservedBy != null) {
            // 도메인 객체에 직접 상태를 설정하기 위해 reserve 메서드 사용
            seat.reserve(this.reservedBy);
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

    public Long getConcertId() {
        return concertId;
    }

    public void setConcertId(Long concertId) {
        this.concertId = concertId;
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

    public String getReservedBy() {
        return reservedBy;
    }

    public void setReservedBy(String reservedBy) {
        this.reservedBy = reservedBy;
    }

    public LocalDateTime getReservedAt() {
        return reservedAt;
    }

    public void setReservedAt(LocalDateTime reservedAt) {
        this.reservedAt = reservedAt;
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

        SeatEntity that = (SeatEntity) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "SeatEntity{" +
            "id=" + id +
            ", concertId=" + concertId +
            ", seatNumber='" + seatNumber + '\'' +
            ", price=" + price +
            ", reservedBy='" + reservedBy + '\'' +
            ", reservedAt=" + reservedAt +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}