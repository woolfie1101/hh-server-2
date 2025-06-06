package kr.hhplus.be.server.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import kr.hhplus.be.server.model.User;

/**
 * 사용자 JPA 엔티티
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static UserEntity fromDomain(User user) {
        UserEntity entity = new UserEntity();
        entity.id = user.getId();
        entity.email = user.getEmail();
        entity.name = user.getName();
        entity.balance = user.getBalance();
        entity.createdAt = user.getCreatedAt();
        entity.updatedAt = user.getUpdatedAt();
        return entity;
    }

    public User toDomain() {
        return User.create(
            this.id,
            this.email,
            this.name,
            this.balance
        );
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
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
            ", name='" + name + '\'' +
            ", email='" + email + '\'' +
            ", balance=" + balance +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
} 