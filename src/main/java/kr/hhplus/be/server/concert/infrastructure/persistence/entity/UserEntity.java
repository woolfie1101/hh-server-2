package kr.hhplus.be.server.concert.infrastructure.persistence.entity;

import kr.hhplus.be.server.concert.domain.model.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * User JPA 엔티티
 * - 데이터베이스 테이블 매핑 전용 객체
 * - 도메인 모델과 분리된 순수한 데이터 구조
 * - JPA 어노테이션과 DB 제약사항 포함
 */
@Entity
@Table(name = "users",
    uniqueConstraints = @UniqueConstraint(columnNames = "uuid"))
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true, length = 50)
    private String uuid;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 기본 생성자 (JPA 필수)
    protected UserEntity() {}

    // 비즈니스 생성자
    public UserEntity(String uuid, String name, BigDecimal balance) {
        this.uuid = uuid;
        this.name = name;
        this.balance = balance;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 도메인 모델로 변환
     */
    public User toDomain() {
        User user = new User(this.uuid, this.name, this.balance);
        if (this.id != null) {
            user.assignId(this.id);
        }
        return user;
    }

    /**
     * 도메인 모델에서 엔티티 생성
     */
    public static UserEntity fromDomain(User user) {
        UserEntity entity = new UserEntity(
            user.getUuid(),
            user.getName(),
            user.getBalance()
        );

        if (user.getId() != null) {
            entity.setId(user.getId());
        }

        return entity;
    }

    /**
     * 도메인 모델의 변경사항을 엔티티에 반영
     */
    public void updateFromDomain(User user) {
        this.name = user.getName();
        this.balance = user.getBalance();
        this.updatedAt = LocalDateTime.now();
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
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

        UserEntity that = (UserEntity) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
            "id=" + id +
            ", uuid='" + uuid + '\'' +
            ", name='" + name + '\'' +
            ", balance=" + balance +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}