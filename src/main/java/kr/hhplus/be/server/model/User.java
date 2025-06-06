package kr.hhplus.be.server.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 사용자 도메인 모델
 */
public class User {
    private final UUID id;
    private final String email;
    private final String name;
    private BigDecimal balance;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private User(UUID id, String email, String name, BigDecimal balance) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.balance = balance;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static User create(UUID id, String email, String name, BigDecimal balance) {
        return new User(id, email, name, balance);
    }

    public void charge(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }
        this.balance = this.balance.add(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public void pay(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("결제 금액은 0보다 커야 합니다.");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalStateException("잔액이 부족합니다.");
        }
        this.balance = this.balance.subtract(amount);
        this.updatedAt = LocalDateTime.now();
    }

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
} 